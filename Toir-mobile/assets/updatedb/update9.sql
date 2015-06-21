drop table if exists 'task_new';
create table 'task_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'users_uuid' text not null, 'create_date' integer not null, 'modify_date' integer not null, 'close_date' integer not null default 0, 'task_status_uuid' text not null, 'attempt_send_date' integer not null default 0, 'attempt_count' integer not null default 0, 'successefull_send' integer not null default 0);
insert into 'task_new' ('uuid', 'users_uuid', 'create_date', 'modify_date', 'close_date', 'task_status_uuid', 'attempt_send_date', 'attempt_count', 'successefull_send') select * from 'task';
drop table if exists 'task';
alter table 'task_new' rename to 'task';
drop table if exists 'task_status_new';
create table 'task_status_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null);
insert into 'task_status_new' ('uuid', 'title') select * from 'task_status';
drop table if exists 'task_status';
alter table 'task_status_new' rename to 'task_status';
drop table if exists 'critical_type_new';
create table 'critical_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'type' integer not null);
insert into 'critical_type_new' ('uuid', 'type') select * from 'critical_type';
drop table if exists 'critical_type';
alter table 'critical_type_new' rename to 'critical_type';
drop table if exists 'operation_type_new';
create table 'operation_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null);
insert into 'operation_type_new' ('uuid', 'title') select * from 'operation_type';
drop table if exists 'operation_type';
alter table 'operation_type_new' rename to 'operation_type';
drop table if exists 'operation_result_new';
create table 'operation_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_type_uuid' text not null, 'title' text not null);
insert into 'operation_result_new' ('uuid', 'operation_type_uuid', 'title') select * from 'operation_result';
drop table if exists 'operation_result';
alter table 'operation_result_new' rename to 'operation_result';
drop table if exists 'documentation_type_new';
create table 'documentation_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null);
insert into 'documentation_type_new' ('uuid', 'title') select * from 'documentation_type';
drop table if exists 'documentation_type';
alter table 'documentation_type_new' rename to 'documentation_type';
drop table if exists 'equipment_type_new';
create table 'equipment_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null);
insert into 'equipment_type_new' ('uuid', 'title') select * from 'equipment_type';
drop table if exists 'equipment_type';
alter table 'equipment_type_new' rename to 'equipment_type';
drop table if exists 'operation_pattern_new';
create table 'operation_pattern_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null);
insert into 'operation_pattern_new' ('uuid', 'title') select * from 'operation_pattern';
drop table if exists 'operation_pattern';
alter table 'operation_pattern_new' rename to 'operation_pattern';
drop table if exists 'measure_type_new';
create table 'measure_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, title text not null);
insert into 'measure_type_new' ('uuid', 'title') select * from 'measure_type';
drop table if exists 'measure_type';
alter table 'measure_type_new' rename to 'measure_type';
drop table if exists 'equipment_documentation_new';
create table 'equipment_documentation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_uuid' text not null, 'documentation_type_uuid' text not null, 'title' text not null, 'path' text not null);
insert into 'equipment_documentation_new' ('uuid', 'equipment_uuid', 'documentation_type_uuid', 'title', 'path') select * from 'equipment_documentation';
drop table if exists 'equipment_documentation';
alter table 'equipment_documentation_new' rename to 'equipment_documentation';
drop table if exists 'operation_pattern_step_new';
create table 'operation_pattern_step_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_pattern_uuid' text not null, 'description' text not null, 'image' text default null, 'first_step' integer default 0, 'last_step' integer default 0);
insert into 'operation_pattern_step_new' ('uuid', 'operation_pattern_uuid', 'description', 'image', 'first_step', 'last_step') select * from 'operation_pattern_step';
drop table if exists 'operation_pattern_step';
alter table 'operation_pattern_step_new' rename to 'operation_pattern_step';
drop table if exists 'operation_pattern_step_result_new';
create table 'operation_pattern_step_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_pattern_step_uuid' text not null, 'next_operation_pattern_step_uuid' text not null, 'title' text not null, 'measure_type_uuid' text default null);
insert into 'operation_pattern_step_result_new' ('uuid', 'operation_pattern_step_uuid', 'next_operation_pattern_step_uuid', 'title', 'measure_type_uuid') select * from 'operation_pattern_step_result';
drop table if exists 'operation_pattern_step_result';
alter table 'operation_pattern_step_result_new' rename to 'operation_pattern_step_result';
drop table if exists 'users_new';
create table 'users_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'name' text not null, 'login' text not null, 'pass' text not null, 'type' integer, 'tag_id' text not null unique, 'active' integer not null default 0);
insert into 'users_new' ('uuid', 'name', 'login', 'pass', 'type', 'tag_id', 'active') select * from 'users';
drop table if exists 'users';
alter table 'users_new' rename to 'users';
drop table if exists 'token_new';
create table 'token_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'access_token' text not null, 'expires_in' integer not null, 'username' text not null unique, '.issued' text not null, '.expires' text not null);
insert into 'token_new' ('uuid', 'access_token', 'expires_in', 'username', '.issued', '.expires') select * from 'token';
drop table if exists 'token';
alter table 'token_new' rename to 'token';
drop table if exists 'equipment_new';
create table 'equipment_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' integer not null default 0, 'location' text not null, 'tag_id' text not null unique);
insert into 'equipment_new' ('uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'location', 'tag_id') select * from 'equipment';
drop table if exists 'equipment';
alter table 'equipment_new' rename to 'equipment';
drop table if exists 'measure_value_new';
create table 'measure_value_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'operation_pattern_step_result' text not null, 'date' integer not null, 'value' text not null);
insert into 'measure_value_new' ('uuid', 'equipment_operation_uuid', 'operation_pattern_step_result', 'date', 'value') select * from 'measure_value';
drop table if exists 'measure_value';
alter table 'measure_value_new' rename to 'measure_value';
drop table if exists 'equipment_operation_result_new';
create table 'equipment_operation_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'start_date' integer not null, 'end_date' integer not null default 0, 'operation_result_uuid' text not null default '00000000-0000-0000-0000-000000000000');
insert into 'equipment_operation_result_new' ('uuid', 'equipment_operation_uuid', 'start_date', 'end_date', 'operation_result_uuid') select * from 'equipment_operation_result';
drop table if exists 'equipment_operation_result';
alter table 'equipment_operation_result_new' rename to 'equipment_operation_result';
drop table if exists 'gps_position_new';
create table 'gps_position_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'user_uuid' text not null, 'cur_date' text not null, 'latitude' text not null, 'longitude' text not null);
insert into 'gps_position_new' ('uuid', 'user_uuid', 'cur_date', 'latitude', 'longitude') select * from 'gps_position';
drop table if exists 'gps_position';
alter table 'gps_position_new' rename to 'gps_position';
drop table if exists 'equipment_operation_new';
create table 'equipment_operation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null, 'operation_status_uuid' text not null);
insert into 'equipment_operation_new' ('uuid', 'task_uuid', 'equipment_uuid', 'operation_type_uuid', 'operation_pattern_uuid', 'operation_status_uuid') select * from 'equipment_operation';
drop table if exists 'equipment_operation';
alter table 'equipment_operation_new' rename to 'equipment_operation';
