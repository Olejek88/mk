CREATE TABLE 'version' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' INTEGER NOT NULL, 'date' NUMERIC NOT NULL, 'key' TEXT NOT NULL);
INSERT INTO 'version' VALUES (1,0.1,1427875663,'');
CREATE TABLE 'var' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL, 'prm' INTEGER NOT NULL, 'podp' INTEGER NOT NULL);
CREATE TABLE 'users' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT, 'login' TEXT, 'pass' TEXT, 'type' INTEGER);
INSERT INTO 'users' VALUES (1,'admin','admin','admin',3);
CREATE TABLE 'tasks_status' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL);
INSERT INTO 'tasks_status' VALUES (1,'Наряд создан');
INSERT INTO 'tasks_status' VALUES (2,'Наряд передан');
INSERT INTO 'tasks_status' VALUES (3,'Наряд получен и подтвержден');
INSERT INTO 'tasks_status' VALUES (4,'Наряд закончен и сдан');
INSERT INTO 'tasks_status' VALUES (5,'Наряд сдан, но не закончен');
CREATE TABLE 'tasks_equipment' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'id_task' INTEGER NOT NULL, 'id_eqpm' INTEGER NOT NULL, 'type' INTEGER NOT NULL, 'status' INTEGER NOT NULL, 'date' NUMERIC, 'len' INTEGER, 'link' INTEGER, 'gps1' REAL NOT NULL, 'gps2' REAL NOT NULL, 'qualuty' INTEGER, 'priority' INTEGER);
INSERT INTO 'tasks_equipment' VALUES (1,1,1,100,0,1427875663,50,NULL,55.3421,50.3421,NULL,NULL);
CREATE TABLE 'tasks' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'operator_id' INTEGER NOT NULL, 'date_start' NUMERIC NOT NULL, 'date_end' NUMERIC NOT NULL, 'status' INTEGER NOT NULL);
INSERT INTO 'tasks' VALUES (1,1,1427875663,'',2);
CREATE TABLE 'results' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL, 'type' INTEGER NOT NULL);
INSERT INTO 'results' VALUES (1,'С оборудованием все в порядке',0);
INSERT INTO 'results' VALUES (2,'Оборудование отсутствует',1);
INSERT INTO 'results' VALUES (3,'Оборудование полностью неисправно и требует замены',1);
INSERT INTO 'results' VALUES (4,'Оборудование частично неисправно и требует ремонта',1);
INSERT INTO 'results' VALUES (5,'Требуется дополнительное обследование специалиста',1);
INSERT INTO 'results' VALUES (6,'Оборудование не используется и подлежит демонтажу',1);
INSERT INTO 'results' VALUES (7,'Оборудование не работает по неизвестным причинам',1);
INSERT INTO 'results' VALUES (8,'Осмотр оборудования не завершен',1);
CREATE TABLE 'register' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'type' INTEGER NOT NULL, 'id_task' INTEGER, 'id_eqpm' INTEGER, 'id_user' INTEGER, 'descr' TEXT, 'date' NUMERIC NOT NULL);
CREATE TABLE 'priority' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL);
CREATE TABLE 'path' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'id_user' INTEGER NOT NULL, 'gps1' REAL NOT NULL, 'gps2' REAL NOT NULL, 'date' NUMERIC);
INSERT INTO 'path' VALUES (1,1,55.6345,50.1345,1427875663);
INSERT INTO 'path' VALUES (2,1,55.6342,50.1342,1427878653);
INSERT INTO 'path' VALUES (3,1,55.6341,50.1343,1427875963);
CREATE TABLE 'operators' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL, 'photo' TEXT, 'login' TEXT, 'pass' TEXT, 'whois' TEXT, 'phone' TEXT, 'tasks' INTEGER, 'tasks_completed' INTEGER, 'task_current' INTEGER);
INSERT INTO 'operators' VALUES (1,'Курнаков Иван Иванович','/photo/ivanych.jpg','iv07',NULL,'Руководитель проекта','89227000000',1,0,1);
CREATE TABLE 'operations' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL, 'type' INTEGER);
INSERT INTO 'operations' VALUES (1,'Осмотр',NULL);
INSERT INTO 'operations' VALUES (2,'Монтаж оборудования',NULL);
INSERT INTO 'operations' VALUES (3,'Демонтаж оборудования',NULL);
INSERT INTO 'operations' VALUES (4,'Ремонт',NULL);
INSERT INTO 'operations' VALUES (5,'Замена',NULL);
INSERT INTO 'operations' VALUES (6,'Пуско-наладочные работы',NULL);
INSERT INTO 'operations' VALUES (7,'Настройка',NULL);
INSERT INTO 'operations' VALUES (8,'Тестирование' ,NULL);
INSERT INTO 'operations' VALUES (9,'Вердикт',NULL);
CREATE TABLE 'masters_result' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'id_eqpm' INTEGER NOT NULL, 'type' INTEGER, 'step' INTEGER NOT NULL, 'result' INTEGER NOT NULL, 'date' NUMERIC NOT NULL);
INSERT INTO 'masters_result' VALUES (1,1,1,1,1,1427875663);
INSERT INTO 'masters_result' VALUES (2,1,1,2,0,'');
INSERT INTO 'masters_result' VALUES (3,1,1,3,0,'');
INSERT INTO 'masters_result' VALUES (4,1,1,4,0,'');
INSERT INTO 'masters_result' VALUES (5,1,1,5,0,'');
INSERT INTO 'masters_result' VALUES (6,1,1,7,0,'');
CREATE TABLE 'masters' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'id_eqmn' INTEGER NOT NULL, 'id_tasks' INTEGER NOT NULL, 'type' INTEGER NOT NULL, 'step' INTEGER NOT NULL, 'descr' TEXT, 'photo' TEXT, 'result1' TEXT, 'nextstep1' INTEGER, 'result2' TEXT, 'nextstep2' INTEGER, 'result3' TEXT, 'nextstep3' INTEGER);
INSERT INTO 'masters' VALUES (1,1,1,1,1,'Визуально проверить есть ли на устройстве питание',NULL,'Есть',2,'Нет',3,'Нет прибора',7);
INSERT INTO 'masters' VALUES (2,1,1,1,2,'Просмотреть текущие показания на дисплее прибора. Для этого стралками вверх вниз, вправо-влево найти меню рабочий стол/параметры.','photo\2_1.jpg','Показания нормальные',4,'Показаний нет',5,'Некоторые показания некорректны',5);
INSERT INTO 'masters' VALUES (3,1,1,99,3,'Вердикт: требуется ремонт',NULL,'Осмотр закончен',0,NULL,NULL,NULL,NULL);
INSERT INTO 'masters' VALUES (4,1,1,99,4,'Вердикт: все в порядке',NULL,'Осмотр закончен',0,NULL,NULL,NULL,NULL);
INSERT INTO 'masters' VALUES (5,1,1,99,5,'Вердикт: требуется дополнительный осмотр',NULL,'Осмотр закончен',0,NULL,NULL,NULL,NULL);
INSERT INTO 'masters' VALUES (7,1,1,99,7,'Вердикт: требуется замена/монтаж',NULL,'Осмотр закончен',0,NULL,NULL,NULL,NULL);
CREATE TABLE 'events' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' INTEGER NOT NULL);
CREATE TABLE 'equipment' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL, 'link' TEXT, 'type' INTEGER NOT NULL, 'year' INTEGER, 'manufacturer' TEXT, 'photo' TEXT, 'priority' INTEGER);
INSERT INTO 'equipment' VALUES (1,'Корректор газа СПГ741','\docs\re741.pdf',100,2013,'ЗАО НПФ Логика','\photo\3234.jpg',NULL);
CREATE TABLE 'docs' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'type' INTEGER NOT NULL, 'link' TEXT NOT NULL);
INSERT INTO 'docs' VALUES (1,2,'\docs\re254.pdf');
CREATE TABLE 'doc_type' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'name' TEXT NOT NULL);
INSERT INTO 'doc_type' VALUES (1,'Паспорт');
INSERT INTO 'doc_type' VALUES (2,'Руководство по эксплуатации');
INSERT INTO 'doc_type' VALUES (3,'Схема');
INSERT INTO 'doc_type' VALUES (4,'Руководство по ремонту');
INSERT INTO 'doc_type' VALUES (5,'Чертеж');
INSERT INTO 'doc_type' VALUES (6,'Фотографии');
INSERT INTO 'doc_type' VALUES (7,'Техническое описание');
CREATE TABLE 'data' ( '_id' INTEGER, 'id_eqmn' INTEGER NOT NULL PRIMARY KEY UNIQUE, 'prm' INTEGER NOT NULL, 'podp' INTEGER NOT NULL, 'date' NUMERIC NOT NULL, 'id_user' INTEGER, 'id_tasks' INTEGER, 'value' REAL NOT NULL);
