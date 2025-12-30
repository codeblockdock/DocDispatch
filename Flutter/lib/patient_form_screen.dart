import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'theme_manager.dart';
import 'utils/patient_utils.dart';

class PatientFormScreen extends StatefulWidget {
  const PatientFormScreen({super.key});

  @override
  State<PatientFormScreen> createState() => PatientFormScreenState();
}

class PatientFormScreenState extends State<PatientFormScreen> with PatientFormLogic {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Patient Details"),
        actions: const [ThemeToggleButton()],
      ),
      body: Form(
        key: formKey,
        autovalidateMode: autoValidate,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: nameController,
              decoration: const InputDecoration(labelText: "Name"),
              inputFormatters: [
                FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z\s]')),
              ],
              validator: (v) => v == null || v.isEmpty ? "Required" : null,
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: TextFormField(
                    controller: ageController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: "Age (5–60)"),
                    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                    validator: (v) {
                      final age = int.tryParse(v ?? "");
                      if (age == null || age < 5 || age > 60) return "Invalid";
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: DropdownButtonFormField<String>(
                    initialValue: gender.isEmpty ? null : gender,
                    decoration: const InputDecoration(labelText: "Gender"),
                    items: const [
                      DropdownMenuItem(value: "Male", child: Text("Male")),
                      DropdownMenuItem(value: "Female", child: Text("Female")),
                      DropdownMenuItem(value: "Other", child: Text("Other")),
                    ],
                    validator: (v) => v == null ? "Required" : null,
                    onChanged: (v) => setState(() => gender = v!),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: TextFormField(
                    controller: tempController,
                    keyboardType: const TextInputType.numberWithOptions(decimal: true),
                    decoration: const InputDecoration(labelText: "Temp (°C)"),
                    inputFormatters: [FilteringTextInputFormatter.allow(RegExp(r'[0-9.]'))],
                    validator: (v) {
                      final t = double.tryParse(v ?? "");
                      if (t == null || t < 35 || t > 41) return "35–41 Only";
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: TextFormField(
                    controller: daysController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: "Days (0–7)"),
                    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                    validator: (v) {
                      final d = int.tryParse(v ?? "");
                      if (d == null || d < 0 || d > 7) return "0–7 Only";
                      return null;
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            CheckboxListTile(
              title: const Text("Condition seems contagious"),
              value: contagious,
              onChanged: (v) => setState(() => contagious = v!),
              contentPadding: EdgeInsets.zero,
            ),
            const Divider(),
            const Text("Location Details", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: TextFormField(
                    controller: zipController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: "Zipcode"),
                    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                    validator: (v) => v == null || v.length != 6 ? "6 Digits" : null,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  flex: 3,
                  child: TextFormField(
                    controller: cityController,
                    decoration: const InputDecoration(labelText: "City"),
                    validator: (v) => v == null || v.isEmpty ? "Required" : null,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              initialValue: selectedState,
              decoration: const InputDecoration(labelText: "State (India)"),
              items: indianStates.map((s) => DropdownMenuItem(value: s, child: Text(s))).toList(),
              onChanged: (v) => setState(() => selectedState = v),
              validator: (v) => v == null ? "Required" : null,
            ),
            const Divider(height: 30),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text("Symptoms (Max 5)", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                Text("${selectedSymptoms.length}/5", style: TextStyle(color: selectedSymptoms.length == 5 ? Colors.red : Colors.grey)),
              ],
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              children: [
                ...commonSymptoms.map((s) => FilterChip(
                  label: Text(s),
                  selected: selectedSymptoms.contains(s),
                  onSelected: (v) => toggleSymptom(s),
                )),
                ...selectedSymptoms.where((s) => !commonSymptoms.contains(s)).map((s) => InputChip(
                  label: Text(s),
                  selected: true,
                  onDeleted: () => toggleSymptom(s),
                  onSelected: (bool value) {},
                )),
                ActionChip(
                  avatar: const Icon(Icons.add, size: 16),
                  label: const Text("Add Custom"),
                  onPressed: showAddSymptomDialog,
                ),
              ],
            ),
            const SizedBox(height: 30),
            ElevatedButton(
              onPressed: loading ? null : handleVerifyButton,
              style: ElevatedButton.styleFrom(minimumSize: const Size(double.infinity, 50)),
              child: loading
                  ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                  : const Text("REVIEW & SUBMIT"),
            ),
          ],
        ),
      ),
    );
  }
}