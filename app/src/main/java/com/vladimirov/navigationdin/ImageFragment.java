package com.vladimirov.navigationdin;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@SuppressLint("ValidFragment")
public class ImageFragment extends Fragment {

    private ImageView imageView;
    private String param;

    public ImageFragment() {
    }

    public ImageFragment(String param) {
        this.param = param;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_item, container, false);

        imageView = view.findViewById(R.id.imageView);

        if(param != null && URLUtil.isValidUrl(param)) {
            showImage(param);
        } else if(param != null && !URLUtil.isValidUrl(param)){
            Toast.makeText(getContext(), "Oops... Invalid url format", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void showImage(String ImageUri) {
        BitmapAsync bitmapAsync = new BitmapAsync();
        bitmapAsync.execute(ImageUri);
    }

    @SuppressLint("StaticFieldLeak")
    class BitmapAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... url) {
            Bitmap bitmap = null;
            try {
                URL Url = new URL(url[0]);
                URLConnection conn = Url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("TAG", "Error getting bitmap", e);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
