import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

mixin PatientFormLogic<T extends StatefulWidget> on State<T> {
  final formKey = GlobalKey<FormState>();

  final nameController = TextEditingController();
  final ageController = TextEditingController();
  final tempController = TextEditingController();
  final daysController = TextEditingController();
  final zipController = TextEditingController();
  final cityController = TextEditingController();

  String? selectedState;
  bool contagious = false;
  bool loading = false;
  String gender = "";
  AutovalidateMode autoValidate = AutovalidateMode.disabled;

  final List<String> commonSymptoms = [
    "Fever", "Cough", "Cold", "Headache", "Sore Throat", "Fatigue"
  ];

  final List<String> suggestionDatabase = [
    "Fever", "Cough", "Cold", "Headache", "Sore Throat", "Fatigue",
    "Nausea", "Vomiting", "Diarrhea", "Body Ache", "Chills",
    "Shortness of Breath", "Loss of Taste", "Loss of Smell",
    "Rash", "Dizziness", "Sneezing", "Runny Nose"
  ];

  final List<String> indianStates = [
    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
    "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
    "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
    "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
    "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
    "Uttar Pradesh", "Uttarakhand", "West Bengal", "Delhi", "Chandigarh"
  ];

  final List<String> selectedSymptoms = [];

  @override
  void initState() {
    super.initState();
    loadSavedAddress();
  }

  @override
  void dispose() {
    nameController.dispose();
    ageController.dispose();
    tempController.dispose();
    daysController.dispose();
    zipController.dispose();
    cityController.dispose();
    super.dispose();
  }

  Future<void> loadSavedAddress() async {
    final prefs = await SharedPreferences.getInstance();
    if (!mounted) return;
    setState(() {
      zipController.text = prefs.getString('saved_zip') ?? "";
      cityController.text = prefs.getString('saved_city') ?? "";
      String? savedState = prefs.getString('saved_state');
      if (savedState != null && indianStates.contains(savedState)) {
        selectedState = savedState;
      }
    });
  }

  Future<void> saveAddressToPrefs() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('saved_zip', zipController.text.trim());
    await prefs.setString('saved_city', cityController.text.trim());
    if (selectedState != null) {
      await prefs.setString('saved_state', selectedState!);
    }
  }

  void toggleSymptom(String symptom) {
    setState(() {
      if (selectedSymptoms.contains(symptom)) {
        selectedSymptoms.remove(symptom);
      } else {
        if (selectedSymptoms.length >= 5) {
          showMaxLimitSnackBar();
        } else {
          selectedSymptoms.add(symptom);
        }
      }
    });
  }

  void showAddSymptomDialog() {
    if (selectedSymptoms.length >= 5) {
      showMaxLimitSnackBar();
      return;
    }
    showDialog(
      context: context,
      builder: (ctx) {
        return AlertDialog(
          title: const Text("Add Symptom"),
          content: Autocomplete<String>(
            optionsBuilder: (TextEditingValue val) {
              if (val.text == '') return const Iterable<String>.empty();
              return suggestionDatabase.where((option) =>
                  option.toLowerCase().contains(val.text.toLowerCase()));
            },
            onSelected: (String selection) {
              if (!selectedSymptoms.contains(selection)) toggleSymptom(selection);
              Navigator.pop(ctx);
            },
            fieldViewBuilder: (context, textController, focusNode, onFieldSubmitted) {
              return TextField(
                controller: textController,
                focusNode: focusNode,
                autofocus: true,
                decoration: InputDecoration(
                  labelText: "Type symptom...",
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.add),
                    onPressed: () {
                      if (textController.text.isNotEmpty &&
                          !selectedSymptoms.contains(textController.text)) {
                        toggleSymptom(textController.text);
                      }
                      Navigator.pop(ctx);
                    },
                  ),
                ),
                onSubmitted: (val) {
                  if (val.isNotEmpty && !selectedSymptoms.contains(val)) {
                    toggleSymptom(val);
                  }
                  Navigator.pop(ctx);
                },
              );
            },
          ),
        );
      },
    );
  }

  void showMaxLimitSnackBar() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Maximum of 5 symptoms allowed")),
    );
  }

  void handleVerifyButton() {
    setState(() {
      autoValidate = AutovalidateMode.onUserInteraction;
    });

    if (!formKey.currentState!.validate()) return;

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return AlertDialog(
          title: const Text("Confirm Details"),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              infoRow("Name", nameController.text),
              infoRow("Age", ageController.text),
              infoRow("Gender", gender),
              const Divider(),
              infoRow("Zipcode", zipController.text),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("EDIT"),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
                submitFinalData();
              },
              child: const Text("CONFIRM"),
            ),
          ],
        );
      },
    );
  }

  Widget infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: RichText(
        text: TextSpan(
          style: TextStyle(color: Theme.of(context).textTheme.bodyMedium?.color),
          children: [
            TextSpan(text: "$label: ", style: const TextStyle(fontWeight: FontWeight.bold)),
            TextSpan(text: value),
          ],
        ),
      ),
    );
  }

  Future<void> submitFinalData() async {
    setState(() => loading = true);
    await saveAddressToPrefs();

    try {
      final prefs = await SharedPreferences.getInstance();
      final contact = prefs.getString("contact") ?? "";

      final body = {
        "contact": contact,
        "name": nameController.text.trim(),
        "age": int.parse(ageController.text),
        "gender": gender,
        "temperature": double.parse(tempController.text).round(),
        "days": int.parse(daysController.text),
        "contagious": contagious ? "yes" : "no",
        "address": {
          "zip": zipController.text.trim(),
          "city": cityController.text.trim(),
          "state": selectedState,
        },
        "symptoms": selectedSymptoms,
      };

      final response = await http.post(
        Uri.parse("${dotenv.env['API_BASE_URL']}/submit"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(body),
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
}