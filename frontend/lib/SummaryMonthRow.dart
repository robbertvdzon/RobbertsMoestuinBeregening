import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';
import 'package:intl/intl.dart';

class SummaryMonthRow extends StatefulWidget {
  final SummaryMonthsPumpUsage monthPumpUsage;
  final Function(int year) onClick;

  const SummaryMonthRow({
    Key? key,
    required this.monthPumpUsage,
    required this.onClick,
  }) : super(key: key);

  @override
  _SummaryMonthRowState createState() => _SummaryMonthRowState();
}

class _SummaryMonthRowState extends State<SummaryMonthRow> {
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
    widget.onClick(widget.monthPumpUsage.year);
    // Navigator.pop(context);
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
              Text('${getMonthName(widget.monthPumpUsage.month)} ${widget.monthPumpUsage.year} :'),
              const SizedBox(width: 8),
              Text('Gazon: ${widget.monthPumpUsage.minutesGazon} min'),
              const SizedBox(width: 8),
              Text('Moestuin: ${widget.monthPumpUsage.minutesMoestuin} min'),
            ]

        )
    );

  }
  String getMonthName(int month) {
    const months = [
      'jan', 'feb', 'mrt', 'apr', 'mei', 'jun',
      'jul', 'aug', 'sep', 'okt', 'nov', 'dec'
    ];

    if (month < 1 || month > 12) {
      throw ArgumentError('Maand moet tussen 1 en 12 liggen.');
    }

    return months[month - 1];
  }
}
