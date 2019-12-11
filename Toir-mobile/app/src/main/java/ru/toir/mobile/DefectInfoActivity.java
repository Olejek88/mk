package ru.toir.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.MediaFile;

import static ru.toir.mobile.fragments.OrderFragment.copyFile;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

public class DefectInfoActivity extends AppCompatActivity {
    private final static String TAG = "DefectInfoActivity";
    private static final int ACTIVITY_PHOTO = 100;
    private static final int ACTIVITY_VIDEO = 101;

    private static final int DRAWER_EXIT = 14;
    private static String defect_uuid;
    private static String equipment_uuid;
    private String photoFilePath;
    private String currentEntityUuid;

    private Realm realmDB;
    private ImageView tv_defect_image;
    private TextView tv_defect_text_name;
    private TextView tv_defect_text_type;
    private TextView tv_equipment_text_uuid;
    private TextView tv_defect_user_name;
    private TextView tv_defect_text_date;
    private TextView tv_defect_text_status;
    private TextView tv_defect_comment;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString("defect_uuid") != null) {
            defect_uuid = b.getString("defect_uuid");
        } else {
            finish();
            return;
        }

        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv_defect_image = findViewById(R.id.defect_image);
        tv_defect_text_name = findViewById(R.id.defect_text_name);
        tv_defect_text_type = findViewById(R.id.defect_text_type);
        tv_equipment_text_uuid = findViewById(R.id.equipment_text_uuid);
        tv_defect_user_name = findViewById(R.id.defect_user_name);
        tv_defect_text_date = findViewById(R.id.defect_text_date);
        tv_defect_text_status = findViewById(R.id.defect_text_status);
        tv_defect_comment = findViewById(R.id.defect_comment);
        //fab_goto_equipment = findViewById(R.id.fab_goto_equipment);

        initView();
    }

    private void initView() {
        final Defect defect = realmDB.where(Defect.class).equalTo("uuid", defect_uuid).findFirst();
        if (defect == null) {
            Toast.makeText(getApplicationContext(), "Дефект не найден", Toast.LENGTH_LONG).show();
            return;
        }
        equipment_uuid = defect.getEquipment().getUuid();

        // TODO когда будет сущность с изображениями - вставить сюда
        //tv_defect_image.setImageResource();

        tv_defect_text_name.setText("Дефект #".concat(String.valueOf(defect.get_id())));
        tv_defect_text_type.setText(defect.getDefectType().getTitle());
        tv_equipment_text_uuid.setText(defect.getEquipment().getTitle());
        tv_defect_user_name.setText(defect.getUser().getName());
        if (defect.getDate() != null) {
            tv_defect_text_date.setText(DateFormat.getDateTimeInstance().format(defect.getDate()));
        } else {
            tv_defect_text_date.setText("-");
        }
        if (defect.isProcess()) {
            tv_defect_text_status.setText(R.string.defect_status_processed);
        } else {
            tv_defect_text_status.setText(R.string.defect_status_non_processed);
        }
        tv_defect_comment.setText(defect.getComment());

        findViewById(R.id.fab_add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null) {
                    return;
                }
                File file = null;
                try {
                    file = createMediaFile("img", ".jpg");
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (file != null) {
                    photoFilePath = file.getAbsolutePath();
                    currentEntityUuid = defect_uuid;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        startActivityForResult(intent, ACTIVITY_PHOTO);
                    } else {
                        Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(intent, ACTIVITY_PHOTO);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, ACTIVITY_PHOTO);
                        }
                    }
                }
            }
        });

        findViewById(R.id.fab_add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null) {
                    return;
                }
                File file = null;
                try {
                    file = createMediaFile("vid", ".mp4");
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (file != null) {
                    photoFilePath = file.getAbsolutePath();
                    currentEntityUuid = defect_uuid;
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        startActivityForResult(intent, ACTIVITY_VIDEO);
                    } else {
                        Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(intent, ACTIVITY_VIDEO);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, ACTIVITY_VIDEO);
                        }
                    }
                }
            }
        });

        findViewById(R.id.fab_goto_equipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent equipmentInfo = new Intent(context, EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("equipment_uuid", equipment_uuid);
                equipmentInfo.putExtras(bundle);
                context.startActivity(equipmentInfo);
            }
        });
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.defect_layout);

        AccountHeader headerResult;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle(R.string.subtitle_repair);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.login_header)
                .withSavedInstance(savedInstanceState)
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            System.exit(0);
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        if (savedInstanceState == null) {
            result.setSelection(21, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }

    private File createMediaFile(String type, String extension) throws IOException {
        if (context == null) {
            return null;
        }
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = type + "_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    // получаем штатными средствами последний снятый кадр в системе
                    String fromFilePath = getLastPhotoFilePath();
                    File fromFile = new File(fromFilePath);

                    // имя файла для сохранения
                    SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.US);
                    StringBuilder fileName = new StringBuilder();
                    fileName.append(currentEntityUuid);
                    fileName.append('-');
                    fileName.append(format.format(new Date()));
                    String extension = fromFilePath.substring(fromFilePath.lastIndexOf('.'));
                    fileName.append(extension);

                    // создаём объект файла фотографии для операции
                    MediaFile mediaFile = new MediaFile();
                    mediaFile.set_id(MediaFile.getLastId() + 1);
                    mediaFile.setEntityUuid(currentEntityUuid);
                    format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    mediaFile.setPath(MediaFile.getImageRoot() + "/" + format.format(mediaFile.getCreatedAt()));
                    mediaFile.setName(fileName.toString());
                    File picDir = getApplicationContext()
                            .getExternalFilesDir(mediaFile.getPath());
                    if (picDir == null) {
                        // какое-то сообщение пользователю что не смогли "сохранить" результат
                        // фотофиксации?
                        return;
                    }

                    if (!picDir.exists()) {
                        if (!picDir.mkdirs()) {
                            Log.d(TAG, "Required media storage does not exist");
                            return;
                        }
                    }

                    File toFile = new File(picDir, mediaFile.getName());
                    try {
                        copyFile(fromFile, toFile);
                        if (!fromFile.delete()) {
                            Log.d(TAG, "Не удалось удалить исходный файл");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
                        getResizedBitmap(toFile.getParent(), toFile.getName(), 1024, 0, new Date().getTime());
                    } catch (Exception e) {
                        Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }

                    // добавляем запись о полученном файле
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealm(mediaFile);
                    realm.commitTransaction();
                    realm.close();
                }

                break;

            default:
                break;
        }
    }

    public String getLastPhotoFilePath() {
        String result;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = getContentResolver();
            String orderBy = android.provider.MediaStore.Video.Media.DATE_TAKEN + " DESC";
            Cursor cursor = resolver.query(uri, projection, null, null, orderBy);
            // TODO: реализовать удаление записи о фотке котрую мы "забрали"
            //resolver.delete(uri,);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index_data);
                cursor.close();
            } else {
                result = null;
            }
        } else {
            result = photoFilePath;
        }

        return result;
    }
}