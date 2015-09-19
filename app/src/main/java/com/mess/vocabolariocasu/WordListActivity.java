package com.mess.vocabolariocasu;

import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuDatabase;
import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuProvider;


/**
 * An activity representing a list of Words. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WordDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WordListFragment} and the item details
 * (if present) is a {@link WordDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link WordListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class WordListActivity extends ActionBarActivity
        implements WordListFragment.Callbacks {
    public static final String TAG = WordListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        if(!dictionaryIsLoaded()){
            startActivity(new Intent(this, LoadingActivity.class));
        }
        if (findViewById(R.id.word_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            Log.d(TAG,"Two pane mode on");
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((WordListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.word_list))
                    .setActivateOnItemClick(true);
        }
        Log.d(TAG, "Two pane mode off");

        handleIntent(getIntent());
    }

    private boolean dictionaryIsLoaded() {
        int finishedLoading = 0;
        CursorLoader cursorLoader = new CursorLoader(getBaseContext(),
                VocabolarioCasuProvider.LOADED_URI,null,null,null,null);
        Cursor cursor = cursorLoader.loadInBackground();
        if(cursor == null){
            Log.d(TAG, "Cursor is null");
        }else if(!cursor.moveToFirst()){
            cursor.close();
        }else {
            int index = cursor.getColumnIndexOrThrow(VocabolarioCasuDatabase.IS_LOADED);
            finishedLoading = cursor.getInt(index);
            Log.d(TAG,"loaded: "+finishedLoading);
            cursor.close();
        }

        return finishedLoading==1;
    }

    public void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent() called");
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG,"Searched: " + query);
            CursorLoader cursorLoader = new CursorLoader(getBaseContext(),
                    VocabolarioCasuProvider.CONTENT_URI,
                    new String[]{BaseColumns._ID},
                    null,
                    new String[]{query},
                    null
                    );
            Cursor cursor = cursorLoader.loadInBackground();
            if(cursor != null) {
                if (cursor.getCount() == 1) {
                    int index = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                    int id = cursor.getInt(index);
                    onItemSelected(Integer.toString(id));
                }else if( cursor.getCount() > 1){
                    int index = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                    int id = cursor.getInt(index);
                    ((WordListFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.word_list))
                            .getListView()
                            .setSelection(id-1);
                            //smoothScrollToPosition(id);
                } else {
                    Toast.makeText(getBaseContext(), "Not Found", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(),"Not Found", Toast.LENGTH_SHORT).show();
            }
        }else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            Uri data = intent.getData();
            String id = data.getLastPathSegment();
            Log.d(TAG, "Selected: " + id);
            onItemSelected(id);
        }
    }

    /**
     * Callback method from {@link WordListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        Log.d(TAG, "onItemSelected() called, id = " + id);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(WordDetailFragment.ARG_ITEM_ID, id);
            WordDetailFragment fragment = new WordDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.word_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, WordDetailActivity.class);
            detailIntent.putExtra(WordDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }
}
