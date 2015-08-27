/**
 * 
 */
package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Dmitriy Logachov
 *
 */
public class BaseDBAdapter {
	
	private DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;
	private final Context mContext;
	
	private String TABLE_NAME;

	public static final String FIELD__ID = "_id";
	public static final String FIELD_UUID = "uuid";
	public static final String FIELD_CREATED_AT = "CreatedAt";
	public static final String FIELD_CHANGED_AT = "ChangedAt";



	/**
	 * 
	 */
	public BaseDBAdapter(Context context, String tableName) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
		TABLE_NAME = tableName;
	}

	/**
	 * Возвращает дату последней модификации среди всех элементов таблицы
	 * @return Количество милисекунд, если записей нет, null
	 */
	public Long getLastChangedAt() {
		Long changed = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, new String[]{ FIELD_CHANGED_AT }, null, null, FIELD_CHANGED_AT, null, FIELD_CHANGED_AT + " DESC" + " LIMIT 1");
		if (cursor.moveToFirst()) {
			changed = cursor.getLong(cursor.getColumnIndex(FIELD_CHANGED_AT));
		}
		return changed;
	}
	
	/**
	 * Собирает выражение sql для склеивания таблиц LEFT JOIN
	 * @param firstTable
	 * @param secondTable
	 * @param firstField
	 * @param secondField
	 * @return
	 */
	protected static String getLeftJoinTables(String firstTable, String secondTable,
			String firstField, String secondField, boolean first) {
		return getJoinTables("LEFT JOIN", firstTable, secondTable, firstField,
				secondField, first);
	}

	/**
	 * Собирает выражение sql для склеивания таблиц с указанным типом склеивания
	 * @param join
	 * @param firstTable
	 * @param secondTable
	 * @param firstField
	 * @param secondField
	 * @return
	 */
	protected static String getJoinTables(String join, String firstTable,
			String secondTable, String firstField, String secondField, boolean first) {
		StringBuilder result = new StringBuilder();

		if (first) {
			result.append(firstTable).append(' ');
		}
		
		result.append(join).append(' ')
				.append(secondTable).append(" ON ").append(firstTable)
				.append('.').append(firstField).append('=').append(secondTable)
				.append('.').append(secondField);
		return result.toString();

	}
	
	/**
	 * Возвращает имя поля в формате table.fieldname
	 * @param table
	 * @param field
	 * @return
	 */
	protected static String getFullName(String table, String field) {
		return new StringBuilder().append(table).append('.').append(field)
				.toString();
	}

}
