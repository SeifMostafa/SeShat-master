package com.example.l.seshatmvp.repositories.imp;

import android.content.Context;
import android.os.Environment;

import com.example.l.seshatmvp.Utils.SharedPreferenceUtils;

import java.io.File;

//this class made to facilitate the accessibility to the data stored in sharedPreference
public class FilesRepositoryImp {

    Context context;
    SharedPreferenceUtils sharedPreferenceUtils;
    private File fileLessonsAchieved;
    private String charsFilePath;


    public FilesRepositoryImp(Context context){
        this.context = context;
        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(context);
    }

    public String getWordFilePath(){
        return sharedPreferenceUtils.getStringValue("WordsFilePath", Environment.getExternalStorageDirectory()+"/SeShatSF/WORDS.txt");
    }

    public String getPhrasesFilePath(){
        return sharedPreferenceUtils.getStringValue("PhrasesFilePath", Environment.getExternalStorageDirectory()+"/SeShatSF/PHRASES.txt");
    }
    public String getSF(){
        return sharedPreferenceUtils.getStringValue("SF", Environment.getExternalStorageDirectory()+"/SeShatSF/");
    }
    public String getFileWordsAchieved(){
        return sharedPreferenceUtils.getStringValue("FileWordsAchieved", Environment.getExternalStorageDirectory()+"/SeShatSF/archive.txt");
    }

    public String getFileLessonsAchieved() {
        return sharedPreferenceUtils.getStringValue("FileLessonsAchieved", Environment.getExternalStorageDirectory()+"/SeShatSF/archive_lessons.txt");
    }

    public String getCharsFilePath() {
        return sharedPreferenceUtils.getStringValue("FileCharsAchieved", Environment.getExternalStorageDirectory()+"/SeShatSF/archive_chars.txt");
    }

}
