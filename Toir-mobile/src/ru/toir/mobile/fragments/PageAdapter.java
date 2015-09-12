package ru.toir.mobile.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.toir.mobile.fragments.EquipmentsFragment;

public class PageAdapter extends FragmentPagerAdapter {
	public final static int ORDER_FRAGMENT = 1; 
	public final static int TASK_FRAGMENT = 6;

	private OrdersFragment ordersFragment;
	private TaskFragment taskFragment;

	public PageAdapter(FragmentManager fm) {	
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment;
		switch (arg0) {
		case 0 :
			fragment = new UserInfoFragment();
			break;
		case ORDER_FRAGMENT :
			if (ordersFragment == null) {
				ordersFragment = new OrdersFragment();
			}
			return ordersFragment;
		case 2 :
			fragment = new ReferenceFragment();
			break;
		case 3 :
			fragment = new ChartsFragment();
			break;
		case 4 :
			fragment = new GPSFragment();
			break;
		case 5 :
			fragment = new EquipmentsFragment();
			break;
		case TASK_FRAGMENT :
			if (taskFragment == null) {
				taskFragment = new TaskFragment();
			}
			return taskFragment;
		case 7 :
			fragment = new NativeCameraFragment();
			break;
		case 8 :
			fragment = new QRTestFragment();
			break;
		default :
			fragment = null;
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 9;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		CharSequence title;
		switch (position) {
		case 0 :
			title = "Пользователь";
			break;
		case ORDER_FRAGMENT :
			title = "Наряды таблица";
			break;
		case 2 :
			title = "Справочники";
			break;
		case 3 :
			title = "Графики";
			break;
		case 4 :
			title = "Тест GPS";
			break;
		case 5 :
			title = "Оборудование";
			break;
		case TASK_FRAGMENT :
			title = "Наряды";
			break;
		case 7 :
			title = "Фото";
			break;
		case 9 :
			title = "QR test";
			break;
		default :
			title = "";
			break;
		}
		return title;
	}

}