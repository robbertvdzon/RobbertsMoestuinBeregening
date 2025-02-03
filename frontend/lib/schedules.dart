import 'dart:async';

import 'package:flutter/material.dart';

import 'ScheduleEditRow.dart';
import 'model.dart';
import 'package:cloud_firestore/cloud_firestore.dart';

class Schedules extends StatefulWidget {
  const Schedules({
    super.key,
    required this.title,
    required this.viewModel,
  });

  final String title;
  final ViewModel? viewModel;

  @override
  State<Schedules> createState() => _SchedulesState();
}

class _SchedulesState extends State<Schedules> {
  late Stream<ViewModel?>
      _schedulesStream; // eigenlijk niet alleen schedules dus!

  @override
  void initState() {
    super.initState();
    _schedulesStream = Stream.periodic(
        Duration(seconds: 1),
        (_) => // Raar, waarom elke seconde?
            widget.viewModel);
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _addCommand(String data) async {
    setState(() {
      final _db = FirebaseFirestore.instance;
      final jsonKeyValue = <String, String>{"command": data};
      _db
          .collection('bewatering')
          .doc('commands')
          .set(jsonKeyValue, SetOptions(merge: true))
          .onError((e, _) => print("Error writing document: $e"));
    });
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Planning'),
      ),
      body: Column(
        children: <Widget>[
          // Als je eventueel een vaste widget boven de ListView wilt,
          // kun je deze hier plaatsen, bijvoorbeeld een header:
          // const Text('Header', style: TextStyle(fontSize: 20)),

          // Zorg ervoor dat de ListView de resterende ruimte krijgt:
          Expanded(
            child: StreamBuilder<ViewModel?>(
              stream: _schedulesStream,
              builder: (context, snapshot) {
                if (!snapshot.hasData) {
                  return const Center(child: CircularProgressIndicator());
                }
                List<EnrichedSchedule> schedules = snapshot.data!.schedules;
                schedules.sort((a, b) => a.schedule.id.compareTo(b.schedule.id));

                return ListView.builder(
                  itemCount: schedules.length,
                  itemBuilder: (context, index) {
                    EnrichedSchedule schedule = schedules[index];
                    return ScheduleEditRow(
                      schedule: schedule,
                      onSave: (updatedSchedule) {
                        _addCommand("ADD_SCHEDULE,${updatedSchedule.id},${updatedSchedule.duration},${updatedSchedule.daysInterval},${updatedSchedule.area.name},${updatedSchedule.enabled},${updatedSchedule.startDate.year},${updatedSchedule.startDate.month},${updatedSchedule.startDate.day},${updatedSchedule.endDate?.year??''},${updatedSchedule.endDate?.month??''},${updatedSchedule.endDate?.day??''},${updatedSchedule.scheduledTime.hour},${updatedSchedule.scheduledTime.minute}");
                      },
                      onDelete: (id) {
                        _addCommand("REMOVE_SCHEDULE,${id}");
                      },

                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
