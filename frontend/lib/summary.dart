import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tuinsproeiersweb/summaryYear.dart';

import 'SummaryYearRow.dart';
import 'viewModelProvider.dart';

class Summary extends StatefulWidget {
  const Summary({super.key, required this.title});

  final String title;

  @override
  State<Summary> createState() => _SummaryState();
}

class _SummaryState extends State<Summary> {
  @override
  void initState() {
    super.initState();
    // _schedulesStream = Stream.value(widget.viewModel);
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final summaryProvider = Provider.of<BeregeningDataProvider>(context);
    final summaryPumpUsage = summaryProvider.summaryPumpUsage;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Log'),
      ),
      body: summaryPumpUsage == null
          ? Center(child: CircularProgressIndicator())
          : Column(
              children: [
                // De lijst met planningen
                Expanded(
                  child: ListView.builder(
                    itemCount: summaryPumpUsage.years.length,
                    itemBuilder: (context, index) {
                      final year = summaryPumpUsage.years[index];
                      return SummaryYearRow(
                        yearPumpUsage: year,
                        onClick: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    SummaryYear(title: 'Log',summaryYearPumpUsage: year)),
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
