package com.example.l.seshatmvp.repositories.imp;

import android.content.Context;

import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.repositories.LessonRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.example.l.seshatmvp.Utils.WordUtils.form_word;

public class ImpLessonRepository implements LessonRepository{
    Context context;
    FilesRepositoryImp filesRepositoryImp;
    private HashMap<String, Boolean> achievedWords;
    private ArrayList<Character> achievedChars;

    public ImpLessonRepository(Context context){
        this.context = context;
        filesRepositoryImp = new FilesRepositoryImp(context);
    }

    //read all lessons and words in each lesson to lesson hashMap
    @Override
    public Map<Integer, Word[]> getLessons() {
        Map<Integer, Word[]> lessons = new HashMap<>();
        try {
            loadAchievedWords();
            loadAchievedChars();
            FileReader phraseReader = new FileReader(filesRepositoryImp.getPhrasesFilePath());
            BufferedReader PhrasesBufferedReader = new BufferedReader(phraseReader);

            int k = 0;
            String phrase;
            while ((phrase = PhrasesBufferedReader.readLine())!=null) {

                phrase = removeUnknownCharacters(phrase);

                String[] words = phrase.split(" ");
                int lessonCapacity = words.length;

                Word[] lessonWords = new Word[lessonCapacity];
                for (int i = 0; i < lessonCapacity; i++) {
                    String word_txt = words[i];
                    lessonWords[i] = form_word(word_txt, phrase, filesRepositoryImp.getSF());
                    lessonWords[i].setHalfAchieved(achievedWords.get(word_txt) == null ? false : achievedWords.get(word_txt));
                    lessonWords[i].setAchieved(achievedChars);
                }
                lessons.put(k, lessonWords);
                k++;
            }
            phraseReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
        return lessons;
    }

    private void loadAchievedChars() throws IOException {
        FileReader wordsReader = new FileReader(filesRepositoryImp.getCharsFilePath());
        BufferedReader WordsBufferedReader = new BufferedReader(wordsReader);

        achievedChars = new ArrayList<>();
        String word;
        while ((word = WordsBufferedReader.readLine())!=null) {
            achievedChars.add(word.charAt(0));
        }
    }

    private void loadAchievedWords() throws IOException {
        FileReader wordsReader = new FileReader(filesRepositoryImp.getWordFilePath());
        BufferedReader WordsBufferedReader = new BufferedReader(wordsReader);

        achievedWords = new HashMap<>();
        String word;
        while ((word = WordsBufferedReader.readLine())!=null) {
            achievedWords.put(word, true);
        }
    }

    private String removeUnknownCharacters(String phrase) {
        String correctString = "";
        if(phrase == null)
            return null;
        for(int i=0;i<phrase.length();i++){
            char currentChar = phrase.charAt(i);
            if((currentChar>=0x0620 && currentChar <= 0x064A)||currentChar == ' '){
                correctString+= currentChar;
            }
        }
        return correctString;
    }
}
