package com.example.l.seshatmvp.presenter;

import android.util.Log;

import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.repositories.WordsRepository;
import com.example.l.seshatmvp.view.MainActivityView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class WordPresenter {

    private MainActivityView mainActivityView;
    private WordsRepository wordsRepository;

    //the constructor take 2 parameter the view and the repository to make an access between them
    public WordPresenter(MainActivityView mainActivityView, WordsRepository wordsRepository) {
        this.mainActivityView = mainActivityView;
        this.wordsRepository = wordsRepository;
    }
    //call wordRepository interface to read words from archive file
    public void loadArchiveWords(){
        List<String> words = wordsRepository.readArchiveWords();
        if(words.isEmpty()){
            mainActivityView.DisplayNoArchiveWords();
        }else {
            mainActivityView.DisplayArchiveWords(words);
        }
    }

//    public void loadArchiveLessons() {
//        Map<Integer, String[]> lessons = wordsRepository.readArchiveLessons();
//        if(lessons.isEmpty()){
//            mainActivityView.DisplayNoArchiveWords();
//        }else {
//            mainActivityView.DisplayArchiveLessons(lessons);
//        }
//    }
    ////call wordRepository interface to save word to archive file

    public void saveWordstoArchive(Word word){
        try {
            System.out.println(word.getText()+" is saved");
            wordsRepository.assignWordAsFinished(word);
            Log.i(TAG, "saveWordstoArchive: Success");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveCharsToArchive(Word word, int currentIndex) {
        wordsRepository.assignCharAsFinished(word, currentIndex);
    }
}
