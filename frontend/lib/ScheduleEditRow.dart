import 'package:flutter/material.dart';

import 'model.dart'; // Zorg dat hierin de enum IrrigationArea staat

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
  late TextEditingController _timeController;
  late TextEditingController _startDayController;
  late TextEditingController _endDayController;
  late TextEditingController _durationController;
  late TextEditingController _intervalController;

  // In plaats van een string gebruiken we direct de enum-waarde.
  late IrrigationArea _selectedArea;

  // Checkbox voor enabled.
  late bool _enabled;
  late String _nextRun;

  @override
  void initState() {
    super.initState();
    // Vul de controllers met de initiële waarden uit schedule
    _timeController = TextEditingController(
      text: widget.schedule.schedule.scheduledTime.formattedTime,
    );
    _startDayController = TextEditingController(
        text: widget.schedule.schedule.startDate.formattedDate);
    _endDayController = TextEditingController(
        text: widget.schedule.schedule.endDate?.formattedDate ?? "");
    _durationController = TextEditingController(
      text: widget.schedule.schedule.duration.toString(),
    );
    _intervalController = TextEditingController(
      text: widget.schedule.schedule.daysInterval.toString(),
    );

    // Initialiseer de dropdown met de huidige area (enum)
    _selectedArea = widget.schedule.schedule.area;
    // Initialiseer de checkbox voor enabled.
    _enabled = widget.schedule.schedule.enabled;
    _nextRun = widget.schedule.nextRun?.formattedDateTime??"geen planning";
  }

  @override
  void dispose() {
    _timeController.dispose();
    _startDayController.dispose();
    _endDayController.dispose();
    _durationController.dispose();
    _intervalController.dispose();
    super.dispose();
  }

  void _handleDelete() {
    widget.onDelete(widget.schedule.schedule.id);
    Navigator.pop(context);
  }

  void _handleSave() {
    Schedule updatedSchedule = Schedule(
      id: widget.schedule.schedule.id,
      startDate: tryParseDate(_startDayController.text) ??
          widget.schedule.schedule.startDate,
      endDate: tryParseDate(_endDayController.text),
      scheduledTime: tryParseTime(_timeController.text) ??
          widget.schedule.schedule.scheduledTime,
      duration: int.tryParse(_durationController.text) ??
          widget.schedule.schedule.duration,
      daysInterval: int.tryParse(_intervalController.text) ??
          widget.schedule.schedule.daysInterval,
      area: _selectedArea,
      enabled: _enabled,
    );
    widget.onSave(updatedSchedule);
    Navigator.pop(context);

  }

  /// Probeert een string te parsen in het formaat dd-mm-yyyy.
  /// Geeft een [ScheduleDate] terug als het lukt, anders `null`.
  static ScheduleDate? tryParseDate(String? input) {
    if (input == null || input.trim().isEmpty) return null;

    // Verwacht formaat: dd-mm-yyyy
    final parts = input.split('-');
    if (parts.length != 3) return null;

    final day = int.tryParse(parts[0]);
    final month = int.tryParse(parts[1]);
    final year = int.tryParse(parts[2]);

    if (day == null || month == null || year == null) return null;

    // Basis validatie
    if (day < 1 || day > 31 || month < 1 || month > 12) return null;

    return ScheduleDate(year: year, month: month, day: day);
  }

  static ScheduleTime? tryParseTime(String? input) {
    if (input == null || input.trim().isEmpty) return null;

    // Verwacht formaat: dd-mm-yyyy
    final parts = input.split(':');
    if (parts.length != 2) return null;

    final hour = int.tryParse(parts[0]);
    final minute = int.tryParse(parts[1]);
    if (hour == null || minute == null) return null;
    return ScheduleTime(hour: hour, minute: minute);
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
            // Eerste rij: uur, minuut, dropdown voor area en duur.
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _timeController,
                    decoration: const InputDecoration(labelText: 'tijd'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: DropdownButtonFormField<IrrigationArea>(
                    value: _selectedArea,
                    decoration: const InputDecoration(labelText: 'Gebied'),
                    items: IrrigationArea.values.map((area) {
                      return DropdownMenuItem<IrrigationArea>(
                        value: area,
                        child: Text(area.toString().split('.').last),
                      );
                    }).toList(),
                    onChanged: (area) {
                      if (area != null) {
                        setState(() {
                          _selectedArea = area;
                        });
                      }
                    },
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
            const SizedBox(height: 8),
            // Tweede rij: startdatum, einddatum en interval.
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _startDayController,
                    decoration: const InputDecoration(labelText: 'Startdatum'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _endDayController,
                    decoration: const InputDecoration(labelText: 'Einddatum'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _intervalController,
                    keyboardType: TextInputType.number,
                    decoration:
                        const InputDecoration(labelText: 'Interval (dagen)'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            // Derde rij: checkbox voor enabled.
            Row(
              children: [
                Checkbox(
                  value: _enabled,
                  onChanged: (bool? newValue) {
                    if (newValue != null) {
                      setState(() {
                        _enabled = newValue;
                      });
                    }
                  },
                ),
                const Text('Ingeschakeld'),
                const SizedBox(width: 8),
                Expanded(
                  child: Align(
                    alignment: Alignment.centerRight, // rechts uitlijnen
                    child: Text('Volgende planning: ' + _nextRun),
                  ),
                ),
              ],


            ),
            const SizedBox(height: 12),
            // Vierde rij: Opslaan en Verwijder knoppen.
            Row(
              children: [
                ElevatedButton(
                  onPressed: _handleSave,
                  child: const Text('Opslaan'),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: _handleDelete,
                  child: const Text('Verwijder'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
