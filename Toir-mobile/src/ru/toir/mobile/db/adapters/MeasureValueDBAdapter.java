package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.MeasureValue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MeasureValueDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "measure_value";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TITLE_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public MeasureValueDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public MeasureValueDBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
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
	 * 
	 * @param uuid
	 * @return
	 */
	public MeasureValue getItem(String uuid) {		
		return getMeasureValue(mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null));		
	}
	
	/**
	 * <p>Возвращает объект MeasureValue</p>
	 * @param cursor
	 * @return
	 */
	public static MeasureValue getMeasureValue(Cursor cursor) {
		if (cursor.moveToFirst()) {
			MeasureValue item = null;
			item = new MeasureValue(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
			return item;
		}
		return null;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TITLE_NAME, title);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(MeasureValue status) {
		return replace(status.getUuid(), status.getTitle());
	}
}
