import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'otp_screen.dart';

class MobileScreen extends StatefulWidget {
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

        verificationCompleted: (PhoneAuthCredential credential) {
          debugPrint("Auto verification completed");
        },

        verificationFailed: (FirebaseAuthException e) {
          setState(() => loading = false);
          debugPrint("Verification failed: ${e.message}");
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(e.message ?? "Verification failed")),
          );
        },

        codeSent: (String verificationId, int? resendToken) {
          setState(() => loading = false);
          debugPrint("OTP sent successfully");

          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => OTPScreen(verificationId: verificationId),
            ),
          );
        },

        codeAutoRetrievalTimeout: (String verificationId) {
          debugPrint("Auto-retrieval timeout");
          setState(() => loading = false);
        },

        timeout: const Duration(seconds: 60),
      );
    } catch (e) {
      setState(() => loading = false);
      debugPrint("Exception: $e");
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Something wrong")),
      );
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Login")),
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
