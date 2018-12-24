package com.example.l.seshatmvp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.seshatmvp.model.Direction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.l.seshatmvp.EzCam.*;
import com.example.l.seshatmvp.model.FullTextAnnotationModel;
import com.example.l.seshatmvp.model.ResponseBodyModel;
import com.example.l.seshatmvp.repositories.KairosListener;
import com.example.l.seshatmvp.repositories.ServiceRepository;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Math.abs;
import static java.lang.Math.pow;


public class WordView extends TextView{

    String originalWord = null;
    private static float POINT_WIDTH = 0;
    public int word_loop = 0;
    Context context;
    ArrayList<Direction> mUserGuidedVectors;
    UpdateWord updateWord;
    ImageButton successBtn, fontBtn;
    Typeface newTypeface = null;
    int indx = 0;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private float mX, mY, mFingerFat;
    private Point lastPoint;
    private ArrayList<Point> mTouchedPoints;
    private GestureDetector mGestureDetector;
    public static ArrayList<String> Expressions = new ArrayList<>();
    private ArrayList<Point> mTouchedPointsForCheck = new ArrayList<>();
    ArrayList<MyPoints> mPoints = new ArrayList<>();
    private Map<Integer, Direction[][]> gesture;
    private int gestureSize = 0;


    private EZCam cam;

    TextureView textureView;
    private final String TAG2 = "WordView";
    private final String TAG = "OCR";

    JSONObject responseObject = null;
    KairosListener kairosListener;
    String app_id = "ebb96702";
    String api_key = "a61f4b4ef5e4d91416cd26c6ce8992d5";
    private int defaultTypeface;
    private int defaultLoop;


    public WordView(Context context) throws IOException {
        super(context);
        this.context = context;
        init();

    }

    public WordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
       this.context = context;

        init();
    }

    public WordView( Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        init();
    }

    /*  public WordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
          super(context, attrs, defStyleAttr, defStyleRes);
          this.context = context;

          init();
      }*/
//defining all attributes
    public void init() {


        kairosListener = new KairosListener() {
            @Override
            public void onSuccess(String response) {
                //Log.i("KAIROS DEMO", response);
                //                try {
                //                    String status = null;
                //                    responseObject = new JSONObject(response);
                //                    JSONArray frames =  responseObject.getJSONArray("frames");
                //                    if(frames.length() > 0)
                //                    {
                //                        JSONArray people = frames.getJSONArray(0);
                //                        if(people.length() > 0)
                //                        {
                //                            JSONObject temp = people.getJSONObject(0);
                //                            JSONObject emotions = temp.getJSONObject("emotions");
                //                            Map<String,Double> emotionsStat = new HashMap<>();
                //                            emotionsStat.put("anger",emotions.getDouble("anger"));
                //                            emotionsStat.put("disgust",emotions.getDouble("disgust"));
                //                            emotionsStat.put("fear",emotions.getDouble("fear"));
                //                            emotionsStat.put("joy",emotions.getDouble("joy"));
                //                            emotionsStat.put("sadness",emotions.getDouble("sadness"));
                //                            emotionsStat.put("surprise",emotions.getDouble("surprise"));
                //                            double max = 0;
                //                            status = null;
                //                            for (Map.Entry<String, Double> entry : emotionsStat.entrySet())
                //                            {
                //                                if(entry.getValue() > max)
                //                                {
                //                                    max = entry.getValue();
                //                                    status = entry.getKey();
                //                                }
                //                            }
                //                            if(max == 0 || status == null)
                //                            {
                //                                status = "confused";
                //                            }
                //
                //                        }
                //                        else
                //                        {
                //                            status = "distracted";
                //                        }
                //
                //                    }
                //                    else
                //                    {
                //                        status = "distracted";
                //                    }
                //                    Expressions.add(status);
                //                } catch (JSONException e) {
                //                    e.printStackTrace();
                //                }
                //                System.out.println("onSuccess: " + response);
            }

            @Override
            public void onFail(String response) {
                Log.i("KAIROS DEMO", response);
                System.out.println("onFail Response: " + response);
            }
        };
        textureView = ((MainActivity)context).findViewById(R.id.textureView);

//        cam = new EZCam(context);
//        cam.setCameraCallback(this);
//        String id = cam.getCamerasList().get(CameraCharacteristics.LENS_FACING_FRONT);
//        cam.selectCamera(id);
//        Log.i(TAG2,"Cam is: " + (cam == null ? "Null": "Not Null"));
//        Dexter.withActivity((MainActivity) context).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onPermissionGranted(PermissionGrantedResponse response) {
//
//                cam.open(CameraDevice.TEMPLATE_PREVIEW, textureView);
//
//            }
//
//            @Override
//            public void onPermissionDenied(PermissionDeniedResponse response) {
//                Log.e(TAG2, "permission denied");
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                token.continuePermissionRequest();
//            }
//        }).check();
        newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
        this.setTypeface(newTypeface);
        Log.i("init", "AM HERE!");
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(8f);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);
        POINT_WIDTH = 1f;
        mUserGuidedVectors = new ArrayList<>();
        setTextColor(Color.BLACK);

        //valid for testing
        fontBtn = ((MainActivity) context).findViewById(R.id.imagebutton_skipFont);
        fontBtn.setOnClickListener(view -> {
            indx++;
            try {
                fontCreator();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void fontCreator() throws InterruptedException {
        setTextColor(Color.BLACK);
        if (indx == 0) {
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl1.ttf");
            word_loop = 0;
        } else if (indx == 1) {
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl2.ttf");
            setTypeface(newTypeface);
            word_loop = 2;
        } else if (indx == 2) {
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/lvl3.ttf");
            setTypeface(newTypeface);
            word_loop = 4;
        } else if (indx == 3) {
            setTextColor(Color.TRANSPARENT);
            word_loop = 6;
        }else if(indx == 4)
        {
            word_loop = 8;
            getInterface();
            this.indx = 0;
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onCameraReady() {
//        cam.setCaptureSetting(CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE, CameraMetadata.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY);
//        cam.startPreview();
//
//        // Delay the handler by 500 ms to take clear pictures.
//        final int delay = 5000;
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                ((MainActivity)context).runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            cam.takePicture();
//                            //Toast.makeText(context, "Picture taken", Toast.LENGTH_LONG).show();
//                        }catch (Exception e){
//                            Log.e(TAG2 +" takePicture: ",e.toString());
//                        }
//                    }
//                });
//
//
//            }
//        }, delay);
//    }

//    @Override
//    public void onPicture(Image image) {
//        try{
//           // cam.stopPreview();
//
//
//        }
//        catch(Exception e)
//        {
//            Log.e(TAG2,e.toString());
//        }
//
////        getEmoji(image);
//
//       // Toast.makeText(context, state, Toast.LENGTH_LONG).show();
//    }

    private void getEmoji(Image image) {
        String fname = "person.jpg";
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,null);
        Matrix matrix = new Matrix();

        matrix.postRotate(90);


        Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
        SaveImage(rotatedBitmap,fname,Bitmap.CompressFormat.JPEG);
        Kairos myKairos = new Kairos();

        myKairos.setAuthentication(getContext(), app_id, api_key);

        try {

            //myKairos.detect(myBitmap, "ROLL", null, kairosListener);
            String root = Environment.getExternalStorageDirectory().toString();
            myKairos.media(root+"/SeShatSF/"+fname,kairosListener);

        }catch (Exception e) {
            e.printStackTrace();
        }



//        ((ImageView)((MainActivity)context).findViewById(R.id.image)).setImageBitmap(myBitmap);


    }

//    @Override
//    public void onCameraDisconnected() {
//        Log.e(TAG2, "Camera disconnected");
//    }
//
//    @Override
//    public void onError(String message) {
//        Log.e(TAG2, message);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    //reset all attributes
    public void reset() {
        mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        invalidate();
        gestureSize = 0;
        mUserGuidedVectors.clear();
        mPoints.clear();
        mTouchedPoints.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);

    }

    //after putting your first point
    private void touch_start(float x, float y, float ff) {
        if(!this.isEnabled())
            return;

        mPath.reset();
        mPath.moveTo(x, y);

        mX = x;
        mY = y;
        mFingerFat = ff;
        mPath.addCircle(mX, mY, POINT_WIDTH, Path.Direction.CW);
        mTouchedPoints = new ArrayList<>();
        //add the point touched
        mTouchedPoints.add(new Point((int) x, (int) y));

    }

    //after moving from your point (Drag)
    private void touch_move(float x, float y) {
        if(!this.isEnabled())
            return;
        float dx = abs(x - mX);
        float dy = abs(y - mY);
        if (dx >= mFingerFat || dy >= mFingerFat) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            circlePath.reset();
            circlePath.addCircle(mX, mY, 20, Path.Direction.CW);
            //add points touched
            mTouchedPoints.add(new Point((int) x, (int) y));
        }

    }




    //change points to directions
    private ArrayList<Direction> appendPoints(ArrayList<Point> touchedPoints, ArrayList<Direction> outputUserGV) {
        if(touchedPoints.size() == 1)
        {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            Direction temp = null;
            if(touchedPoints.get(0).y < (height/2))
            {

                temp = Direction.UP;
            }
            else
            {
                temp = Direction.DOWN;
            }
           if(!inPointArray(mPoints,touchedPoints.get(0)))
            {
                mPoints.add(new MyPoints(touchedPoints.get(0),Direction.NOMATTER,temp));
                outputUserGV.add(Direction.NOMATTER);
                outputUserGV.add(temp);

            }
        }
        else if (touchedPoints.size() >= 2) {
            mTouchedPointsForCheck.addAll(touchedPoints);
            for (int i = 0; i < touchedPoints.size() - 1; i++) {
                Point point1 = touchedPoints.get(i);
                Point point2 = touchedPoints.get(i + 1);
                Direction[] directions = ComparePointsToCheckFV(point1, point2);
                if(directions == null) break;
                Direction XDirection = directions[0];
                Direction YDirection = directions[1];
                if (outputUserGV.size() > 0) {
                    if ((outputUserGV.get(outputUserGV.size() - 2) != XDirection || outputUserGV.get(outputUserGV.size() - 1) != YDirection) &&
                            ((XDirection != Direction.SAME && YDirection == Direction.SAME)||(XDirection == Direction.SAME && YDirection != Direction.SAME)) ) {

                        Log.i(TAG2,"FROM Last If");
                        Log.i(TAG2,"XDirection = " +XDirection);
                        Log.i(TAG2,"YDirection = " +YDirection);

                        mPoints.add(new MyPoints(point1,XDirection,YDirection));
                        mPoints.add(new MyPoints(point2,XDirection,YDirection));
                        outputUserGV.add(XDirection);
                        outputUserGV.add(YDirection);
                    }
                    else if((XDirection == Direction.SAME && YDirection == Direction.DOWN) || (XDirection == Direction.SAME && YDirection == Direction.UP))
                    {

                        Log.i(TAG2,"FROM 1st If");
                        Log.i(TAG2,"XDirection = " +XDirection);
                        Log.i(TAG2,"YDirection = " +YDirection);
                        if(notSameLineX(point1,mPoints.get(mPoints.size()-1).point)&& notSameLineX(point2,mPoints.get(mPoints.size()-1).point)) {
                            mPoints.add(new MyPoints(point1, XDirection, YDirection));
                            mPoints.add(new MyPoints(point2, XDirection, YDirection));
                            outputUserGV.add(XDirection);
                            outputUserGV.add(YDirection);
                        }
                    }
                    else if((XDirection == Direction.LEFT && YDirection == Direction.SAME )||(XDirection == Direction.RIGHT && YDirection == Direction.SAME ) )
                    {
                        Log.i(TAG2,"FROM 2nd If");
                        Log.i(TAG2,"XDirection = " +XDirection);
                        Log.i(TAG2,"YDirection = " +YDirection);

                        if(notSameLineY(point1,mPoints.get(mPoints.size()-1).point)&& notSameLineY(point2,mPoints.get(mPoints.size()-1).point)) {
                            mPoints.add(new MyPoints(point1, XDirection, YDirection));
                            mPoints.add(new MyPoints(point2, XDirection, YDirection));
                            outputUserGV.add(XDirection);
                            outputUserGV.add(YDirection);
                        }
                    }

                } else {
                    if ((XDirection != Direction.SAME || YDirection != Direction.SAME)) {
                        mPoints.add(new MyPoints(point1,XDirection,YDirection));
                        mPoints.add(new MyPoints(point2,XDirection,YDirection));
                        outputUserGV.add(XDirection);
                        outputUserGV.add(YDirection);
                    }
                   /* else if ((XDirection == Direction.SAME && YDirection == Direction.SAME)) {
                        outputUserGV.add(XDirection);
                        outputUserGV.add(YDirection);
                    }*/
                }

            }
            lastPoint = new Point(touchedPoints.get(touchedPoints.size() - 1));
        } else {

            // single point
            if (outputUserGV.size() != 0) {
                Log.i("WordView","LastPoint: " + lastPoint.toString());
                Direction[] directions = ComparePointsToCheckFV(lastPoint, touchedPoints.get(touchedPoints.size() - 1));
                if(directions != null)
                {
                    mPoints.add(new MyPoints(lastPoint,directions[0],directions[1]));
                    outputUserGV.add(directions[0]);
                    outputUserGV.add(directions[1]);
                }
            }
        }
        Log.i(TAG2,"mPoints: " + mPoints.toString());
        Log.i(TAG2,"outputUserGV: " +outputUserGV.toString());
        return outputUserGV;
    }


    private boolean notSameLineX(Point p1,Point p2)
    {
        Log.i(TAG2,"abs(p1.x- p2.x): " + abs(p1.x- p2.x));
        return abs(p1.x- p2.x) > 2*mFingerFat ;
    }

    private boolean notSameLineY(Point p1,Point p2)
    {
        Log.i(TAG2,"abs(p1.y-p2.y): " + abs(p1.y-p2.y));
        return  abs(p1.y-p2.y) > 2*mFingerFat;
    }


    private boolean inPointArray(ArrayList<MyPoints> mPoints, Point point) {
        for(MyPoints p : mPoints)
        {
            Log.i(TAG2,"Points in Function: " + p.getPoint().toString()+", "+ point.toString());

            double distance= Math.sqrt((pow((point.x-p.getPoint().x),2))+(pow((point.y-p.getPoint().y),2)));
            if(distance <= 5)
            {
                return true;
            }
        }
        return false;
    }

    /*
        called every time user touch screen to draw something and up his/her finger
     */
    //check directions
    private void wholeCheck(ArrayList<Direction> outputUserGV) {

        if (gestureSize == 0) {
            //filling of actual directions
            for (int j = 0; j < gesture.get(0).length; j++) {
                gestureSize += gesture.get(0)[j].length;
            }
        }
        try {

            boolean checkResult = mGestureDetector.check(outputUserGV);

            /* take action upon the checkResult: update word/update char */
            Log.i("WordView", " gestureSize/gesture.get(0).length =  " + gestureSize / gesture.get(0).length);
            Log.i("WordView", " gestureSize-outputUserGV.size() =  " + (gestureSize - outputUserGV.size()));

            if (!checkResult) {
                int trials = 1;  // already checked for 1st time
                while (trials < gesture.size()) {
                    //check other versions
                    GestureDetector GD_otherV = new GestureDetector(gesture.get(trials++));
                    //  if(charsPassed+2==gesture.get(0).length) GD_otherV.setThreshold(50);
                    if (GD_otherV.check(outputUserGV)) {
                        Log.i("WordView", "tryOtherVersions:: trials:Success in trial: " + trials);
                        checkResult = true;
                        break;
                    }
                }
            }
            if (checkResult) {
                successBtn.setBackground(context.getDrawable(R.drawable.success_btn));
                successBtn.setEnabled(false);
                new Handler().postDelayed(()->
                {
                    successBtn.setEnabled(true);
                    successBtn.setVisibility(INVISIBLE);
                    successBtn.setBackground(context.getDrawable(R.drawable.done));
                },(word_loop+1 < (defaultLoop * defaultTypeface)) ? 2000:1500);

                gestureSize = 0;
                reset();
                Log.i("WordView", " completed ");
                if(word_loop+1 < (defaultLoop * defaultTypeface))
                    ((MainActivity) context).voiceoffer(null, context.getString(R.string.congrats));
                else{
                    ((MainActivity) context).voiceoffer(null, context.getString(R.string.final_congrats));
                }
                // ((MainActivity)context).SaveOnSharedPref(MainActivity.WordLoopKey, String.valueOf(word_loop));
                getInterface();


            } else {
                successBtn.setBackground(context.getDrawable(R.drawable.fail_btn));
                successBtn.setEnabled(false);
                new Handler().postDelayed(()->
                {
                    successBtn.setEnabled(true);
                    successBtn.setVisibility(INVISIBLE);
                    successBtn.setBackground(context.getDrawable(R.drawable.done));
                },2000);

                Log.i("WordView", "Try Again");
                ((MainActivity) context).voiceoffer(null, context.getString(R.string.tryAgain));

                reset();
                outputUserGV.clear();
            }
            mGestureDetector = new GestureDetector(gesture.get(0));
        } catch (Exception e) {
            Log.e("WordView: ", "error5: " + e.toString());
        }
    }

    private void getInterface() {
        newTypeface = updateWord.updateWordLoop(this.getTypeface(), ++word_loop);

        if(word_loop % 2 == 0)indx++;

        if (newTypeface != null) {
            this.setTypeface(newTypeface);
            invalidate();
        } else {
            if(word_loop >= 8){
                newTypeface = Typeface.createFromAsset( context.getAssets(), "fonts/lvl1.ttf");
                this.setTextColor(Color.TRANSPARENT);
            }
            if (word_loop != 0) {
                this.setTextColor(Color.TRANSPARENT);

                /*if(word_loop >  8)
                {
                 Thread.sleep(2500);
                 ((MainActivity) context).voiceoffer(null, context.getString(R.string.said_what_you_learned));
                 Thread.sleep(2100);
                 ((MainActivity) context).promptSpeechInput();
                }*/
                this.invalidate();
                Log.i(TAG2, "from touch_up: blank level");
            }
        }
    }
    private void touch_up() {
        if(!this.isEnabled())
            return;
        mPath.lineTo(mX, mY);

        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        Log.i("WordView","mTouchedPoints: "+ mTouchedPoints.toString());
        mUserGuidedVectors = appendPoints(mTouchedPoints, mUserGuidedVectors);
        Log.i("WordView","mUserGuidedVectors: "+ mUserGuidedVectors.toString());
        checkSize(mUserGuidedVectors);

    }

    private void checkSize(final ArrayList<Direction> outputUserGV) {
        if (gestureSize == 0) {
            for (int j = 0; j < gesture.get(0).length; j++) {
                for (int l = 0; l < gesture.get(0)[j].length; l++) {
                    gestureSize++;
                }
            }
        }
        Log.i(TAG2,"Length: " +( gestureSize - outputUserGV.size()));
        Log.i(TAG2,"Size of Word: " +(gestureSize/gesture.get(0).length));

        if (gestureSize - (outputUserGV.size()+15) <= (gestureSize/gesture.get(0).length)) {
            successBtn.setVisibility(VISIBLE);


            successBtn.setOnClickListener(view -> {

                mPoints.clear();


                SaveImage(mBitmap,"temp.png",Bitmap.CompressFormat.PNG);
                check(outputUserGV);
                //wholeCheck(mUserGuidedVectors);
            });
        }
        mGestureDetector = new GestureDetector(gesture.get(0));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float fingerFat = 20;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y, fingerFat);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    private Direction[] ComparePointsToCheckFV(Point point1, Point point2) {
        float dx = abs(point1.x - point2.x);
        float dy = abs(point1.y - point2.y);
        Direction XDirection = null, YDirection = null;
        Log.i(TAG2,"mPoints.size(): " + mPoints.size()+"\nmPoints: " + mPoints.toString());
        Log.i("WordView","Dx and Dy: " + dx +", " + dy);


        if (point1.y > point2.y) YDirection = Direction.UP;
        else if (point1.y < point2.y) YDirection = Direction.DOWN;

        if (dy <= mFingerFat) YDirection = Direction.SAME;

        if (point1.x > point2.x) XDirection = Direction.LEFT;
        else if (point1.x < point2.x) XDirection = Direction.RIGHT;

        if (dx <= mFingerFat) XDirection = Direction.SAME;



        return new Direction[]{XDirection, YDirection};
    }

    public void setGuidedVector(Map<Integer, Direction[][]> gvVersions,String word) {
       // ArrayList<PointDirection> pointDirections
        originalWord = word;
        gestureSize = 0;
        mGestureDetector = new GestureDetector(gvVersions.get(0)); // first version .. first char
        gesture = gvVersions;

        for (int j = 0; j < gesture.get(0).length; j++) {
            for (int l = 0; l < gesture.get(0)[j].length; l++) {
                gestureSize++;

               // Log.i("WordView","gesture.get(0)[j][l] = "+j+" "+l+" "+gesture.get(0)[j][l].toString());
            }
        }

    }

    private void SaveImage(Bitmap finalBitmap,String fname,Bitmap.CompressFormat type) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/SeShatSF");
        if(!myDir.exists())
            myDir.mkdirs();


        File file = new File (myDir, fname);

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(type, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void deleteFile()
    {
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/SeShatSF");
//        String fname =  "temp.png";
//
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
    }
    void check(ArrayList<Direction>mUserGuidedVectors)
    {
        ((MainActivity)context).findViewById(R.id.loadingSpinner).setVisibility(View.VISIBLE);
       try {


            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeShatSF/temp.png";
            String contentBase64 = encodeFileToBase64Binary(new File(fileName));


            JsonObject requestsObject = new JsonObject();
            JsonArray requestsArray = new JsonArray();
            JsonObject contentObject = new JsonObject();
            JsonArray featuresArray = new JsonArray();
            JsonObject typeObject = new JsonObject();
            JsonArray languageHintsArray = new JsonArray();
            JsonObject languageHints = new JsonObject();
            JsonObject requestsBodyObject = new JsonObject();

            languageHintsArray.add("ar");
            languageHints.add("languageHints",languageHintsArray);

            typeObject.addProperty("type","DOCUMENT_TEXT_DETECTION");

            featuresArray.add(typeObject);


            contentObject.addProperty("content",contentBase64);


            requestsBodyObject.add("image",contentObject);
            requestsBodyObject.add("features",featuresArray);
            requestsBodyObject.add("imageContext",languageHints);

            requestsArray.add(requestsBodyObject);


            requestsObject.add("requests",requestsArray);


            System.out.println("Json: " + contentBase64);

            OCRGoogleVision(requestsObject,mUserGuidedVectors);

        }
        catch (Exception e) {
            System.err.printf("Error1: %s" , e.getMessage());
            ((MainActivity)context).findViewById(R.id.loadingSpinner).setVisibility(View.GONE);
        }


    }
    public void OCRGoogleVision(final JsonObject jsonObject,final ArrayList<Direction>mUserGuidedVectors)
    {
        Log.i(TAG,"I'm In for " +word_loop);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder okHttp = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttp.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(((MainActivity)context).getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttp.build())
                .build();
        ServiceRepository OCRGoogleVisionAPI = retrofit.create(ServiceRepository.class);
        Call<ResponseBodyModel> call = OCRGoogleVisionAPI.OCRGoogleVision(jsonObject);
        call.enqueue(new Callback<ResponseBodyModel>() {
            @Override
            public void onResponse(Call<ResponseBodyModel> call, retrofit2.Response<ResponseBodyModel> response) {

                ((MainActivity)context).findViewById(R.id.loadingSpinner).setVisibility(View.GONE);

                try {
                    ResponseBodyModel responseBody = response.body();
                    if(responseBody != null) {
                        if(responseBody.getResponses() != null && responseBody.getResponses().size() > 0) {

                            FullTextAnnotationModel wordModel = responseBody.getResponses().get(0).getFullTextAnnotation();

                            if (wordModel != null) {
                                String word = wordModel.getText().trim();
                                Log.i(TAG, "Original Word: " + originalWord);
                                Log.i(TAG, "User Word: " + word);
                                //System.out.println(word.equals(originalWord));
                                //System.out.println(word.contains(originalWord));
                                if (originalWord.equals(word)) {
                                    if(word_loop+1 < (defaultLoop * defaultTypeface))
                                        ((MainActivity) context).voiceoffer(null, context.getString(R.string.congrats));
                                    else{
                                        ((MainActivity) context).voiceoffer(null, context.getString(R.string.final_congrats));
                                    }
                                    successBtn.setBackground(context.getDrawable(R.drawable.success_btn));
                                    successBtn.setEnabled(false);
                                    new Handler().postDelayed(()->
                                    {
                                        successBtn.setEnabled(true);
                                        successBtn.setVisibility(INVISIBLE);
                                        successBtn.setBackground(context.getDrawable(R.drawable.done));
                                    },(word_loop+1 < (defaultLoop * defaultTypeface)) ? 2000:1500);

                                    reset();

                                    Log.i("WordView", " completed");

                                    // ((MainActivity)context).SaveOnSharedPref(MainActivity.WordLoopKey, String.valueOf(word_loop));
                                    getInterface();
                                } else {
                                    successBtn.setBackground(context.getDrawable(R.drawable.fail_btn));
                                    successBtn.setEnabled(false);
                                    new Handler().postDelayed(()->
                                    {
                                        successBtn.setEnabled(true);
                                        successBtn.setVisibility(INVISIBLE);
                                        successBtn.setBackground(context.getDrawable(R.drawable.done));
                                    },2000);

                                    Log.i("WordView", "Try Again");
                                    ((MainActivity) context).voiceoffer(null, context.getString(R.string.tryAgain));

                                    reset();
                                }
                                //
                                //   System.out.println("responseBody: " + responseBody.toString());


                            } else {
                               /* Log.i("WordView", "Try Again");
                                ((MainActivity) context).voiceoffer(null, context.getString(R.string.tryAgain));
                                reset();*/
                                wholeCheck(mUserGuidedVectors);

                            }
                        }
                        else {
                           /* Log.i("WordView", "Try Again");
                            ((MainActivity) context).voiceoffer(null, context.getString(R.string.tryAgain));
                            reset();*/
                            wholeCheck(mUserGuidedVectors);

                        }
                    }else {
                        /*Log.i("WordView", "Try Again");
                        ((MainActivity) context).voiceoffer(null, context.getString(R.string.tryAgain));
                        reset();
*/
                        wholeCheck(mUserGuidedVectors);
                    }
                    deleteFile();

                } catch (Exception e) {
                    Log.e(TAG,"Error2: "+ e.getMessage());
                }

                //findViewById(R.id.loadingSpinner).setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<ResponseBodyModel> call, Throwable t) {
                ((MainActivity)context).findViewById(R.id.loadingSpinner).setVisibility(View.GONE);
               Log.e(TAG,"Error3: " + t.toString());
                wholeCheck(mUserGuidedVectors);

            }
        });
    }


    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        byte[] bytes = loadFile(fileName);
        byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(fileName));
        String encodedString = new String(encoded);
        return encodedString;
    }

    public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out.printf("Error4: %s\n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    out.printf("Text: %s\n", annotation.getDescription());
                    out.printf("Position : %s\n", annotation.getBoundingPoly());
                }
            }
        }
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }




    public void setFragment(Fragment fragment) {
        this.updateWord = (UpdateWord) fragment;

        successBtn = fragment.getView().findViewById(R.id.imagebutton_success);
    }

    public void setDefaultTypeface(int defaultTypeface) {
        this.defaultTypeface = defaultTypeface;
    }

    public void setDefaultLoop(int defaultLoop) {
        this.defaultLoop = defaultLoop;
    }

    public void resetWordLoop() {
        word_loop = 0;
    }

    class MyPoints
    {
        private Point point;
        private Direction dX;
        private Direction dY;

        public MyPoints(Point point, Direction dX, Direction dY) {
            this.point = point;
            this.dX = dX;
            this.dY = dY;
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }

        public Direction getdX() {
            return dX;
        }

        public void setdX(Direction dX) {
            this.dX = dX;
        }

        public Direction getdY() {
            return dY;
        }

        public void setdY(Direction dY) {
            this.dY = dY;
        }

        @Override
        public String toString() {
            return "{" +
                    "point= " + point +
                    ", dX= " + dX +
                    ", dY= " + dY +
                    "}";
        }
    }
}