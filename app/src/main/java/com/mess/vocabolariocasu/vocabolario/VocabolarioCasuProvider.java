package com.mess.vocabolariocasu.vocabolario;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by davide on 31/07/15.
 */
public class VocabolarioCasuProvider extends ContentProvider{
    public static final String TAG = VocabolarioCasuProvider.class.getSimpleName();

    public static String AUTHORITY = "com.mess.vocabolariocasu.VocabolarioCasuProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/vocabolario");
    //public static final Uri WORDLIST_URI = Uri.parse("content://"+AUTHORITY+"/vocabolario/*");
    public static final Uri LOADED_URI = Uri.parse("content://"+AUTHORITY+"/loaded");

    private static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+
                        "/vnd.com.mess.vocabolariocasu";
    private static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+
                        "/vnd.com.mess.vocabolariocasu";

    private VocabolarioCasuDatabase dictionary;

    private static final int SEARCH_WORD = 0;
    private static final int GET_WORDS = 1;
    private static final int SEARCH_SUGGEST = 2;
    //private static final int GET_WORD_LIST = 3;
    private static final int REFRESH_SHORTCUT = 3;
    private static final int LOADED = 4;
    private static final int GET_IS_LOADED =5;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(AUTHORITY, "vocabolario/", SEARCH_WORD);
        sUriMatcher.addURI(AUTHORITY, "vocabolario/#", GET_WORDS);
        sUriMatcher.addURI(AUTHORITY, "loaded/",LOADED);
        sUriMatcher.addURI(AUTHORITY, "loaded/#", GET_IS_LOADED);
        //sUriMatcher.addURI(AUTHORITY, "vocabolario/*", GET_WORD_LIST);
        sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
    }

    @Override
    public boolean onCreate() {
        dictionary = new VocabolarioCasuDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)){
            case GET_WORDS:
                return dictionary.getWordById(uri.getLastPathSegment(), projection);
            case SEARCH_WORD:
                if (selectionArgs == null)
                    return dictionary.getWordList();
                    //throw new IllegalArgumentException("selection Args must be provided for uri: "+uri);
                return dictionary.getWordMatches(selectionArgs[0].toLowerCase(),
                                                 new String[]{BaseColumns._ID,
                                                            VocabolarioCasuDatabase.KEY_WORD,
                                                            VocabolarioCasuDatabase.KEY_DEFINITION});
            case SEARCH_SUGGEST:
                if (selectionArgs == null)
                    throw new IllegalArgumentException("selection Args must be provided for uri: "+uri);
                return dictionary.getWordMatches(selectionArgs[0],
                        new String[]{BaseColumns._ID,
                                VocabolarioCasuDatabase.KEY_WORD,
                                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID});
            case LOADED:
            case GET_IS_LOADED:
                return dictionary.getIsLoaded();
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case GET_WORDS:
                return WORDS_MIME_TYPE;
            case SEARCH_WORD:
                return DEFINITION_MIME_TYPE;
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
