import 'dart:async';
import 'dart:convert';
import 'dart:html' as html;

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:uuid/uuid.dart';

import 'firebase_options.dart';
import 'model.dart';


class Schedules extends StatefulWidget {
  const Schedules({super.key, required this.title, required this.viewModel,});

  final String title;
  final ViewModel? viewModel;

  @override
  State<Schedules> createState() => _SchedulesState();
}



class _SchedulesState extends State<Schedules> {
late Stream<ViewModel?> _schedulesStream; // eigenlijk niet alleen schedules dus!


  @override
  void initState() {
    super.initState();
    _schedulesStream = Stream.periodic(Duration(seconds: 1), (_) => // Raar, waarom elke seconde?
    widget.viewModel
    );
  }

  @override
  void dispose() {
    // Zorg ervoor dat de timer wordt geannuleerd om resource-lekken te voorkomen
    // _timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Schedules'),
      ),
      body: Column(
        // mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          SizedBox(height: 400),

          StreamBuilder<ViewModel?>(
            stream: _schedulesStream,
            builder: (context, snapshot) {
              if (!snapshot.hasData) {
                return CircularProgressIndicator();
              }
              List<EnrichedSchedule> schedules = snapshot.data!.schedules;

              return ListView.builder(
                shrinkWrap: true, // Past de hoogte aan de inhoud aan
                itemCount: schedules.length,
                itemBuilder: (context, index) {
                  EnrichedSchedule schedule = schedules[index];
                  String id = schedule.schedule.id.toString();
                  String hour = schedule.schedule.scheduledTime.hour.toString();
                  String minute =
                      schedule.schedule.scheduledTime.minute.toString();
                  String area = schedule.schedule.erea.name;
                  String showertime = schedule.schedule.duration.toString();
                  String interval = schedule.schedule.daysInterval.toString();

                  String nextDay = schedule.nextRun?.day.toString() ?? "";
                  String nextMonth = schedule.nextRun?.month.toString() ?? "";
                  String nextYear = schedule.nextRun?.year.toString() ?? "";
                  String nextHour = schedule.nextRun?.hour.toString() ?? "";
                  String nextMinute = schedule.nextRun?.minute.toString() ?? "";

                  String scheduleStr =
                      "$id : at $hour:$minute $area, every $interval day(s) for $showertime minutes";
                  String nextRun =
                      "Next run: $nextDay/$nextMonth/$nextYear $nextHour:$nextMinute";
                  String textnaam = "$scheduleStr\n$nextRun";
                  return Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    // mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      // Tekstelement met de naam van de schedule
                      Text(textnaam, style: TextStyle(fontSize: 16)),

                      // Knop om de schedule te verwijderen
                      ElevatedButton(
                        onPressed: () {
                          //_deleteSchedule(schedule);
                        },
                        child: Text('Verwijder'),
                      ),
                    ],
                  );
                },
              );
            },
          ),
        ],
      ),
    );
  }
}

//--------
