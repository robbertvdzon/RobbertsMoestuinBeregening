import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';
import 'dart:convert';
import 'model.dart';

class BeregeningDataProvider with ChangeNotifier {
  BeregeningData? _beregeningData;
  BeregeningData? get beregeningData => _beregeningData;

  SummaryPumpUsage? _summaryPumpUsage;
  SummaryPumpUsage? get summaryPumpUsage => _summaryPumpUsage;


  BeregeningDataProvider() {
    _listenToFirestore();
    _listenToFirestoreForSummary();
  }

  void _listenToFirestore() {
    FirebaseFirestore.instance
        .collection('bewatering')
        .doc('status')
        .snapshots()
        .listen((snapshot) {
      if (snapshot.exists && snapshot.data() != null) {
        Map<String, dynamic> data = snapshot.data()!;
        String jsonString = data['viewModel'].toString();
        final jsonMap = json.decode(jsonString) as Map<String, dynamic>;
        final viewModel = ViewModel.fromJson(jsonMap);
        String lastupdate = data['lastupdate'].toString();
        _beregeningData = BeregeningData(viewModel: viewModel,lastUpdate: lastupdate);
        notifyListeners();
      }
    });
  }

  void _listenToFirestoreForSummary() {
    FirebaseFirestore.instance
        .collection('bewatering')
        .doc('pumpsummary')
        .snapshots()
        .listen((snapshot) {
      if (snapshot.exists && snapshot.data() != null) {
        Map<String, dynamic> data = snapshot.data()!;
        String jsonString = data['summary'].toString();
        final jsonMap = json.decode(jsonString) as Map<String, dynamic>;
        _summaryPumpUsage = SummaryPumpUsage.fromJson(jsonMap);
        notifyListeners();
      }
    });
  }

}
