import 'package:json_annotation/json_annotation.dart';

part 'summarymodel.g.dart';

@JsonSerializable(explicitToJson: true)
class SummaryPumpUsage {
  final int minutesGazon;
  final int minutesMoestuin;
  final List<SummaryYearPumpUsage> years;

  SummaryPumpUsage({
    required this.minutesGazon,
    required this.minutesMoestuin,
    required this.years,
  });

  factory SummaryPumpUsage.fromJson(Map<String, dynamic> json) =>
      _$SummaryPumpUsageFromJson(json);

  Map<String, dynamic> toJson() => _$SummaryPumpUsageToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SummaryYearPumpUsage {
  final int year;
  final int minutesGazon;
  final int minutesMoestuin;
  final List<SummaryMonthsPumpUsage> months;

  SummaryYearPumpUsage({
    required this.year,
    required this.minutesGazon,
    required this.minutesMoestuin,
    required this.months,
  });

  factory SummaryYearPumpUsage.fromJson(Map<String, dynamic> json) =>
      _$SummaryYearPumpUsageFromJson(json);

  Map<String, dynamic> toJson() => _$SummaryYearPumpUsageToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SummaryMonthsPumpUsage {
  final int year;
  final int month;
  final int minutesGazon;
  final int minutesMoestuin;
  final List<SummaryDaysPumpUsage> days;

  SummaryMonthsPumpUsage({
    required this.year,
    required this.month,
    required this.minutesGazon,
    required this.minutesMoestuin,
    required this.days,
  });

  factory SummaryMonthsPumpUsage.fromJson(Map<String, dynamic> json) =>
      _$SummaryMonthsPumpUsageFromJson(json);

  Map<String, dynamic> toJson() => _$SummaryMonthsPumpUsageToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SummaryDaysPumpUsage {
  final int year;
  final int month;
  final int day;
  final int minutesGazon;
  final int minutesMoestuin;

  SummaryDaysPumpUsage({
    required this.year,
    required this.month,
    required this.day,
    required this.minutesGazon,
    required this.minutesMoestuin,
  });

  factory SummaryDaysPumpUsage.fromJson(Map<String, dynamic> json) =>
      _$SummaryDaysPumpUsageFromJson(json);

  Map<String, dynamic> toJson() => _$SummaryDaysPumpUsageToJson(this);
}

@JsonSerializable(explicitToJson: true)
class PumpLogState {
  final int minutes;
  final List<PumpLogItem> log;

  PumpLogState({
    required this.minutes,
    required this.log,
  });

  factory PumpLogState.fromJson(Map<String, dynamic> json) =>
      _$PumpLogStateFromJson(json);

  Map<String, dynamic> toJson() => _$PumpLogStateToJson(this);
}

@JsonSerializable(explicitToJson: true)
class PumpLogItem {
  final int year;
  final int month;
  final int day;
  final int hour;
  final int minute;

  PumpLogItem({
    required this.year,
    required this.month,
    required this.day,
    required this.hour,
    required this.minute,
  });

  factory PumpLogItem.fromJson(Map<String, dynamic> json) =>
      _$PumpLogItemFromJson(json);

  Map<String, dynamic> toJson() => _$PumpLogItemToJson(this);
}
