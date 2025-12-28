import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

final ValueNotifier<ThemeMode> themeNotifier = ValueNotifier(ThemeMode.light);

Future<void> loadThemeFromPrefs() async {
  final prefs = await SharedPreferences.getInstance();
  final isDark = prefs.getBool('isDarkMode') ?? false;
  themeNotifier.value = isDark ? ThemeMode.dark : ThemeMode.light;
}

class ThemeToggleButton extends StatelessWidget {
  const ThemeToggleButton({super.key});

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<ThemeMode>(
      valueListenable: themeNotifier,
      builder: (_, currentMode, __) {
        final isDark = currentMode == ThemeMode.dark;
        return IconButton(
          icon: Icon(isDark ? Icons.light_mode : Icons.dark_mode),
          onPressed: () async {
            final newMode = isDark ? ThemeMode.light : ThemeMode.dark;
            themeNotifier.value = newMode;

            final prefs = await SharedPreferences.getInstance();
            await prefs.setBool('isDarkMode', newMode == ThemeMode.dark);
          },
        );
      },
    );
  }
}