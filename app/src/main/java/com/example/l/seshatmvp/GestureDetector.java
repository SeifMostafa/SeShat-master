package com.example.l.seshatmvp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.example.l.seshatmvp.model.Direction;
import com.example.l.seshatmvp.model.PointDirection;
import com.example.l.seshatmvp.model.ResponseBodyModel;
import com.example.l.seshatmvp.presenter.WrongDirectionsPresenter;
import com.example.l.seshatmvp.repositories.ServiceRepository;
import com.example.l.seshatmvp.repositories.imp.ImpWrongDirectionsRepo;
import com.example.l.seshatmvp.view.GestureDetectorView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by seif on 10/9/17.
 */

public class GestureDetector implements GestureDetectorView{

    int counter = 0;
    private double THRESHOLD;
    private ArrayList<Direction> wholeWord;
    private ArrayList<Direction> noMatterForwholeWord;
    private ArrayList<Direction> noMatterForUserWord;
    private HashMap<String, Integer> wrongDirectionsCounter = new HashMap<>();
    WrongDirectionsPresenter presenter;
    GestureDetectorView view;
    String TAG = "GestureDetector";

    //constructor
    GestureDetector(Direction[][] Gesture)
    {
        view = this;        //getting word directions
        wholeWord = new ArrayList<>();
        for (Direction[] aGesture : Gesture) {
            wholeWord.addAll(Arrays.asList(aGesture));
        }
        if (this.wholeWord.size() < 2)
            Log.i("GestureDetector", "Hasn't gesture to detect!");
        System.out.println("Word size "+wholeWord.size());
        //setting customized Threshold for each word
        THRESHOLD += 70+Gesture.length * (wholeWord.size() >0? (10 / wholeWord.size()):0);
        System.out.println("TH "+THRESHOLD);
    }

    //ignore directions that does not exist in user directions that between letters
    void removeLeftUp(int index,ArrayList<Direction> mUser)
    {

        if(index < mUser.size()-5)
        {
            Direction currentX = wholeWord.get(index);
            Direction currentY = wholeWord.get(index+1);
            Direction nextX = wholeWord.get(index+2);
            Direction nextY = wholeWord.get(index+3);
            Direction afterNextX = wholeWord.get(index+4);
            Direction afterNextY = wholeWord.get(index+5);

            Direction currentUserX = mUser.get(index);
            Direction currentUserY = mUser.get(index+1);
            Direction nextUserX = mUser.get(index+2);
            Direction nextUserY = mUser.get(index+3);
            Direction afterNextUserX = mUser.get(index+4);
            Direction afterNextUserY = mUser.get(index+5);

            Log.i(TAG,"From removeLeftUp Original: "+ currentX+ ", " + currentY+ ", " + nextX + ", " + nextY + afterNextX + ", " + afterNextY);
            Log.i(TAG,"From removeLeftUp User: "+ currentUserX+ ", " + currentUserY+ ", " + nextUserX + ", " + nextUserY+ afterNextUserX + ", " + afterNextUserY);

            boolean isEqual = currentX == currentUserX && currentY == currentUserY && nextX == nextUserX && nextY == nextUserY && afterNextX == afterNextUserX && afterNextY == afterNextUserY;

            boolean isLeftSameCurrent = currentX == Direction.LEFT && currentY == Direction.SAME;
            boolean isSameUpNext = nextX == Direction.SAME && nextY == Direction.UP;
            boolean isSameUpAfterNext = afterNextX == Direction.SAME && afterNextY == Direction.UP;
            boolean isLeftSameNext = nextX == Direction.LEFT && nextY == Direction.SAME;

            boolean isLeftSameUserCurrent = currentUserX == Direction.LEFT && currentUserY == Direction.SAME;
            boolean isSameUpUserNext = nextUserX == Direction.SAME && nextUserY == Direction.UP;
            boolean isSameUpUserAfterNext = afterNextUserX == Direction.SAME && afterNextUserY == Direction.UP;
            boolean isLeftSameUserNext = nextUserX == Direction.LEFT && nextUserY == Direction.SAME;

            if(isEqual)
                return;

            if(!isLeftSameUserCurrent || !isSameUpUserNext)
            {
                if(isLeftSameCurrent && isSameUpNext)
                {

                    wholeWord.remove(index+3);
                    wholeWord.remove(index+2);
                    wholeWord.remove(index+1);
                    wholeWord.remove(index);
                    return;
                }
            }
            if(!isLeftSameUserCurrent || !isLeftSameUserNext || !isSameUpUserAfterNext)
            {
                if (isLeftSameCurrent && isLeftSameNext) {
                    if(isSameUpAfterNext)
                    {
                        wholeWord.remove(index+5);
                        wholeWord.remove(index+4);
                    }
                    wholeWord.remove(index + 3);
                    wholeWord.remove(index + 2);
                    return;
                }
            }
        }
        else if (index<mUser.size()-3)
        {
            Direction currentX = wholeWord.get(index);
            Direction currentY = wholeWord.get(index+1);
            Direction nextX = wholeWord.get(index+2);
            Direction nextY = wholeWord.get(index+3);

            Direction currentUserX = mUser.get(index);
            Direction currentUserY = mUser.get(index+1);
            Direction nextUserX = mUser.get(index+2);
            Direction nextUserY = mUser.get(index+3);

            Log.i(TAG,"From removeLeftUp Original: "+ currentX+ ", " + currentY+ ", " + nextX + ", " + nextY );
            Log.i(TAG,"From removeLeftUp User: "+ currentUserX+ ", " + currentUserY+ ", " + nextUserX + ", " + nextUserY);

            boolean isEqual = currentX == currentUserX && currentY == currentUserY && nextX == nextUserX && nextY == nextUserY;

            boolean isLeftSameCurrent = currentX == Direction.LEFT && currentY == Direction.SAME;
            boolean isSameUpNext = nextX == Direction.SAME && nextY == Direction.UP;
            boolean isLeftSameNext = nextX == Direction.LEFT && nextY == Direction.SAME;

            boolean isLeftSameUserCurrent = currentUserX == Direction.LEFT && currentUserY == Direction.SAME;
            boolean isSameUpUserNext = nextUserX == Direction.SAME && nextUserY == Direction.UP;
            boolean isLeftSameUserNext = nextUserX == Direction.LEFT && nextUserY == Direction.SAME;

            if(isEqual)
                return;

            if(!isLeftSameUserCurrent || !isSameUpUserNext)
            {
                if(isLeftSameCurrent && isSameUpNext)
                {

                    wholeWord.remove(index+3);
                    wholeWord.remove(index+2);
                    wholeWord.remove(index+1);
                    wholeWord.remove(index);
                    return;
                }
            }
            if(!isLeftSameUserCurrent || !isLeftSameUserNext)
            {
                if(isLeftSameCurrent && isLeftSameNext)
                {
                    wholeWord.remove(index + 3);
                    wholeWord.remove(index + 2);
                    return;
                }
            }
        }

    }

    //remove points from the Original word directions to another ArrayList
    void removeNoMatterFromOrg()
    {
        noMatterForwholeWord = new ArrayList<>();
        for(int i = 0;i < wholeWord.size()-1;)
        {
            Log.i(TAG,"From no matter wholeWord: " + wholeWord.get(i) + ", " + wholeWord.get(i+1));

            if(wholeWord.get(i) == Direction.NOMATTER && (wholeWord.get(i+1) == Direction.UP ||wholeWord.get(i+1) == Direction.DOWN))
            {

                noMatterForwholeWord.add(wholeWord.get(i));
                noMatterForwholeWord.add(wholeWord.get(i+1));

                wholeWord.remove(i+1);
                wholeWord.remove(i);

            }

            else
                i+=2;
        }
    }

    //remove points from the user word directions to another ArrayList
    ArrayList<Direction> removeNoMatterFromUser(ArrayList<Direction>User)
    {  Log.i(TAG, "mUserGV before= " + User.toString());

        noMatterForUserWord = new ArrayList<>();
        for(int i = 0;i < User.size()-1;)
        {

            Log.i(TAG,"From no matter user: " + User.get(i) + ", " + User.get(i+1));
            if(User.get(i) == Direction.NOMATTER &&(User.get(i+1) == Direction.UP ||User.get(i+1) == Direction.DOWN))
            {

                noMatterForUserWord.add(User.get(i));
                noMatterForUserWord.add(User.get(i+1));

                User.remove(i+1);
                User.remove(i);

            }

            else
                i+=2;
        }
        return User;
    }



    //check user directions and word directions after remove the unwanted directions and points
    boolean check(ArrayList<Direction> mUserGV)
    {

        Log.i(TAG, "wholeWord before= " + wholeWord.toString());
        removeNoMatterFromOrg();
        mUserGV = removeNoMatterFromUser(mUserGV);
        Log.i(TAG, "wholeWord.size() = " + this.wholeWord.size());
        Log.i(TAG, "mUserGV.size() = " + mUserGV.size());
        Log.i(TAG, "noMatterForwholeWord.size() = " + this.noMatterForwholeWord.size());
        Log.i(TAG, "noMatterForUserWord.size() = " + this.noMatterForUserWord.size());
        Log.i(TAG, "mUserGV = " + mUserGV.toString());
        Log.i(TAG, "wholeWord = " + wholeWord.toString());
        Log.i(TAG, "noMatterForwholeWord = " + noMatterForwholeWord.toString());
        Log.i(TAG, "noMatterForUserWord = " + noMatterForUserWord.toString());

        double successPercentage = 100;
        double progressStep = (100.0 / (double) wholeWord.size());
        Log.i(TAG,"progressStep = " +progressStep);
        boolean isDetected = false;
        try {

            if (mUserGV.size() <= wholeWord.size()) {
                int i = 0;
                if(mUserGV.size() < 2)
                {
                    isDetected = false;
                    return isDetected;
                }
                //when painted directions less than the actual one
                for (; i < mUserGV.size() - 1; i += 2) {
                    Direction d_X = mUserGV.get(i);
                    Direction d_Y = mUserGV.get(i + 1);
                    Direction ORG_d_X = wholeWord.get(i);
                    Direction ORG_d_Y = wholeWord.get(i + 1);
                    try {
                        //start comparing
                        if(ORG_d_X == Direction.LEFT && ORG_d_Y == Direction.SAME)
                        {
                            removeLeftUp(i,mUserGV);
                            Log.i(TAG,"WholeWord Size: " + wholeWord.size());
                            if(wholeWord.size() < mUserGV.size())break;
                        }
                            if (ORG_d_X == Direction.NOMATTER || ORG_d_Y == Direction.NOMATTER) {
                            //when point is a dot
                            if (ORG_d_X == Direction.NOMATTER && ORG_d_Y != Direction.NOMATTER) {
                                if (d_Y != ORG_d_Y) {
                                    //when painted not equal actual
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                        checkWrongDirection(ORG_d_X, ORG_d_Y);
                                    }
                                }
                            } else if (ORG_d_X != Direction.NOMATTER) {
                                if (d_X != ORG_d_X) {
                                    //when painted not equal actual
                                    if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                        successPercentage -= progressStep;
                                        checkWrongDirection(ORG_d_X, ORG_d_Y);
                                    }
                                }
                            }
                        } else {
                            //word at any direction
                            if ((d_X != ORG_d_X || d_Y != ORG_d_Y)) {
                                if (!approximateCheck(mUserGV, ORG_d_X, ORG_d_Y, i)) {
                                    //when painted not equal actual
                                    successPercentage -= progressStep;
                                    checkWrongDirection(ORG_d_X, ORG_d_Y);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("GestureDetector", "CompareGuidedVector" + e.toString());
                    }

                }
//                if(i < wholeWord.size())
//                {
//                    successPercentage -= (progressStep * (wholeWord.size()-i)/2);
//                }

                if(i < mUserGV.size())
                {
                    isDetected = false;
                    return isDetected;
                }
                int counterPoints = checkPoints();
                Log.i(TAG,"counter Points: " + counterPoints);
                if(noMatterForwholeWord.size() !=  noMatterForUserWord.size())
                {
                    // successPercentage -= progressStep;
                    successPercentage -= (progressStep * counterPoints);
                }
                successPercentage -= (progressStep*((Math.abs(wholeWord.size()-mUserGV.size()))/2));

                if (successPercentage > THRESHOLD) {
                    isDetected = true;
                }

                Log.i(TAG, "successPercentage = " + successPercentage);
                Log.i(TAG, "THRESHOLD = " + THRESHOLD);
                Log.i(TAG, "isDetected = " + isDetected);

            } else {

                Log.i("GestureDetector", "check " + "shortage in touched points data");

            }
        } catch (Exception e) {
            Log.e("GestureDetector", "check:: e: " + e.getMessage());
        }
        return isDetected;
    }


    //check 2 ArrayLists that contain points from user and original word
    private int checkPoints()
    {
        int counter = 0;
        if(noMatterForwholeWord.size() == noMatterForUserWord.size())
        {
            for(int i = 0 ; i < noMatterForwholeWord.size()-1;i+=2)
            {
                if(noMatterForUserWord.get(i) != noMatterForwholeWord.get(i))
                {
                    counter++;
                }

            }
        }
        else
        {
            counter = (Math.abs(noMatterForwholeWord.size() -  noMatterForUserWord.size())/2);
        }
        return counter;
    }

    private void checkWrongDirection(Direction ORG_d_X, Direction ORG_d_Y) throws IOException
    {
        presenter = new WrongDirectionsPresenter(new ImpWrongDirectionsRepo(), view);
        if (wrongDirectionsCounter.isEmpty()) {
            presenter.loadwrongDirections();
        }
        if (wrongDirectionsCounter.containsKey((ORG_d_X.toString()) + (ORG_d_Y.toString()))) {
            counter = wrongDirectionsCounter.get((ORG_d_X.toString()) + (ORG_d_Y.toString()));
            wrongDirectionsCounter.replace((ORG_d_X.toString()) + (ORG_d_Y.toString()), counter, ++counter);
        } else {
            counter = 0;
            wrongDirectionsCounter.put((ORG_d_X.toString()) + (ORG_d_Y.toString()), ++counter);
        }
        Log.i("GestureDetector", wrongDirectionsCounter.toString());
        presenter.saveWrongDirections(wrongDirectionsCounter);
    }

    //approximate check
    private boolean approximateCheck(ArrayList<Direction> mUserGV, Direction XDirection, Direction YDirection, int index)
    {
        boolean isDetected = false;
        if (index + 3 <= mUserGV.size()) {
            Direction nextDx, nextDy, currentDx, currentDy;

            nextDx = mUserGV.get(index + 2);
            nextDy = mUserGV.get(index + 3);

            currentDx = mUserGV.get(index);
            currentDy = mUserGV.get(index + 1);

            if (nextDx == Direction.SAME || currentDx == Direction.SAME) {
                if (nextDx == Direction.SAME) {
                    if ((XDirection == currentDx || XDirection == Direction.NOMATTER) && (YDirection == currentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((XDirection == nextDx || XDirection == Direction.NOMATTER) && (YDirection == currentDy || YDirection == nextDy || YDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            } else if (nextDy == Direction.SAME || currentDy == Direction.SAME) {
                if (nextDy == Direction.SAME) {
                    if ((YDirection == currentDy || YDirection == Direction.NOMATTER) && (XDirection == currentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                } else {
                    if ((YDirection == nextDy || YDirection == Direction.NOMATTER) && (XDirection == currentDx || XDirection == nextDx || XDirection == Direction.NOMATTER))
                        isDetected = true;
                }
            }
        }
        return isDetected;
    }


@Override
public void writeMapToFile(HashMap<String, Integer> hashMap) {
        wrongDirectionsCounter = hashMap;
        }

}