import 'package:flutter/material.dart';

import 'model.dart';

class ScheduleEditRow extends StatefulWidget {
  final EnrichedSchedule schedule;
  final Function(Schedule updatedSchedule) onSave;
  final Function(String id) onDelete;

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
  late TextEditingController _startDayController;
  late TextEditingController _endDayController;
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

    _startDayController = TextEditingController(
        text: widget.schedule.schedule.startDate.formattedDate);
    _endDayController = TextEditingController(
        text: widget.schedule.schedule.endDate?.formattedDate ?? "");
    _areaController = TextEditingController(
      text: widget.schedule.schedule.area.name,
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
    _startDayController.dispose();
    _endDayController.dispose();
    _areaController.dispose();
    _durationController.dispose();
    _intervalController.dispose();
    super.dispose();
  }

  void _handleDelete() {
    widget.onDelete(widget.schedule.schedule.id);
  }

  void _handleSave() {
    Schedule updatedSchedule = Schedule(
      id: widget.schedule.schedule.id,
      startDate: tryParse(_startDayController.text) ??
          widget.schedule.schedule.startDate,
      endDate: tryParse(_endDayController.text),
      scheduledTime: widget.schedule.schedule.scheduledTime,
      duration: widget.schedule.schedule.duration,
      daysInterval: widget.schedule.schedule.daysInterval,
      area: widget.schedule.schedule.area,
      enabled: widget.schedule.schedule.enabled,
    );
    widget.onSave(updatedSchedule);
  }

  /// Probeert een string te parsen in het formaat dd-mm-yyyy.
  /// Geeft een [ScheduleDate] terug als het lukt, anders `null`.
  static ScheduleDate? tryParse(String? input) {
    if (input == null || input.trim().isEmpty) return null;

    // Verwacht formaat: dd-mm-yyyy
    final parts = input.split('-');
    if (parts.length != 3) return null;

    final day = int.tryParse(parts[0]);
    final month = int.tryParse(parts[1]);
    final year = int.tryParse(parts[2]);

    if (day == null || month == null || year == null) return null;

    // Optioneel: je kunt hier nog een basis validatie toevoegen,
    // bijvoorbeeld of de dag en maand binnen de verwachte grenzen vallen.
    if (day < 1 || day > 31 || month < 1 || month > 12) return null;

    return ScheduleDate(year: year, month: month, day: day);
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
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _areaController,
                    decoration: const InputDecoration(labelText: 'Gebied'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _durationController,
                    keyboardType: TextInputType.number,
                    decoration:
                        const InputDecoration(labelText: 'Duur (minuten)'),
                  ),
                ),
              ],
            ),
            Row(children: [
              Expanded(
                  child: TextField(
                controller: _startDayController,
                decoration: const InputDecoration(labelText: 'Startdatum'),
              )),
              const SizedBox(width: 8),
              Expanded(
                  child: TextField(
                controller: _endDayController,
                decoration: const InputDecoration(labelText: 'Eind datum'),
              )),
              const SizedBox(width: 8),
              Expanded(
                  child: TextField(
                controller: _intervalController,
                keyboardType: TextInputType.number,
                decoration:
                    const InputDecoration(labelText: 'Interval (dagen)'),
              )),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              ElevatedButton(
                onPressed: _handleSave,
                child: const Text('Opslaan'),
              ),
              ElevatedButton(
                onPressed: _handleDelete,
                child: const Text('Verwijder'),
              ),
            ])
          ],
        ),
      ),
    );
  }
}
