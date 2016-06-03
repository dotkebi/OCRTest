package com.github.dotkebi.ocrtest;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/ocrtest/";

    private static final String LANG = "kor";

    ImageView imageView;
    TextView textView;

    Bitmap bitmap;
    String[] sampleList;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

        bitmap = null;
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);

        try {
            sampleList = getAssets().list("test");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start:
                OCRAsync ocrAsync = new OCRAsync();
                ocrAsync.execute(bitmap);
                break;

            case R.id.next:
                setImage();
                break;
        }
        return true;
    }

    private void setImage() {
        textView.setText("");
        if (index >= sampleList.length) {
            Toast.makeText(MainActivity.this, R.string.endOfSamples, Toast.LENGTH_SHORT).show();
            return;
        }
        ImageAsync imageAsync = new ImageAsync();
        imageAsync.execute(sampleList[index]);
    }

    private void initData() {
        String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(LANG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(LANG, "Created directory " + path + " on sdcard");
                }
            }
        }

        if (!(new File(DATA_PATH + "tessdata/" + LANG + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + LANG + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + LANG + ".traineddata");

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(LANG, "Copied " + LANG + " traineddata");
            } catch (IOException e) {
                Log.e(LANG, "Was unable to copy " + LANG + " traineddata " + e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    class ImageAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getAssets().open("test/" + params[0]));
                ++index;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            MainActivity.this.bitmap = bitmap;
            imageView.setImageBitmap(bitmap);
        }
    }

    ProgressDialog progressDialog;
    class OCRAsync extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.app_name), getString(R.string.working), true, false);
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.init(DATA_PATH, LANG);
            baseApi.setImage(params[0]);
            String recognizedText = baseApi.getUTF8Text();
            baseApi.end();
            return recognizedText;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if (TextUtils.isEmpty(s)) {
                Toast.makeText(MainActivity.this, R.string.unrecognizedText, Toast.LENGTH_SHORT).show();
                return;
            }
            textView.setText(s);
        }
    }

}
