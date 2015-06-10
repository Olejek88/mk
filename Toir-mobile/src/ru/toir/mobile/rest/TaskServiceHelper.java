/**
 * 
 */
package ru.toir.mobile.rest;

import android.content.Context;
import android.os.Bundle;

/**
 * @author Dmitriy Logachov
 *
 */
public class TaskServiceHelper extends ServiceHelperBase {

	/**
	 * @param context
	 * @param providerId
	 * @param resultAction
	 */

	public TaskServiceHelper(Context context, String resultAction) {
		super(context, ProcessorService.Providers.TASK_PROVIDER, resultAction);
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void GetTask(String tag) {
		Bundle bundle = new Bundle();
		bundle.putString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG, tag);
		RunMethod(TaskServiceProvider.Methods.GET_TASK, bundle);
	}

}