import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

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
  String gender = "";
  AutovalidateMode autoValidate = AutovalidateMode.disabled;

  final List<String> symptomsList = [
    "Fever",
    "Cough",
    "Cold",
    "Headache",
    "Sore Throat",
    "Fatigue"
  ];
  final List<String> selectedSymptoms = [];

  Future<void> submitForm() async {
    setState(() {
      autoValidate = AutovalidateMode.onUserInteraction;
    });

    if (!_formKey.currentState!.validate()) return;

    setState(() => loading = true);

    try {
      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      final response = await http.post(
        Uri.parse("${dotenv.env['API_BASE_URL']}/submit"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({
          "contact": contact,
          "name": nameController.text.trim(),
          "age": int.parse(ageController.text),
          "gender": gender,
          "temperature": double.parse(tempController.text).round(),
          "days": int.parse(daysController.text),
          "contagious": contagious ? "yes" : "no",
          "symptoms": selectedSymptoms,
        }),
      );

      if (!mounted) return;

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Submitted successfully")),
        );
        Navigator.pop(context);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Server error (${response.statusCode})")),
        );
      }
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Network error")),
      );
    } finally {
      if (mounted) {
        setState(() => loading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Patient Details")),
      body: Form(
        key: _formKey,
        autovalidateMode: autoValidate,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: nameController,
              decoration: const InputDecoration(labelText: "Name"),
              validator: (v) => v == null || v.isEmpty ? "Required" : null,
            ),

            const SizedBox(height: 12),
            TextFormField(
              controller: ageController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: "Age (5–60)"),
              validator: (v) {
                final age = int.tryParse(v ?? "");
                if (age == null || age < 5 || age > 60) {
                  return "Age must be between 5 and 60";
                }
                return null;
              },
            ),

            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              initialValue: gender.isEmpty ? null : gender,
              decoration: const InputDecoration(labelText: "Gender"),
              items: const [
                DropdownMenuItem(value: "Male", child: Text("Male")),
                DropdownMenuItem(value: "Female", child: Text("Female")),
                DropdownMenuItem(value: "Other", child: Text("Other")),
              ],
              validator: (v) {
                if (v == null || v.isEmpty) {
                  return "Please select gender";
                }
                return null;
              },
              onChanged: (v) {
                setState(() {
                  gender = v!;
                });
              },
            ),

            const SizedBox(height: 12),
            TextFormField(
              controller: tempController,
              keyboardType: TextInputType.number,
              decoration:
              const InputDecoration(labelText: "Temperature (°C 35–41)"),
              validator: (v) {
                final temp = double.tryParse(v ?? "");
                if (temp == null || temp < 35 || temp > 41) {
                  return "Temperature must be 35–41 °C";
                }
                return null;
              },
            ),

            const SizedBox(height: 12),
            TextFormField(
              controller: daysController,
              keyboardType: TextInputType.number,
              decoration:
              const InputDecoration(labelText: "Days in condition (0–7)"),
              validator: (v) {
                final d = int.tryParse(v ?? "");
                if (d == null || d < 0 || d > 7) {
                  return "Days must be between 0 and 7";
                }
                return null;
              },
            ),

            const SizedBox(height: 12),
            CheckboxListTile(
              title: const Text("Condition seems contagious"),
              value: contagious,
              onChanged: (v) => setState(() => contagious = v!),
            ),

            const SizedBox(height: 8),
            const Text("Symptoms (optional)"),
            Wrap(
              spacing: 8,
              children: symptomsList.map((s) {
                final selected = selectedSymptoms.contains(s);
                return FilterChip(
                  label: Text(s),
                  selected: selected,
                  onSelected: (v) {
                    setState(() {
                      v
                          ? selectedSymptoms.add(s)
                          : selectedSymptoms.remove(s);
                    });
                  },
                );
              }).toList(),
            ),

            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: loading ? null : submitForm,
              child: loading
                  ? const CircularProgressIndicator(color: Colors.white)
                  : const Text("SUBMIT"),
            ),
          ],
        ),
      ),
    );
  }
}
