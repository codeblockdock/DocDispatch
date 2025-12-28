import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class PatientFormScreen extends StatefulWidget {
  const PatientFormScreen({super.key});

  @override
  State<PatientFormScreen> createState() => _PatientFormScreenState();
}

class _PatientFormScreenState extends State<PatientFormScreen> {
  final _formKey = GlobalKey<FormState>();

  final nameController = TextEditingController();
  final ageController = TextEditingController();
  final tempController = TextEditingController();
  final daysController = TextEditingController();

  bool contagious = false;
  bool loading = false;
  String gender = "Male";

  final symptoms = ["Fever", "Cough", "Cold"];
  final selected = <String>[];

  Future<void> submitForm() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => loading = true);

    try {
      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      await http.post(
        Uri.parse("https://example.com/api/submit"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({
          "contact": contact,
          "name": nameController.text,
          "age": int.parse(ageController.text),
          "gender": gender,
          "temperature": double.parse(tempController.text),
          "days": int.parse(daysController.text),
          "contagious": contagious ? "yes" : "no",
          "symptoms": selected,
        }),
      );

      if (!mounted) return;

      setState(() => loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Submitted successfully")),
      );
      Navigator.pop(context);
    } catch (_) {
      if (!mounted) return;
      setState(() => loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Server error")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Patient Details")),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: nameController,
              decoration: const InputDecoration(labelText: "Name"),
              validator: (v) => v!.isEmpty ? "Required" : null,
            ),
            ElevatedButton(
              onPressed: loading ? null : submitForm,
              child: loading
                  ? const CircularProgressIndicator()
                  : const Text("SUBMIT"),
            ),
          ],
        ),
      ),
    );
  }
}
