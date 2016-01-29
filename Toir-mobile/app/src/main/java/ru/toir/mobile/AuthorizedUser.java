/**
 * 
 */
package ru.toir.mobile;

/**
 * @author Dmitriy Logachov
 *
 */
public class AuthorizedUser {
	
	private String mUuid;
	private String mTagId;
	private String mToken;
	private static AuthorizedUser mInstance;
	
	public static synchronized AuthorizedUser getInstance() {
		if (mInstance == null) {
			mInstance = new AuthorizedUser();
		}
		return mInstance;
	}

	/**
	 * @return the mUuid
	 */
	public String getUuid() {
		return mUuid;
	}

	/**
	 * @param mUuid the mUuid to set
	 */
	public void setUuid(String Uuid) {
		this.mUuid = Uuid;
	}

	/**
	 * @return the mTagId
	 */
	public String getTagId() {
		return mTagId;
	}

	/**
	 * @param mTagId the mTagId to set
	 */
	public void setTagId(String TagId) {
		this.mTagId = TagId;
	}

	/**
	 * @return the mToken
	 */
	public String getToken() {
		return mToken;
	}

	/**
	 * @param mToken the mToken to set
	 */
	public void setToken(String Token) {
		this.mToken = Token;
	}

}