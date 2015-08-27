package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class OperationResult {

	@Expose
	private String Id;
	@Expose
	private String Title;
	@Expose
	private Date CreatedAt;
	@Expose
	private Date ChangedAt;
	@Expose
	private OperationType OperationType; 

	/**
	 * 
	 * @return The Id
	 */
	public String getId() {
		return Id;
	}

	/**
	 * 
	 * @param Id
	 *            The Id
	 */
	public void setId(String Id) {
		this.Id = Id;
	}

	/**
	 * 
	 * @return The Title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * 
	 * @param Title
	 *            The Title
	 */
	public void setTitle(String Title) {
		this.Title = Title;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @param createdAt
	 *            the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}

	/**
	 * @return the changedAt
	 */
	public Date getChangedAt() {
		return ChangedAt;
	}

	/**
	 * @param changedAt
	 *            the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

	/**
	 * @return the operationType
	 */
	public OperationType getOperationType() {
		return OperationType;
	}

	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(OperationType operationType) {
		OperationType = operationType;
	}

}