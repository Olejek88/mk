package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OperationStatus;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class OperationStatusAdapter extends RealmBaseAdapter<OperationStatus> implements ListAdapter {
    public static final String TABLE_NAME = "OperationStatus";

    private static class ViewHolder{
        TextView uuid;
        TextView title;
    }

    public OperationStatusAdapter(@NonNull Context context, int resId, RealmResults<OperationStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OperationStatus> rows = realm.where(OperationStatus.class).findAll();
        return rows.size();
    }

    @Override
    public OperationStatus getItem(int position) {
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
            OperationStatus operationStatus = adapterData.get(position);
            textView.setText(operationStatus.getTitle());
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = (TextView) convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OperationStatus operationStatus = adapterData.get(position);
            viewHolder.title.setText(operationStatus.getTitle());
            viewHolder.uuid.setText(operationStatus.getUuid());
            return convertView;
        }
        return convertView;
    }
}