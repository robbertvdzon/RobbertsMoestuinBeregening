import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';

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
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text('${widget.monthPumpUsage.year}-${widget.monthPumpUsage.month}'),
                const SizedBox(width: 8),
                Text('Gazon: ${widget.monthPumpUsage.minutesGazon}'),
                const SizedBox(width: 8),
                Text('Moestuin: ${widget.monthPumpUsage.minutesMoestuin}'),
                ElevatedButton(
                  onPressed: () =>
                      _handleClick(),
                  child: Text('meer...'),
                ),
              ],

            ),
          ],
        ),
      ),
    );
  }
}
