package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.utils.DataUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	private TaskStatusDBAdapter taskstdbadapter;

	public static final String TABLE_NAME = "task";

	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_USER_UUID_NAME = "users_uuid";
	public static final String FIELD_CREATE_DATE_NAME = "create_date";
	public static final String FIELD_MODIFY_DATE_NAME = "modify_date";
	public static final String FIELD_CLOSE_DATE_NAME = "close_date";
	public static final String FIELD_TASK_STATUS_UUID_NAME = "task_status_uuid";
	public static final String FIELD_ATTEMPT_SEND_DATE_NAME = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT_NAME = "attempt_count";
	public static final String FIELD_UPDATED_NAME = "updated";

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME,
			FIELD_USER_UUID_NAME, FIELD_CREATE_DATE_NAME,
			FIELD_MODIFY_DATE_NAME, FIELD_CLOSE_DATE_NAME,
			FIELD_TASK_STATUS_UUID_NAME, FIELD_ATTEMPT_SEND_DATE_NAME,
			FIELD_ATTEMPT_COUNT_NAME, FIELD_UPDATED_NAME };

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public TaskDBAdapter(Context context) {
		mContext = context;
	}

	/**
	 * Открываем базу данных
	 */
	public TaskDBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, TOiRDBAdapter.getDbName(),
				null, TOiRDBAdapter.getAppDbVersion());
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Закрываем базу данных
	 */
	public void close() {
		mDb.close();
		mDbHelper.close();
	}

	/**
	 * Возвращает список всех нарядов пользователя
	 * 
	 * @param uuid
	 * @return
	 */
	public ArrayList<Task> getOrdersByUser(String uuid) {
		ArrayList<Task> arrayList = new ArrayList<Task>();
		Cursor cursor;

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * Возвращает список нарядов по пользователю и статусу наряда
	 * 
	 * @param user_uuid
	 * @param status_uuid
	 * @return Если ни одной записи нет, возвращает null
	 */
	public ArrayList<Task> getTaskByUserAndStatus(String user_uuid,
			String status_uuid) {
		ArrayList<Task> list = null;
		Cursor cursor;

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME
				+ "=? AND " + FIELD_TASK_STATUS_UUID_NAME + "=?", new String[] {
				user_uuid, status_uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			list = new ArrayList<Task>();
			do {
				list.add(getItem(cursor));
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param uuid
	 * @param users_uuid
	 * @param create_date
	 * @param modify_date
	 * @param close_date
	 * @param task_status_uuid
	 * @param attempt_send_date
	 * @param attempt_count
	 * @param updated
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(String uuid, String users_uuid, long create_date,
			long modify_date, long close_date, String task_status_uuid,
			long attempt_send_date, int attempt_count, boolean updated) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_USER_UUID_NAME, users_uuid);
		values.put(FIELD_CREATE_DATE_NAME, create_date);
		values.put(FIELD_MODIFY_DATE_NAME, modify_date);
		values.put(FIELD_CLOSE_DATE_NAME, close_date);
		values.put(FIELD_TASK_STATUS_UUID_NAME, task_status_uuid);
		values.put(FIELD_ATTEMPT_SEND_DATE_NAME, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT_NAME, attempt_count);
		values.put(FIELD_UPDATED_NAME, updated == true ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param task
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(Task task) {
		return replace(task.getUuid(), task.getUsers_uuid(),
				task.getCreate_date(), task.getModify_date(),
				task.getClose_date(), task.getTask_status_uuid(),
				task.getAttempt_send_date(), task.getAttempt_count(),
				task.isUpdated());
	}

	public String getStatusNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		// taskstatus = new TaskStatus();
		// String getNameByUUID(String uuid)
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return taskstdbadapter.getNameByUUID(cur.getString(1));
		} else
			return "неизвестен";
	}

	public String getCompleteTimeByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return DataUtils.getDate(cur.getLong(5), "dd-MM-yyyy hh:mm:ss");
		} else
			return "";
	}
	
	/**
	 * Возвращает объект Task
	 * @param cursor
	 * @return
	 */
	public static Task getItem(Cursor cursor) {
		Task item = new Task();
		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		item.setUsers_uuid(cursor.getString(cursor.getColumnIndex(FIELD_USER_UUID_NAME)));
		item.setCreate_date(cursor.getLong(cursor.getColumnIndex(FIELD_CREATE_DATE_NAME)));
		item.setModify_date(cursor.getLong(cursor.getColumnIndex(FIELD_MODIFY_DATE_NAME)));
		item.setClose_date(cursor.getLong(cursor.getColumnIndex(FIELD_CLOSE_DATE_NAME)));
		item.setTask_status_uuid(cursor.getString(cursor.getColumnIndex(FIELD_TASK_STATUS_UUID_NAME)));
		item.setAttempt_send_date(cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE_NAME)));
		item.setAttempt_count(cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT_NAME)));
		item.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED_NAME)) == 0 ? false : true);
		return item;
	}
	
	/**
	 * Возвращает наряд по uuid
	 * @param uuid
	 * @return если наряда нет, возвращает null
	 */
	public Task getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		} else {
			return null;
		}
	}

	/**
	 * Возвращает список нарядов по пользователю и с флагом updated=1
	 * @param uuid
	 * @return Если ни одной записи нет, возвращает null
	 */
	public ArrayList<Task> getTaskByUserAndUpdated(String uuid) {
		ArrayList<Task> list = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=? AND " + FIELD_UPDATED_NAME + "=1", new String[] { uuid }, null, null, null);
		
		if (cursor.moveToFirst()) {
			list = new ArrayList<Task>();
			do {
				list.add(getItem(cursor));
			} while(cursor.moveToNext());
		}

		return list;
	}
	
	/**
	 * Возвращает наряд по uuid с флагом updated=1
	 * @param uuid
	 * @return Если записи нет, возвращает null
	 */
	public Task getTaskByUuidAndUpdated(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=? AND " + FIELD_UPDATED_NAME + "=1", new String[] { uuid }, null, null, null);
		
		if (cursor.moveToFirst()) {
				return getItem(cursor);
		} else {
			return null;
		}
	}
}
