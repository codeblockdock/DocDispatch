import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'otp_screen.dart';
import 'theme_manager.dart';

class MobileScreen extends StatefulWidget {
  const MobileScreen({super.key});
  @override
  State<MobileScreen> createState() => _MobileScreenState();
}

class _MobileScreenState extends State<MobileScreen> {
  final TextEditingController mobileController = TextEditingController();
  bool loading = false;

  void sendOtp() async {
    final mobile = mobileController.text.trim();

    if (mobile.length != 10) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Enter valid 10-digit number")),
      );
      return;
    }

    setState(() => loading = true);

    try {
      await FirebaseAuth.instance.verifyPhoneNumber(
        phoneNumber: "+91$mobile",
        verificationCompleted: (_) {},
        verificationFailed: (e) {
          if (!mounted) return;
          setState(() => loading = false);
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(e.message ?? "Verification failed")),
          );
        },
        codeSent: (verificationId, _) {
          if (!mounted) return;
          setState(() => loading = false);
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => OTPScreen(verificationId: verificationId),
            ),
          );
        },
        codeAutoRetrievalTimeout: (_) {
          if (!mounted) return;
          setState(() => loading = false);
        },
      );
    } catch (_) {
      if (!mounted) return;
      setState(() => loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Something went wrong")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Login"),
        actions: const [
          ThemeToggleButton(),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: mobileController,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(
                labelText: "Mobile Number",
                prefixText: "+91 ",
              ),
            ),
            const SizedBox(height: 20),
            loading
                ? const CircularProgressIndicator()
                : ElevatedButton(
              onPressed: sendOtp,
              child: const Text("Send OTP"),
            )
          ],
        ),
      ),
    );
  }
}