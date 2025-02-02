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


class ScheduleEditRow extends StatefulWidget {
  final EnrichedSchedule schedule;
  final Function(EnrichedSchedule updatedSchedule) onSave;
  final Function(EnrichedSchedule updatedSchedule) onDelete;

  const ScheduleEditRow({
    Key? key,
    required this.schedule,
    required this.onSave,
    required this.onDelete,
  }) : super(key: key);

  @override
  _ScheduleEditRowState createState() => _ScheduleEditRowState();
}

class _ScheduleEditRowState extends State<ScheduleEditRow> {
  late TextEditingController _hourController;
  late TextEditingController _minuteController;
  late TextEditingController _areaController;
  late TextEditingController _durationController;
  late TextEditingController _intervalController;

  @override
  void initState() {
    super.initState();
    // Vul de controllers met de initiële waarden uit schedule
    _hourController = TextEditingController(
      text: widget.schedule.schedule.scheduledTime.hour.toString(),
    );
    _minuteController = TextEditingController(
      text: widget.schedule.schedule.scheduledTime.minute.toString(),
    );
    _areaController = TextEditingController(
      text: widget.schedule.schedule.erea.name,
    );
    _durationController = TextEditingController(
      text: widget.schedule.schedule.duration.toString(),
    );
    _intervalController = TextEditingController(
      text: widget.schedule.schedule.daysInterval.toString(),
    );
  }

  @override
  void dispose() {
    _hourController.dispose();
    _minuteController.dispose();
    _areaController.dispose();
    _durationController.dispose();
    _intervalController.dispose();
    super.dispose();
  }
  void _handleDelete() {
    widget.onDelete(widget.schedule);
  }

  void _handleSave() {
    // Maak een bijgewerkte schedule aan met de waarden uit de tekstvelden.
    // Let op: Je zult dit moeten aanpassen aan de structuur van je model.
    // Hieronder is een voorbeeld, afhankelijk van hoe je model is gedefinieerd.

    // Eerst kan je de bestaande schedule kopiëren en de gewijzigde waarden instellen.
    EnrichedSchedule updatedSchedule = widget.schedule;
    // EnrichedSchedule updatedSchedule = widget.schedule.copyWith(
    //   schedule: widget.schedule.schedule.copyWith(
    //     scheduledTime: DateTime(
    //       widget.schedule.schedule.scheduledTime.year,
    //       widget.schedule.schedule.scheduledTime.month,
    //       widget.schedule.schedule.scheduledTime.day,
    //       int.tryParse(_hourController.text) ?? widget.schedule.schedule.scheduledTime.hour,
    //       int.tryParse(_minuteController.text) ?? widget.schedule.schedule.scheduledTime.minute,
    //     ),
    //     erea: widget.schedule.schedule.erea.copyWith(
    //       name: _areaController.text,
    //     ),
    //     duration: int.tryParse(_durationController.text) ?? widget.schedule.schedule.duration,
    //     daysInterval: int.tryParse(_intervalController.text) ?? widget.schedule.schedule.daysInterval,
    //   ),
    // );

    // Roep de onSave callback aan, zodat de parent weet dat deze schedule is bijgewerkt.
    widget.onSave(updatedSchedule);
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Weergeef bijvoorbeeld het ID of andere info als label
            Text("Schedule ID: ${widget.schedule.schedule.id}"),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _hourController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Uur',
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _minuteController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Minuut',
                    ),
                  ),
                ),
              ],
            ),
            TextField(
              controller: _areaController,
              decoration: const InputDecoration(labelText: 'Gebied'),
            ),
            TextField(
              controller: _durationController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Duur (minuten)'),
            ),
            TextField(
              controller: _intervalController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Interval (dagen)'),
            ),
            const SizedBox(height: 12),
            ElevatedButton(
              onPressed: _handleSave,
              child: const Text('Opslaan'),
            ),
            ElevatedButton(
              onPressed: _handleDelete,
              child: const Text('Verwijder'),
            ),
          ],
        ),
      ),
    );
  }
}
