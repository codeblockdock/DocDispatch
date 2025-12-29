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
    return QueryModel(
      name: json['name'] ?? 'Unknown',
      attended: json['attended'] ?? 0,
      doctor: json['doctor'] ?? 'Pending',
      hospital: json['hospital'] ?? 'Unknown Hospital',
      city: json['city'] ?? 'Unknown City',
      treatment: json['treatment'] ?? '',
      diagnosis: json['diagnosis'] ?? 'Under Observation',
      advice: json['advice'] ?? 'No specific advice',
      date: json['date'] ?? '',
      appointment: (json['appointment'] == null || json['appointment'] == "")
          ? "Not Applicable"
          : json['appointment'],
    );
  }
}