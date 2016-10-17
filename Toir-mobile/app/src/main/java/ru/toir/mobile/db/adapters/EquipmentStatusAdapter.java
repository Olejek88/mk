package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.EquipmentStatus;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentStatusAdapter extends RealmBaseAdapter<EquipmentStatus> implements ListAdapter {
    public static final String TABLE_NAME = "EquipmentStatus";

    private static class ViewHolder{
        TextView uuid;
        TextView title;
        ImageView icon;
    }

    public EquipmentStatusAdapter(@NonNull Context context, int resId, RealmResults<EquipmentStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EquipmentStatus> rows = realm.where(EquipmentStatus.class).findAll();
        return rows.size();
    }

    @Override
    public EquipmentStatus getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
            EquipmentStatus equipmentStatus = adapterData.get(position);
            textView.setText(equipmentStatus.getTitle());
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            EquipmentStatus equipmentStatus = adapterData.get(position);
            viewHolder.title.setText(equipmentStatus.getTitle());
            return convertView;
        }
        return convertView;
    }
}