import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';
import '../models/query_model.dart';
import '../utils/theme_manager.dart';
import '../utils/query_utils.dart';

class QueriesScreen extends StatefulWidget {
  const QueriesScreen({super.key});

  @override
  State<QueriesScreen> createState() => QueriesScreenState();
}

class QueriesScreenState extends State<QueriesScreen> {
  bool loading = true;
  bool error = false;
  List<QueryModel> queries = [];

  @override
  void initState() {
    super.initState();
    loadInitialData();
  }

  Future<void> loadInitialData() async {
    final cached = await QueryUtils.getCachedQueries();
    if (cached.isNotEmpty) {
      setState(() {
        queries = cached;
        loading = false;
      });
    }
    refresh();
  }

  Future<void> refresh() async {
    try {
      final freshData = await QueryUtils.fetchAndCacheQueries();
      if (!mounted) return;
      setState(() {
        queries = freshData;
        loading = false;
        error = false;
      });
    } catch (e) {
      if (!mounted) return;
      if (queries.isEmpty) setState(() => error = true);
      setState(() => loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("My Queries"),
        actions: const [ThemeToggleButton()],
      ),
      body: RefreshIndicator(
        onRefresh: refresh,
        child: buildBody(),
      ),
    );
  }

  Widget buildBody() {
    if (loading && queries.isEmpty) return const ShimmerLoading();
    if (error && queries.isEmpty) return buildStatusMessage("Server error. Pull to retry.");
    if (queries.isEmpty) return buildStatusMessage("No queries found.");

    return ListView.builder(
      physics: const AlwaysScrollableScrollPhysics(),
      itemCount: queries.length,
      itemBuilder: (_, i) => QueryCard(q: queries[i]),
    );
  }

  Widget buildStatusMessage(String msg) {
    return ListView(
      children: [
        SizedBox(
          height: MediaQuery.of(context).size.height * 0.8,
          child: Center(child: Text(msg)),
        ),
      ],
    );
  }
}

class ShimmerLoading extends StatelessWidget {
  const ShimmerLoading({super.key});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Shimmer.fromColors(
      baseColor: isDark ? Colors.grey[800]! : Colors.grey[300]!,
      highlightColor: isDark ? Colors.grey[700]! : Colors.grey[100]!,
      child: ListView.builder(
        itemCount: 6,
        itemBuilder: (_, _) => Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
          child: Container(
            height: 80,
            decoration: BoxDecoration(
              color: isDark ? Colors.black : Colors.white,
              borderRadius: BorderRadius.circular(8),
            ),
          ),
        ),
      ),
    );
  }
}

class QueryCard extends StatelessWidget {
  final QueryModel q;
  const QueryCard({super.key, required this.q});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final secondaryTextColor = isDark ? Colors.grey[400] : Colors.grey[600];

    if (q.attended == 0) {
      return Card(
        margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        child: ListTile(
          leading: const Icon(Icons.access_time, color: Colors.orange),
          title: Text(q.name),
          subtitle: const Text("Status: Pending review"),
          trailing: Text(
            q.date,
            style: TextStyle(color: secondaryTextColor, fontSize: 12),
          ),
          onTap: () {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text("Query to be attended shortly"),
                duration: Duration(seconds: 2),
              ),
            );
          },
        ),
      );
    }

    final bool hasAppt = q.appointment != "Not Required" && q.appointment != "Not Applicable";

    final highlightColor = isDark
        ? Colors.deepPurple.withValues(alpha: 0.3)
        : Colors.deepPurple.withValues(alpha: 0.1);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      child: ExpansionTile(
        leading: const Icon(Icons.check_circle, color: Colors.green),
        title: Text(q.name),
        subtitle: Text("Diagnosis: ${q.diagnosis}"),
        children: [
          infoTile(context, Icons.person, "Doctor", q.doctor, Colors.blue),
          infoTile(context, Icons.local_hospital, "Location", "${q.hospital}, ${q.city}", Colors.red),
          infoTile(context, Icons.medication, "Treatment", q.treatment, Colors.purpleAccent),
          Container(
            color: hasAppt ? highlightColor : null,
            child: infoTile(
              context,
              Icons.calendar_month,
              "Appointment",
              q.appointment,
              hasAppt ? Colors.deepPurple[200]! : Colors.grey,
              isBold: hasAppt,
              overrideColor: hasAppt && isDark ? Colors.white : null,
            ),
          ),
          infoTile(context, Icons.info_outline, "Advice", q.advice, Colors.orange),

          if (q.date.isNotEmpty) ...[
            const Divider(height: 1),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Icon(Icons.history, size: 16, color: secondaryTextColor),
                  const SizedBox(width: 6),
                  Text(
                    "Attended on: ${q.date}",
                    style: TextStyle(
                      color: secondaryTextColor,
                      fontSize: 12,
                      fontStyle: FontStyle.italic,
                    ),
                  ),
                ],
              ),
            ),
          ]
        ],
      ),
    );
  }

  Widget infoTile(BuildContext context, IconData icon, String title, String sub, Color iconColor, {bool isBold = false, Color? overrideColor}) {
    if (sub.isEmpty || sub == "null") return const SizedBox.shrink();

    return ListTile(
      leading: Icon(icon, color: iconColor),
      title: Text(title, style: const TextStyle(fontSize: 13, color: Colors.grey)),
      subtitle: Text(
        sub,
        style: TextStyle(
          fontWeight: isBold ? FontWeight.bold : FontWeight.normal,
          fontSize: 15,
          color: overrideColor ?? Theme.of(context).textTheme.bodyLarge?.color,
        ),
      ),
    );
  }
}