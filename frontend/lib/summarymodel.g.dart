// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'summarymodel.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SummaryPumpUsage _$SummaryPumpUsageFromJson(Map<String, dynamic> json) =>
    SummaryPumpUsage(
      minutesGazon: (json['minutesGazon'] as num).toInt(),
      minutesMoestuin: (json['minutesMoestuin'] as num).toInt(),
      years: (json['years'] as List<dynamic>)
          .map((e) => SummaryYearPumpUsage.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$SummaryPumpUsageToJson(SummaryPumpUsage instance) =>
    <String, dynamic>{
      'minutesGazon': instance.minutesGazon,
      'minutesMoestuin': instance.minutesMoestuin,
      'years': instance.years.map((e) => e.toJson()).toList(),
    };

SummaryYearPumpUsage _$SummaryYearPumpUsageFromJson(
        Map<String, dynamic> json) =>
    SummaryYearPumpUsage(
      year: (json['year'] as num).toInt(),
      minutesGazon: (json['minutesGazon'] as num).toInt(),
      minutesMoestuin: (json['minutesMoestuin'] as num).toInt(),
      months: (json['months'] as List<dynamic>)
          .map(
              (e) => SummaryMonthsPumpUsage.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$SummaryYearPumpUsageToJson(
        SummaryYearPumpUsage instance) =>
    <String, dynamic>{
      'year': instance.year,
      'minutesGazon': instance.minutesGazon,
      'minutesMoestuin': instance.minutesMoestuin,
      'months': instance.months.map((e) => e.toJson()).toList(),
    };

SummaryMonthsPumpUsage _$SummaryMonthsPumpUsageFromJson(
        Map<String, dynamic> json) =>
    SummaryMonthsPumpUsage(
      year: (json['year'] as num).toInt(),
      month: (json['month'] as num).toInt(),
      minutesGazon: (json['minutesGazon'] as num).toInt(),
      minutesMoestuin: (json['minutesMoestuin'] as num).toInt(),
      days: (json['days'] as List<dynamic>)
          .map((e) => SummaryDaysPumpUsage.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$SummaryMonthsPumpUsageToJson(
        SummaryMonthsPumpUsage instance) =>
    <String, dynamic>{
      'year': instance.year,
      'month': instance.month,
      'minutesGazon': instance.minutesGazon,
      'minutesMoestuin': instance.minutesMoestuin,
      'days': instance.days.map((e) => e.toJson()).toList(),
    };

SummaryDaysPumpUsage _$SummaryDaysPumpUsageFromJson(
        Map<String, dynamic> json) =>
    SummaryDaysPumpUsage(
      year: (json['year'] as num).toInt(),
      month: (json['month'] as num).toInt(),
      day: (json['day'] as num).toInt(),
      minutesGazon: (json['minutesGazon'] as num).toInt(),
      minutesMoestuin: (json['minutesMoestuin'] as num).toInt(),
    );

Map<String, dynamic> _$SummaryDaysPumpUsageToJson(
        SummaryDaysPumpUsage instance) =>
    <String, dynamic>{
      'year': instance.year,
      'month': instance.month,
      'day': instance.day,
      'minutesGazon': instance.minutesGazon,
      'minutesMoestuin': instance.minutesMoestuin,
    };

PumpLogState _$PumpLogStateFromJson(Map<String, dynamic> json) => PumpLogState(
      minutes: (json['minutes'] as num).toInt(),
      log: (json['log'] as List<dynamic>)
          .map((e) => PumpLogItem.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$PumpLogStateToJson(PumpLogState instance) =>
    <String, dynamic>{
      'minutes': instance.minutes,
      'log': instance.log.map((e) => e.toJson()).toList(),
    };

PumpLogItem _$PumpLogItemFromJson(Map<String, dynamic> json) => PumpLogItem(
      year: (json['year'] as num).toInt(),
      month: (json['month'] as num).toInt(),
      day: (json['day'] as num).toInt(),
      hour: (json['hour'] as num).toInt(),
      minute: (json['minute'] as num).toInt(),
    );

Map<String, dynamic> _$PumpLogItemToJson(PumpLogItem instance) =>
    <String, dynamic>{
      'year': instance.year,
      'month': instance.month,
      'day': instance.day,
      'hour': instance.hour,
      'minute': instance.minute,
    };
