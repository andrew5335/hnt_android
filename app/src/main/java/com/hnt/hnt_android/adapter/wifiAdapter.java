package com.hnt.hnt_android.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnt.hnt_android.R;
import com.hnt.hnt_android.dialog.wifiDialog;

import java.util.List;

public class wifiAdapter extends RecyclerView.Adapter<wifiAdapter.MyViewHolder> {

    private List<ScanResult> items;
    private Context mContext;

    public wifiAdapter(List<ScanResult> items){
        this.items=items;
    }

    @NonNull
    @Override
    public wifiAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_item , parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWifiName;
        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        String ssid = items.get(pos).SSID;

                        wifiDialog customDialog = new wifiDialog(mContext);
                        customDialog.callFunction(ssid);
                    }
                }
            });

            tvWifiName=itemView.findViewById(R.id.tv_wifiName);
        }
        public void setItem(ScanResult item){
            tvWifiName.setText(item.SSID);
        }
    }
}