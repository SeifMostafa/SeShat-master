package com.example.l.seshatmvp.layout;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.transition.Fade;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.l.seshatmvp.MainActivity;
import com.example.l.seshatmvp.R;

import static com.example.l.seshatmvp.MainActivity.WordKey;

public class WordAnimationFragment extends Fragment {

    private String word;
    private int currentIndex;

    private TextView wordTextView;
    private TextView charactersTextView;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            word = arguments.getString(WordKey);
            currentIndex = arguments.getInt("char_index");
        }
        index = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_animation, container, false);

        wordTextView = view.findViewById(R.id.word);
        charactersTextView = view.findViewById(R.id.characters);

        wordTextView.setText(word);
        for(int i=0;i<word.length();i++){
            charactersTextView.append(String.format("%s%s", word.charAt(i), i < word.length() - 1 ? "  " : ""));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).voiceoffer(wordTextView, word);

        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setDuration(1000);

        charactersTextView.setVisibility(View.VISIBLE);
        charactersTextView.setAnimation(fadeIn);
        fadeIn.start();

        new Handler().postDelayed(()->{
            SpannableString string = new SpannableString(charactersTextView.getText());
            string.setSpan(new ForegroundColorSpan(Color.GREEN),currentIndex+currentIndex*2,currentIndex+currentIndex*2+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            charactersTextView.setText(string, TextView.BufferType.SPANNABLE);
            ((MainActivity)getActivity()).voiceoffer(null, word.charAt(currentIndex) + ".wav");
            backToWordFragment();
        },1200);
//        Handler characterAnimationHandler = new Handler();
//        characterAnimationHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String newChars = charactersTextView.getText() + String.format("%s%s", word.charAt(index), index < word.length() - 1 ? "  " : "");
//                charactersTextView.setText(newChars);
//                ((MainActivity)getActivity()).voiceoffer(null, word.charAt(index) + ".wav");
//                index++;
//                if (index < word.length())
//                    characterAnimationHandler.postDelayed(this, 1000);
//                else
//                    backToWordFragment();
//            }
//        },1000);
    }

    private void backToWordFragment() {
        new Handler().postDelayed(()-> ((MainActivity)getActivity()).backToWordFragment(),1000);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
