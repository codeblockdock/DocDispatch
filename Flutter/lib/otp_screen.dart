import 'package:flutter/material.dart';

class OTPScreen extends StatelessWidget {
  final String verificationId;

  const OTPScreen({super.key, required this.verificationId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Enter OTP")),
      body: Center(
        child: Text(
          "OTP sent\nVerificationId:\n$verificationId",
          textAlign: TextAlign.center,
        ),
      ),
    );
  }
}
