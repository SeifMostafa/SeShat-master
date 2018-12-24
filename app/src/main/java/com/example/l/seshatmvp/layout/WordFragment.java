package com.example.l.seshatmvp.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
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
import com.example.l.seshatmvp.VideoActivity;
import com.example.l.seshatmvp.WordView;
import com.example.l.seshatmvp.model.Direction;
import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.presenter.WordPresenter;
import com.example.l.seshatmvp.repositories.imp.ImpWordsRepository;
import com.example.l.seshatmvp.view.MainActivityView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static com.google.android.gms.internal.zzt.TAG;

public class WordFragment extends Fragment implements UpdateWord{

    private Word word;
    private int currentIndex;

    private WordView characterView;
    private Button imageBtn_repeat_frame;
    private Button imageBtn_play_frame;
    private Button imagebutton_moreInfo_frame, teacher_btn_frame;
    private FrameLayout prevlesson_layout, play_layout , repeat_layout;
    private ImageButton helpiBtn, teacherBtn, PlaySoundiBtn, DisplayImageiBtn, mike_voice_btn, imagebutton_repeat;
    private ImageView nextChar;
    private ImageView prevChar;
    private LinearLayout starsLayout;
    private int DEFAULT_LOOP_COUNTER = 1;
    private int DEFAULT_TYPEFACE_LEVELS = 4;

    private WordPresenter wordPresenter;
    private View view;
    private Typeface tf;
    private boolean animated;
    private boolean bouncing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        wordPresenter = new WordPresenter((MainActivityView) getActivity(), new ImpWordsRepository(getActivity().getApplication()));
        if(arguments != null){
            word = arguments.getParcelable(MainActivity.WordKey);
        }
        animated = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_word, container, false);

        bouncing = false;
        starsLayout = view.findViewById(R.id.stars_layout);
        generateStars();

        characterView = view.findViewById(R.id.characterView);

        repeat_layout = view.findViewById(R.id.repeat_layout);
        play_layout = view.findViewById(R.id.play_layout);
        prevlesson_layout = getActivity().findViewById(R.id.prevlesson_layout);

        nextChar = view.findViewById(R.id.nextChar);
        prevChar = view.findViewById(R.id.prevChar);

        nextChar.setVisibility((currentIndex < word.getText().length() - 1 && word.isAchieved(word.getText().charAt(currentIndex))) ? View.VISIBLE : View.GONE);
        prevChar.setVisibility((currentIndex > 0) ? View.VISIBLE : View.GONE);

        nextChar.setOnClickListener(v -> {
            updateChar(1);
        });
        prevChar.setOnClickListener(v -> {
            updateChar(-1);
        });

        imageBtn_repeat_frame = view.findViewById(R.id.imagebutton_repeat_frame);
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

            characterView.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.repeat_character));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });
        teacher_btn_frame = view.findViewById(R.id.teacher_btn_frame);
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

            characterView.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.teacher));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });
        imageBtn_play_frame = view.findViewById(R.id.imagebutton_soundhelp_frame);
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

            characterView.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.play_character));
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

            characterView.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.urachievements));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });

        mike_voice_btn = view.findViewById(R.id.mike_voice_btn);
        mike_voice_btn.setOnClickListener(view98 ->
        {
            imagebutton_moreInfo_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            imageBtn_repeat_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            imageBtn_play_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            teacher_btn_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);

            characterView.setEnabled(bouncing);

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

        View successBtn = view.findViewById(R.id.imagebutton_success);
        helpiBtn = getActivity().findViewById(R.id.imagebutton_moreInfo);
        helpiBtn.setOnClickListener(view15 -> {
            successBtn.setVisibility(INVISIBLE);
            ((MainActivity) getActivity()).openHelpFragment();
        });

        imagebutton_repeat = view.findViewById(R.id.imagebutton_repeat);
        imagebutton_repeat.setOnClickListener(view99 -> {

            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            imagebutton_repeat.startAnimation(shake);
//            currWordCall();
        });

        PlaySoundiBtn = view.findViewById(R.id.imagebutton_soundhelp);
        PlaySoundiBtn.setOnClickListener(view12 -> {
            try {
                //playing Audio file of word
                ((MainActivity) getActivity()).voiceoffer(PlaySoundiBtn, word.getText().charAt(currentIndex)+".wav");
                Log.i("PlaySoundiBtn", word.getText());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PlaySoundiBtn", e.toString());
            }
        });

        teacherBtn = view.findViewById(R.id.teacher_btn);
        teacherBtn.setOnClickListener(view1 -> {

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.angry_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            FrameLayout yes = dialog.findViewById(R.id.true_btn);
            FrameLayout no = dialog.findViewById(R.id.false_btn);

            yes.setOnClickListener(view -> {
                Log.i(TAG,"In Yes");

                startActivityForResult(new Intent(getActivity(), VideoActivity.class), 2);
                dialog.dismiss();
            });
            no.setOnClickListener(view -> {
                Log.i(TAG,"In No");
                dialog.dismiss();
            });
            String emoji = "confused";
            Log.i(TAG,"emoji: " + emoji);
            if(emoji.equals("happy")) {
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.happy));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.angry));
                dialog.show();

            }
        });

        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        //TODO::animation for splitting word into its chars
        characterView.setDefaultLoop(DEFAULT_LOOP_COUNTER);
        characterView.setDefaultTypeface(DEFAULT_TYPEFACE_LEVELS);

        characterView.setGuidedVector(getCharVector(currentIndex),word.getText().charAt(currentIndex)+"");
        characterView.setFragment(this);
        characterView.setTypeface(tf);
        characterView.setText(String.format("%s", word.getText().charAt(currentIndex)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!animated){
            ((MainActivity)getActivity()).openWordAnimationFragment(word,currentIndex);
            animated = true;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            ((MainActivity)getActivity()).backToLessonFragment();
        }
    }


    private void updateChar(int increment) {
        currentIndex += increment;
        ((MainActivity)getActivity()).openWordAnimationFragment(word,currentIndex);
        animated = true;
    }

    private Map<Integer, Direction[][]> getCharVector(int currentIndex) {
        Map<Integer, Direction[][]> wordVector = word.getFV();
        Map<Integer, Direction[][]> charVector = new HashMap<>();

        for(int key:wordVector.keySet()){
            Direction[] charDirections = wordVector.get(key)[currentIndex];
            Direction[][] directions = new Direction[1][charDirections.length];
            directions[0] = charDirections ;
            charVector.put(key,directions);
        }

        return charVector;

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

    private void setupStars(int counter)
    {
        int i = 0;
        for(; i < counter;i++) {
            ImageView imageView = (ImageView) starsLayout.getChildAt(DEFAULT_LOOP_COUNTER * DEFAULT_TYPEFACE_LEVELS - 1 - i);
            imageView.setBackgroundResource(R.drawable.star_full);
        }
    }

    @Override
    public Typeface updateWordLoop(Typeface typeface, int word_loop) {
        setupStars(word_loop);
        Typeface tf;
        if (word_loop < (DEFAULT_LOOP_COUNTER * DEFAULT_TYPEFACE_LEVELS)) {
            if (word_loop % DEFAULT_LOOP_COUNTER == 0) {
                // change font
                if (word_loop > 0 && word_loop == DEFAULT_LOOP_COUNTER) {
                    //level 2 (less dots level)
                    tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl2.ttf");
                } else if (word_loop > DEFAULT_LOOP_COUNTER && word_loop == DEFAULT_LOOP_COUNTER * 2) {
                    //level 3 (less dots and arrows level)
                    tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl3.ttf");
                } else {
                    //level 4 (Blank level)
                    return null;
                }
            } else {
                tf = typeface;
            }
        } else {
            word.addAchievedChar(word.getText().charAt(currentIndex));
            wordPresenter.saveCharsToArchive(word, currentIndex);
            currentIndex++;
            if(currentIndex >= word.getText().length()) {
                showBreakDialog();
                tf = null;
            }else {
                new Handler().postDelayed(()->{
                    ((MainActivity)getActivity()).openWordAnimationFragment(word, currentIndex);
                    animated = true;
                },2000);

                //return to font level 1 (hollow with arrows)
                tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
            }
        }
        return tf;
    }

    private void showBreakDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.angry_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        FrameLayout yes = dialog.findViewById(R.id.true_btn);
        FrameLayout no = dialog.findViewById(R.id.false_btn);

        yes.setOnClickListener(view -> {
            Log.i(TAG,"In Yes");

            startActivityForResult(new Intent(getActivity(), VideoActivity.class), 1);
            dialog.dismiss();
        });
        no.setOnClickListener(view -> {
            Log.i(TAG,"In No");

            dialog.dismiss();
            ((MainActivity)getActivity()).backToLessonFragment();
        });

        dialog.setOnShowListener(dialogInterface -> {
            ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.angry));
        });
        getView().setEnabled(false);
        new Handler().postDelayed(dialog::show,1500);



    }


    @Override
    public void setmContext(Context context) {

    }

    @Override
    public void setLessonFragment(LessonFragment lessonFragment) {

    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }
}
