package com.example.master.catplayer;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class Mp3Filter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String filename) {
        return (filename.endsWith(".mp3"));
    }
}

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String SD_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    private String[] STAR = {"*"};
    private Handler myHandler = new Handler();
    private ImageButton imageButtonPlay;
    private ImageButton imageButtonNext;
    private ImageButton imageButtonPrev;
    private TextView textViewCurentTime;
    private TextView textViewAllTime;
    private TextView textViewSoundName;
    private ImageView imageViewAlbom;
    private SeekBar seekBarSound;

    private long allTime = 0;
    private long curentTime = 0;
    private ArrayList<String> songs = new ArrayList<String>();
    private int positionSongs = 0;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean resume = false;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileWork fileWork = new FileWork(SD_PATH, songs);
        fileWork.updatePlayList();

        textViewCurentTime = (TextView) findViewById(R.id.textViewSoundTime);
        textViewAllTime = (TextView) findViewById(R.id.textViewSoundAllTime);
        textViewSoundName = (TextView) findViewById(R.id.textViewSound);

        imageButtonPlay = (ImageButton) findViewById(R.id.imageButtonPlay);
        imageButtonNext = (ImageButton) findViewById(R.id.imageButtonNext);
        imageButtonPrev = (ImageButton) findViewById(R.id.imageButtonPrev);
        imageButtonPlay.setTag(R.drawable.image_play);
        imageButtonPlay.setOnClickListener(this);
        imageButtonNext.setOnClickListener(this);
        imageButtonPrev.setOnClickListener(this);

        seekBarSound = (SeekBar) findViewById(R.id.seekBarSound);
        seekBarSound.setClickable(true);
        seekBarSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                resume(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonPlay:
                Integer image = (Integer)imageButtonPlay.getTag();
                if(image == R.drawable.image_play) {
                    if(resume) {
                        resume(length);
                        resume = false;
                    }else {
                        play(positionSongs);
                    }
                }else{
                    mediaPlayer.pause();
                    length = mediaPlayer.getCurrentPosition();
                    imageButtonPlay.setImageResource(R.drawable.image_play);
                    imageButtonPlay.setTag(R.drawable.image_play);
                    resume = true;
                }
                break;
            case R.id.imageButtonNext:
                if(positionSongs < songs.size()) {
                    mediaPlayer.stop();
                    positionSongs++;
                    play(positionSongs);
                    resume = false;
                }
                break;
            case R.id.imageButtonPrev:
                if(positionSongs > 0) {
                    mediaPlayer.stop();
                    positionSongs--;
                    play(positionSongs);
                    resume = false;
                }
                break;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.allSongs:
                Intent intent = new Intent(MainActivity.this, AllSongsList.class);
                intent.putStringArrayListExtra("songsList", songs);
                startActivityForResult(intent, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mediaPlayer.stop();
        if(resultCode == RESULT_OK){
            positionSongs = data.getIntExtra("positionSongs", positionSongs);
            resume = false;
            play(positionSongs);
        }
    }

    private void setTimeSong(MediaPlayer timeSong){
        curentTime = timeSong.getCurrentPosition();
        textViewCurentTime.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) curentTime),
                TimeUnit.MILLISECONDS.toSeconds((long) curentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) curentTime))));
        allTime = timeSong.getDuration();
        textViewAllTime.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) allTime),
                TimeUnit.MILLISECONDS.toSeconds((long) allTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) allTime))));

        seekBarSound.setProgress((int)curentTime);
        seekBarSound.setMax((int)allTime);
    }


    private Runnable updateSongTime = new Runnable() {
        public void run() {
            curentTime = mediaPlayer.getCurrentPosition();
            textViewCurentTime.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) curentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) curentTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) curentTime))));
            Log.d("!!!!!!", curentTime + " " + allTime);
            if((allTime - curentTime) < 500){
                if(positionSongs < songs.size() - 1) {
                    positionSongs++;
                    play(positionSongs);
                }else{
                    positionSongs = 0;
                }
            }
            seekBarSound.setProgress((int) curentTime);
            myHandler.postDelayed(this, 100);
        }
    };

    private void play(int positionSongs){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(SD_PATH + songs.get(positionSongs));
            mediaPlayer.prepare();
            setTimeSong(mediaPlayer);
            textViewSoundName.setText(songs.get(positionSongs));
            mediaPlayer.start();
            imageButtonPlay.setImageResource(R.drawable.image_pouse);
            imageButtonPlay.setTag(R.drawable.image_pouse);
            myHandler.postDelayed(updateSongTime,100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resume(int length){
        mediaPlayer.seekTo(length);
        imageButtonPlay.setImageResource(R.drawable.image_pouse);
        imageButtonPlay.setTag(R.drawable.image_pouse);
        mediaPlayer.start();
    }
}
