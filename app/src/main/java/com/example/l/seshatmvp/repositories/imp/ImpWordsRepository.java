package com.example.l.seshatmvp.repositories.imp;

import android.content.Context;
import android.util.Log;

import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.repositories.WordsRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImpWordsRepository implements WordsRepository{
    Context context;
    FilesRepositoryImp filesRepositoryImp;
    public ImpWordsRepository(Context context){
        this.context = context;
        filesRepositoryImp = new FilesRepositoryImp(context);
    }

    //get finished words from archive
    @Override
    public List<String> readArchiveWords() {
        List<String> words = new ArrayList<>();
        try {
            FileReader reader = new FileReader(filesRepositoryImp.getWordFilePath());
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                Log.i("MainActivity", "readArchiveWords: " + line);
                words.add(line.split(", ")[0]);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    //store written word to archive
    @Override
    public Map<Integer, String[]> readArchiveLessons() {
        Map<Integer,String[]> lessons = new HashMap<>();
        try {
            FileReader reader = new FileReader(filesRepositoryImp.getFileLessonsAchieved());
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            int i=0;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("line "+line);
                Log.i("MainActivity", "readArchiveWords: " + line);
                String[] lessonWords = line.split(", ");
                lessons.put(i++, lessonWords);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    @Override
    public void assignCharAsFinished(Word word, int currentIndex) {
        if (!readArchiveChars().contains(word.getText().charAt(currentIndex))) {
            try {
                FileWriter writer = new FileWriter(filesRepositoryImp.getCharsFilePath(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write((word.getText().charAt(currentIndex)+"") + '\n');
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Character> readArchiveChars() {
        FileReader wordsReader;
        List<Character> achievedChars = new ArrayList<>();
        try {
            wordsReader = new FileReader(filesRepositoryImp.getCharsFilePath());
            BufferedReader WordsBufferedReader = new BufferedReader(wordsReader);

            String word;
            while ((word = WordsBufferedReader.readLine())!=null) {
                achievedChars.add(word.charAt(0));
            }
            return achievedChars;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return achievedChars;
    }

    @Override
    public void assignWordAsFinished(Word word) {
        if (!readArchiveWords().contains(word.getText())) {
            try {
                FileWriter writer = new FileWriter(filesRepositoryImp.getWordFilePath(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(word.getText() + "\n");
                bufferedWriter.close();
                Log.i("MainActivity", "assignWordAsFinished: " + word.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        updateArchivedLessons(word);
    }
//
//    private void updateArchivedLessons(Word word) {
//        String achieved = "";
//
////        boolean lessonFound =false;
//        try {
//            FileReader reader = new FileReader(filesRepositoryImp.getFileLessonsAchieved());
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            String lesson;
//            while((lesson = bufferedReader.readLine())!=null){
//                List<String> lessonWords = Arrays.asList(lesson.split(", "));
//                achieved += lesson;
//                if(lessonWords.get(0).equals(word.getPhrase())) {
//                    if(!lessonWords.contains(word.getText()))
//                        achieved += ", " + word.getText();
////                    lessonFound = true;
//                }
//
//                achieved += '\n';
//            }
//            bufferedReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        if(!lessonFound){
////            achieved += word.getPhrase() + ", " + word.getText() + '\n';
////        }
//        try {
//            FileWriter writer = new FileWriter(filesRepositoryImp.getFileLessonsAchieved(), false);
//            BufferedWriter bufferedWriter = new BufferedWriter(writer);
//            bufferedWriter.write(achieved);
//            bufferedWriter.close();
//            Log.i("MainActivity", "assignWordAsFinished: " + achieved);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
