package com.example.l.seshatmvp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {


    private static final float EMOJI_SCALE_FACTOR = .9f;
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    /**
     * Method for detecting faces in a bitmap, and drawing emoji depending on the facial
     * expression.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static String detectFacesandOverlayEmoji(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        Matrix matrix = new Matrix();

        matrix.postRotate(90);
        Bitmap temp = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);
        ((ImageView)((MainActivity)context).findViewById(R.id.image)).setImageBitmap(temp);

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(temp).build();
        Log.i("Emoji",frame.toString());
        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces

        // Initialize result bitmap to original picture
        Bitmap resultBitmap = temp;
        String state = "none";
        Log.i("Emoji","faces.size(): " + faces.size());
        // If there are no faces detected, show a Toast message
        if (faces.size() == 0) {
            state = "none";
           // Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {

            // Iterate through the faces
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                Emoji emoji = whichEmoji(face);
                Log.i("Emoji",emoji.toString());

                switch (emoji) {
                    case SMILE:
                        state = "smile";
                        break;
                    case Angry:
                        state = "frown";
                        break;
                    case LEFT_WINK:
                        state = "left_wink";
                        break;
                    case RIGHT_WINK:
                        state = "right_wink";
                        break;
                    case LEFT_WINK_FROWN:
                        state = "LEFT_WINK_FROWN";
                        break;
                    case RIGHT_WINK_FROWN:
                        state = "RIGHT_WINK_FROWN";
                        break;
                    case CLOSED_EYE_SMILE:
                        state = "CLOSED_EYE_SMILE";
                        break;
                    case CLOSED_EYE_FROWN:
                        state = "CLOSED_EYE_FROWN";
                        break;
                    default:
                        state = "confused";
                        Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
                }
            }
        }


        // Release the detector
        detector.release();
        return state;
    }


    /**
     * Determines the closest emoji to the expression on the face, based on the
     * odds that the person is smiling and has each eye open.
     *
     * @param face The face for which you pick an emoji.
     */

    private static Emoji whichEmoji(Face face) {

        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;

        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;


        // Determine and log the appropriate emoji
        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.Angry;
            }
        }

        // return the chosen Emoji
        return emoji;
    }

    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        Angry,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }

}
