class QueryModel {
  final String name;
  final int attended;
  final String doctor;
  final String hospital;
  final String city;
  final String treatment;
  final String diagnosis;
  final String advice;
  final String date;
  final String appointment;

  QueryModel({
    required this.name,
    required this.attended,
    required this.doctor,
    required this.hospital,
    required this.city,
    required this.treatment,
    required this.diagnosis,
    required this.advice,
    required this.date,
    required this.appointment,
  });

  factory QueryModel.fromJson(Map<String, dynamic> json) {
    String getString(String key, String defaultValue) {
      final val = json[key];
      if (val == null || val is! String || val.trim().isEmpty) {
        return defaultValue;
      }
      return val;
    }

    return QueryModel(
      name: getString('name', 'Unknown'),
      attended: json['attended'] ?? 0,
      doctor: getString('doctor', 'Pending'),
      hospital: getString('hospital', 'Unknown Hospital'),
      city: getString('city', 'Unknown City'),
      diagnosis: getString('diagnosis', 'Healthy'),
      treatment: getString('treatment', 'Not Applicable'),
      advice: getString('advice', 'No specific advice'),
      date: getString('date', ''),
      appointment: (json['appointment'] == null || json['appointment'] == "")
          ? "Not Applicable"
          : json['appointment'],
    );
  }
}