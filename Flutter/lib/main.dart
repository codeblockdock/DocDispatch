import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'mobile_screen.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(); // 🔥 THIS LINE IS MANDATORY
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MobileScreen(),
    );
  }
}
