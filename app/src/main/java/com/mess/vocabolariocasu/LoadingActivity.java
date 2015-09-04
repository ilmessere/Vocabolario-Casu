package com.mess.vocabolariocasu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuDatabase;
import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuProvider;


public class LoadingActivity extends ActionBarActivity {
    private static final String TAG = LoadingActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textView = (TextView)findViewById(R.id.textViewCentral);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void loop(){
        while (!dictionaryLoaded());
        //progressBar.setVisibility(View.GONE);
        //textView.setText("Vocabolary fully loaded");
        startActivity(new Intent(this, WordListActivity.class));
    }
    private boolean dictionaryLoaded() {
        int finishedLoading = 0;
        Cursor cursor = getContentResolver().query(VocabolarioCasuProvider.LOADED_URI,
                null,null,null,null);//cursorLoader.loadInBackground();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(cursor == null){
            Log.e(TAG, "Cursor is null");
        }else if(!cursor.moveToFirst()){
            Log.e(TAG, "Cursor is void");
            cursor.close();
        }else {
            int index = cursor.getColumnIndexOrThrow(VocabolarioCasuDatabase.IS_LOADED);
            finishedLoading = cursor.getInt(index);
            //Log.v(TAG,"loaded: "+finishedLoading);
            cursor.close();
        }

        return finishedLoading==1;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        }).start();
    }
    @Override
    protected void onPause(){
        super.onPause();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
