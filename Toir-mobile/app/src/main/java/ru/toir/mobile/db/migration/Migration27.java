package ru.toir.mobile.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;
import ru.toir.mobile.db.realm.Operation;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 12/11/17.
 */

public class Migration27 implements IToirMigration {

    @Override
    public void migration(DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 26");
        RealmSchema schema = realm.getSchema();
        schema.get("Operation").removeField("taskStage");
    }
}
