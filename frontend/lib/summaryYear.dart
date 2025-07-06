import 'package:flutter/material.dart';
import 'package:tuinsproeiersweb/summaryMonth.dart';
import 'package:tuinsproeiersweb/summarymodel.dart';

import 'SummaryMonthRow.dart';

class SummaryYear extends StatefulWidget {
  const SummaryYear({super.key, required this.title, required this.summaryYearPumpUsage});

  final String title;
  final SummaryYearPumpUsage summaryYearPumpUsage;

  @override
  State<SummaryYear> createState() => _SummaryYearState();
}

class _SummaryYearState extends State<SummaryYear> {

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
    final summaryPumpUsage = widget.summaryYearPumpUsage;

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
                    itemCount: summaryPumpUsage.months.length,
                    itemBuilder: (context, index) {
                      final month = summaryPumpUsage.months[index];
                      return SummaryMonthRow(
                        monthPumpUsage: month,
                        onClick: (year) {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    SummaryMonth(title: 'Log',summaryMonthPumpUsage: month)),
                          );

                        },
                      );
                    },
                  ),
                ),
              ],
            ),
    );
  }
}
