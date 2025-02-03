import 'dart:async';

import 'package:flutter/material.dart';

import 'ScheduleEditRow.dart';
import 'model.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:uuid/uuid.dart';

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
  final uuid = Uuid();


  @override
  void initState() {
    super.initState();
    _schedulesStream = Stream.value(widget.viewModel);
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
                List<EnrichedSchedule> schedules = List.from(snapshot.data!.schedules);
                schedules.sort((a, b) => a.schedule.id.compareTo(b.schedule.id));

                // Maak een lege schedule (dummy) aan
                final emptySchedule = EnrichedSchedule(
                  schedule: Schedule(
                    id: uuid.v4(), // lege id = nieuwe schedule
                    startDate: ScheduleDate(
                      year: DateTime.now().year,
                      month: DateTime.now().month,
                      day: DateTime.now().day,
                    ),
                    endDate: null,
                    scheduledTime: ScheduleTime(hour: 8,minute: 0),
                    duration: 15,
                    daysInterval: 1,
                    area: IrrigationArea.GAZON, // standaard waarde
                    enabled: true,
                  ),
                  nextRun: null,
                );
                // Voeg de lege schedule toe aan de lijst
                schedules.add(emptySchedule);

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
