package com.example.l.seshatmvp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.l.seshatmvp.repositories.KairosListener;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class Kairos {

    protected String my_app_id;
    protected String my_api_key;
    protected String my_host;
    protected Context my_context;


    public void setAuthentication(Context ctx, String app_id, String api_key) {

        my_context = ctx;
        my_app_id = app_id;
        my_api_key = api_key;
        my_host = "http://api.kairos.com/";

    }


    /*
     * List galleries
     */
    public void listGalleries(final KairosListener callback) throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/gallery/list_all", entity, "application/json", responseHandler);
    }


    /*
     * Subjects for gallery
     */
    public void listSubjectsForGallery(String galleryId, final KairosListener callback) throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("gallery_name", galleryId);
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/gallery/view", entity, "application/json", responseHandler);

    }



    /*
     * Delete subject from gallery
     */
    public void deleteGallery(String galleryId, final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("gallery_name", galleryId);
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/gallery/remove", entity, "application/json", responseHandler);

    }





    /*
     * Delete subject from gallery
     */
    public void deleteSubject(String subjectId, String galleryId, final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("subject_id", subjectId);
        jsonParams.put("gallery_name", galleryId);
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/gallery/remove_subject", entity, "application/json", responseHandler);

    }

    /*
     * Delete subject from gallery
     */

    public void detect(String image,
                       String selector,
                       String minHeadScale,
                       final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.d("Kairos","on Start");
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                Log.d("Kairos","on Success");
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                   System.out.println("Throwable:" + e.getMessage());
                    for (Header header:headers)
                        System.out.println("headers:" + header);
                    callback.onFail(responseString);
                }
                else
                {
                    System.out.println("There is an error 8areeeb");
                    System.out.println("Throwable:" + e.getMessage());
                    System.out.println("headers:" + headers.toString());

                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("image", image);

        if(selector != null) {
            jsonParams.put("selector", selector);
        }

        if(minHeadScale != null) {
            jsonParams.put("minHeadScale", minHeadScale);
        }

        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);

        client.post(my_context, "http://api.kairos.com/detect", entity, "application/json", responseHandler);

    }





    /*
     * Delete subject from gallery (Image)
     */

    public void detect(Bitmap image,
                       String selector,
                       String minHeadScale,
                       final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("image", base64FromBitmap(image));


        if(selector != null) {
            jsonParams.put("selector", selector);
        }

        if(minHeadScale != null) {
            jsonParams.put("minHeadScale", minHeadScale);
        }

        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/detect", entity, "application/json", responseHandler);

    }



    public void media(String image,final KairosListener callback) {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
                else
                {
                    System.err.println("Error: " + e.getMessage());
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };


        try {
            File imageFile = new File(image);
            System.out.println("imageFile: " + imageFile.exists() );
            RequestParams requestParams = new RequestParams();
            requestParams.put("source",imageFile,"multipart/form-data","source");

            StringEntity entity = new StringEntity(requestParams.toString());

            client.addHeader("app_id", my_app_id);
            client.addHeader("app_key", my_api_key);
            String url = "https://api.kairos.com/v2/media";
            client.post(url,requestParams,responseHandler);
            //client.post(my_context, "https://api.kairos.com/v2/media",entity, null, responseHandler);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }










    /*
     * Enroll subject into gallery (many params)
     */
    public void enroll(String image,
                       String subjectId,
                       String galleryId,
                       String selector,
                       String multipleFaces,
                       String minHeadScale,
                       final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("image", image);
        jsonParams.put("subject_id", subjectId);
        jsonParams.put("gallery_name", galleryId);


        if(selector != null) {
            jsonParams.put("selector", selector);
        }

        if(minHeadScale != null) {
            jsonParams.put("minHeadScale", minHeadScale);
        }

        if(multipleFaces != null) {
            jsonParams.put("multiple_faces", multipleFaces);
        }

        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/enroll", entity, "application/json", responseHandler);

    }





   /* *//*
     * Enroll subject into gallery (Image)
     */
    public void enroll(Bitmap image,
                       String subjectId,
                       String galleryId,
                       String selector,
                       String multipleFaces,
                       String minHeadScale,
                       final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("image", base64FromBitmap(image));
        jsonParams.put("subject_id", subjectId);
        jsonParams.put("gallery_name", galleryId);

        if(selector != null) {
            jsonParams.put("selector", selector);
        }

        if(minHeadScale != null) {
            jsonParams.put("minHeadScale", minHeadScale);
        }

        if(multipleFaces != null) {
            jsonParams.put("multiple_faces", multipleFaces);
        }

        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/enroll", entity, "application/json", responseHandler);

    }









    /*
     * Recognize image in gallery (URL)
     */
    public void recognize(String imageURL,
                          String galleryId,
                          String selector,
                          String threshold,
                          String minHeadScale,
                          String maxNumResults,
                          final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("image", imageURL);
        jsonParams.put("gallery_name", galleryId);

        if(selector != null) {
            jsonParams.put("selector", selector);
        }

        if(minHeadScale != null) {
            jsonParams.put("minHeadScale", minHeadScale);
        }

        if(threshold != null) {
            jsonParams.put("threshold", threshold);
        }

        if(maxNumResults != null) {
            jsonParams.put("max_num_results", maxNumResults);
        }

        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/recognize", entity, "application/json", responseHandler);

    }




    /*
     * Recognize image in gallery (Image)
     */
    public void recognize(Bitmap image,
                          String galleryId,
                          String selector,
                          String threshold,
                          String minHeadScale,
                          String maxNumResults,
                          final KairosListener callback)  throws JSONException, UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String responseString = new String(response);
                callback.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(errorResponse != null)
                {
                    //Check null in case errorResponse is null
                    String responseString = new String(errorResponse);
                    callback.onFail(responseString);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        };

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("image", base64FromBitmap(image));
        jsonParams.put("gallery_name", galleryId);

        if(selector != null) {
            jsonParams.put("selector", selector);
        }

        if(minHeadScale != null) {
            jsonParams.put("minHeadScale", minHeadScale);
        }

        if(threshold != null) {
            jsonParams.put("threshold", threshold);
        }

        if(maxNumResults != null) {
            jsonParams.put("max_num_results", maxNumResults);
        }

        StringEntity entity = new StringEntity(jsonParams.toString());
        client.addHeader("app_id", my_app_id);
        client.addHeader("app_key", my_api_key);
        client.post(my_context, "http://api.kairos.com/recognize", entity, "application/json", responseHandler);

    }











    protected String base64FromBitmap(Bitmap image){


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }



}
