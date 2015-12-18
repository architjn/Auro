package com.architjn.acjmusicplayer.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afollestad.async.Action;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class FetchArtistArtWork extends Action {

    private Context context;
    private String name;
    private int random;
    private String url;
    private String jsonResult;
    private Bitmap downloadedImg;

    public FetchArtistArtWork(Context context, String name, int random) {
        this.context = context;
        this.name = name;
        this.random = random;
        if (!isFragmentSame()) cancel();
        if (name == null || name.matches("<unknown>")) cancel();
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
    }

    private boolean isFragmentSame() {
        return ((MainActivity) context).currentFragment == MainActivity.FragmentName.Artists;
    }

    @NonNull
    @Override
    public String id() {
        return this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    protected Object run() throws InterruptedException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        HttpClient httpclient = new DefaultHttpClient();
        if (!isFragmentSame()) cancel();
        HttpPost httppost = new HttpPost(url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            jsonResult = inputStreamToString(response.getEntity().getContent())
                    .toString();
            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray imageArray = jsonResponse.getJSONObject("artist").getJSONArray("image");
                for (int i = 0; i < imageArray.length(); i++) {
                    JSONObject image = imageArray.getJSONObject(i);
                    if (image.optString("size").matches("large") && !image.optString("#text").matches("")) {
                        downloadedImg = downloadBitmap(image.optString("#text"));
                        onDownloadComplete(saveImageToStorage(downloadedImg));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ClientProtocolException e) {
            Log.e("e", "error1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("e", "error2");
            e.printStackTrace();
        }
        return null;
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine;
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

    private String saveImageToStorage(Bitmap bitmap) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("cache-img-");
        Calendar c = Calendar.getInstance();
        fileName.append(c.get(Calendar.DATE) + "-");
        fileName.append(c.get(Calendar.MONTH) + "-");
        fileName.append(c.get(Calendar.YEAR) + "-");
        fileName.append(c.get(Calendar.HOUR) + "-");
        fileName.append(c.get(Calendar.MINUTE) + "-");
        fileName.append(c.get(Calendar.SECOND) + "-");
        fileName.append(random + "-");
        fileName.append((random / 3) * 5);
        fileName.append(".png");
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        String filePath = sdCardDirectory + "/" + context.getResources()
                .getString(R.string.app_name) + "/artist/";
        (new File(filePath)).mkdirs();
        File image = new File(filePath, fileName.toString());
        if (image.exists()) image.delete();
        try {
            FileOutputStream out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return image.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap downloadBitmap(String url) {
        try {
            return Picasso.with(context).load(url).get();
        } catch (Exception e) {
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + url + e.toString());
        }
        return null;
    }

    public abstract void onDownloadComplete(String url);

}
