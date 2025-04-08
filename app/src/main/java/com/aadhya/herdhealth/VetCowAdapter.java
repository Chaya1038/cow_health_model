package com.aadhya.herdhealth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VetCowAdapter extends RecyclerView.Adapter<VetCowAdapter.VetCowViewHolder> {
    private List<VetCowModel> cowList;
    private Context context;

    public VetCowAdapter(List<VetCowModel> cowList, Context context) {
        this.cowList = cowList;
        this.context = context;
    }

    @NonNull
    @Override
    public VetCowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vet_cow, parent, false);
        return new VetCowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VetCowViewHolder holder, int position) {
        VetCowModel cow = cowList.get(position);

        // Set data to TextViews
        holder.tvCowId.setText("Cow ID: " + cow.getId());
        holder.tvCowName.setText("Name: " + cow.getName());
        holder.tvStatus.setText("Status: " + cow.getStatus());
        holder.tvFarmer.setText("Farmer: " + cow.getFarmerName());
        holder.tvContact.setText("Contact: " + cow.getFarmerContact());

        // Click event to open VetCowDetails
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Context itemContext = v.getContext();
                Intent intent = new Intent(itemContext, VetCowDetails.class);
                intent.putExtra("cowId", cow.getId());
                intent.putExtra("cowName", cow.getName());
                intent.putExtra("status", cow.getStatus());
                intent.putExtra("lameness", cow.getLameness());
                intent.putExtra("estrus", cow.getEstrus());
                intent.putExtra("prediction", cow.getDigitalTwinPrediction());
                intent.putExtra("farmerName", cow.getFarmerName());
                intent.putExtra("farmerContact", cow.getFarmerContact());
                itemContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cowList.size();
    }

    public static class VetCowViewHolder extends RecyclerView.ViewHolder {
        TextView tvCowId, tvCowName, tvStatus, tvFarmer, tvContact;

        public VetCowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCowId = itemView.findViewById(R.id.tvCowId);
            tvCowName = itemView.findViewById(R.id.tvCowName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvFarmer = itemView.findViewById(R.id.tvFarmer);
            tvContact = itemView.findViewById(R.id.tvContact);
        }
    }
}
