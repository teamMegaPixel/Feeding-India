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
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

private HashMap<String,DonationDetails> donationDetailsHashMap = new HashMap<>();

private ArrayList<DonationDetails> userDonations = new ArrayList<>();


    public static class DonationViewHolder extends  RecyclerView.ViewHolder{
        public ImageView donationImageView,hungerSpotImageView;
        public TextView donationDetailsText,donationStatusText;
        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            donationImageView = itemView.findViewById(R.id.donation_image);
            hungerSpotImageView = itemView.findViewById(R.id.hunger_spot_image);
            donationDetailsText = itemView.findViewById(R.id.donation_detail);
            donationStatusText = itemView.findViewById(R.id.donation_status);
        }
    }


    public DonationAdapter(HashMap<String,DonationDetails> hashMap) {
        donationDetailsHashMap = hashMap;
        Set mSet = donationDetailsHashMap.entrySet();
        Iterator iterator = mSet.iterator();
        ObjectMapper objectMapper = new ObjectMapper();

        while (iterator.hasNext()) {
            Map.Entry myMapEntry = (Map.Entry) iterator.next();
            DonationDetails donationDetails = objectMapper.convertValue(myMapEntry.getValue(),DonationDetails.class);
            userDonations.add(donationDetails);
        }
    }



    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.donation_item,viewGroup,false);
        DonationViewHolder dvh = new DonationViewHolder(v);
        return dvh;
    }



    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder donationViewHolder, int i) {
        donationViewHolder.donationDetailsText.setText(userDonations.get(i).getFoodDescription());
        donationViewHolder.donationStatusText.setText(userDonations.get(i).getStatus());
        Picasso.get().load(userDonations.get(i).getImageUrl()).into(donationViewHolder.donationImageView);
        Picasso.get().load(userDonations.get(i).getHungerSpotImgUrl()).into(donationViewHolder.hungerSpotImageView);
    }

    @Override
    public int getItemCount() {
        return userDonations.size();
    }

}
