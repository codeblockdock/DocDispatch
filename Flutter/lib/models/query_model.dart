class QueryModel {
  final int qid;
  final String name;
  final int age;
  final int attended;

  final String? doctor;
  final String? treatment;
  final String? remarks;

  QueryModel({
    required this.qid,
    required this.name,
    required this.age,
    required this.attended,
    this.doctor,
    this.treatment,
    this.remarks,
  });

  factory QueryModel.fromJson(Map<String, dynamic> json) {
    return QueryModel(
      qid: json['qid'],
      name: json['name'],
      age: json['age'],
      attended: json['attended'],
      doctor: json['doctor'],
      treatment: json['treatment'],
      remarks: json['remarks'],
    );
  }
}
