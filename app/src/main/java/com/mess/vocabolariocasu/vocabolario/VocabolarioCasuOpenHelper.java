package com.mess.vocabolariocasu.vocabolario;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.mess.vocabolariocasu.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by davide on 31/07/15.
 */
public class VocabolarioCasuOpenHelper extends SQLiteOpenHelper {
    private final static String TAG = VocabolarioCasuOpenHelper.class.getSimpleName();

    private static final String CREATE_TABLE_VOCABOLARIO ="CREATE VIRTUAL TABLE "
            + VocabolarioCasuDatabase.TABLE_VOCABOLARIO +
            " USING fts3 (" + VocabolarioCasuDatabase.KEY_WORD +
            ", " + VocabolarioCasuDatabase.KEY_DEFINITION + ");";
    private static final String CREATE_TABLE_LOADED ="CREATE VIRTUAL TABLE " +
            VocabolarioCasuDatabase.TABLE_LOADED + " USING fts3 ( " +
            VocabolarioCasuDatabase.IS_LOADED + ");";

    private Context context;
    private SQLiteDatabase database;

    public VocabolarioCasuOpenHelper(Context context){
        super(context, VocabolarioCasuDatabase.DATABASE_NAME, null, VocabolarioCasuDatabase.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        database = db;
        database.execSQL(CREATE_TABLE_VOCABOLARIO);
        database.execSQL(CREATE_TABLE_LOADED);

        ContentValues values = new ContentValues();
        values.put(VocabolarioCasuDatabase.IS_LOADED,0);
        database.insert(VocabolarioCasuDatabase.TABLE_LOADED,null,values);

        loadDictionary();
    }

    private void loadDictionary() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadWords();
                }catch (IOException e){
                    Log.d(TAG, e.getMessage());
                    throw new RuntimeException();
                }
            }
        }).start();
    }

    private void loadWords() throws IOException{
        final Resources r = context.getResources();
        InputStream inputStream = r.openRawResource(R.raw.vocabolario);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        long startTime;
        try {
            Log.d(TAG, "Starting database loading");
            startTime = System.currentTimeMillis();
            while ((line = br.readLine()) != null){
                String[] splittedLine = TextUtils.split(line, "£");
                if(splittedLine.length < 2)
                    continue;
                long id = addWord(splittedLine[0].trim(), splittedLine[1].trim());
                if (id < 0)
                    Log.e(TAG, "Unable to add word "+splittedLine[0]);
            }
        }finally {
            br.close();

            ContentValues values = new ContentValues();
            values.put(VocabolarioCasuDatabase.IS_LOADED,1);
            database.update(VocabolarioCasuDatabase.TABLE_LOADED, values, null, null);
            Log.d(TAG, "Database Loaded updated");

        }
        long time = System.currentTimeMillis()-startTime;
        Log.d(TAG, "Finished loading words in: "+time/1000+" s");
    }

    private long addWord(String word, String desc) {
        ContentValues values = new ContentValues();
        values.put(VocabolarioCasuDatabase.KEY_WORD, word);
        values.put(VocabolarioCasuDatabase.KEY_DEFINITION, desc);

        return database.insert(VocabolarioCasuDatabase.TABLE_VOCABOLARIO, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+VocabolarioCasuDatabase.TABLE_VOCABOLARIO);
        db.execSQL("DROP TABLE IF EXISTS "+VocabolarioCasuDatabase.TABLE_LOADED);
        onCreate(db);
    }
}
