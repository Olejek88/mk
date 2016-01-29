/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationResult extends BaseTable {

	private String equipment_operation_uuid;
	private long start_date;
	private long end_date;
	private String operation_result_uuid;
	private int type;
	private long attempt_send_date;
	private int attempt_count;
	private boolean updated;

	/**
	 * 
	 */
	public EquipmentOperationResult() {
	}
	
	/**
	 * @return the equipment_operation_uuid
	 */
	public String getEquipment_operation_uuid() {
		return equipment_operation_uuid;
	}

	/**
	 * @param equipment_operation_uuid the equipment_operation_uuid to set
	 */
	public void setEquipment_operation_uuid(String equipment_operation_uuid) {
		this.equipment_operation_uuid = equipment_operation_uuid;
	}

	/**
	 * @return the start_date
	 */
	public long getStart_date() {
		return start_date;
	}

	/**
	 * @param start_date the start_date to set
	 */
	public void setStart_date(long start_date) {
		this.start_date = start_date;
	}

	/**
	 * @return the end_date
	 */
	public long getEnd_date() {
		return end_date;
	}

	/**
	 * @param end_date the end_date to set
	 */
	public void setEnd_date(long end_date) {
		this.end_date = end_date;
	}

	/**
	 * @return the operation_result_uuid
	 */
	public String getOperation_result_uuid() {
		return operation_result_uuid;
	}

	/**
	 * @param operation_result_uuid the operation_result_uuid to set
	 */
	public void setOperation_result_uuid(String operation_result_uuid) {
		this.operation_result_uuid = operation_result_uuid;
	}

	/**
	 * @return the attempt_send_date
	 */
	public long getAttempt_send_date() {
		return attempt_send_date;
	}

	/**
	 * @param attempt_send_date the attempt_send_date to set
	 */
	public void setAttempt_send_date(long attempt_send_date) {
		this.attempt_send_date = attempt_send_date;
	}

	/**
	 * @return the attempt_count
	 */
	public int getAttempt_count() {
		return attempt_count;
	}

	/**
	 * @param attempt_count the attempt_count to set
	 */
	public void setAttempt_count(int attempt_count) {
		this.attempt_count = attempt_count;
	}

	/**
	 * @return the updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}