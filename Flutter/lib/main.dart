import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'splash_screen.dart';
import 'theme_manager.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  await loadThemeFromPrefs();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<ThemeMode>(
      valueListenable: themeNotifier,
      builder: (context, currentMode, child) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          title: 'DocDispatch',
          theme: ThemeData(
            colorScheme: ColorScheme.fromSeed(
              seedColor: Colors.deepPurple,
              brightness: Brightness.light,
            ),
            useMaterial3: true,
            appBarTheme: const AppBarTheme(
              backgroundColor: Colors.deepPurple,
              foregroundColor: Colors.white,
            ),
          ),
          darkTheme: ThemeData(
            colorScheme: ColorScheme.fromSeed(
              seedColor: Colors.deepPurple,
              brightness: Brightness.dark,
            ),
            useMaterial3: true,
            appBarTheme: const AppBarTheme(
              backgroundColor: Color(0xFF1E1E2C),
              foregroundColor: Colors.white,
            ),
          ),
          themeMode: currentMode,
          home: const SplashScreen(),
        );
      },
    );
  }
}