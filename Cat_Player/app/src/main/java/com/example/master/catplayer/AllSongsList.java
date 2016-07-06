package com.example.master.catplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by master on 01.07.2016.
 */
public class AllSongsList extends ListActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_songs_list);

        intent = getIntent();
        ArrayList<String> arrayListSongs = intent.getStringArrayListExtra("songsList");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.song_item, arrayListSongs);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        intent = new Intent();
        intent.putExtra("positionSongs", position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
