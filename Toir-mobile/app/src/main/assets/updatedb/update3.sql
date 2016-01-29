drop table if exists 'users';
create table 'users' ('uuid' text not null primary key, 'name' TEXT, 'login' TEXT, 'pass' TEXT, 'type' INTEGER, 'tag_id' TEXT NOT NULL UNIQUE, 'active' integer not null default 0);
insert into 'users' values ('4462ed77-9bf0-4542-b127-f4ecefce49da', 'admin', 'admin', 'admin', 3, '01234567', 1);
drop table if exists 'tasks_status';
drop table if exists 'task_status';
create table 'task_status' ('uuid' text not null primary key, 'title' text not null);
drop table if exists 'tasks';
drop table if exists 'task';
create table 'task' ('uuid' text not null primary key, 'user_uuid' text not null, 'create_date' text not null, 'modify_date' text not null, 'close_date' text not null default '0000-00-00 00:00:00', 'task_status_uuid' text not null);
drop table if exists 'priority';
drop table if exists 'critical_type';
create table 'critical_type' ('uuid' text not null primary key, 'type' integer not null);
drop table if exists 'operations';
drop table if exists 'operation_type';
create table 'operation_type' ('uuid' text not null primary key, 'title' text not null);
drop table if exists 'results';
drop table if exists 'operation_result';
create table 'operation_result' ('uuid' text not null primary key, 'operation_type_uuid' text not null, 'title' text not null);
drop table if exists 'doc_type';
drop table if exists 'documentation_type';
create table 'documentation_type' ('uuid' text not null primary key, 'title' text not null);
drop table if exists 'docs';
drop table if exists 'equipment_documentation';
create table 'equipment_documentation' ('uuid' text not null primary key, 'equipment_uuid' text not null, 'documentation_type_uuid' text not null, 'title' text not null, 'path' text not null);
drop table if exists 'equipment';
create table 'equipment' ('uuid' text not null primary key, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' text not null, 'location' text not null);
drop table if exists 'equipment_type';
create table 'equipment_type' ('uuid' text not null primary key, 'title' text not null);
drop table if exists 'tasks_equipment';
drop table if exists 'equipment_operation';
create table 'equipment_operation' ('uuid' text not null primary key, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null);
drop table if exists 'masters';
drop table if exists 'operation_pattern';
create table 'operation_pattern' ('uuid' text not null primary key, 'title' text not null);
drop table if exists 'operation_pattern_step';
create table 'operation_pattern_step' ('uuid' text not null primary key, 'operation_pattern_uuid' text not null, 'description' text not null, 'image' text default null, 'first_step' integer default 0, 'last_step' integer default 0);
drop table if exists 'operation_pattern_step_result';
create table 'operation_pattern_step_result' ('uuid' text not null primary key, 'operation_pattern_step_uuid' text not null, 'next_operation_pattern_step_uuid' text not null, 'title' text not null);
drop table if exists 'masters_result';
drop table if exists 'equipment_operation_result';
create table 'equipment_operation_result' ('uuid' text not null primary key, 'equipment_operation_uuid' text not null, 'start_date' text not null, 'end_date' text not null, 'operation_result_uuid' text not null default '', 'measured_value' text not null default '');