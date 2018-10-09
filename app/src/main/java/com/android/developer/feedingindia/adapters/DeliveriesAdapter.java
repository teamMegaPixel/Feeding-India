package com.android.developer.feedingindia.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.pojos.DeliveryDetails;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;


public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.ViewHolder>{

    private List<DeliveryDetails> deliveries;
    private static ClickListener clickListener;

    public DeliveriesAdapter(List<DeliveryDetails> deliveries){
        this.deliveries = deliveries;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView donorAddress,hungerSpotAddress,status;
        public ImageView deliveryImage;

        public ViewHolder(View view){
            super(view);
            donorAddress = view.findViewById(R.id.donor_address);
            hungerSpotAddress = view.findViewById(R.id.hunger_spot_address);
            status = view.findViewById(R.id.status);
            deliveryImage = view.findViewById(R.id.delivery_img);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(),view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener){

        DeliveriesAdapter.clickListener = clickListener;

    }

    public interface ClickListener{
        void onItemClick(int position,View view);
    }

    @NonNull
    @Override
    public DeliveriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.delivery_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DeliveriesAdapter.ViewHolder viewHolder, int i) {

        HashMap<String,String> donorAddressHashMap;
        donorAddressHashMap = deliveries.get(i).getDonorAddress();
        String donorAddressString = donorAddressHashMap.get("address").trim() + "\n" +
                donorAddressHashMap.get("city").trim() + "\n" +
                donorAddressHashMap.get("state").trim() + "-" + donorAddressHashMap.get("pinCode").trim();
        viewHolder.donorAddress.setText(donorAddressString);

        HashMap<String,String> hungerSpotAddressHashMap;
        hungerSpotAddressHashMap = deliveries.get(i).getHungerSpotAddress();
        String hungerSpotAddressString = hungerSpotAddressHashMap.get("address").trim() + "\n" +
                hungerSpotAddressHashMap.get("city").trim() + "\n" +
                hungerSpotAddressHashMap.get("state").trim() + "-" + hungerSpotAddressHashMap.get("pinCode").trim();
        viewHolder.hungerSpotAddress.setText(hungerSpotAddressString);

        viewHolder.status.setText(deliveries.get(i).getStatus().toUpperCase());

        viewHolder.itemView.setTag(deliveries.get(i).getDonationId());
        if(deliveries.get(i).getDeliveryImgUrl() != null){
            viewHolder.deliveryImage.setVisibility(View.VISIBLE);
            Picasso.get().load(deliveries.get(i).getDeliveryImgUrl()).into(viewHolder.deliveryImage);
        }else{
            viewHolder.deliveryImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }
}
