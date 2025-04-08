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

        TextView cowId = convertView.findViewById(R.id.cowId);
        TextView digitalTwinStatus = convertView.findViewById(R.id.digitalTwinStatus);
        ImageView healthIndicator = convertView.findViewById(R.id.healthIndicator);

        Cow cow = cowList.get(position);
        cowId.setText(cow.getId());
        digitalTwinStatus.setText(cow.getDigitalTwinStatus());

        // Set Health Status Indicator
        if (cow.getHealthStatus().equals("Healthy")) {
            healthIndicator.setImageResource(R.drawable.green_dot);
        } else if (cow.getHealthStatus().equals("Warning")) {
            healthIndicator.setImageResource(R.drawable.yellow_dot);
        } else {
            healthIndicator.setImageResource(R.drawable.red_dot);
        }

        // Set click listener to open CowDetailsActivity
        convertView.setOnClickListener(view -> {
            Intent intent = new Intent(context, CowDetailsActivity.class);
            intent.putExtra("cowId", cow.getId());
            intent.putExtra("healthStatus", cow.getHealthStatus());
            intent.putExtra("digitalTwinStatus", cow.getDigitalTwinStatus());
            context.startActivity(intent);
        });

        return convertView;
    }
}
