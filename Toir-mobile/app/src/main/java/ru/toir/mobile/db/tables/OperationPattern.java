/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationPattern extends BaseTable {

	private String title;
	private String operation_type_uuid;
	
	/**
	 * 
	 */
	public OperationPattern() {
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the title
	 */
	public String getOperation_type_uuid() {
		return operation_type_uuid;
	}

	/**
	 * @param title the title to set
	 */
	public void setOperation_type_uuid(String operation_type_uuid) {
		this.operation_type_uuid = operation_type_uuid;
	}

}