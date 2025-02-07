import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'dart:convert';
import 'model.dart';

class ViewModelProvider with ChangeNotifier {
  ViewModel? _viewModel;

  ViewModel? get viewModel => _viewModel;

  ViewModelProvider() {
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
        _viewModel = ViewModel.fromJson(jsonMap);
        notifyListeners(); // Notificeer de UI dat de ViewModel is geüpdatet
      }
    });
  }
}
