package com.mess.vocabolariocasu.word;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuDatabase;
import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuProvider;

/**
 * Created by davide on 31/07/15.
 */
public class WordContent {

    public static Cursor getItem(String id, Activity activity){
        Uri uri = Uri.withAppendedPath(VocabolarioCasuProvider.CONTENT_URI, String.valueOf(id));
        Cursor cursor = activity.getContentResolver()
                .query( uri,
                        new String[]{VocabolarioCasuDatabase.KEY_WORD,VocabolarioCasuDatabase.KEY_DEFINITION},
                        null,
                        null,
                        null);
        if(cursor == null) {
            return null;
        }else if(!cursor.moveToFirst()) {
            return null;
        }
        return cursor;
    }

}
