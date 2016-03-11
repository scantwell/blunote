package com.drexelsp.blunote.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by scantwell on 3/10/2016.
 */
class MetaStoreOpenHelper extends SQLiteOpenHelper {
    private static final String NAME = DbSchema.DB_NAME;
    private static final int VERSION = 1;

    public MetaStoreOpenHelper(Context context)
    {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbSchema.DDL_CREATE_TBL_TRACK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbSchema.DDL_DROP_TBL_TRACK);
        onCreate(db);
    }
}
