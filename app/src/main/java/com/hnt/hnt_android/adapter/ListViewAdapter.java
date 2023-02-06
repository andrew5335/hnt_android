package com.hnt.hnt_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnt.hnt_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {

    private List<Map<String, Object>> itemList;
    private Context context;
    private View.OnClickListener onClickItem;

    public ListViewAdapter(Context context, List<Map<String, Object>> itemList, View.OnClickListener onClickItem) {
        this.context = context;
        this.itemList = itemList;
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ViewHolder holder, int position) {
        Map<String, Object> item = itemList.get(position);

        holder.imageView.setImageResource(R.mipmap.device);
        holder.imageView.setTag(String.valueOf(item.get("sensor_uuid")) + "," + String.valueOf(item.get("sensor_name")));
        holder.imageView.setOnClickListener(onClickItem);

        holder.textview.setText(String.valueOf(item.get("sensor_name")));
        holder.textview.setTag(String.valueOf(item.get("sensor_uuid")) + "," + String.valueOf(item.get("sensor_name")));
        holder.textview.setOnClickListener(onClickItem);
    }

    @Override
    public int getItemCount() {
        if(null != itemList && 0 < itemList.size()) {
            return itemList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textview;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.device_image);
            textview = itemView.findViewById(R.id.item_textview);
        }
    }
}
