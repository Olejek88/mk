package ru.toir.mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.LoadTestData;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private static final String TAG = "ToirSettings";

    private PreferenceScreen drvSettingScr;
    private PreferenceCategory drvSettingCategory;

    private static String appVersion;

    @SuppressWarnings("deprecation")
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = (Toolbar) bar.getChildAt(0);

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }

        setupSimplePreferencesScreen();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        // получаем список драйверов по имени класса
        List<String> driverClassList = new ArrayList<>();
        try {
            DexFile df = new DexFile(getApplicationContext()
                    .getPackageCodePath());
            for (Enumeration<String> iter = df.entries(); iter
                    .hasMoreElements(); ) {
                String classPath = iter.nextElement();
                if (classPath.contains("ru.toir.mobile.rfid.driver")
                        && !classPath.contains("$")) {
                    driverClassList.add(classPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // строим список драйверов с именами и классами
        List<String> drvNames = new ArrayList<>();
        List<String> drvKeys = new ArrayList<>();
        String name;

        for (String classPath : driverClassList) {
            name = RfidDriverBase.getDriverName(classPath);
            if (name != null) {
                drvNames.add(name);
                drvKeys.add(classPath);
            }
        }

        // категория с экраном настроек драйвера
        drvSettingCategory = (PreferenceCategory) SettingsActivity.this.findPreference("drvSettingsCategory");

        // элемент интерфейса со списком драйверов считывателей
        ListPreference drvList = (ListPreference) this.findPreference(getResources().getString(
                R.string.rfidDriverListPrefKey));
        drvSettingScr = (PreferenceScreen) this.findPreference(getResources()
                .getString(R.string.rfidDrvSettingKey));

        // указываем названия и значения для элементов списка
        drvList.setEntries(drvNames.toArray(new String[]{""}));
        drvList.setEntryValues(drvKeys.toArray(new String[]{""}));

        // при изменении драйвера, включаем дополнительный экран с
        // настройками драйвера
        drvList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {

                String value = (String) newValue;
                PreferenceScreen screen = getDrvSettingsScreen(value,
                        drvSettingScr);

                // проверяем есть ли настройки у драйвера
                if (screen != null) {
                    drvSettingCategory.setEnabled(true);
                } else {
                    drvSettingCategory.setEnabled(false);
                }

                return true;
            }
        });

        // проверяем есть ли настройки у драйвера
        String currentDrv = preferences.getString(
                getResources().getString(R.string.rfidDriverListPrefKey), null);
        if (currentDrv != null) {
            if (getDrvSettingsScreen(currentDrv, drvSettingScr) != null) {
                drvSettingCategory.setEnabled(true);
            } else {
                drvSettingCategory.setEnabled(false);
            }
        } else {
            drvSettingCategory.setEnabled(false);
        }

        Preference button = this.findPreference(getString(R.string.load_test_data));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LoadTestData.LoadAllTestData();
                return true;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            //toolbar.setBackgroundColor(getResources().getColor(R.color.larisaBlueColor));
            //toolbar.setSubtitle("Обслуживание и ремонт");
            toolbar.setTitleTextColor(Color.WHITE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        if (preference != null) {
            if (preference instanceof PreferenceScreen) {
                if (((PreferenceScreen) preference).getDialog() != null) {
                    ((PreferenceScreen) preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
                    setUpNestedScreen((PreferenceScreen) preference);
                }
            }
        }

        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        AppBarLayout appBar;

        View listRoot = dialog.findViewById(android.R.id.list);
        ViewGroup mRootView = (ViewGroup) dialog.findViewById(android.R.id.content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent().getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else {
            ListView content = (ListView) mRootView.getChildAt(0);
            mRootView.removeAllViews();

            LinearLayout LL = new LinearLayout(this);
            LL.setOrientation(LinearLayout.VERTICAL);

            ViewGroup.LayoutParams LLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LL.setLayoutParams(LLParams);

            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, mRootView, false);

            LL.addView(appBar);
            LL.addView(content);

            mRootView.addView(LL);
        }

        if (listRoot != null) {
            listRoot.setPadding(0, listRoot.getPaddingTop(), 0, listRoot.getPaddingBottom());
        }

        Toolbar Tbar = (Toolbar) appBar.getChildAt(0);

        Tbar.setTitle(preferenceScreen.getTitle());

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private PreferenceScreen getDrvSettingsScreen(String classPath,
                                                  PreferenceScreen rootScreen) {

        Class<?> driverClass;
        PreferenceScreen screen;

        try {
            // пытаемся получить класс драйвера
            driverClass = Class.forName(classPath);

            // пытаемся создать объект драйвера
            Constructor<?> c = driverClass.getConstructor();
            RfidDriverBase driver = (RfidDriverBase) c.newInstance();

            // передаём драйверу "чистый" экран
            rootScreen.removeAll();

            // пытаемся вызвать метод
            screen = driver.getSettingsScreen(rootScreen);

            // возвращаем результат
            return screen;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        // do what ever you want with this key
        if (key.equals(getString(R.string.serverUrl))) {
            final EditTextPreference URLPreference = (EditTextPreference) findPreference(getString(R.string.serverUrl));
            final AlertDialog dialog = (AlertDialog) URLPreference.getDialog();
            URLPreference.getEditText().setError(null);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String errorMessage;
                            String text = URLPreference.getEditText().getText().toString();
                            try {
                                URL tURL = new URL(text);
                                URLPreference.getEditText().setText(tURL.toString().replaceAll("/+$", ""));
                                errorMessage = null;
                            } catch (MalformedURLException e) {
                                if (!text.isEmpty()) {
                                    errorMessage = "Не верный URL!";
                                } else {
                                    errorMessage = null;
                                }
                            }
                            EditText edit = URLPreference.getEditText();
                            if (errorMessage == null) {
                                edit.setError(null);
                                URLPreference.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                                dialog.dismiss();
                                ToirApplication.serverUrl = edit.getText().toString();
                            } else {
                                edit.setError(errorMessage);
                            }
                        }
                    });
        }
        return true;
    }
}