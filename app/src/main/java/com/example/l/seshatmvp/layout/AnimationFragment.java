package com.example.l.seshatmvp.layout;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.l.seshatmvp.MainActivity;
import com.example.l.seshatmvp.R;
import com.example.l.seshatmvp.TypeWriter;


public class AnimationFragment extends Fragment {
    public static String AnimationFragment_TAG = "LessonFragment";
    String word;
    TypeWriter custTextView;
    ImageButton helpiBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //getting word text from MainActivity
            this.word = getArguments().getString(MainActivity.WordKey);
            if (word != null) Log.i("AnimationFragment", "Word != null");
            else Log.i("AnimationFragment", "Word = null");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animation, container, false);
        custTextView = view.findViewById(R.id.textView_maintext);
        custTextView.setTypeface(tf);
        custTextView.setVisibility(View.VISIBLE);

        System.out.println("passed word "+word);
        custTextView.setWord(word);
        if (word.contains(" ")) {
            //if word acts as a phrase
            //setting word delay
            custTextView.setCharacterDelay(500);
            //animate phrase
            custTextView.animatePhrase();
        } else {
            //if word acts as a word
            //setting char delay
            custTextView.setCharacterDelay(600);
            //animate word
            custTextView.animateText(word);
        }
//        helpiBtn = getActivity().findViewById(R.id.imagebutton_moreInfo);
//        helpiBtn.setVisibility(View.VISIBLE);
//        helpiBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                helpiBtn.setVisibility(View.GONE);
//                Log.i("helpiBtn", "is clicked!");
//                try {
//                    custTextView.clearAnimation();
//                } catch (Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//                //open help fragment
//                ((MainActivity) getActivity()).openHelpFragment();
//                LessonFragment.wordIsAnimated = false;
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int timeToWaitBeforeOpeningPickPhraseFragment;
        if (word.contains(" ")) {
            //time to wait for a phrase
            int numOfWords = word.split(" ").length;
            timeToWaitBeforeOpeningPickPhraseFragment = (int) (numOfWords * 1550 + (numOfWords)*(400+custTextView.getDelay()))+400;
        } else {
            //time to wait for a word
            timeToWaitBeforeOpeningPickPhraseFragment = (
                    word.length() + 2) * 1200;
        }
        //waiting to finish animation
        Handler handler = new Handler();
        Runnable r = () -> {
            //return Back to lesson fragment
            if (isVisible())
                ((MainActivity) getActivity()).backToLessonFragment();
        };
        handler.postDelayed(r, timeToWaitBeforeOpeningPickPhraseFragment);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        custTextView.stopAnimation();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        System.out.println("destroyed");
        super.onDestroy();
    }
}
