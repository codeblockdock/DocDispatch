import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'models/query_model.dart';

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

  Future<void> fetchQueries() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      final response = await http.get(
        Uri.parse("https://example.com/api/queries?contact=$contact"),
      );

      if (response.statusCode == 200) {
        final List data = jsonDecode(response.body);
        queries = data.map((e) => QueryModel.fromJson(e)).toList();
        setState(() => loading = false);
      } else {
        throw Exception();
      }
    } catch (_) {
      setState(() {
        loading = false;
        error = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("My Queries")),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : error
          ? const Center(child: Text("Server error"))
          : queries.isEmpty
          ? const Center(child: Text("No queries found"))
          : ListView.builder(
        itemCount: queries.length,
        itemBuilder: (_, i) {
          final q = queries[i];

          if (q.attended == 0) {
            return ListTile(
              title: Text(q.name),
              subtitle: Text("Age: ${q.age}"),
              trailing: const Icon(Icons.hourglass_empty),
              onTap: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content:
                    Text("Query to be attended shortly"),
                  ),
                );
              },
            );
          }

          return ExpansionTile(
            title: Text(q.name),
            subtitle: Text("Age: ${q.age}"),
            children: [
              ListTile(
                title: Text("Doctor: ${q.doctor ?? '-'}"),
              ),
              ListTile(
                title:
                Text("Treatment: ${q.treatment ?? '-'}"),
              ),
              if (q.remarks != null &&
                  q.remarks!.isNotEmpty)
                ListTile(
                  title: Text("Remarks: ${q.remarks}"),
                ),
            ],
          );
        },
      ),
    );
  }
}
