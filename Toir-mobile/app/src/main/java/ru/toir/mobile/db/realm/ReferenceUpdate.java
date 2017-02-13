package ru.toir.mobile.db.realm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 26.01.17.
 */

public class ReferenceUpdate extends RealmObject {
    @PrimaryKey
    private String referenceName;
    private Date updateDate;

    public static Date lastChanged(String name) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReferenceUpdate> list = realm.where(ReferenceUpdate.class).equalTo("referenceName", name).findAll();
        if (list.isEmpty()) {
            return new Date(0);
        } else {
            return list.first().updateDate;
        }
    }

    public static String lastChangedAsStr(String name) {
        String result = null;
        Date date = ReferenceUpdate.lastChanged(name);
        result = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(date);
        return result;
    }

    /**
     * @param referenceName Название справочника (используем имя класса).
     * @param list          Список объектов справочника для сохранения в базу.
     * @param updateDate    Дата обновления содержимого справочника.
     */
    public static void saveReferenceData(String referenceName, List list, Date updateDate) {
        Realm realm = Realm.getDefaultInstance();

        ReferenceUpdate item = new ReferenceUpdate();
        item.setReferenceName(referenceName);
        item.setUpdateDate(updateDate);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(list);
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    /**
     * @param referenceName Название справочника (используем имя класса).
     * @param updateDate    Дата обновления содержимого справочника.
     */
    public static void saveReferenceData(String referenceName, Date updateDate) {
        Realm realm = Realm.getDefaultInstance();

        ReferenceUpdate item = new ReferenceUpdate();
        item.setReferenceName(referenceName);
        item.setUpdateDate(updateDate);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}