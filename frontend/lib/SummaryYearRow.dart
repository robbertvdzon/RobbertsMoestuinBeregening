import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';

class SummaryYearRow extends StatefulWidget {
  final SummaryYearPumpUsage yearPumpUsage;
  final Function(int year) onClick;

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
    widget.onClick(widget.yearPumpUsage.year);
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
                Text('${widget.yearPumpUsage.year}'),
                const SizedBox(width: 8),
                Text('Gazon: ${widget.yearPumpUsage.minutesGazon}'),
                const SizedBox(width: 8),
                Text('Moestuin: ${widget.yearPumpUsage.minutesMoestuin}'),
                const SizedBox(height: 8),
                Text('Moestuin: ${widget.yearPumpUsage.months.length}'),
                ElevatedButton(
                  onPressed: () =>
                      _handleClick(),
                  child: Text('meer...'),
                ),
              ],

            ),
            // Row(
            //   children: [
            //     // De lijst met planningen
            //     Expanded(
            //       child: ListView.builder(
            //         // itemCount: widget.yearPumpUsage.months.length,
            //         itemCount: 1,
            //         itemBuilder: (context, index) {
            //           // final month = widget.yearPumpUsage.months[index];
            //           return
            //             Row(
            //               children: [
            //                 Text('tss'),
            //                 // Text('${month.year}-${month.month}'),
            //                 // const SizedBox(width: 8),
            //                 // Text('Gazon: ${month.minutesGazon}'),
            //                 // const SizedBox(width: 8),
            //                 // Text('Moestuin: ${month.minutesMoestuin}'),
            //                 // const SizedBox(height: 8),
            //                 // ElevatedButton(
            //                 //   onPressed: () =>
            //                 //       _handleClick(),
            //                 //   child: Text('meer...'),
            //                 // ),
            //               ],
            //             );
            //         },
            //       ),
            //     ),
            //   ],
            // )

            // Row(
            //   children: [
            //     // De lijst met planningen
            //     Expanded(
            //       child: ListView.builder(
            //         // itemCount: widget.yearPumpUsage.months.length,
            //         itemCount: 1,
            //         itemBuilder: (context, index) {
            //           // final month = widget.yearPumpUsage.months[index];
            //           return Text('tss');
            //         },
            //       ),
            //     ),
            //   ],
            // )
          ],
        ),
      ),
    );
  }
}
