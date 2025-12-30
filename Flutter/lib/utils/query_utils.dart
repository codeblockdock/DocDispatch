import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/query_model.dart';

class QueryUtils {
  static const String _cacheKey = "cached_queries";

  static Future<List<QueryModel>> fetchAndCacheQueries() async {
    final prefs = await SharedPreferences.getInstance();
    final contact = prefs.getString("contact") ?? "";

    final res = await http.get(
      Uri.parse("${dotenv.env['API_BASE_URL']}/queries?contact=$contact"),
    );

    if (res.statusCode == 200) {
      await prefs.setString(_cacheKey, res.body);
      final data = jsonDecode(res.body) as List;
      return data.map((e) => QueryModel.fromJson(e)).toList();
    } else {
      throw Exception("Failed to load data");
    }
  }

  static Future<List<QueryModel>> getCachedQueries() async {
    final prefs = await SharedPreferences.getInstance();
    final cachedData = prefs.getString(_cacheKey);

    if (cachedData != null) {
      final data = jsonDecode(cachedData) as List;
      return data.map((e) => QueryModel.fromJson(e)).toList();
    }
    return [];
  }
}