/**
 * 
 */
package ru.toir.mobile;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;

import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.Task;

/**
 * @author Dmitriy Logachov
 *
 */
public class TaskResult {
	
	private Context mContext;
	public Task mTask;
	public ArrayList<EquipmentOperation> mEquipmentOperations;
	public ArrayList<EquipmentOperationResult> mEquipmentOperationResults;
	public ArrayList<MeasureValue> mMeasureValues;

	/**
	 * 
	 */
	public TaskResult(Context context) {
		mContext = context;
	}
	
	/**
	 * Выбирает из базы все данные связанные с результатами выполнения наряда.
	 * Наряд, список операций в наряде, список результатов выполнения операций, список результатов измерения.
	 * Списки результатов выполнения операций и результатов измерений могут быть пустыми.
	 * @param taskUuid
	 * @return true если есть наряд и операции связанные с этим нарядом
	 */
	public boolean getTaskResult(String taskUuid) {
		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
		mTask = taskDBAdapter.getItem(taskUuid);
		taskDBAdapter.close();
		if (mTask != null) {
			EquipmentOperationDBAdapter equipmentOperationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(mContext)).open();
			mEquipmentOperations = equipmentOperationDBAdapter.getItems(taskUuid);
			equipmentOperationDBAdapter.close();
			if (mEquipmentOperations != null) {
				mEquipmentOperationResults = new ArrayList<EquipmentOperationResult>();
				mMeasureValues = new ArrayList<MeasureValue>();
				Iterator<EquipmentOperation> iterator = mEquipmentOperations.iterator();
				EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(mContext)).open();
				MeasureValueDBAdapter measureValueDBAdapter = new MeasureValueDBAdapter(new TOiRDatabaseContext(mContext)).open();
				String operationUuid;
				while (iterator.hasNext()) {
					operationUuid = iterator.next().getUuid();
					mEquipmentOperationResults.add(equipmentOperationResultDBAdapter.getItemByOperation(operationUuid));
					mMeasureValues.addAll(measureValueDBAdapter.getItems(operationUuid));
				}
				equipmentOperationResultDBAdapter.close();
				measureValueDBAdapter.close();
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
}