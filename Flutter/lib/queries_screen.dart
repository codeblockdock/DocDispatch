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

  Future<void> fetchQueries() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      final res = await http.get(
        Uri.parse("${dotenv.env['API_BASE_URL']}/queries?contact=$contact"),
      );

      if (!mounted) return;

      if (res.statusCode == 200) {
        final data = jsonDecode(res.body) as List;
        queries = data.map((e) => QueryModel.fromJson(e)).toList();
        setState(() => loading = false);
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
    if (loading) {
      return Scaffold(
        appBar: AppBar(
          title: const Text("My Queries"),
          actions: const [ThemeToggleButton()],
        ),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    if (error) {
      return Scaffold(
        appBar: AppBar(
          title: const Text("My Queries"),
          actions: const [ThemeToggleButton()],
        ),
        body: const Center(child: Text("Server error")),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("My Queries"),
        actions: const [
          ThemeToggleButton(),
        ],
      ),
      body: ListView.builder(
        itemCount: queries.length,
        itemBuilder: (_, i) {
          final q = queries[i];
          if (q.attended == 0) {
            return ListTile(
              title: Text(q.name),
              onTap: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text("Query to be attended shortly"),
                  ),
                );
              },
            );
          }
          return ExpansionTile(
            title: Text(q.name),
            children: [
              ListTile(title: Text("Doctor: ${q.doctor}")),
              ListTile(title: Text("Treatment: ${q.treatment}")),
            ],
          );
        },
      ),
    );
  }
}