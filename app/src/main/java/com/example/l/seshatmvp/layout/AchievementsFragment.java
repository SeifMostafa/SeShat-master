package com.example.l.seshatmvp.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.seshatmvp.MainActivity;
import com.example.l.seshatmvp.R;
import com.example.l.seshatmvp.Utils.SharedPreferenceUtils;
import com.example.l.seshatmvp.VideoActivity;
import com.example.l.seshatmvp.WordView;
import com.example.l.seshatmvp.model.Word;
import com.example.l.seshatmvp.presenter.WordPresenter;
import com.example.l.seshatmvp.view.MainActivityView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.internal.zzt.TAG;

public class AchievementsFragment extends Fragment implements MainActivityView {

    ExpandableListView achievementsList;
    Map<Integer, Word[]> lessons;
    public Word[] lesson;
    public int wordIndex;
    WordPresenter wordPresenter;
    private View mike_voice_btn;
    private Button teacher_btn_frame;
    private Button book_btn_frame;
    private ImageView teacherBtn;
    private ImageView bookBtn;
    private boolean bouncing;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //getting lesson
            lesson = (Word[]) getArguments().getParcelableArray(MainActivity.LessonKey);
            //getting word index through lesson
            wordIndex = getArguments().getInt(MainActivity.WordIndexKey);
        }
        lessons = ((MainActivity)getActivity()).getLessons();
//        MainActivityView view = this;

//        wordPresenter = new WordPresenter(view, new ImpWordsRepository(getActivity()));
//        wordPresenter.loadArchiveLessons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        bouncing = false;

        teacherBtn = view.findViewById(R.id.teacher_btn);
        teacherBtn.setOnClickListener(view1 -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.angry_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            FrameLayout yes = dialog.findViewById(R.id.true_btn);
            FrameLayout no = dialog.findViewById(R.id.false_btn);

            yes.setOnClickListener(view2 -> {
                Log.i(TAG,"In Yes");

                startActivityForResult(new Intent(getContext(), VideoActivity.class), 2);
                dialog.dismiss();
            });
            no.setOnClickListener(view22 -> {
                Log.i(TAG,"In No");
                dialog.dismiss();
            });
            String emoji = "unhappy";
            Log.i(TAG,"emoji: " + emoji);
            if(emoji.equals("happy")) {
                ((MainActivity) getContext()).voiceoffer(null, getContext().getString(R.string.happy));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                ((MainActivity) getContext()).voiceoffer(null, getContext().getString(R.string.angry));
                dialog.show();

            }
            // request prev word
            /*prevWordCall();
            setteacherBtnVisibilty();
            setNextiBtnVisibility();*/

        });

        bookBtn = view.findViewById(R.id.imagebutton_book);
        bookBtn.setOnClickListener(view12 -> {
            try {
                //return to current lesson
                LessonFragment.isPicked = false;
                SharedPreferenceUtils.getInstance(getContext()).setValue(MainActivity.WordIndexKey, String.valueOf(wordIndex));
                ((MainActivity) getActivity()).updateLesson(0);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn", e.toString());
            }
        });
        achievementsList = view.findViewById(R.id.achievementsList);
        achievementsList.setAdapter(new AchievementsAdapter(getActivity().getApplicationContext(),lessons));

        teacher_btn_frame = view.findViewById(R.id.teacher_btn_frame);
        teacher_btn_frame.setOnClickListener(view78 ->
        {
            stopBounce(teacherBtn);
            stopBounce(bookBtn);

            teacher_btn_frame.setVisibility(View.GONE);
            book_btn_frame.setVisibility(View.GONE);

            achievementsList.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.teacher));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });

        book_btn_frame = view.findViewById(R.id.book_btn_frame);
        book_btn_frame.setOnClickListener(view78 ->
        {
            stopBounce(teacherBtn);
            stopBounce(bookBtn);

            teacher_btn_frame.setVisibility(View.GONE);
            book_btn_frame.setVisibility(View.GONE);

            achievementsList.setEnabled(true);

            try {
                //help instruction for btn played
                ((MainActivity) getActivity()).voiceoffer(null, getActivity().getString(R.string.backcurrentlesson));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CurrentlessoniBtn_help", e.toString());
            }
        });

        mike_voice_btn = view.findViewById(R.id.mike_voice_btn);
        mike_voice_btn.setOnClickListener(view98 ->
        {
            teacher_btn_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);
            book_btn_frame.setVisibility(!bouncing ? View.VISIBLE : View.GONE);

            achievementsList.setEnabled(bouncing);

            if(!bouncing) {
                startBounce(teacherBtn);
                startBounce(bookBtn);
            }else{
                stopBounce(teacherBtn);
                stopBounce(bookBtn);
            }

        });

        View achievementsButton = getActivity().findViewById(R.id.imagebutton_moreInfo);
        achievementsButton.setVisibility(View.GONE);

        return  view;
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

    private void stopBounce(View view)
    {
        bouncing = false;
        if(view != null)view.clearAnimation();
    }

    private void startBounce(View view)
    {
        bouncing = true;
        if (view != null) {
            Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            view.startAnimation(bounce);

        }
    }

    @Override
    public void DisplayLessons(Map<Integer, Word[]> lessons) {

    }

    @Override
    public void DisplayNoLessons() {

    }

    @Override
    public void DisplayArchiveWords(List<String> words) {

    }

    @Override
    public void DisplayNoArchiveWords() {

    }

    @Override
    public void DisplayArchiveLessons(Map<Integer, String[]> lessons) {
//        this.lessons = lessons;
    }


    private class AchievementsAdapter extends BaseExpandableListAdapter{

        private Context context;
        private Map<Integer, Word[]> achievements;

        private AchievementsAdapter(Context context, Map<Integer, Word[]> achievements) {
            this.context = context;
            this.achievements = achievements;
        }

        @Override
        public int getGroupCount() {
            return achievements.size();
        }

        @Override
        public int getChildrenCount(int i) {
            int numOfStrings = achievements.get(i).length;
            System.out.println("Number of Strings in lesson "+i+" are "+numOfStrings);
            return numOfStrings;
        }

        @Override
        public String getGroup(int i) {
            return achievements.get(i)[0].getPhrase();
        }

        @Override
        public Word getChild(int i, int i1) {
            Word chosenString = achievements.get(i)[i1];
            System.out.println("String in "+i+", "+i1+" "+chosenString.getText());
            return chosenString;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.achievement_item,null,false);
            }
            TextView textView = view.findViewById(R.id.achievement);
            ImageView repeatButton = view.findViewById(R.id.repeat);

            textView.setText(getGroup(i));

            repeatButton.setOnClickListener(view1 -> {
                //return back to previous lesson
                ((MainActivity) getActivity()).openLessonFragment(i,0,true,true);
            });

            if(i>0 && !getChild(i,0).isHalfAchieved() && !getChild(i-1,getChildrenCount(i-1)-1).isAchieved()){
                //TODO::put grey shade on repeat button
                repeatButton.setVisibility(View.INVISIBLE);
                repeatButton.setEnabled(false);
            }else{
                repeatButton.setVisibility(View.VISIBLE);
                repeatButton.setEnabled(true);
            }
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.layout_word_listview_ite,null,false);
            }
            TextView textView = view.findViewById(R.id.textView_word_item_txt);
            ImageView playButton = view.findViewById(R.id.imageButton_word_item_soundhelp);
            ImageView repeatButton = view.findViewById(R.id.imageButton_word_item_back_to);

            playButton.setImageResource(R.drawable.play_btn);
            repeatButton.setImageResource(R.drawable.repeat);
            Word word = getChild(i,i1);
            textView.setText(word.getText());

            playButton.setOnClickListener(view1 -> {
                try {
                    //play sound for the word selected
                    ((MainActivity) getActivity()).voiceoffer(playButton, word.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("imageButton_sound_help", e.toString());
                }
            });
            repeatButton.setOnClickListener(view12 -> {
                try {
                    //open word again for just writing it
                    ((MainActivity) getActivity()).openLessonFragment(i,i1,true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("imageButton_redo", e.toString());
                }
            });
            //TODO::Put grey shade on word as disabled
            view.setEnabled(word.isHalfAchieved());
            repeatButton.setVisibility((word.isHalfAchieved() ? View.VISIBLE : View.GONE));
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
