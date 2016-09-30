package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.Tasks;

/**
 * @author olejek
 * Created by olejek on 13.09.16.
 */
public class TaskAdapter extends RealmBaseAdapter<Tasks> implements ListAdapter {
    public static final String TABLE_NAME = "Tasks";

    public TaskAdapter(@NonNull Context context, RealmResults<Tasks> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
    }

    @Override
    public Tasks getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Tasks task;
        if (adapterData != null) {
            task = adapterData.get(position);
            return task.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Realm realmDB = Realm.getDefaultInstance();
        TaskStatus taskStatus;
        String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_item_n, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.ti_Name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.ti_Status);
            viewHolder.equipment = (TextView) convertView.findViewById(R.id.ti_Equipment);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ti_ImageStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            Tasks task = adapterData.get(position);
            viewHolder.title.setText(task.getTaskTemplate().getTitle());
            //viewHolder.status.setText(task.getTaskStatus().getTitle());
            viewHolder.equipment.setText(task.getEquipment().getTitle());

            taskStatus = realmDB.where(TaskStatus.class).equalTo("uuid", task.getTaskStatusUuid()).findFirst();
            pathToImages = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "Android"
                    + File.separator + "data"
                    + File.separator + "ru.toir.mobile"
                    + File.separator + "img"
                    + File.separator;
            File imgFile = new File(pathToImages + taskStatus.getIcon());
            if (imgFile.exists() && imgFile.isFile()) {
                Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                        .getAbsolutePath());
                viewHolder.icon.setImageBitmap(mBitmap);
            } else {
                imgFile = new File(pathToImages + "help_32.png");
                if (imgFile.exists() && imgFile.isFile()) {
                    Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                            .getAbsolutePath());
                    viewHolder.icon.setImageBitmap(mBitmap);
                }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        TextView status;
        ImageView icon;
    }
}
