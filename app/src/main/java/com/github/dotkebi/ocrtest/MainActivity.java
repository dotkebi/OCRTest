package com.github.dotkebi.ocrtest;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements OnClickCallback {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initData();

    RecyclerView recyclerView = findViewById(R.id.listView);

    try {
      BitmapRecyclerAdapter adapter = new BitmapRecyclerAdapter(this, this, getAssets().list("test"));
      recyclerView.setAdapter(adapter);

      recyclerView.setLayoutManager(new LinearLayoutManager(this));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void initData() {
    String[] paths = new String[]{Constants.DATA_PATH, Constants.DATA_PATH + "tessdata/"};

    for (String path : paths) {
      File dir = new File(path);
      if (!dir.exists()) {
        if (!dir.mkdirs()) {
          Log.v(Constants.LANG, "ERROR: Creation of directory " + path + " on sdcard failed");
          return;
        } else {
          Log.v(Constants.LANG, "Created directory " + path + " on sdcard");
        }
      }
    }

    if (!(new File(Constants.DATA_PATH + "tessdata/" + Constants.LANG + ".traineddata")).exists()) {
      try {
        AssetManager assetManager = getAssets();
        InputStream in = assetManager.open("tessdata/" + Constants.LANG + ".traineddata");
        OutputStream out = new FileOutputStream(Constants.DATA_PATH
            + "tessdata/" + Constants.LANG + ".traineddata");

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        in.close();
        out.close();

        Log.v(Constants.LANG, "Copied " + Constants.LANG + " traineddata");
      } catch (IOException e) {
        Log.e(Constants.LANG,
            "Was unable to copy " + Constants.LANG + " traineddata " + e.toString());
      }
    }
  }


  @Override
  public void onClick(String resourceName) {

    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
    intent.putExtra("item", resourceName);
    startActivity(intent);
  }
}
