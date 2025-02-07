import 'dart:async';

import 'package:flutter/material.dart';

import 'ScheduleEditRow.dart';
import 'model.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:uuid/uuid.dart';
import 'package:provider/provider.dart';
import 'viewModelProvider.dart';


class Schedules extends StatefulWidget {
  const Schedules({
    super.key,
    required this.title
  });

  final String title;

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
    // _schedulesStream = Stream.value(widget.viewModel);
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
    final viewModelProvider = Provider.of<ViewModelProvider>(context);
    final viewModel = viewModelProvider.viewModel;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Planning'),
      ),
      body: viewModel == null
          ? Center(child: CircularProgressIndicator())
          : Column(
        children: [
          // Knop toevoegen boven de lijst
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: ElevatedButton(
              onPressed: () {
                // Maak een lege schedule aan
                final emptySchedule = EnrichedSchedule(
                  schedule: Schedule(
                    id: uuid.v4(), // genereer een nieuwe uuid
                    startDate: ScheduleDate(
                      year: DateTime.now().year,
                      month: DateTime.now().month,
                      day: DateTime.now().day,
                    ),
                    endDate: null,
                    scheduledTime: ScheduleTime(hour: 8, minute: 0),
                    duration: 15,
                    daysInterval: 1,
                    area: IrrigationArea.GAZON, // standaard waarde
                    enabled: true,
                  ),
                  nextRun: null,
                );

                // Navigeer naar een pagina waarin de lege schedule bewerkt kan worden.
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => Scaffold(
                      appBar: AppBar(title: const Text('Nieuwe planning')),
                      body: ScheduleEditRow(
                        schedule: emptySchedule,
                        onSave: (updatedSchedule) {
                          _addCommand(
                            "ADD_SCHEDULE,${updatedSchedule.id},${updatedSchedule.duration},${updatedSchedule.daysInterval},${updatedSchedule.area.name},${updatedSchedule.enabled},${updatedSchedule.startDate.year},${updatedSchedule.startDate.month},${updatedSchedule.startDate.day},${updatedSchedule.endDate?.year ?? ''},${updatedSchedule.endDate?.month ?? ''},${updatedSchedule.endDate?.day ?? ''},${updatedSchedule.scheduledTime.hour},${updatedSchedule.scheduledTime.minute}",
                          );
                          // Navigator.pop(context);
                        },
                        onDelete: (id) {
                          Navigator.pop(context);
                        },
                      ),
                    ),
                  ),
                );
              },
              child: const Text("Nieuwe planning toevoegen"),
            ),
          ),

          // De lijst met planningen
          Expanded(
            child: ListView.builder(
              itemCount: viewModel.schedules.length,
              itemBuilder: (context, index) {
                final schedule = viewModel.schedules[index];
                return ScheduleEditRow(
                  schedule: schedule,
                  onSave: (updatedSchedule) {
                    _addCommand(
                        "ADD_SCHEDULE,${updatedSchedule.id},${updatedSchedule.duration},${updatedSchedule.daysInterval},${updatedSchedule.area.name},${updatedSchedule.enabled},${updatedSchedule.startDate.year},${updatedSchedule.startDate.month},${updatedSchedule.startDate.day},${updatedSchedule.endDate?.year ?? ''},${updatedSchedule.endDate?.month ?? ''},${updatedSchedule.endDate?.day ?? ''},${updatedSchedule.scheduledTime.hour},${updatedSchedule.scheduledTime.minute}");
                  },
                  onDelete: (id) {
                    _addCommand("REMOVE_SCHEDULE,${id}");
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
