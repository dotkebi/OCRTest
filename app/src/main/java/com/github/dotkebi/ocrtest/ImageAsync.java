package com.github.dotkebi.ocrtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;

class ImageAsync extends AsyncTask<InputStream, Void, Bitmap> {

  private ImageReadyCallback callback;

  public ImageAsync(ImageReadyCallback callback) {
    this.callback = callback;
  }

  @Override
  protected Bitmap doInBackground(InputStream... params) {
    return BitmapFactory.decodeStream(params[0]);
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    super.onPostExecute(bitmap);
    callback.ready(bitmap);
  }
}
