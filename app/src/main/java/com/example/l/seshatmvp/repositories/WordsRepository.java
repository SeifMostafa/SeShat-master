package com.example.l.seshatmvp.repositories;

import com.example.l.seshatmvp.model.Word;

import java.util.List;
import java.util.Map;

public interface WordsRepository {
    List<String> readArchiveWords();
    void assignWordAsFinished(Word Word);

    Map<Integer, String[]> readArchiveLessons();

    void assignCharAsFinished(Word word, int currentIndex);
}
