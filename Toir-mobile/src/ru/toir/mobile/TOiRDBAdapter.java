package ru.toir.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author koputo
 * <p>Класс для работы с базой данных ТОиР</p>
 */
public class TOiRDBAdapter{
	private static final String TAG = "TOiRDBAdapter";
	
	/**
	 * <p>Имя файла базы данных</p>
	 */
	private static final String DATABASE_NAME = "toir.db";
	/**
	 * <p>Версия базы данных с которой работает приложение</p>
	 */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * база приложения
	 */
	private SQLiteDatabase db;
	
	/**
	 * контекст приложения
	 */
	private Context context;
	
	/**
	 * вспомогательный класс для работы с базой
	 */
	private TOiRDbHelper dbHelper;
	
	/**
	 * <p>Конструктор адаптера</p>
	 * @param _context Контекст приложения
	 */
	public TOiRDBAdapter(Context context){
		Log.d(TAG, "TOiRDBAdapter()");
		this.context = new TOiRDatabaseContext(context);
		dbHelper = new TOiRDbHelper(this.context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * <p>Возвращает имя файла базы данных</p>
	 * @return String
	 */
	public static String getDbName() {
		return DATABASE_NAME;
	}
	
	/**
	 * <p>Возвращает имя файла базы данных</p>
	 * @return String
	 */
	public String getDbPath(String dbName) {
		return context.getDatabasePath(dbName).toString();
	}

	/**
	 * <p>Возвращает текущую версию базы</p>
	 * @return int Текущая версия базы
	 */
	public int getDbVersion(){
		return db.getVersion();
	}
	
	/**
	 * <p>Возвращает текущую версию базы с которой работет приложение</p>
	 * @return int Текущая версия базы с которой работает приложение
	 */
	public int getAppDbVersion(){
		return DATABASE_VERSION;
	}
	
	/**
	 * <p>Проверяет актуальна ли версия базы</p>
	 * <p>Проверяется факт того, что версия базы данных совпадает с
	 * версией базы с которой работает приложение.</p>
	 * <p>Пример: Текущая версия базы 3, версия базы с которой работает приложение 3.
	 * Необходимо изменить структуру базы. В приложении меняем версию с которой работает приложение на 4.
	 * Подготавливаем sql скрипт для изменения структуры базы update4.sql
	 * Собираем приложение, устанавливаем на устройство. Но скрипт обновления по какой-то причине не выполняется.
	 * В этой ситуации версия базы данных останется 3, а версия приложения будет 4, что говорит нам о
	 * невозможности продолжать работу, так как в базе данных нет необходимых изменений.</p>
	 * @return true если версии совпадают, false в противном случае
	 */
	public boolean isActual(){
		return DATABASE_VERSION == getDbVersion();
	}
	
	/**
	 * <p>"Открывает" базу для работы</p>
	 * @return Объект класса TOiRDBAdapter 
	 * @throws SQLException
	 */
	public TOiRDBAdapter open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * <p>"Закрывает" базу для работы</p>
	 */
	public void close(){
		db.close();
		db = null;
	}

	/**
	 * <p>Функция делает новую запись в БД </p>
	 * @param String имя таблицы
	 * @param String 
	 * @param ContentValues список значений
	 */
	public long insert(String table_name, String nullColumnHack, ContentValues values){
		return db.insert(table_name, nullColumnHack, values);
	}

	/**
	 * <p>Функция удаляет записи из БД</p>
	 * @param id ид для удаления (0 - все)
	 * @return int количество удалённых записей
	 */
	/**
	 * <p>Метод удаляет записи из БД</p>
	 * <p>Если selection = null то удаляем все записи в таблице</p>
	 * @param String table
	 * @param String whereClause
	 * @param String[] whereArgs
	 * @return int количество удалённых записей
	 */
	public int delete(String table, String whereClause, String[] whereArgs){
		if (whereClause == null) {
			return db.delete(table, null, null);
		}
		else {
			return db.delete(table, whereClause, whereArgs);
		}
	}
	
	/**
	 * <p>Метод читает данные из базы по указанных параметрам.</p>
	 * @param table таблица из которой читаем
	 * @param columns массив столбцов для выборки
	 * @param selection условия отбора (можно использовать знак вопроса(?) для замены из списка параметров отбора)
	 * @param selectionArgs параметры для условий отбора
	 * @param groupBy группировка
	 * @param having
	 * @param orderBy сортировка
	 * @return
	 */
	public Cursor read(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	/**
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return db.update(table, values, whereClause, whereArgs);
	}
	
	/**
	 * <p>Вспомогательный класс для работы с базой данных</p>
	 * @author koputo
	 *
	 */
	private class TOiRDbHelper extends SQLiteOpenHelper{
		private static final String TAG = "TOiRDbHelper";
		private String updatePath = "updatedb";
		
		/**
		 * <p>Конструктор</p>
		 * @param _context
		 * @param _name
		 * @param _factory
		 * @param _version
		 */
		public TOiRDbHelper(Context context, String name, CursorFactory factory, int version){
			super(context, name, factory, version);
		}
		
		/**
		 * <p>Создаёт новую базу данных</p>
		 */
		@Override
		public void onCreate(SQLiteDatabase db){
			Log.d(TAG, "TOiRDbHelper.onCreate: db.version=" + db.getVersion());
			loadAndExecSQLUpdate(db, 0, DATABASE_VERSION);
		}

		/**
		 * <p>Обновляет текущую базу данных</p>
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.d(TAG, "TOiRDbHelper.onUpgrade: db.version=" + db.getVersion());
			Log.d(TAG, "TOiRDbHelper.onUpgrade: oldVersion=" + oldVersion + ", newVersion=" + newVersion);
			loadAndExecSQLUpdate(db, oldVersion, newVersion);
		}
		
		/**
		 * <p>Метод загружает файлы с обновлениями структуры базы данных, и выполняет SQL инструкции из этих файлов</p>
		 * <p>Алгоритм работы работы следующий: в папке с обновлениями структуры базы данных
		 * ищутся все файлы от текущей версии базы данных до текущей версии с которой работает приложение.
		 * Последовательно выполняются. Если всё скрипты выполнились успешно, версия базы меняется до запрошенной.
		 * Если нет, остаётся равной oldVersion, то есть база не создаётся или не обновляется.</p>
		 * @param db SQLiteDatabase объект базы данных которую нужно обновить
		 * @param oldVersion int текущая версия базы данных
		 * @param newVersion int версия до которой нужно обновить базу данных
		 */
		private void loadAndExecSQLUpdate(SQLiteDatabase db, int oldVersion, int newVersion){
			AssetManager am = context.getAssets();
			boolean transactionSuccefful = true;

			db.beginTransaction();
			for(int i = oldVersion + 1; i <= newVersion; i++){
				try{
					InputStream is = am.open(updatePath + "/update" + i + ".sql");
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String line;
					while((line = br.readLine()) != null){
						try{
							db.execSQL(line);
						}catch(SQLException e){
							transactionSuccefful = false;
							Log.d(TAG, e.toString());
						}
					}
				}catch(IOException e){
					transactionSuccefful = false;
					Log.d(TAG, e.toString());
				}
			}
			if(transactionSuccefful){
				db.setTransactionSuccessful();
			}
			db.endTransaction();
		}
	}
}
