package ru.toir.mobile.fragments;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.api.IMapController;
//import org.osmdroid.api.Marker;
//import org.osmdroid.bonuspack.overlays.Marker;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.tables.*;
import ru.toir.mobile.gps.TestGPSListener;
import ru.toir.mobile.utils.TaskItemizedOverlay;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.location.Location;

import org.osmdroid.views.overlay.ItemizedIconOverlay;

public class GPSFragment extends Fragment {
	private IMapController mapController;
	private MapView mapView;
	private double curLatitude, curLongitude;
	Location location;
	TextView gpsLog;
	ArrayList<OverlayItem> aOverlayItemArray;

	/**
	 * Класс объекта оборудования для отображения на крате
	 * 
	 * @author koputo
	 * 
	 */
	class EquipmentOverlayItem extends OverlayItem {
		public Equipment equipment;

		public EquipmentOverlayItem(String a, String b, GeoPoint p) {
			super(a, b, p);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);
		// Hardcoded !!
		String tagId = "01234567";
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(
				getActivity().getApplicationContext())).open();
		// запрашиваем данные текущего юзера, хотя нам нужен только его uuid
		// (если он будет храниться глобально, то запрашивать постоянно уже не
		// надо будет)
		Users user = users.getUserByTagId(tagId);
		LocationManager lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		if (lm != null) {
			TestGPSListener tgpsl = new TestGPSListener(
					(TextView) rootView.findViewById(R.id.gpsTextView),
					getActivity().getApplicationContext(), user.getUuid());
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1,
					tgpsl);
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			gpsLog = (TextView) rootView.findViewById(R.id.gpsTextView);
			if (location != null) {
				curLatitude = location.getLatitude();
				curLongitude = location.getLongitude();
				gpsLog.append("Altitude:"
						+ String.valueOf(location.getAltitude()) + "\n");
				gpsLog.append("Latitude:"
						+ String.valueOf(location.getLatitude()) + "\n");
				gpsLog.append("Longtitude:"
						+ String.valueOf(location.getLongitude()) + "\n");
			}
		}
		mapView = (MapView) rootView.findViewById(R.id.mapview);
		// mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setUseDataConnection(false);
		mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(17);
		GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
		mapController.setCenter(point2);

		// добавляем тестовый маркер
		aOverlayItemArray = new ArrayList<OverlayItem>();
		OverlayItem overlayItem = new OverlayItem("We are here", "WAH",
				new GeoPoint(curLatitude, curLongitude));
		aOverlayItemArray.add(overlayItem);
		ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(
				getActivity().getApplicationContext(), aOverlayItemArray, null);
		mapView.getOverlays().add(aItemizedIconOverlay);
		ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();

		TaskDBAdapter dbOrder = new TaskDBAdapter(new TOiRDatabaseContext(
				getActivity().getApplicationContext())).open();
		ArrayList<Task> ordersList = dbOrder.getOrdersByUser(user.getUuid(),
				TaskStatusDBAdapter.STATUS_UUID_RECIEVED, "");
		Integer cnt = 0, cnt2 = 0;
		while (cnt < ordersList.size()) {
			EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext())).open();
			ArrayList<EquipmentOperation> equipOperationList = operationDBAdapter
					.getItems(ordersList.get(cnt).getUuid());
			// запрашиваем перечень оборудования
			cnt2 = 0;
			while (cnt2 < equipOperationList.size()) {
				// equipOpList.get(cnt2).getUuid();
				String location = "";
				EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
						new TOiRDatabaseContext(getActivity()
								.getApplicationContext())).open();
				if (equipOperationList.get(cnt2).getEquipment_uuid() != null) {
					location = eqDBAdapter.getLocationByUUID(equipOperationList
							.get(cnt2).getEquipment_uuid());
					// TODO: добавить парсинг реальных координат
					String coordinates[] = location.split("[NSWE]");
					// coordinates[0]; // Latitude
					// coordinates[1]; // Longitude
					// N60.04535 E30.12754
					curLatitude = curLatitude - 0.0001 * cnt2;
					curLongitude = curLongitude - 0.0001 * cnt2;

					Equipment equipment = eqDBAdapter
							.getItem(equipOperationList.get(cnt2)
									.getEquipment_uuid());
					EquipmentOverlayItem olItem = new EquipmentOverlayItem(
							equipment.getTitle(), "Device", new GeoPoint(
									curLatitude, curLongitude));
					olItem.equipment = equipment;
					Drawable newMarker = this.getResources().getDrawable(
							R.drawable.marker_equip);
					olItem.setMarker(newMarker);
					overlayItemArray.add(olItem);

					// aOverlayItemArray.add(olItem);
				}
				cnt2 = cnt2 + 1;
			}
			cnt = cnt + 1;
		}

		TaskItemizedOverlay overlay = new TaskItemizedOverlay(getActivity()
				.getApplicationContext(), overlayItemArray) {
			@Override
			protected boolean onLongPressHelper(int index, OverlayItem item) {
				Equipment equipment = ((EquipmentOverlayItem) item).equipment;
				Toast.makeText(
						mContext,
						"UUID оборудования " + equipment.getTitle() + " - "
								+ equipment.getUuid(), Toast.LENGTH_SHORT)
						.show();

				// пример тупой, но полагю это почти то что тебе было нужно
				ViewPager pager = (ViewPager) getActivity().findViewById(
						R.id.pager);
				pager.setCurrentItem(PageAdapter.TASK_FRAGMENT);

				return super.onLongPressHelper(index, item);
			}
		};
		mapView.getOverlays().add(overlay);

		users.close();
		dbOrder.close();
		onInit(rootView);
		return rootView;
	}

	public void onInit(View view) {

	}
}
