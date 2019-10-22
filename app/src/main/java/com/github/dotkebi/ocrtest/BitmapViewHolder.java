package com.github.dotkebi.ocrtest;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStream;

/**
 * @author by dotkebi on 2018. 3. 4..
 */
public class BitmapViewHolder extends RecyclerView.ViewHolder implements ImageReadyCallback {

  private OnClickCallback callback;
  private ImageView image;
  private View parent;

  public BitmapViewHolder(View view, OnClickCallback callback) {
    super(view);
    parent = view;
    image = view.findViewById(R.id.image);
    this.callback = callback;
  }


  public void display(InputStream inputStream, final String resourceName) {
    ImageAsync imageAsync = new ImageAsync(this);
    imageAsync.execute(inputStream);

    parent.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        callback.onClick(resourceName);
      }
    });

  }

  @Override
  public void ready(Bitmap bitmap) {
    image.setImageBitmap(bitmap);
  }

}
