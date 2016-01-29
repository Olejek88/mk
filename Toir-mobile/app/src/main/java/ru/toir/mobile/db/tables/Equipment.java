/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class Equipment extends BaseTable {

	private String title;
	private String equipment_type_uuid;
	private String critical_type_uuid;
	private long start_date;
	private float latitude;
	private float longitude;
	private String tag_id;
	private String image;
	private String equipment_status_uuid;
	private String inventory_number;
	private String location;

	/**
	 * 
	 */
	public Equipment() {
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
	 * @return the equipment_type_uuid
	 */
	public String getEquipment_type_uuid() {
		return equipment_type_uuid;
	}

	/**
	 * @param equipment_type_uuid the equipment_type_uuid to set
	 */
	public void setEquipment_type_uuid(String equipment_type_uuid) {
		this.equipment_type_uuid = equipment_type_uuid;
	}

	/**
	 * @return the critical_type_uuid
	 */
	public String getCritical_type_uuid() {
		return critical_type_uuid;
	}

	/**
	 * @param critical_type_uuid the critical_type_uuid to set
	 */
	public void setCritical_type_uuid(String critical_type_uuid) {
		this.critical_type_uuid = critical_type_uuid;
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
	 * @return the tag_id
	 */
	public String getTag_id() {
		return tag_id;
	}

	/**
	 * @param tag_id the tag_id to set
	 */
	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	/**
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}


	/**
	 * @return the status_uuid
	 */
	public String getEquipmentStatus_uuid() {
		return equipment_status_uuid;
	}

	/**
	 * @param status uuid to set
	 */
	public void setEquipmentStatus_uuid(String status_uuid) {
		this.equipment_status_uuid = status_uuid;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the inventory number
	 */
	public String getInventoryNumber() {
		return inventory_number;
	}
	/**
	 * @param inventory_number to set
	 */
	public void setInventoryNumber(String inventory_number) {
		this.inventory_number = inventory_number;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the equipment_status_uuid
	 */
	public String getEquipment_status_uuid() {
		return equipment_status_uuid;
	}

	/**
	 * @param equipment_status_uuid the equipment_status_uuid to set
	 */
	public void setEquipment_status_uuid(String equipment_status_uuid) {
		this.equipment_status_uuid = equipment_status_uuid;
	}

	/**
	 * @return the inventory_number
	 */
	public String getInventory_number() {
		return inventory_number;
	}

	/**
	 * @param inventory_number the inventory_number to set
	 */
	public void setInventory_number(String inventory_number) {
		this.inventory_number = inventory_number;
	}

}