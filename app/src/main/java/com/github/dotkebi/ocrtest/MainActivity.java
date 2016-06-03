package com.github.dotkebi.ocrtest;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
                startOCR();
                break;

            case R.id.next:
                setImage();
                break;
        }
        return true;
    }

    private void setImage() {
        if (index >= sampleList.length) {
            Toast.makeText(MainActivity.this, R.string.endOfSamples, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("test/" + sampleList[index]));
            imageView.setImageBitmap(bitmap);
            ++index;
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void startOCR() {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, LANG);
        baseApi.setImage(bitmap);
        baseApi.setPageSegMode(7);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        if (TextUtils.isEmpty(recognizedText)) {
            Toast.makeText(MainActivity.this, R.string.unrecognizedText, Toast.LENGTH_SHORT).show();
            return;
        }
        textView.setText(recognizedText);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bitmap.recycle();
    }
}
