import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'home_screen.dart';
import 'theme_manager.dart';

class OTPScreen extends StatefulWidget {
  final String verificationId;
  const OTPScreen({super.key, required this.verificationId});

  @override
  State<OTPScreen> createState() => _OTPScreenState();
}

class _OTPScreenState extends State<OTPScreen> {
  final otpController = TextEditingController();
  bool loading = false;

  Future<void> verifyOtp() async {
    if (otpController.text.length != 6) return;

    setState(() => loading = true);

    try {
      final credential = PhoneAuthProvider.credential(
        verificationId: widget.verificationId,
        smsCode: otpController.text.trim(),
      );

      final result =
      await FirebaseAuth.instance.signInWithCredential(credential);

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(
        "contact",
        result.user?.phoneNumber ?? "",
      );

      if (!mounted) return;

      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (_) => const HomeScreen()),
            (_) => false,
      );
    } catch (_) {
      if (!mounted) return;
      setState(() => loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Invalid OTP")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Verify OTP"),
        actions: const [
          ThemeToggleButton(),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: otpController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: "OTP"),
            ),
            const SizedBox(height: 20),
            loading
                ? const CircularProgressIndicator()
                : ElevatedButton(
              onPressed: verifyOtp,
              child: const Text("Verify"),
            ),
          ],
        ),
      ),
    );
  }
}