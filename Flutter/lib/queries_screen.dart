import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'models/query_model.dart';
import 'theme_manager.dart';

class QueriesScreen extends StatefulWidget {
  const QueriesScreen({super.key});

  @override
  State<QueriesScreen> createState() => _QueriesScreenState();
}

class _QueriesScreenState extends State<QueriesScreen> {
  bool loading = true;
  bool error = false;
  List<QueryModel> queries = [];

  @override
  void initState() {
    super.initState();
    fetchQueries();
  }

  Future<void> fetchQueries({bool isRefresh = false}) async {
    try {
      if (!isRefresh) {
        setState(() => loading = true);
      }

      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      final res = await http.get(
        Uri.parse("${dotenv.env['API_BASE_URL']}/queries?contact=$contact"),
      );

      if (!mounted) return;

      if (res.statusCode == 200) {
        final data = jsonDecode(res.body) as List;
        setState(() {
          queries = data.map((e) => QueryModel.fromJson(e)).toList();
          loading = false;
          error = false;
        });
      } else {
        throw Exception();
      }
    } catch (_) {
      if (!mounted) return;
      setState(() {
        loading = false;
        error = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("My Queries"),
        actions: const [ThemeToggleButton()],
      ),
      body: Builder(
        builder: (context) {
          if (loading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (error) {
            return RefreshIndicator(
              onRefresh: () => fetchQueries(isRefresh: true),
              child: ListView(
                physics: const AlwaysScrollableScrollPhysics(),
                children: [
                  SizedBox(
                    height: MediaQuery.of(context).size.height * 0.8,
                    child: const Center(child: Text("Server error. Pull to retry.")),
                  ),
                ],
              ),
            );
          }

          if (queries.isEmpty) {
            return RefreshIndicator(
              onRefresh: () => fetchQueries(isRefresh: true),
              child: ListView(
                physics: const AlwaysScrollableScrollPhysics(),
                children: [
                  SizedBox(
                    height: MediaQuery.of(context).size.height * 0.8,
                    child: const Center(child: Text("No queries found.")),
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: () => fetchQueries(isRefresh: true),
            child: ListView.builder(
              physics: const AlwaysScrollableScrollPhysics(),
              itemCount: queries.length,
              itemBuilder: (_, i) {
                final q = queries[i];

                if (q.attended == 0) {
                  return Card(
                    margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                    child: ListTile(
                      leading: const Icon(Icons.access_time, color: Colors.orange),
                      title: Text(q.name),
                      subtitle: const Text("Status: Pending review"),
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text("Query to be attended shortly")),
                        );
                      },
                    ),
                  );
                }

                final bool hasAppointment = q.appointment != "Not Required" &&
                    q.appointment != "Not Applicable";

                return Card(
                  margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                  child: ExpansionTile(
                    leading: const Icon(Icons.check_circle, color: Colors.green),
                    title: Text(q.name),
                    subtitle: Text("Diagnosis: ${q.diagnosis}"),
                    children: [
                      const Divider(),
                      ListTile(
                        leading: const Icon(Icons.person, color: Colors.blue),
                        title: const Text("Doctor"),
                        subtitle: Text(q.doctor),
                      ),
                      ListTile(
                        leading: const Icon(Icons.local_hospital, color: Colors.red),
                        title: const Text("Hospital / Location"),
                        subtitle: Text("${q.hospital}, ${q.city}"),
                      ),
                      ListTile(
                        leading: const Icon(Icons.medication, color: Colors.purpleAccent),
                        title: const Text("Treatment"),
                        subtitle: Text(q.treatment),
                      ),
                      Container(
                        color: hasAppointment
                            ? Colors.deepPurple.withOpacity(0.1)
                            : null,
                        child: ListTile(
                          leading: Icon(
                            Icons.calendar_month,
                            color: hasAppointment ? Colors.deepPurple : Colors.grey,
                          ),
                          title: Text(
                            "Appointment",
                            style: TextStyle(
                              fontWeight: hasAppointment
                                  ? FontWeight.bold
                                  : FontWeight.normal,
                              color: hasAppointment ? Colors.deepPurple : null,
                            ),
                          ),
                          subtitle: Text(
                            q.appointment,
                            style: TextStyle(
                              fontWeight: hasAppointment
                                  ? FontWeight.bold
                                  : FontWeight.normal,
                              color: hasAppointment ? Colors.deepPurple : null,
                            ),
                          ),
                        ),
                      ),
                      ListTile(
                        leading: const Icon(Icons.info_outline, color: Colors.orange),
                        title: const Text("Advice"),
                        subtitle: Text(q.advice),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Align(
                          alignment: Alignment.bottomRight,
                          child: Text(
                            "Responded: ${q.date}",
                            style: const TextStyle(color: Colors.grey, fontSize: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}