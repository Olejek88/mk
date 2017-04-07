package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.StageStatus;

/**
 * @author olejek
 * Created by olejek on 16.03.17.
 */
public class StageStatusAdapter extends RealmBaseAdapter<StageStatus> implements ListAdapter {
    public static final String TABLE_NAME = "StageStatus";

    public StageStatusAdapter(@NonNull Context context, RealmResults<StageStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
    }

    @Override
    public StageStatus getItem(int position) {
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
            StageStatus taskStageStatus;
            if (adapterData != null) {
                taskStageStatus = adapterData.get(position);
                textView.setText(taskStageStatus.getTitle());
            }
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
            StageStatus taskStageStatus;
            if (adapterData != null) {
                taskStageStatus = adapterData.get(position);
                if (taskStageStatus != null) {
                    viewHolder.title.setText(taskStageStatus.getTitle());
                    viewHolder.uuid.setText(taskStageStatus.getUuid());
                }
            }
            //TODO сопоставление изображений
            //viewHolder.icon.setImageResource(R.drawable.img_3);
            return convertView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }
}