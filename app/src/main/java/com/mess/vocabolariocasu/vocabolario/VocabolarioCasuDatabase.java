package com.mess.vocabolariocasu.vocabolario;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Created by davide on 31/07/15.
 */
public class VocabolarioCasuDatabase {
    public static final String TAG = VocabolarioCasuDatabase.class.getSimpleName();

    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String IS_LOADED = "is_loaded";

    protected static final int DATABASE_VERSION = 6;
    protected static final String DATABASE_NAME = "Vocabolario Casu";
    protected static final String TABLE_VOCABOLARIO = "Vocabolario";
    protected static final String TABLE_LOADED = "Loaded";

    private final VocabolarioCasuOpenHelper openHelper;
    private static final HashMap<String,String> columnMap = buildColumnMap();

    /**
     * Builds a map for all columns that may be requested, which will be given to the
     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
     * all columns, even if the value is the key. This allows the ContentProvider to request
     * columns w/o the need to know real column names and create the alias itself.
     */
    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_WORD,KEY_WORD);
        map.put(KEY_DEFINITION, KEY_DEFINITION);
        map.put(BaseColumns._ID, "rowid AS "+ BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    public VocabolarioCasuDatabase(Context context) {
        openHelper = new VocabolarioCasuOpenHelper(context);
    }

    public Cursor getIsLoaded(){
        return openHelper.getReadableDatabase().rawQuery("SELECT * FROM Loaded",null);
    }

    public Cursor getWordById(String rowId, String[] col){
        String selection = "rowid = ?";
        String[] selArgs = new String[]{rowId};

        return query(selection,selArgs,col);
    }

    public Cursor getWordMatches(String query, String[] col){
        String selection = KEY_WORD + " MATCH ?";
        String[] selArgs = new String[] {query+"*"};
        return query(selection,selArgs,col);
    }

    public Cursor getWordList(){
        return query(null,null, new String[]{BaseColumns._ID, KEY_WORD});
    }

    public Cursor query(String selection, String[] selectionArgs, String[] col){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_VOCABOLARIO);
        queryBuilder.setProjectionMap(columnMap);

        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, col, selection, selectionArgs, null, null, null);
        if(cursor == null){
            return null;
        } else if( !cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        return cursor;
    }
}
