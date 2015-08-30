package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ru.toir.mobile.db.tables.Equipment;

/**
 * @author olejek
 *         <p>
 *         Класс для работы с оборудованием
 *         </p>
 * 
 */
public class EquipmentDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "equipment";

	public static final String FIELD_TITLE = "title";
	public static final String FIELD_EQUIPMENT_TYPE_UUID = "equipment_type_uuid";
	public static final String FIELD_CRITICAL_TYPE_UUID = "critical_type_uuid";
	public static final String FIELD_START_DATE = "start_date";
	public static final String FIELD_LATITUDE = "latitude";
	public static final String FIELD_LONGITUDE = "longitude";
	public static final String FIELD_TAG_ID = "tag_id";
	public static final String FIELD_IMG = "img";
	public static final String FIELD_EQUIPMENT_STATUS_UUID = "equipment_status_uuid";
	public static final String FIELD_INVENTORY_NUMBER = "inventory_number";
	public static final String FIELD_LOCATION = "location";

	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_'
				+ FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_'
				+ FIELD_CHANGED_AT;

		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
		public static final String EQUIPMENT_TYPE_UUID = TABLE_NAME + '_'
				+ FIELD_EQUIPMENT_TYPE_UUID;
		public static final String CRITICAL_TYPE_UUID = TABLE_NAME + '_'
				+ FIELD_CRITICAL_TYPE_UUID;
		public static final String START_DATE = TABLE_NAME + '_'
				+ FIELD_START_DATE;
		public static final String LATITUDE = TABLE_NAME + '_' + FIELD_LATITUDE;
		public static final String LONGITUDE = TABLE_NAME + '_'
				+ FIELD_LONGITUDE;
		public static final String TAG_ID = TABLE_NAME + '_' + FIELD_TAG_ID;
		public static final String IMG = TABLE_NAME + '_' + FIELD_IMG;
		public static final String EQUIPMENT_STATUS_UUID = TABLE_NAME + '_'
				+ FIELD_EQUIPMENT_STATUS_UUID;
		public static final String INVENTORY_NUMBER = TABLE_NAME + '_'
				+ FIELD_INVENTORY_NUMBER;
		public static final String LOCATION = TABLE_NAME + '_' + FIELD_LOCATION;

	}

	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID)
				+ " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID)
				+ " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT,
				getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS "
						+ Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT,
				getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS "
						+ Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE)
				+ " AS " + Projection.TITLE);
	}

	/**
	 * @param context
	 * @return EquipmentDBAdapter
	 */
	public EquipmentDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}

	/**
	 * <p>
	 * Возвращает все записи из таблицы equipment
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<Equipment> getAllItems(String type, String critical_type) {
		ArrayList<Equipment> arrayList = new ArrayList<Equipment>();
		Cursor cursor;
		// можем или отобрать все оборудование или только определенного типа
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);

		if (type.equals("") && !critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_CRITICAL_TYPE_UUID
					+ "=?", new String[] { critical_type }, null, null, null);
		}
		if (!type.equals("") && critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_TYPE_UUID
					+ "=?", new String[] { type }, null, null, null);
		}
		if (!type.equals("") && !critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_CRITICAL_TYPE_UUID
					+ "=? AND " + FIELD_EQUIPMENT_TYPE_UUID + "=?",
					new String[] { critical_type, type }, null, null, null);
		}
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				arrayList.add(getItem(cursor));
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		cursor.close();
		return arrayList;
	}

	/**
	 * <p>
	 * Возвращает запись из таблицы equipment
	 * </p>
	 * 
	 * @param uuid
	 * @return Equipment
	 */
	public Equipment getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}

	/**
	 * <p>
	 * Возвращает запись из таблицы equipment
	 * </p>
	 * 
	 * @param uuid
	 * @return Equipment
	 */
	public Cursor getItemCursor(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		return cursor;
	}

	/**
	 * <p>
	 * Возвращает Equipment
	 * </p>
	 * 
	 * @param uuid
	 * @return Equipment
	 */
	public Equipment getItem(Cursor cursor) {
		Equipment equipment = new Equipment();
		getItem(cursor, equipment);
		equipment.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		equipment.setEquipment_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_TYPE_UUID)));
		equipment.setCritical_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_CRITICAL_TYPE_UUID)));
		equipment.setStart_date(cursor.getLong(cursor
				.getColumnIndex(FIELD_START_DATE)));
		equipment.setLatitude(cursor.getFloat(cursor.getColumnIndex(FIELD_LATITUDE)));
		equipment.setLongitude(cursor.getFloat(cursor
				.getColumnIndex(FIELD_LONGITUDE)));
		equipment.setTag_id(cursor.getString(cursor.getColumnIndex(FIELD_TAG_ID)));
		equipment.setImg(cursor.getString(cursor.getColumnIndex(FIELD_IMG)));
		equipment.setEquipmentStatus_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_STATUS_UUID)));
		equipment.setInventoryNumber(cursor.getString(cursor
				.getColumnIndex(FIELD_INVENTORY_NUMBER)));
		equipment.setLocation(cursor.getString(cursor.getColumnIndex(FIELD_LOCATION)));

		return equipment;
	}

	/**
	 * <p>
	 * Добавляет/заменяет запись в таблице equipments
	 * </p>
	 * 
	 * @param uuid
	 * @param title
	 * @param equipment_type_uuid
	 * @param critical_type_uuid
	 * @param start_date
	 * @param location
	 * @param tag_id
	 * @param img
	 * @param status_uuid
	 * @param inventory_number
	 * @param location
	 * @return
	 */
	public long replace(String uuid, String title, String equipment_type_uuid,
			String critical_type_uuid, long start_date, float latitude,
			float longitude, String tag_id, String img, String status_uuid,
			String inventory_number, String location, long createdAt,
			long changedAt) {
		ContentValues values = new ContentValues();
		values.put(EquipmentDBAdapter.FIELD_UUID, uuid);
		values.put(EquipmentDBAdapter.FIELD_TITLE, title);
		values.put(EquipmentDBAdapter.FIELD_EQUIPMENT_TYPE_UUID,
				equipment_type_uuid);
		values.put(EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID,
				critical_type_uuid);
		values.put(EquipmentDBAdapter.FIELD_START_DATE, start_date);
		values.put(EquipmentDBAdapter.FIELD_LATITUDE, latitude);
		values.put(EquipmentDBAdapter.FIELD_LONGITUDE, longitude);
		values.put(EquipmentDBAdapter.FIELD_TAG_ID, tag_id);
		values.put(EquipmentDBAdapter.FIELD_IMG, img);
		values.put(EquipmentDBAdapter.FIELD_EQUIPMENT_STATUS_UUID, status_uuid);
		values.put(EquipmentDBAdapter.FIELD_INVENTORY_NUMBER, inventory_number);
		values.put(EquipmentDBAdapter.FIELD_LOCATION, location);
		values.put(FIELD_CREATED_AT, createdAt);
		values.put(FIELD_CHANGED_AT, changedAt);
		return mDb.replace(EquipmentDBAdapter.TABLE_NAME, null, values);
	}

	/**
	 * <p>
	 * Добавляет/заменяет запись в таблице equipments
	 * </p>
	 * 
	 * @param equipment
	 * @return
	 */
	public long replace(Equipment equipment) {
		return replace(equipment.getUuid(), equipment.getTitle(),
				equipment.getEquipment_type_uuid(),
				equipment.getCritical_type_uuid(), equipment.getStart_date(),
				equipment.getLatitude(), equipment.getLongitude(),
				equipment.getTag_id(), equipment.getImg(),
				equipment.getEquipmentStatus_uuid(),
				equipment.getInventoryNumber(), equipment.getLocation(),
				equipment.getCreatedAt(), equipment.getChangedAt());
	}

	/**
	 * <p>
	 * Возвращает название оборудования по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getEquipsNameByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
		} else
			return "неизвестно";
	}

	/**
	 * <p>
	 * Возвращает тип оборудования по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getEquipsTypeByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_EQUIPMENT_TYPE_UUID));
		} else
			return "неизвестно";
	}

	/**
	 * <p>
	 * Возвращает тип оборудования по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getCriticalByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_CRITICAL_TYPE_UUID));
		} else
			return "неизвестен";
	}

	/**
	 * <p>
	 * Возвращает координаты по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getLocationCoordinatesByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getFloat(cursor.getColumnIndex(FIELD_LATITUDE)) + " "
					+ cursor.getFloat(cursor.getColumnIndex(FIELD_LONGITUDE));
		} else
			return "неизвестны";
	}

	/**
	 * <p>
	 * Возвращает местоположение по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getLocationByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_LOCATION));
		} else
			return "неизвестно";
	}

	/**
	 * <p>
	 * Возвращает фото по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getImgByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_IMG));
		} else
			return "/data/img/img.png";
	}

	/**
	 * <p>
	 * Возвращает tag_id по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getTagIDByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TAG_ID));
		} else
			return "0-0-0";
	}

	/**
	 * <p>
	 * Возвращает inventory number по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getInventoryNumberByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_INVENTORY_NUMBER));
		} else
			return "-";
	}

	public void saveItems(ArrayList<Equipment> list) {
		mDb.beginTransaction();
		for (Equipment item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}

	/**
	 * @return the mProjection
	 */
	public static Map<String, String> getProjection() {
		Map<String, String> projection = new HashMap<String, String>();
		projection.putAll(mProjection);
		projection.remove(Projection._ID);
		return projection;
	}

}
