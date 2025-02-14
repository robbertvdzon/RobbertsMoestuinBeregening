import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';

class SummaryYearRow extends StatefulWidget {
  final SummaryYearPumpUsage yearPumpUsage;
  final Function() onClick;

  const SummaryYearRow({
    Key? key,
    required this.yearPumpUsage,
    required this.onClick,
  }) : super(key: key);

  @override
  _SummaryYearRowState createState() => _SummaryYearRowState();
}

class _SummaryYearRowState extends State<SummaryYearRow> {
  @override
  void initState() {
    super.initState();
    // Vul de controllers met de initiële waarden uit schedule
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _handleClick() {
    widget.onClick();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onTap: () {
          // Functie aanroepen bij klikken op de Row
          _handleClick();
        },
        child: Row(
          children: [
            Text('${widget.yearPumpUsage.year} :'),
            const SizedBox(width: 8),
            Text('Gazon: ${widget.yearPumpUsage.minutesGazon} min'),
            const SizedBox(width: 8),
            Text('Moestuin: ${widget.yearPumpUsage.minutesMoestuin} min'),
          ]

        )
    );
  }
}
