package com.example.l.seshatmvp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.l.seshatmvp.Utils.SharedPreferenceUtils;
import com.example.l.seshatmvp.layout.AchievementsFragment;
import com.example.l.seshatmvp.layout.AnimationFragment;
import com.example.l.seshatmvp.layout.LessonFragment;
import com.example.l.seshatmvp.layout.PhrasePickFragment;
import com.example.l.seshatmvp.layout.WordAnimationFragment;
import com.example.l.seshatmvp.layout.WordFragment;
import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.presenter.LessonPresenter;
import com.example.l.seshatmvp.presenter.WordPresenter;
import com.example.l.seshatmvp.repositories.imp.ImpLessonRepository;
import com.example.l.seshatmvp.repositories.imp.ImpWordsRepository;
import com.example.l.seshatmvp.view.MainActivityView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.l.seshatmvp.Utils.WordUtils.form_word;
import static com.example.l.seshatmvp.layout.LessonFragment.LessonFragment_TAG;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    public final String WORDS_PREFS_NAME = "WordsPrefsFile";
    public static final String WordIndexKey = "i";
    public static final String WordKey = "w";
    public static final String PhraseKey = "p";
    public static final String LessonKey = "L";
    public static final String RepeatLesson = "repeat_lesson";
    public static String AnimatePhrase="animate_phrase";
    public final String WordLoopKey = "wl";

    private final int PERMISSIONS_MULTIPLE_REQUEST = 122;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static String firstTimekey = "1stTime";

    private String AchievementsFragment_TAG = "achievements_fragment";
    private String WordFragment_TAG = "word_fragment";
    private String firstPhrase = "أنا إسمي ";

    private int subscreensOnTheStack = 0;
    private int lastWordIndex;
    private int word_index = 0;
    private int lesson_index = 1;

    private Map<Integer, Word[]> lessons;

    private SharedPreferenceUtils sharedPreferenceUtils;

    private SpeechRecognizer sr;
    private MediaPlayer mediaPlayer;

    private LessonPresenter presenter;
    private WordPresenter wordpresenter;

    private ImageButton voiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //startActivity(new Intent(this, VideoActivity.class));
        //  sr = SpeechRecognizer.createSpeechRecognizer(this);
        // sr.setRecognitionListener(new listener());
        voiceBtn = findViewById(R.id.imagebutton_voice);
        voiceBtn.setOnClickListener(view -> {
            // Toast.makeText(this, "Talk", Toast.LENGTH_SHORT).show();
            // startVoice();
            promptSpeechInput();
        });
        presenter = new LessonPresenter(this, new ImpLessonRepository(getApplication()));
        wordpresenter = new WordPresenter(this, new ImpWordsRepository(getApplication()));

        //setting lessons
        presenter.loadLessons();

        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startApp();

    }

    @Override
    public void onBackPressed() {
        //fragmentManager.popBackStack("LessonFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("RecognitionListener", "onResults " + result.get(0));
                    Toast.makeText(MainActivity.this, "Result: " + result.get(0), Toast.LENGTH_SHORT).show();

                    //txtSpeechInput.setText(result.get(0));
                }
                break;
            }
        }
    }

    private void startApp() {
        //saving indexes on shared preferences
        sharedPreferenceUtils.setValue(LessonKey, String.valueOf(lesson_index));
        sharedPreferenceUtils.setValue(WordIndexKey, String.valueOf(word_index));
        sharedPreferenceUtils.setValue(firstTimekey, String.valueOf(false));

        if(getIntent().getBooleanExtra("load last word",false)){
            openLessonFragment(new Word(getIntent().getStringExtra("last word")));
        } else if (Boolean.valueOf(sharedPreferenceUtils.getStringValue(firstTimekey, "true"))) {
            lesson_index = 1;
            word_index = 0;
            String tempWord =  sharedPreferenceUtils.getStringValue("word_for_splash_screen",null);
            Word phrase;
            if(tempWord == null)
                 phrase = new Word(firstPhrase + lessons.get(lesson_index)[0].getText());
            else
                phrase = new Word(tempWord);

            sharedPreferenceUtils.setValue("word_for_splash_screen",phrase.getText());
            openLessonFragment(phrase);

//            try {
//                voiceoffer(null, getResources().getString(R.string.myname));
//                sleep(1000);
//                voiceoffer(null, lessons.get(lesson_index)[0].getText());
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //starting lesson
//                        updateLesson(0);
//                    }
//                }, 1500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        } else {

            word_index = lastWordIndex;
            openLessonFragment(lesson_index,word_index, false, true);
        }
    }

    private int getLessonIndex(String phrase) {
        for(int i:lessons.keySet()){
            if(lessons.get(i)[0].getPhrase().equals(phrase)){
                System.out.println(lessons.get(i)[0].getPhrase()+" "+phrase);
                return i;
            }
        }
        return 0;
    }

    private void findLastLesson(Map<Integer, Word[]> lessons) {

        for(int i=0;i<lessons.size();i++){
            int j=0;
            for(Word word:lessons.get(i)){
                if(!word.isAchieved()){
                    lesson_index = i;
                    lastWordIndex = j;
                    return;
                }
                j++;
            }
        }
    }

    /**
     * Showing google speech input dialog
     **/
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //open Lesson Fragment by Lesson ID
    public void openLessonFragment(int i, int word_index, boolean repeat, boolean animatePhrase) {
        sharedPreferenceUtils.setValue(LessonKey, String.valueOf(i));

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Word[] lesson = lessons.get(i);
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(LessonKey, lesson);
        bundle.putInt(WordIndexKey, word_index);
        bundle.putBoolean(RepeatLesson, repeat);
        bundle.putBoolean(AnimatePhrase, animatePhrase);

        LessonFragment lessonFragment = new LessonFragment();
        lessonFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment,LessonFragment_TAG);
        fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();

        Log.i("MainActivity", "openLessonFragment:: lesson_index" + lesson_index);
    }

    //open Lesson Fragment for just one word -- using in animation fragment
    public void openLessonFragment(Word word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        word = form_word(word.getText(), word.getPhrase(), sharedPreferenceUtils.getStringValue("SF",""));
        bundle.putParcelable(WordKey, word);
        bundle.putInt(WordIndexKey, 0);

        LessonFragment lessonFragment = new LessonFragment();
        lessonFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment,LessonFragment_TAG);
        fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();

        Log.i("MainActivity", "openLessonFragment:: lesson_index" + lesson_index);
    }

    public void openLessonFragment(String phrase) {
        openLessonFragment(getLessonIndex(phrase), 0, false, true);
    }

    //Return back to the latest state of Lesson fragment
    public void backToLessonFragment() {
        Log.i("MainActivity", "backToLessonFragment :: am here!");

        FragmentManager fragmentManager = getFragmentManager();
        LessonFragment lessonFragment = (LessonFragment) fragmentManager.findFragmentByTag(LessonFragment_TAG);
        if (lessonFragment != null) {
            Log.i("MainActivity", "backToLessonFragment != null");

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_replacement, lessonFragment, LessonFragment_TAG);
            fragmentTransaction.addToBackStack(LessonFragment_TAG);
            fragmentTransaction.commit();
        } else {
            Log.i("MainActivity", "lessonFragment = null");
        }
    }

    //open phrase fragment to pick the word learned
    public void openPhraseFragment(String phrase, String word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(PhraseKey, phrase);
        bundle.putString(WordKey, word);

        PhrasePickFragment phrasePickFragment = new PhrasePickFragment();
        phrasePickFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, phrasePickFragment);
        //fragmentTransaction.addToBackStack(PhrasePickFragment_TAG);
        fragmentTransaction.commit();
    }

    //open animation fragment to analyse phrases' words and words' chars
    public void openAnimationFragment(String word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(WordKey, word);

        AnimationFragment animationFragment = new AnimationFragment();
        animationFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, animationFragment);
//        fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();
    }

    //open Help Fragment
    public void openHelpFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        word_index = Integer.parseInt(sharedPreferenceUtils.getStringValue(WordIndexKey, "0"));
        Bundle bundle = new Bundle();
        bundle.putInt(WordIndexKey, word_index);
        bundle.putParcelableArray(LessonKey, lessons.get(1));

        Fragment achievementsFragment = fragmentManager.findFragmentByTag(AchievementsFragment_TAG);
        if(achievementsFragment == null)
            achievementsFragment = new AchievementsFragment();
        achievementsFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, achievementsFragment);
        fragmentTransaction.addToBackStack(AchievementsFragment_TAG);
        fragmentTransaction.commit();
    }

    public void openWordFragment(Word word) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(WordKey, word);

        Fragment wordFragment = new WordFragment();
        wordFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, wordFragment, WordFragment_TAG);
        fragmentTransaction.addToBackStack(WordFragment_TAG);
        fragmentTransaction.commit();
    }

    public void openWordAnimationFragment(Word word, int currentIndex) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(WordKey, word.getText());
        bundle.putInt("char_index", currentIndex);

        Fragment animationFragment = new WordAnimationFragment();
        animationFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_replacement, animationFragment);
//        fragmentTransaction.addToBackStack(LessonFragment_TAG);
        fragmentTransaction.commit();
    }

    public void backToWordFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        WordFragment wordFragment = (WordFragment) fragmentManager.findFragmentByTag(WordFragment_TAG);
        if (wordFragment != null) {
            Log.i("MainActivity", "backToLessonFragment != null");

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_replacement, wordFragment, WordFragment_TAG);
            fragmentTransaction.addToBackStack(WordFragment_TAG);
            fragmentTransaction.commit();
        } else {
            Log.i("MainActivity", "lessonFragment = null");
        }
    }

    //play voice of any Audio file path
    public void voiceoffer(View view, String DataPath2Bplayed) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        if (mediaPlayer != null) {

            if (mediaPlayer.isLooping() || mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        try {

            mediaPlayer = new MediaPlayer();
            System.out.println("Path to play "+sharedPreferenceUtils.getStringValue("SF","") + DataPath2Bplayed);
            mediaPlayer.setDataSource(sharedPreferenceUtils.getStringValue("SF","") + DataPath2Bplayed);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IllegalStateException | IOException e) {
            Log.e("MainActivity", "voiceoffer::e: " + e.toString());
            Log.e("MainActivity", "voiceoffer::DataPath2Bplayed: " + sharedPreferenceUtils.getStringValue("SF","") + DataPath2Bplayed);
        }
        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.stop());
    }

    private void startVoice() {
        Log.i("RecognitionListener", "I'm in startVoice");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-JO");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "tk.oryx.voice");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500); // value to wait

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);  // 1 is the maximum number of results to be returned.
        sr.startListening(intent);
    }

    // stopping the media player
    public void StopMediaPlayer() {
        Log.i("MainActivity", "StopMediaPlayer");
        if (mediaPlayer.isPlaying() || mediaPlayer.isLooping()) {
            mediaPlayer.stop();
        }
    }

    public void updateLesson(int ToFlag) {
        lesson_index = Integer.parseInt(sharedPreferenceUtils.getStringValue(LessonKey, "1"));
        switch (ToFlag) {
            case 0:
                openLessonFragment(lesson_index, 0, false, true);
                break;
            case -1:
                if (lesson_index > 1) {
                    openLessonFragment(lesson_index - 1, 0, false, true);
                } else {
                    openLessonFragment(lesson_index, 0, false, true);
                }
                break;
            case 1:
                openLessonFragment(Math.min(lesson_index + 1,lessons.size()-1), 0, false, true);
                break;
        }
    }

    //displaying help Photo
    public void helpbypic(View view, String img2Bdisplayed) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_sample_pic_help, null);
        ImageView imageView = dialogLayout.findViewById(R.id.picsample);
        File imgFile = new File(sharedPreferenceUtils.getStringValue("SF","") + img2Bdisplayed);
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void DisplayLessons(Map<Integer, Word[]> lessons) {
        this.lessons = lessons;
        findLastLesson(lessons);
    }

    @Override
    public void DisplayNoLessons() {
        Toast.makeText(MainActivity.this, "no lessons", Toast.LENGTH_LONG).show();
    }

    @Override
    public void DisplayArchiveWords(List<String> words) {
        Toast.makeText(MainActivity.this, "" + words.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void DisplayNoArchiveWords() {

    }

    @Override
    public void DisplayArchiveLessons(Map<Integer, String[]> lessons) {
//        findLastLesson(lessons);
    }

    public Map<Integer, Word[]> getLessons() {
        return lessons;
    }

    /* public void addSubscreen(Fragment fragment) {
         getSupportFragmentManager()
                 .beginTransaction()
                 .replace(R.id.container, fragment)
                 .addToBackStack(null)
                 .commit();
         subscreensOnTheStack++;
     }


     public void popOffSubscreens() {
         while (subscreensOnTheStack > 0) {
             fragments.popBackStackImmediate();
         }
     }*/



}
