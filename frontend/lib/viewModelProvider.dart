import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'dart:convert';
import 'model.dart';

class BeregeningDataProvider with ChangeNotifier {
  BeregeningData? _beregeningData;

  BeregeningData? get beregeningData => _beregeningData;

  BeregeningDataProvider() {
    _listenToFirestore();
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
}
