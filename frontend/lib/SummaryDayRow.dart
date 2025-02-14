import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';

class SummaryDayRow extends StatefulWidget {
  final SummaryDaysPumpUsage dayPumpUsage;

  const SummaryDayRow({
    Key? key,
    required this.dayPumpUsage,
  }) : super(key: key);

  @override
  _SummaryDayRowState createState() => _SummaryDayRowState();
}

class _SummaryDayRowState extends State<SummaryDayRow> {
  @override
  void initState() {
    super.initState();
    // Vul de controllers met de initiële waarden uit schedule
  }

  @override
  void dispose() {
    super.dispose();
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
            Row(
              children: [
                Text('${widget.dayPumpUsage.year}-${widget.dayPumpUsage.month}-${widget.dayPumpUsage.day}'),
                const SizedBox(width: 8),
                Text('Gazon: ${widget.dayPumpUsage.minutesGazon}'),
                const SizedBox(width: 8),
                Text('Moestuin: ${widget.dayPumpUsage.minutesMoestuin}'),
              ],

            ),
          ],
        ),
      ),
    );
  }
}
