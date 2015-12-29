package com.architjn.acjmusicplayer.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.handlers.ArtistImgHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by architjn on 18/12/15.
 */
public class FetchArtist {
    private int random;
    private ArtistImgHandler handler;
    private String url;
    private Context context;
    private String name;
    private String jsonResult;
    private Bitmap downloadedImg;

    public FetchArtist(Context context, String name, int random, ArtistImgHandler handler) {
        this.context = context;
        this.name = name;
        this.context = context;
        this.name = name;
        this.random = random;
        this.handler = handler;
        if (name == null || name.matches("<unknown>")) {
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(context.getResources().getString(R.string.artist_fetch_url));
            try {
                builder.append("&artist=" + URLEncoder.encode(name, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            builder.append("&api_key=" + context.getResources().getString(R.string.api));
            builder.append("&format=json");
            this.url = builder.toString();
            runTask();
        }
    }

    public void runTask() {
        new Action<String>() {
            @NonNull
            @Override
            public String id() {
                return name; //some unique Id
            }

            @Nullable
            @Override
            protected String run() throws InterruptedException {
                backgroundTask();
                return null;
            }

            @Override
            protected void done(@Nullable String result) {
            }
        }.execute();
    }

    private void backgroundTask() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            jsonResult = inputStreamToString(response.getEntity().getContent())
                    .toString();
            if (jsonResult != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    JSONArray imageArray = jsonResponse.getJSONObject("artist").getJSONArray("image");
                    for (int i = 0; i < imageArray.length(); i++) {
                        JSONObject image = imageArray.getJSONObject(i);
                        if (image.optString("size").matches("large") &&
                                !image.optString("#text").matches("")) {
                            downloadedImg = downloadBitmap(image.optString("#text"));
                            String newUrl = saveImageToStorage(downloadedImg);
                            handler.updateArtistArtWorkInDB(name, newUrl);
                            handler.onDownloadComplete(newUrl);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClientProtocolException e) {
            Log.e("e", "error1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("e", "error2");
            e.printStackTrace();
        }
    }

    private String saveImageToStorage(Bitmap bitmap) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("cache-img-");
        Calendar c = Calendar.getInstance();
        fileName.append(c.get(Calendar.DATE)).append("-");
        fileName.append(c.get(Calendar.MONTH)).append("-");
        fileName.append(c.get(Calendar.YEAR)).append("-");
        fileName.append(c.get(Calendar.HOUR)).append("-");
        fileName.append(c.get(Calendar.MINUTE)).append("-");
        fileName.append(c.get(Calendar.SECOND)).append("-");
        fileName.append(random).append("-");
        fileName.append((random / 3) * 5);
        fileName.append(".png");
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        String filePath = sdCardDirectory + "/" + context.getResources()
                .getString(R.string.app_name) + "/artist/";
        (new File(filePath)).mkdirs();
        File noMedia = new File(filePath, ".nomedia");
        if (!noMedia.exists()) try {
            noMedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File image = new File(filePath, fileName.toString());
        if (image.exists()) image.delete();
        try {
            FileOutputStream out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return image.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap downloadBitmap(String url) {
        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();

        //forming a HttoGet request
        final HttpGet getRequest = new HttpGet(url);
        try {

            HttpResponse response = client.execute(getRequest);

            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;

            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    // getting contents from the stream
                    inputStream = entity.getContent();

                    // decoding stream data back into image Bitmap that android understands
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + url + e.toString());
        }

        return null;
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

}
