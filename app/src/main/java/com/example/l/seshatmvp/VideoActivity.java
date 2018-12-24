package com.example.l.seshatmvp;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Random;


public class VideoActivity extends AppCompatActivity {

    VideoView videoView;
    String SF =  Environment.getExternalStorageDirectory() + "/SeShatSF/";
    String TAG = "VideoActivity";
    int max = 1;
    int min = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

       try
       {
           videoView = findViewById(R.id.video_view);
           int random = new Random().nextInt((max - min) + 1);
           String path = SF+"video"+random+(random == 0?".mkv":".mp4");
           videoView.setVideoURI(Uri.parse(path));
           videoView.start();


           videoView.setOnCompletionListener(mediaPlayer -> finish());
       }
       catch (Exception e)
       {
           Log.e(TAG,e.toString());
       }


    }

    public void close(View view) {
        onBackPressed();
    }
}
