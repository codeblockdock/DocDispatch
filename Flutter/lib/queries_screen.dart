import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';
import 'models/query_model.dart';
import 'theme_manager.dart';
import 'utils/query_utils.dart';

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
    _loadInitialData();
  }

  Future<void> _loadInitialData() async {
    final cached = await QueryUtils.getCachedQueries();
    if (cached.isNotEmpty) {
      setState(() {
        queries = cached;
        loading = false;
      });
    }
    _handleRefresh();
  }

  Future<void> _handleRefresh() async {
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
        onRefresh: _handleRefresh,
        child: _buildBody(),
      ),
    );
  }

  Widget _buildBody() {
    if (loading && queries.isEmpty) return const ShimmerLoading();
    if (error && queries.isEmpty) return _buildStatusMessage("Server error. Pull to retry.");
    if (queries.isEmpty) return _buildStatusMessage("No queries found.");

    return ListView.builder(
      physics: const AlwaysScrollableScrollPhysics(),
      itemCount: queries.length,
      itemBuilder: (_, i) => QueryCard(q: queries[i]),
    );
  }

  Widget _buildStatusMessage(String msg) {
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
    return Shimmer.fromColors(
      baseColor: Colors.grey[300]!,
      highlightColor: Colors.grey[100]!,
      child: ListView.builder(
        itemCount: 6,
        itemBuilder: (_, __) => Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
          child: Container(
            height: 80,
            decoration: BoxDecoration(
              color: Colors.white,
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
    if (q.attended == 0) {
      return Card(
        margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
        child: ListTile(
          leading: const Icon(Icons.access_time, color: Colors.orange),
          title: Text(q.name),
          subtitle: const Text("Status: Pending review"),
        ),
      );
    }

    final bool hasAppt = q.appointment != "Not Required" && q.appointment != "Not Applicable";

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      child: ExpansionTile(
        leading: const Icon(Icons.check_circle, color: Colors.green),
        title: Text(q.name),
        subtitle: Text("Diagnosis: ${q.diagnosis}"),
        children: [
          _infoTile(Icons.person, "Doctor", q.doctor, Colors.blue),
          _infoTile(Icons.local_hospital, "Location", "${q.hospital}, ${q.city}", Colors.red),
          _infoTile(Icons.medication, "Treatment", q.treatment, Colors.purpleAccent),
          Container(
            color: hasAppt ? Colors.deepPurple.withOpacity(0.1) : null,
            child: _infoTile(
                Icons.calendar_month,
                "Appointment",
                q.appointment,
                hasAppt ? Colors.deepPurple : Colors.grey,
                isBold: hasAppt
            ),
          ),
          _infoTile(Icons.info_outline, "Advice", q.advice, Colors.orange),
        ],
      ),
    );
  }

  Widget _infoTile(IconData icon, String title, String sub, Color color, {bool isBold = false}) {
    return ListTile(
      leading: Icon(icon, color: color),
      title: Text(title),
      subtitle: Text(sub, style: TextStyle(fontWeight: isBold ? FontWeight.bold : FontWeight.normal)),
    );
  }
}