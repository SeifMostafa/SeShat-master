package com.example.l.seshatmvp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.l.seshatmvp.Utils.SharedPreferenceUtils;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    AppCompatButton current_lesson_btn, start_lesson_btn;
    SharedPreferenceUtils sharedPreferenceUtils;
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 122;

    private final String SFKEY = "0";
    private String WordsFilePath = "WORDS.txt", PhrasesFilePath = "PHRASES.txt", SF = "/SeShatSF/";
    private String FileWordsAchieved = "archive.txt";
    private String FileLessonsAchieved = "archive_lessons.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initializeData();
        setSharedPreferencesData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

    }

    private void setSharedPreferencesData() {
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);

        //setting file paths
        SF = Environment.getExternalStorageDirectory() + SF;

        WordsFilePath = SF + WordsFilePath;
        PhrasesFilePath = SF + PhrasesFilePath;
        FileWordsAchieved = SF + FileWordsAchieved;
        FileLessonsAchieved = SF + FileLessonsAchieved;

        sharedPreferenceUtils.setValue(SFKEY, SF);
        sharedPreferenceUtils.setValue("SF", SF);
        sharedPreferenceUtils.setValue("WordsFilePath", WordsFilePath);
        sharedPreferenceUtils.setValue("PhrasesFilePath", PhrasesFilePath);
        sharedPreferenceUtils.setValue("FileWordsAchieved", FileWordsAchieved);
        sharedPreferenceUtils.setValue("FileLessonsAchieved", FileLessonsAchieved);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.INTERNET) + ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.INTERNET) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.RECORD_AUDIO)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions to upload profile photo",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        v -> requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},
                                PERMISSIONS_MULTIPLE_REQUEST)).show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean RecordAudioPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean write_storagePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean read_storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (!(RecordAudioPermission && InternetPermission && write_storagePermission && read_storagePermission)) {
                        Log.i("onRequestPermResult", "" + RecordAudioPermission + "," + InternetPermission + "," + write_storagePermission + "," + read_storagePermission);
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to be able to work",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                v -> requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                                                Manifest.permission.RECORD_AUDIO},
                                        PERMISSIONS_MULTIPLE_REQUEST)).show();
                    }
                }
                break;
        }
    }

    private void initializeData()
    {
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);
        String word = sharedPreferenceUtils.getStringValue("word_for_splash_screen","انا");

        current_lesson_btn = findViewById(R.id.current_lesson_btn);
        current_lesson_btn.setText(word);
        current_lesson_btn.animate().alpha(1f).setDuration(1500);
        current_lesson_btn.setOnClickListener(view ->{
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("load last word",true);
            intent.putExtra("last word",word);
            startActivity(intent);
        });

        start_lesson_btn = findViewById(R.id.start_lesson_btn);
        start_lesson_btn.animate().alpha(1f).setDuration(1500);
        start_lesson_btn.setOnClickListener(view ->{
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("load last word",false);
            startActivity(intent);
        });
        setFaceType(current_lesson_btn);
        setFaceType(start_lesson_btn);
    }
    private void setFaceType(Button button)
    {
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Droid Sans Arabic.ttf");
        button.setTypeface(typeface);
    }
}
