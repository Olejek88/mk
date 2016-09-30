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
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.TaskStageStatus;
import ru.toir.mobile.db.realm.TaskStages;

/**
 * @author olejek
 * Created by olejek on 14.09.16.
 */
public class TaskStageAdapter extends RealmBaseAdapter<TaskStages> implements ListAdapter {
    public static final String TABLE_NAME = "TaskStages";

    public TaskStageAdapter(@NonNull Context context, RealmResults<TaskStages> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realmDB = Realm.getDefaultInstance();
        RealmResults<TaskStages> rows = realmDB.where(TaskStages.class).findAll();
        return rows.size();
    }

    @Override
    public TaskStages getItem(int position) {
        TaskStages taskStages;
        if (adapterData != null) {
            taskStages = adapterData.get(position);
            return taskStages;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        TaskStages taskStages;
        if (adapterData != null) {
            taskStages = adapterData.get(position);
            return taskStages.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Realm realmDB = Realm.getDefaultInstance();
        TaskStageStatus taskStageStatus;
        String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.taskstage_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tsi_Name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.tsi_Status);
            viewHolder.start_date = (TextView) convertView.findViewById(R.id.tsi_StartDate);
            viewHolder.end_date = (TextView) convertView.findViewById(R.id.tsi_EndDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            TaskStages taskStage = adapterData.get(position);
            viewHolder.title.setText(taskStage.getTaskStageTemplate().getTitle());
            viewHolder.status.setText(taskStage.getTaskStageStatus().getTitle());
            if (taskStage.getStartDate()!=null) {
                viewHolder.start_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(taskStage.getStartDate()));
            }
            if (taskStage.getEndDate()!=null) {
                viewHolder.end_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(taskStage.getEndDate()));
            }

            taskStageStatus = realmDB.where(TaskStageStatus.class).equalTo("uuid",taskStage.getTaskStageStatusUuid()).findFirst();
            pathToImages = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "Android"
                    + File.separator + "data"
                    + File.separator + "ru.toir.mobile"
                    + File.separator + "img"
                    + File.separator;
            File imgFile = new File(pathToImages + taskStageStatus.getIcon());
            if (imgFile.exists() && imgFile.isFile()) {
                Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                        .getAbsolutePath());
                viewHolder.icon.setImageBitmap(mBitmap);
            }
            else {
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
        TextView taskTitle;
        TextView description;
        TextView normative;
        TextView comment;
        TextView taskStageType;
        TextView taskStageStatus;
        TextView taskStageVerdict;
        TextView status;
        ImageView icon;
        ImageView image;
        TextView start_date;
        TextView end_date;
    }
}
