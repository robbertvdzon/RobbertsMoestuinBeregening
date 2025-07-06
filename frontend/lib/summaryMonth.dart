import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/SummaryDayRow.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';


class SummaryMonth extends StatefulWidget {
  const SummaryMonth({super.key, required this.title, required this.summaryMonthPumpUsage});

  final String title;
  final SummaryMonthsPumpUsage summaryMonthPumpUsage;

  @override
  State<SummaryMonth> createState() => _SummaryMonthState();
}

class _SummaryMonthState extends State<SummaryMonth> {

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final summaryPumpUsage = widget.summaryMonthPumpUsage;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Log'),
      ),
      body: summaryPumpUsage == null
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                // De lijst met planningen
                Expanded(
                  child: ListView.builder(
                    itemCount: summaryPumpUsage.days.length,
                    itemBuilder: (context, index) {
                      final day = summaryPumpUsage.days[index];
                      return SummaryDayRow(
                        dayPumpUsage: day,
                      );
                    },
                  ),
                ),
              ],
            ),
    );
  }
}
