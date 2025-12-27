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

  final List<String> symptomsList = [
    "Fever",
    "Cough",
    "Cold",
    "Headache",
    "Vomiting",
    "Diarrhea",
    "Sore throat",
    "Fatigue"
  ];

  final List<String> selectedSymptoms = [];

  Future<void> submitForm() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => loading = true);

    try {
      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      final body = {
        "contact": contact,
        "name": nameController.text.trim(),
        "age": int.parse(ageController.text),
        "gender": gender,
        "temperature": double.parse(tempController.text),
        "days": int.parse(daysController.text),
        "contagious": contagious ? "yes" : "no",
        "symptoms": selectedSymptoms
      };

      /// 🔗 Placeholder URL
      final response = await http.post(
        Uri.parse("https://example.com/api/submit"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(body),
      );

      setState(() => loading = false);

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Submitted successfully")),
        );
        Navigator.pop(context);
      } else {
        throw Exception("Server error");
      }
    } catch (_) {
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
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: nameController,
                decoration: const InputDecoration(labelText: "Name"),
                validator: (v) =>
                v!.isEmpty ? "Name required" : null,
              ),

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

              DropdownButtonFormField<String>(
                value: gender,
                items: ["Male", "Female", "Other"]
                    .map((e) => DropdownMenuItem(
                  value: e,
                  child: Text(e),
                ))
                    .toList(),
                onChanged: (v) => gender = v!,
                decoration: const InputDecoration(labelText: "Gender"),
              ),

              TextFormField(
                controller: tempController,
                keyboardType: TextInputType.number,
                decoration:
                const InputDecoration(labelText: "Temperature (°C)"),
                validator: (v) {
                  final t = double.tryParse(v ?? "");
                  if (t == null || t < 35 || t > 41) {
                    return "Temperature must be 35–41 °C";
                  }
                  return null;
                },
              ),

              TextFormField(
                controller: daysController,
                keyboardType: TextInputType.number,
                decoration:
                const InputDecoration(labelText: "Days (0–7)"),
                validator: (v) {
                  final d = int.tryParse(v ?? "");
                  if (d == null || d < 0 || d > 7) {
                    return "Days must be 0–7";
                  }
                  return null;
                },
              ),

              CheckboxListTile(
                title: const Text("Condition seems contagious"),
                value: contagious,
                onChanged: (v) => setState(() => contagious = v!),
              ),

              const SizedBox(height: 10),
              const Align(
                alignment: Alignment.centerLeft,
                child: Text("Symptoms (optional)"),
              ),

              Wrap(
                spacing: 8,
                children: symptomsList.map((symptom) {
                  final selected = selectedSymptoms.contains(symptom);
                  return FilterChip(
                    label: Text(symptom),
                    selected: selected,
                    onSelected: (v) {
                      setState(() {
                        v
                            ? selectedSymptoms.add(symptom)
                            : selectedSymptoms.remove(symptom);
                      });
                    },
                  );
                }).toList(),
              ),

              const SizedBox(height: 24),

              loading
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                onPressed: submitForm,
                child: const Text("SUBMIT"),
              )
            ],
          ),
        ),
      ),
    );
  }
}
