import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:uuid/uuid.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'dart:html' as html;
import 'package:json_annotation/json_annotation.dart';
import 'dart:convert';
import 'dart:async';
import 'schedules.dart';

part 'model.g.dart';


// Top-level ViewModel class
@JsonSerializable(explicitToJson: true)
class ViewModel {
  final String ipAddress;
  final PumpStatus pumpStatus;
  final IrrigationArea currentIrrigationArea;
  final Timestamp pumpingEndTime;
  final List<EnrichedSchedule> schedules;
  final String nextSchedule;

  ViewModel({
    required this.ipAddress,
    required this.pumpStatus,
    required this.currentIrrigationArea,
    required this.pumpingEndTime,
    required this.schedules,
    required this.nextSchedule,
  });

  // Factory method for JSON deserialization
  factory ViewModel.fromJson(Map<String, dynamic> json) => _$ViewModelFromJson(json);

  // Method for JSON serialization
  Map<String, dynamic> toJson() => _$ViewModelToJson(this);
}


// Enum for PumpStatus
enum PumpStatus { OPEN, CLOSE }

// Enum for IrrigationArea
enum IrrigationArea { MOESTUIN, GAZON }

// Timestamp class
@JsonSerializable()
class Timestamp {
  final int year;
  final int month;
  final int day;
  final int hour;
  final int minute;
  final int second;

  Timestamp({
    required this.year,
    required this.month,
    required this.day,
    required this.hour,
    required this.minute,
    required this.second,
  });

  factory Timestamp.fromJson(Map<String, dynamic> json) => _$TimestampFromJson(json);

  Map<String, dynamic> toJson() => _$TimestampToJson(this);
}

// Timestamp class
@JsonSerializable()
class ScheduleTime {
  final int hour;
  final int minute;

  ScheduleTime({
    required this.hour,
    required this.minute,
  });

  factory ScheduleTime.fromJson(Map<String, dynamic> json) => _$ScheduleTimeFromJson(json);

  Map<String, dynamic> toJson() => _$ScheduleTimeToJson(this);
}

// ScheduleDate class
@JsonSerializable()
class ScheduleDate {
  final int year;
  final int month;
  final int day;

  ScheduleDate({
    required this.year,
    required this.month,
    required this.day,
  });

  factory ScheduleDate.fromJson(Map<String, dynamic> json) => _$ScheduleDateFromJson(json);

  Map<String, dynamic> toJson() => _$ScheduleDateToJson(this);

  /// Geeft de datum terug in het formaat dd-mm-yyyy
  String get formattedDate {
    // Zorg dat dag en maand altijd twee cijfers hebben.
    final String dd = day.toString().padLeft(2, '0');
    final String mm = month.toString().padLeft(2, '0');
    final String yyyy = year.toString();
    return "$dd-$mm-$yyyy";
  }
}

// EnrichedSchedule class
@JsonSerializable(explicitToJson: true)
class EnrichedSchedule {
  final Schedule schedule;
  final Timestamp? nextRun;

  EnrichedSchedule({
    required this.schedule,
    this.nextRun,
  });

  factory EnrichedSchedule.fromJson(Map<String, dynamic> json) => _$EnrichedScheduleFromJson(json);

  Map<String, dynamic> toJson() => _$EnrichedScheduleToJson(this);
}

// Schedule class
@JsonSerializable(explicitToJson: true)
class Schedule {
  final String id;
  final ScheduleDate startDate;
  final ScheduleDate? endDate;
  final ScheduleTime scheduledTime;
  final int duration;
  final int daysInterval;
  final IrrigationArea area;
  final bool enabled;

  Schedule({
    required this.id,
    required this.startDate,
    this.endDate,
    required this.scheduledTime,
    required this.duration,
    required this.daysInterval,
    required this.area,
    required this.enabled,
  });

  factory Schedule.fromJson(Map<String, dynamic> json) => _$ScheduleFromJson(json);

  Map<String, dynamic> toJson() => _$ScheduleToJson(this);
}

