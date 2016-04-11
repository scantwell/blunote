package com.drexelsp.blunote.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.drexelsp.blunote.provider.MetaStoreContract.Album;
import com.drexelsp.blunote.provider.MetaStoreContract.Artist;
import com.drexelsp.blunote.provider.MetaStoreContract.Track;

/**
 * Created by scantwell on 3/10/2016.
 */

public final class MetaStore extends ContentProvider {

    private static final int ALBUM_LIST = 1;
    private static final int ALBUM_ID = 2;
    private static final int ARTIST_LIST = 3;
    private static final int ARTIST_ID = 4;
    private static final int TRACK_LIST = 5;
    private static final int TRACK_ID = 6;
    private static final UriMatcher URI_MATCHER;
    private MetaStoreOpenHelper mHelper = null;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(MetaStoreContract.AUTHORITY, "album", ALBUM_LIST);
        URI_MATCHER.addURI(MetaStoreContract.AUTHORITY, "album/#", ALBUM_ID);
        URI_MATCHER.addURI(MetaStoreContract.AUTHORITY, "artist", ARTIST_LIST);
        URI_MATCHER.addURI(MetaStoreContract.AUTHORITY, "artist/#", ARTIST_ID);
        URI_MATCHER.addURI(MetaStoreContract.AUTHORITY, "track", TRACK_LIST);
        URI_MATCHER.addURI(MetaStoreContract.AUTHORITY, "track/#", TRACK_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new MetaStoreOpenHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues values[]) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_LIST:
                db.beginTransaction();
                for (int i = 0; i < values.length; ++i) {
                    long id =
                            db.insert(
                                    DbSchema.TBL_ALBUM,
                                    null,
                                    values[i]);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case ARTIST_LIST:
                db.beginTransaction();
                for (int i = 0; i < values.length; ++i) {
                    long id =
                            db.insert(
                                    DbSchema.TBL_ARTIST,
                                    null,
                                    values[i]);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case TRACK_LIST:
                db.beginTransaction();
                for (int i = 0; i < values.length; ++i) {
                    long id =
                            db.insert(
                                    DbSchema.TBL_TRACK,
                                    null,
                                    values[i]);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_LIST:
                builder.setTables(DbSchema.TBL_ALBUM);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Album.SORT_ORDER_DEFAULT;
                }
                break;
            case ALBUM_ID:
                builder.setTables(DbSchema.TBL_ALBUM);
                // limit query to one row at most:
                builder.appendWhere(Album._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case ARTIST_LIST:
                builder.setTables(DbSchema.TBL_ARTIST);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Artist.SORT_ORDER_DEFAULT;
                }
                break;
            case ARTIST_ID:
                builder.setTables(DbSchema.TBL_ARTIST);
                // limit query to one row at most:
                builder.appendWhere(Artist._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case TRACK_LIST:
                builder.setTables(DbSchema.TBL_TRACK);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Track.SORT_ORDER_DEFAULT;
                }
                break;
            case TRACK_ID:
                builder.setTables(DbSchema.TBL_TRACK);
                // limit query to one row at most:
                builder.appendWhere(Track._ID + " = " +
                        uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    MetaStoreContract.CONTENT_URI);
        } else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_LIST:
                return MetaStoreContract.Album.CONTENT_TYPE;
            case ALBUM_ID:
                return MetaStoreContract.Album.CONTENT_ITEM_TYPE;
            case ARTIST_LIST:
                return MetaStoreContract.Artist.CONTENT_TYPE;
            case ARTIST_ID:
                return MetaStoreContract.Artist.CONTENT_ITEM_TYPE;
            case TRACK_LIST:
                return MetaStoreContract.Track.CONTENT_TYPE;
            case TRACK_ID:
                return MetaStoreContract.Track.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (URI_MATCHER.match(uri) == ALBUM_LIST) {
            long id =
                    db.insert(
                            DbSchema.TBL_ALBUM,
                            null,
                            values);
            return getUriForId(id, uri);
        } else if (URI_MATCHER.match(uri) == ARTIST_LIST) {
            long id =
                    db.insert(
                            DbSchema.TBL_ARTIST,
                            null,
                            values);
            return getUriForId(id, uri);
        } else if (URI_MATCHER.match(uri) == TRACK_LIST) {
            long id =
                    db.insert(
                            DbSchema.TBL_TRACK,
                            null,
                            values);
            return getUriForId(id, uri);
        } else {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int delCount = 0;
        String idStr;
        String where;
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_LIST:
                delCount = db.delete(
                        DbSchema.TBL_ALBUM,
                        selection,
                        selectionArgs);
                break;
            case ALBUM_ID:
                idStr = uri.getLastPathSegment();
                where = Album._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        DbSchema.TBL_ALBUM,
                        where,
                        selectionArgs);
                break;
            case ARTIST_LIST:
                delCount = db.delete(
                        DbSchema.TBL_ARTIST,
                        selection,
                        selectionArgs);
                break;
            case ARTIST_ID:
                idStr = uri.getLastPathSegment();
                where = Artist._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        DbSchema.TBL_ARTIST,
                        where,
                        selectionArgs);
                break;
            case TRACK_LIST:
                delCount = db.delete(
                        DbSchema.TBL_TRACK,
                        selection,
                        selectionArgs);
                break;
            case TRACK_ID:
                idStr = uri.getLastPathSegment();
                where = Track._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        DbSchema.TBL_TRACK,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for deleting photos or entities
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount = 0;
        String idStr;
        String where;
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_LIST:
                updateCount = db.update(
                        DbSchema.TBL_ALBUM,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ALBUM_ID:
                idStr = uri.getLastPathSegment();
                where = Album._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        DbSchema.TBL_ALBUM,
                        values,
                        where,
                        selectionArgs);
                break;
            case ARTIST_LIST:
                updateCount = db.update(
                        DbSchema.TBL_ARTIST,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ARTIST_ID:
                idStr = uri.getLastPathSegment();
                where = Artist._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        DbSchema.TBL_ARTIST,
                        values,
                        where,
                        selectionArgs);
                break;
            case TRACK_LIST:
                updateCount = db.update(
                        DbSchema.TBL_TRACK,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TRACK_ID:
                idStr = uri.getLastPathSegment();
                where = Track._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        DbSchema.TBL_TRACK,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
}
