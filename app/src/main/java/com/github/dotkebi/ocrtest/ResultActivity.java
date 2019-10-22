package com.github.dotkebi.ocrtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity implements ImageReadyCallback {

  private ImageView imageView;
  private TextView textView;
  private ProgressDialog progressDialog;

  private Bitmap bitmap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();

    if (intent == null) {
      finish();
      return;
    }

    imageView = findViewById(R.id.image);
    textView = findViewById(R.id.text);

    String name = intent.getStringExtra("item");

    progressDialog = ProgressDialog
        .show(ResultActivity.this, getString(R.string.app_name), getString(R.string.working),
            true,
            false);

    try {
      InputStream inputStream = getAssets().open(name);

      ImageAsync imageAsync = new ImageAsync(this);
      imageAsync.execute(inputStream);
    } catch (Exception e) {
      Log.e("error", e.getMessage());
    }


  }

  @Override
  public void ready(Bitmap b) {
    this.bitmap = b;

    imageView.setImageBitmap(bitmap);

    OCRAsync ocrAsync = new OCRAsync();
    ocrAsync.execute(bitmap);
  }


  public class OCRAsync extends AsyncTask<Bitmap, Void, String> {

    @Override
    protected String doInBackground(Bitmap... params) {
      TessBaseAPI baseApi = new TessBaseAPI();
      baseApi.init(Constants.DATA_PATH, Constants.LANG);
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
        Toast.makeText(ResultActivity.this, R.string.unrecognizedText, Toast.LENGTH_SHORT).show();
        return;
      }
      textView.setText(s);
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


}
