package com.example.l.seshatmvp.layout;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.l.seshatmvp.MainActivity;
import com.example.l.seshatmvp.R;
import com.example.l.seshatmvp.UpdateWord;
import com.example.l.seshatmvp.Utils.SharedPreferenceUtils;
import com.example.l.seshatmvp.VideoActivity;
import com.example.l.seshatmvp.WordView;
import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.presenter.WordPresenter;
import com.example.l.seshatmvp.repositories.imp.ImpWordsRepository;
import com.example.l.seshatmvp.view.MainActivityView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;

public class LessonFragment extends Fragment implements UpdateWord {

    public static final int RESULT_SPEECH = 177, WAIT2SayInstructions = 1000;
    public static int DEFAULT_LOOP_COUNTER = 2;
    public static int DEFAULT_TYPEFACE_LEVELS = 4;

    public static String LessonFragment_TAG = "LessonFragment";

    public static boolean phraseIsAnimated = false;
    public static boolean wordIsAnimated = false;
    public static boolean isPicked = false;

    private ArrayList<ImageView> starsArrayList;

    private Boolean isPronunced = false;
    private Boolean isWritten = false;
    private Boolean firstTime = false;

    private int CurrentWordsArrayIndex = 0;
    private String TAG = "LessonFragment";

    private Word[] words;
    private Word word = null;

    private Thread Thread_WordJourney = null;
    private Context mContext;
    private LessonFragment instance;
    private WordPresenter wordPresenter;

    private View view;

    private ImageView nextWord;
    private ImageView prevWord;
    private ImageView wordImage;

    private Button imageBtn_repeat_frame;
    private Button imageBtn_play_frame;
    private Button imagebutton_moreInfo_frame, teacher_btn_frame;

    private LinearLayout starsLayout;
    private FrameLayout prevlesson_layout, play_layout , repeat_layout;
    private ImageButton helpiBtn, teacherBtn, PlaySoundiBtn, mike_voice_btn, imagebutton_repeat;
    private WordView wordView_MainText;
    private boolean repeatLesson;
    private boolean bouncing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordIsAnimated = false;
        phraseIsAnimated = false;

        if (getArguments() != null) {
            //getting lesson words when sending lesson key through openLessonFragment(int i)
            words = (Word[]) getArguments().getParcelableArray(MainActivity.LessonKey);//will be null when calling animation

            //getting text when sending word text through openLessonFragment(String word)
            word = getArguments().getParcelable(MainActivity.WordKey);//will be not null when calling animation
            //getting boolean to check if first time or not
            firstTime = getArguments().getBoolean(MainActivity.firstTimekey);
            //getting word index (till now always 0 for each lesson )
            CurrentWordsArrayIndex = getArguments().getInt(MainActivity.WordIndexKey);

            repeatLesson = getArguments().getBoolean(MainActivity.RepeatLesson,false);

            phraseIsAnimated = !getArguments().getBoolean(MainActivity.AnimatePhrase,true);

            if (word == null && words != null) {
                //filling when calling by openLessonFragment(int i)
                word = words[CurrentWordsArrayIndex];
                Log.i("onCreate", "from LessonFragment" + "word == null");
            }
        }

        SharedPreferenceUtils sharedPreferenceUtils = SharedPreferenceUtils.getInstance(getActivity());
        sharedPreferenceUtils.setValue("word_for_splash_screen",word.getText());
        instance = this;
        mContext = getActivity();
    }

    public WordView getWordView_MainText()
    {
        return this.wordView_MainText;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_mai, container, false);
        starsLayout = view.findViewById(R.id.stars_layout);


        wordView_MainText = view.findViewById(R.id.textView_maintext);
        prevlesson_layout = getActivity().findViewById(R.id.prevlesson_layout);
        repeat_layout = view.findViewById(R.id.repeat_layout);
        play_layout = view.findViewById(R.id.play_layout);
        wordImage = view.findViewById(R.id.wordImage);

        nextWord = view.findViewById(R.id.nextWord);
        prevWord = view.findViewById(R.id.prevWord);

        wordView_MainText.setDefaultLoop(DEFAULT_LOOP_COUNTER);
        wordView_MainText.setDefaultTypeface(DEFAULT_TYPEFACE_LEVELS);

        generateStars();
        if(words != null) {
            nextWord.setVisibility((CurrentWordsArrayIndex < words.length - 1 && word.isAchieved()) ? View.VISIBLE : View.GONE);
            prevWord.setVisibility((CurrentWordsArrayIndex > 0) ? View.VISIBLE : View.GONE);
        }

        bouncing = false;
        nextWord.setOnClickListener(view -> updateWord(1));
        prevWord.setOnClickListener(view -> updateWord(-1));

        Picasso.get().load("file://"+word.getImageFilePath()).into(wordImage);
        wordView_MainText.setText(word.getText());
        wordView_MainText.setFragment(this);


        if (word.getFV() != null) {

            //setting word guided vector in the wordView to help you check writing
            wordView_MainText.setGuidedVector(word.getFV(),word.getText());
            Log.d("LessonFragment", "FV = " + word.getFV());
        } else if (!firstTime) {
            //setting word from lessons' words
            word = words[CurrentWordsArrayIndex];
            //setting word guided vector in the wordView to help you check writing
            wordView_MainText.setGuidedVector(word.getFV(),word.getText());
            //putting text into wordView
            wordView_MainText.setText(word.getText());
            Log.d("LessonFragment", "FV = " + word.getFV());

        }
        imageBtn_repeat_frame = this.view.findViewById(R.id.imagebutton_repeat_frame);
        imageBtn_repeat_frame.setOnClickListener(view78 ->
        {
            stopBounce(play_layout);
            stopBounce(repeat_layout);
            stopBounce(prevlesson_layout);
            stopBounce(teacherBtn);

            imageBtn_repeat_frame.setVisibility(View.GONE);
            imagebutton_moreInfo_frame.setVisibility(View.GONE);
            imageBtn_play_frame.setVisibility(View.GONE);
            teacher_btn_frame.setVisibility(View.GONE);

            wordView_MainText.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.repeat_word));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });
        teacher_btn_frame = this.view.findViewById(R.id.teacher_btn_frame);
        teacher_btn_frame.setOnClickListener(view78 ->
        {
            stopBounce(play_layout);
            stopBounce(repeat_layout);
            stopBounce(prevlesson_layout);
            stopBounce(teacherBtn);

            imageBtn_repeat_frame.setVisibility(View.GONE);
            imagebutton_moreInfo_frame.setVisibility(View.GONE);
            imageBtn_play_frame.setVisibility(View.GONE);
            teacher_btn_frame.setVisibility(View.GONE);

            wordView_MainText.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.teacher));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });
        imageBtn_play_frame = this.view.findViewById(R.id.imagebutton_soundhelp_frame);
        imageBtn_play_frame.setOnClickListener(view78 ->
        {
            stopBounce(play_layout);
            stopBounce(repeat_layout);
            stopBounce(prevlesson_layout);
            stopBounce(teacherBtn);

            imageBtn_repeat_frame.setVisibility(View.GONE);
            imagebutton_moreInfo_frame.setVisibility(View.GONE);
            imageBtn_play_frame.setVisibility(View.GONE);
            teacher_btn_frame.setVisibility(View.GONE);

            wordView_MainText.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.play_word));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });
        imagebutton_moreInfo_frame = getActivity().findViewById(R.id.imagebutton_moreInfo_frame);
        imagebutton_moreInfo_frame.setOnClickListener(view78 ->
        {
            stopBounce(play_layout);
            stopBounce(repeat_layout);
            stopBounce(prevlesson_layout);
            stopBounce(teacherBtn);

            imageBtn_repeat_frame.setVisibility(View.GONE);
            imagebutton_moreInfo_frame.setVisibility(View.GONE);
            imageBtn_play_frame.setVisibility(View.GONE);
            teacher_btn_frame.setVisibility(View.GONE);

            wordView_MainText.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.urachievements));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });
        View successBtn = view.findViewById(R.id.imagebutton_success);
        helpiBtn = getActivity().findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(view15 -> {
            wordIsAnimated = false;
            phraseIsAnimated = false;
            successBtn.setVisibility(INVISIBLE);
            try {
                if (Thread_WordJourney != null) {
                    if (Thread_WordJourney.isAlive()) {
                        Thread_WordJourney.interrupt();
                        Log.i("helpiBtn", "is clicked!" + "Thread_WordJourney.is alive");
                    }
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            ((MainActivity) getActivity()).openHelpFragment();

            wordIsAnimated = false;
            phraseIsAnimated = false;
        });

        teacherBtn = view.findViewById(R.id.teacher_btn);
        teacherBtn.setOnClickListener(view13 -> {

            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.angry_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            FrameLayout yes = dialog.findViewById(R.id.true_btn);
            FrameLayout no = dialog.findViewById(R.id.false_btn);

            yes.setOnClickListener(view -> {
                Log.i(TAG,"In Yes");
                wordIsAnimated = false;

                startActivityForResult(new Intent(mContext, VideoActivity.class), 2);
                dialog.dismiss();
            });
            no.setOnClickListener(view -> {
                Log.i(TAG,"In No");
                wordIsAnimated = false;
                dialog.dismiss();
            });
            String emoji = "unhappy";
            Log.i(TAG,"emoji: " + emoji);
            if(emoji.equals("happy")) {
                ((MainActivity) mContext).voiceoffer(null, mContext.getString(R.string.happy));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                getView().setEnabled(false);
                ((MainActivity) mContext).voiceoffer(null, mContext.getString(R.string.angry));
                dialog.show();

            }
        });
        imagebutton_repeat = view.findViewById(R.id.imagebutton_repeat);
        imagebutton_repeat.setOnClickListener(view99 -> {

            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            imagebutton_repeat.startAnimation(shake);
            currWordCall();
        });

        mike_voice_btn = view.findViewById(R.id.mike_voice_btn);
        mike_voice_btn.setOnClickListener(view98 ->
        {
            imagebutton_moreInfo_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            imageBtn_repeat_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            imageBtn_play_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            teacher_btn_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);

            wordView_MainText.setEnabled(bouncing);

            if(!bouncing) {
                startBounce(play_layout);
                startBounce(repeat_layout);
                startBounce(prevlesson_layout);
                startBounce(teacherBtn);
            }else{
                stopBounce(play_layout);
                stopBounce(repeat_layout);
                stopBounce(prevlesson_layout);
                stopBounce(teacherBtn);
            }

        });


        PlaySoundiBtn = view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(view12 -> {
            try {
                //playing Audio file of word
                ((MainActivity) getActivity()).voiceoffer(PlaySoundiBtn, word.getText());
                Log.i("PlaySoundiBtn", word.getText());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PlaySoundiBtn", e.toString());
            }
        });

//        setNextiBtnVisibility();
        setteacherBtnVisibilty();


        return view;
    }

    private void generateStars() {
        for(int i=0;i<DEFAULT_LOOP_COUNTER*DEFAULT_TYPEFACE_LEVELS;i++){
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.star_empty);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            starsLayout.addView(imageView);
            imageView.getLayoutParams().width = ((int)getResources().getDimension(R.dimen.star_icon_size));
            imageView.getLayoutParams().height = ((int)getResources().getDimension(R.dimen.star_icon_size));
        }
    }


    private void startBounce(View view)
    {
        bouncing = true;
        if (view != null) {
            Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            view.startAnimation(bounce);

        }
    }
    private void stopBounce(View view)
    {
        bouncing = false;
        if(view != null)view.clearAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        helpiBtn.setVisibility(View.VISIBLE);
        if (!firstTime && !wordIsAnimated) {
            //word not animated yet
            if (!phraseIsAnimated && words != null) {
                //word's phrase not animated
                ((MainActivity) getActivity()).openAnimationFragment(word.getPhrase());
                phraseIsAnimated = true;
            } else {
                //word phrase animated but word not animated
                ((MainActivity) getActivity()).openAnimationFragment(word.getText());
                wordIsAnimated = true;
            }
        } else if (!firstTime && instance.isWritten && !isPicked ) {
            if(word.getPhrase() == null)
                ((MainActivity)getActivity()).openHelpFragment();
            else {
                //word has been written and not picked from phrase
                //saying picking instructions and start phrase fragment to pick word
                ((MainActivity) getActivity()).openPhraseFragment(word.getPhrase(), word.getText());
            }
        } else if (!firstTime && isPicked) {
            //word has been picked
            if (instance.CurrentWordsArrayIndex + 1 == instance.words.length) {
                if(repeatLesson)
                    ((MainActivity)getActivity()).openHelpFragment();
                else {
                    //if it's the last word
                    //update lesson, reset all word and phrase booleans, get the next lesson word and setting it's guided vector
                    Log.i("LessonFragment: ", "UpdateLesson: ");
                    CurrentWordsArrayIndex = 0;
                    instance.word = instance.words[CurrentWordsArrayIndex];
                    isPicked = false;
                    instance.isWritten = false;
                    instance.isPronunced = false;
                    wordIsAnimated = false;
                    phraseIsAnimated = false;

                    instance.wordView_MainText.setGuidedVector(instance.word.getFV(), instance.word.getText());
                    instance.wordView_MainText.setText(
                            instance.word.getText());

                    instance.wordView_MainText.invalidate();
                    setNextiBtnVisibility();
                    setteacherBtnVisibilty();

                    // ((MainActivity) instance.mContext).updatelesson(1, yes);
                    ((MainActivity) instance.mContext).updateLesson(1);
                }
            } else {
                //not the last word in lesson so get next word
                phraseIsAnimated = false;
                wordIsAnimated = false;
                instance.nextWordCall();
                instance.setteacherBtnVisibilty();
                instance.setNextiBtnVisibility();
            }
        }
    }

    //next button visibility checking
    private void setNextiBtnVisibility() {
        if (words == null) {
            //when calling openAnimationFragment(String word) and words == null

        } else {
            //during the lesson adventure
            if (CurrentWordsArrayIndex == words.length - 1) {
                //if last word
            } else {
                //not last word
            }
        }
    }

    //previous button visibility checking
    private void setteacherBtnVisibilty() {
        if (CurrentWordsArrayIndex == 0) {
            //if first word
            teacherBtn.setVisibility(View.VISIBLE);
        } else {
            //not first word
            teacherBtn.setVisibility(View.VISIBLE);
        }
    }

    //instructions after finishing word trip
    private Thread Thread_WordJourney_voice_speech() {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.angry_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        FrameLayout yes = dialog.findViewById(R.id.true_btn);
        FrameLayout no = dialog.findViewById(R.id.false_btn);

        yes.setOnClickListener(view -> {
            Log.i(TAG,"In Yes");

            startActivityForResult(new Intent(mContext, VideoActivity.class), 1);
            dialog.dismiss();
        });
        no.setOnClickListener(view -> {
            Log.i(TAG,"In No");

            dialog.dismiss();
            continueLesson();

        });

        Thread_WordJourney = new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("XX", "XX");
                    sleep(WAIT2SayInstructions);
                } catch (InterruptedException ignored) {
                }
                ((MainActivity) mContext).runOnUiThread(() -> {
                    try {
                        String emoji = "unHappy";
                        Log.i(TAG,"emoji: " + emoji);
                        if(emoji.equals("happy"))
                        {
                            ((MainActivity) mContext).voiceoffer(null, mContext.getString(R.string.happy));
                            Thread.sleep(3000);

                            try
                            {

                                ((MainActivity) mContext).voiceoffer(null, instance.word.getText());
                                Thread.sleep(1500);

                                if (words == null) {
                                    //when calling openAnimationFragment(String word) and words == null
                                    //using archive words
                                    ((MainActivity) getActivity()).openHelpFragment();
                                } else {
                                    //playing pick instruction when finish writing
                                    ((MainActivity) mContext).voiceoffer(instance.wordView_MainText, mContext.getString(R.string.pickwordinstr));
                                    Thread.sleep(2500);
                                    //  instance.voicerec(null);
                                    //start picking word
                                    ((MainActivity) getActivity()).openPhraseFragment(word.getPhrase(), word.getText());
                                    Thread.sleep(2500);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG,"Error::Happy: " + e.getMessage());
                            }
                        }
                        else
                        {


                            // set the custom dialog components - text, image and button
                            getView().setEnabled(false);
                            dialog.show();

                            Thread.sleep(1500);
                            ((MainActivity) getContext()).voiceoffer(null, getContext().getString(R.string.angry));

//                            ((MainActivity) mContext).voiceoffer(null, mContext.getString(R.string.pause_1));
//                            ((MainActivity) mContext).voiceoffer(null, mContext.getString(R.string.pause_2));
                           // Thread_WordJourney.wait();
                            /*final Thread videoThread = new Thread() {
                            @Override
                            public void run () {
                                final Dialog dialog = new Dialog(mContext);
                                dialog.setContentView(R.layout.angry_dialog);

                                ImageView yes = dialog.findViewById(R.id.true_btn);
                                ImageView no = dialog.findViewById(R.id.true_btn);

                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivityForResult(new Intent(mContext, VideoActivity.class), 1);
                                        dialog.dismiss();
                                    }
                                });
                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        dialog.dismiss();
                                    }
                                });

                                // set the custom dialog components - text, image and button

                                dialog.show();

                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                });
                            }
                        };
                            videoThread.join();*/

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void interrupt() {
                super.interrupt();
                ((MainActivity) getActivity()).StopMediaPlayer();
                onDetach();
            }
        };
        return Thread_WordJourney;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            continueLesson();

        }

    }
    public void continueLesson()
    {
//        if(!word.isAchieved()){
        new Handler().postDelayed(()-> ((MainActivity)getActivity()).openWordFragment(word),400);
//        }else {
//            try
//            {
//                if (words == null) {
//                    //when calling openAnimationFragment(String word) and words == null
//                    //using archive words
//                    ((MainActivity) getActivity()).openHelpFragment();
//                } else {
//                    ((MainActivity) getActivity()).openPhraseFragment(word.getPhrase(), word.getText());
//                    new Handler().postDelayed(() -> {
//                        System.out.println("say again");
//                        //playing pick instruction when finish writing
//                        ((MainActivity) mContext).voiceoffer(instance.wordView_MainText, mContext.getString(R.string.pickwordinstr));
//                    },500);
//                }
//            }
//            catch (Exception e)
//            {
//                Log.e(TAG,"Error::onActivityResult: " + e.toString());
//            }
//        }
    }
    //an override method from UpdateWord interface to make WordView interact with LessonFragment to update word's fonts levels
    @Override
    public Typeface updateWordLoop(Typeface typeface, int word_loop) {
        setupStars(word_loop);
        Typeface tf;
        //check word loop counter
        Log.i(TAG,"DEFAULT_LOOP_COUNTER: " + DEFAULT_LOOP_COUNTER);
        Log.i(TAG,"DEFAULT_TYPEFACE_LEVELS: " + DEFAULT_TYPEFACE_LEVELS);
        Log.i(TAG,"word_loop: " + word_loop);
        if (word_loop < (DEFAULT_LOOP_COUNTER * DEFAULT_TYPEFACE_LEVELS)) {
            if (word_loop % DEFAULT_LOOP_COUNTER == 0) {
                // change font
                if (word_loop > 0 && word_loop == DEFAULT_LOOP_COUNTER) {
                    //level 2 (less dots level)
                    tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/lvl2.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER && word_loop == DEFAULT_LOOP_COUNTER * 2) {
                    //level 3 (less dots and arrows level)
                    tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/lvl3.ttf");
                } else {
                    //level 4 (Blank level)
                    return null;
                }
            } else {
                tf = typeface;
            }
        } else {
            //finish writing
            // change word
            isWritten = true;
            word.setHalfAchieved(true);
            //store word in archive
            if(instance.word.getPhrase() != null) {
                wordPresenter = new WordPresenter((MainActivityView) getActivity(), new ImpWordsRepository(mContext));
                wordPresenter.saveWordstoArchive(instance.word);
            }
            instance.Thread_WordJourney_voice_speech().start();
            Log.i("LessonFragment: ", "UpdateWordLoop: changeword");
            //return to font level 1 (hollow with arrows)
            tf = null;
        }
        return tf;
    }

    private void setupStars(int counter)
    {

        for(int i = 0; i < counter;i++)
        {
            ImageView imageView = (ImageView) starsLayout.getChildAt(DEFAULT_LOOP_COUNTER*DEFAULT_TYPEFACE_LEVELS-1-i);
            imageView.setBackgroundResource(R.drawable.star_full);
        }
    }

    public String getEmoji()
    {
        int smileCounter = 0;
        int angryCounter = 0;
        int confusedCounter = 0;
        int destractedCounter = 0;
        for(String Emoji: WordView.Expressions)
        {

            if(Emoji.equals("joy"))
            {
                smileCounter++;
            }
            else if(Emoji.equals("anger")||Emoji.equals("sadness") || Emoji.equals("fear"))
            {
                angryCounter++;
            }
            else if(Emoji.equals("distracted"))
            {
                destractedCounter++;
            }
            else if(Emoji.equals("confused"))
            {
                confusedCounter++;
            }
        }
        Map<String,Integer> counters = new HashMap<>();
        counters.put("happy",smileCounter);
        counters.put("angry",angryCounter);
        counters.put("distracted",destractedCounter);
        counters.put("confused",confusedCounter);

        int max = 0;
        String result = null;

        for (Map.Entry<String, Integer> entry : counters.entrySet())
        {
            if(entry.getValue() > max)
            {
                max = entry.getValue();
                result = entry.getKey();
            }
        }
        Log.i(TAG,"Max: " + max);

        if(max == 0 || result == null)
        {
            result = "distracted";
        }

        return result;
    }

    @Override
    public void setmContext(Context context) {
    }

    @Override
    public void setLessonFragment(LessonFragment fragment) {
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("LessonFragment", "onStop");
        if (Thread_WordJourney != null) {
            Thread_WordJourney.interrupt();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("LessonFragment", "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateWord(int increment) {
        instance = this;
        if (instance.Thread_WordJourney != null) instance.Thread_WordJourney.interrupt();

        instance.CurrentWordsArrayIndex = instance.CurrentWordsArrayIndex+increment;
        //get next word
        instance.word = instance.words[instance.CurrentWordsArrayIndex];

        //save word index
        SharedPreferenceUtils.getInstance(getContext()).setValue(MainActivity.WordIndexKey, String.valueOf(instance.CurrentWordsArrayIndex));


        //animate the new word if not animated
        ((MainActivity) getActivity()).openAnimationFragment(instance.word.getText());

        wordIsAnimated = true;

        //reset word operations
        isPicked = false;
        instance.isWritten = false;
        instance.isPronunced = false;
        //set word guided vector
        instance.wordView_MainText.setGuidedVector(instance.word.getFV(),instance.word.getText());
        instance.wordView_MainText.invalidate();
    }

    //request next word
    private void nextWordCall() {
        instance = this;
        if (instance.Thread_WordJourney != null) instance.Thread_WordJourney.interrupt();

        //get next word
        instance.word = instance.words[++instance.CurrentWordsArrayIndex];

        //save word index
        SharedPreferenceUtils.getInstance(getContext()).setValue(MainActivity.WordIndexKey, String.valueOf(instance.CurrentWordsArrayIndex));

        if (!phraseIsAnimated) {

            //animate the new phrase if not animated
            ((MainActivity) getActivity()).openAnimationFragment(instance.word.getPhrase());
            phraseIsAnimated = true;
        } else {

            //animate the new word if not animated
            ((MainActivity) getActivity()).openAnimationFragment(instance.word.getText());

            wordIsAnimated = true;
        }
        //reset word operations
        isPicked = false;
        instance.isWritten = false;
        instance.isPronunced = false;
        //set word guided vector
        instance.wordView_MainText.setGuidedVector(instance.word.getFV(),instance.word.getText());
        instance.wordView_MainText.setText(
                instance.word.getText());

        instance.wordView_MainText.invalidate();

    }

    //request previous word
    private void currWordCall() {
        if (instance.Thread_WordJourney != null) instance.Thread_WordJourney.interrupt();

        if(words != null) {
            //getting previous word
            instance.word = instance.words[instance.CurrentWordsArrayIndex];
        }

        //store word index
        SharedPreferenceUtils.getInstance(getContext()).setValue(MainActivity.WordIndexKey, String.valueOf(instance.CurrentWordsArrayIndex));
        phraseIsAnimated = true;
        wordIsAnimated = false;

        //animate the new word if not animated
        ((MainActivity) getActivity()).openAnimationFragment(instance.word.getText());
        wordIsAnimated = true;

        //reset word operation
        isPicked = false;
        instance.isPronunced = false;

        //set word guided vector
        instance.wordView_MainText.setGuidedVector(instance.word.getFV(),instance.word.getText());
        instance.wordView_MainText.setText(
                instance.word.getText());

        instance.wordView_MainText.invalidate();
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }
}
