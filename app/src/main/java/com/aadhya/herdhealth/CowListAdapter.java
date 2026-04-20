package com.aadhya.herdhealth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class CowListAdapter extends ArrayAdapter<Cow> {

    private Context context;
    private List<Cow> cowList;

    public CowListAdapter(Context context, List<Cow> cowList) {
        super(context, R.layout.item_cow, cowList);
        this.context = context;
        this.cowList = cowList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cow, parent, false);
        }

        Cow cow = cowList.get(position);

        TextView cowIdTextView = convertView.findViewById(R.id.cowIdTextView);
        TextView healthStatusTextView = convertView.findViewById(R.id.healthStatusTextView);
        ImageView healthStatusImageView = convertView.findViewById(R.id.healthStatusImageView);

        cowIdTextView.setText(cow.getId());
        healthStatusTextView.setText(cow.getHealthStatus());

        // Set health indicator
        if (cow.getHealthStatus().equals("Urgent")) {
            healthStatusImageView.setImageResource(R.drawable.red_dot);
        } else if (cow.getHealthStatus().equals("Warning")) {
            healthStatusImageView.setImageResource(R.drawable.yellow_dot);
        } else {
            healthStatusImageView.setImageResource(R.drawable.green_dot);
        }

        return convertView;
    }
}
