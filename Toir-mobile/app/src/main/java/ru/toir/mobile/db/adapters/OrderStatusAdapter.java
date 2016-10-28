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
import ru.toir.mobile.db.realm.OrderStatus;

/**
 * @author koputo
 *         Created by koputo on 08.09.16.
 */
public class OrderStatusAdapter extends RealmBaseAdapter<OrderStatus> implements ListAdapter {
    public static final String TABLE_NAME = "OrderStatus";
    public class Status {
        public static final String NEW = "789b4d73-044c-471b-a08d-26f36ebb22ba";
        public static final String IN_WORK = "78980db5-934c-4ddb-999a-04c6c3daca59";
        public static final String COMPLETE = "786dca37-2cc9-44da-aff9-19bf143e611a";
        public static final String UN_COMPLETE = "783c08ec-89d9-47df-b7cf-63a05d56594c";
    }

    public OrderStatusAdapter(@NonNull Context context, RealmResults<OrderStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrderStatus> rows = realm.where(OrderStatus.class).findAll();
        return rows.size();
    }

    @Override
    public OrderStatus getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        } else return null;
    }

    @Override
    public long getItemId(int position) {
        OrderStatus orderStatus;
        if (adapterData != null) {
            orderStatus = adapterData.get(position);
            return orderStatus.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
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
            OrderStatus orderStatus;
            if (adapterData != null) {
                orderStatus = adapterData.get(position);
                viewHolder.title.setText(orderStatus.getTitle());
                viewHolder.uuid.setText(orderStatus.getUuid());
            }
            return convertView;
        }
        if (parent.getId() == R.id.simple_spinner || convertView == null) {
            TextView textView = new TextView(context);
            OrderStatus orderStatus;
            if (adapterData != null) {
                orderStatus = adapterData.get(position);
                textView.setText(orderStatus.getTitle());
            }
            return textView;
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }
}
