package com.hnt.hnt_android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnt.hnt_android.databinding.ItemAccesspointBinding;
import com.hnt.hnt_android.vo.AccessPoint;

import java.util.Vector;

public class AccessPointAdapter extends RecyclerView.Adapter {

    private Vector<AccessPoint> accessPoints;
    private Context context;

    public AccessPointAdapter(Vector<AccessPoint> accessPoints, Context context) {
        this.accessPoints = accessPoints;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        ItemAccesspointBinding binding = ItemAccesspointBinding.inflate(LayoutInflater.from(context), parent, false);
        holder = new AccessPointHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccessPointHolder accessPointHolder = (AccessPointHolder)holder;
        final ItemAccesspointBinding binding = accessPointHolder.binding;
        binding.cardView.setRadius(20.0f);
        String ssid = "SSID : " + accessPoints.get(position).getSsid();
        String bSsid = "BSSID : " + accessPoints.get(position).getBssid();
        String rssi = "RSSI : " + accessPoints.get(position).getRssi();
        binding.ssidTextView.setText(ssid);
        binding.bssidTextView.setText(bSsid);
        binding.rssiLevelTextView.setText(rssi);

    }

    @Override
    public int getItemCount() {
        return accessPoints.size();
    }

    private class AccessPointHolder extends RecyclerView.ViewHolder {
        ItemAccesspointBinding binding;

        AccessPointHolder(ItemAccesspointBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

