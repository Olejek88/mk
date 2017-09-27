package ru.toir.mobile.rfid.driver;

import android.hardware.uhf.magic.IMultiInventoryCallback;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 5/3/17.
 */

public class C5Callback implements IMultiInventoryCallback {

    private final static String TAG = "C5Callback";
    public static final String DRIVER_NAME = "С5 Callback";

    private String[] tagIds;
    private Set<String> foundTagIds;
    private String foundTagId;

    public C5Callback(@NonNull String[] tags) {
        foundTagIds = new HashSet<>();
        tagIds = tags;
    }

    @Override
    public boolean processTag(String readedTagId) {
        Log.e(TAG, "Call callback: " + readedTagId);
        String tagIdCheck = readedTagId.substring(4);

        // ищем метку среди переданных
        if (tagIds.length > 0) {
            for (String tagId : tagIds) {
                if (tagIdCheck.equals(tagId)) {
                    Log.d(TAG, tagIdCheck + " tagId found!!!");
                    // сохраняем в глобальную переменную найденную метку
                    foundTagId = readedTagId;
                    // сигнализируем что можно останавливать поиск и разбор
                    return false;
                }
            }
        } else {
            // просто добавляем все уникальные найденные метки в список
            foundTagIds.add(readedTagId);
        }

        // сигнализируем что нужно продолжать поиск и разбор
        return true;
    }

    public Set<String> getFoundTagIds() {
        return foundTagIds;
    }

    public String getFoundTagId() {
        return foundTagId;
    }
}
