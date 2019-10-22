package com.github.dotkebi.ocrtest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;

/**
 * @author by dotkebi on 2018. 3. 3..
 */
public class BitmapRecyclerAdapter extends
    RecyclerView.Adapter<BitmapViewHolder> {

  private Context context;
  private OnClickCallback callback;
  private String[] resIds;

  public BitmapRecyclerAdapter(Context context, OnClickCallback callback, String[] resIds) {
    this.context = context;
    this.callback = callback;
    this.resIds = resIds;
  }

  @NonNull
  @Override
  public BitmapViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    return new BitmapViewHolder(view, callback);
  }

  @Override
  public void onBindViewHolder(@NonNull BitmapViewHolder viewHolder, int position) {
    int viewType = getItemViewType(position);

    try {
      String name = "test/" + resIds[position];
      viewHolder.display(context.getAssets().open(name), name);
    } catch (IOException e) {
      Log.e("error", e.getMessage());
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return resIds.length;
  }


}
