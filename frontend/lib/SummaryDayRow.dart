import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';

class SummaryDayRow extends StatefulWidget {
  final SummaryDaysPumpUsage dayPumpUsage;

  const SummaryDayRow({
    super.key,
    required this.dayPumpUsage,
  });

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
    return Row(children: [
      Text(
          '${widget.dayPumpUsage.day}-${widget.dayPumpUsage.month}-${widget.dayPumpUsage.year} :'),
      const SizedBox(width: 8),
      Text('Gazon: ${widget.dayPumpUsage.minutesGazon} min'),
      const SizedBox(width: 8),
      Text('Moestuin: ${widget.dayPumpUsage.minutesMoestuin} min'),
    ]);
  }
}
