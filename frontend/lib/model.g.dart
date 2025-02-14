// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ViewModel _$ViewModelFromJson(Map<String, dynamic> json) => ViewModel(
      ipAddress: json['ipAddress'] as String,
      pumpStatus: $enumDecode(_$PumpStatusEnumMap, json['pumpStatus']),
      currentIrrigationArea:
          $enumDecode(_$IrrigationAreaEnumMap, json['currentIrrigationArea']),
      valveStatus: $enumDecode(_$ValveStatusEnumMap, json['valveStatus']),
      pumpingEndTime:
          Timestamp.fromJson(json['pumpingEndTime'] as Map<String, dynamic>),
      schedules: (json['schedules'] as List<dynamic>)
          .map((e) => EnrichedSchedule.fromJson(e as Map<String, dynamic>))
          .toList(),
      nextSchedule: json['nextSchedule'] as String,
    );

Map<String, dynamic> _$ViewModelToJson(ViewModel instance) => <String, dynamic>{
      'ipAddress': instance.ipAddress,
      'pumpStatus': _$PumpStatusEnumMap[instance.pumpStatus]!,
      'currentIrrigationArea':
          _$IrrigationAreaEnumMap[instance.currentIrrigationArea]!,
      'valveStatus': _$ValveStatusEnumMap[instance.valveStatus]!,
      'pumpingEndTime': instance.pumpingEndTime.toJson(),
      'schedules': instance.schedules.map((e) => e.toJson()).toList(),
      'nextSchedule': instance.nextSchedule,
    };

const _$PumpStatusEnumMap = {
  PumpStatus.OPEN: 'OPEN',
  PumpStatus.CLOSE: 'CLOSE',
};

const _$IrrigationAreaEnumMap = {
  IrrigationArea.MOESTUIN: 'MOESTUIN',
  IrrigationArea.GAZON: 'GAZON',
};

const _$ValveStatusEnumMap = {
  ValveStatus.IDLE: 'IDLE',
  ValveStatus.MOVING: 'MOVING',
};

Timestamp _$TimestampFromJson(Map<String, dynamic> json) => Timestamp(
      year: (json['year'] as num).toInt(),
      month: (json['month'] as num).toInt(),
      day: (json['day'] as num).toInt(),
      hour: (json['hour'] as num).toInt(),
      minute: (json['minute'] as num).toInt(),
      second: (json['second'] as num).toInt(),
    );

Map<String, dynamic> _$TimestampToJson(Timestamp instance) => <String, dynamic>{
      'year': instance.year,
      'month': instance.month,
      'day': instance.day,
      'hour': instance.hour,
      'minute': instance.minute,
      'second': instance.second,
    };

ScheduleTime _$ScheduleTimeFromJson(Map<String, dynamic> json) => ScheduleTime(
      hour: (json['hour'] as num).toInt(),
      minute: (json['minute'] as num).toInt(),
    );

Map<String, dynamic> _$ScheduleTimeToJson(ScheduleTime instance) =>
    <String, dynamic>{
      'hour': instance.hour,
      'minute': instance.minute,
    };

ScheduleDate _$ScheduleDateFromJson(Map<String, dynamic> json) => ScheduleDate(
      year: (json['year'] as num).toInt(),
      month: (json['month'] as num).toInt(),
      day: (json['day'] as num).toInt(),
    );

Map<String, dynamic> _$ScheduleDateToJson(ScheduleDate instance) =>
    <String, dynamic>{
      'year': instance.year,
      'month': instance.month,
      'day': instance.day,
    };

EnrichedSchedule _$EnrichedScheduleFromJson(Map<String, dynamic> json) =>
    EnrichedSchedule(
      schedule: Schedule.fromJson(json['schedule'] as Map<String, dynamic>),
      nextRun: json['nextRun'] == null
          ? null
          : Timestamp.fromJson(json['nextRun'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$EnrichedScheduleToJson(EnrichedSchedule instance) =>
    <String, dynamic>{
      'schedule': instance.schedule.toJson(),
      'nextRun': instance.nextRun?.toJson(),
    };

Schedule _$ScheduleFromJson(Map<String, dynamic> json) => Schedule(
      id: json['id'] as String,
      startDate:
          ScheduleDate.fromJson(json['startDate'] as Map<String, dynamic>),
      endDate: json['endDate'] == null
          ? null
          : ScheduleDate.fromJson(json['endDate'] as Map<String, dynamic>),
      scheduledTime:
          ScheduleTime.fromJson(json['scheduledTime'] as Map<String, dynamic>),
      duration: (json['duration'] as num).toInt(),
      daysInterval: (json['daysInterval'] as num).toInt(),
      area: $enumDecode(_$IrrigationAreaEnumMap, json['area']),
      enabled: json['enabled'] as bool,
    );

Map<String, dynamic> _$ScheduleToJson(Schedule instance) => <String, dynamic>{
      'id': instance.id,
      'startDate': instance.startDate.toJson(),
      'endDate': instance.endDate?.toJson(),
      'scheduledTime': instance.scheduledTime.toJson(),
      'duration': instance.duration,
      'daysInterval': instance.daysInterval,
      'area': _$IrrigationAreaEnumMap[instance.area]!,
      'enabled': instance.enabled,
    };
