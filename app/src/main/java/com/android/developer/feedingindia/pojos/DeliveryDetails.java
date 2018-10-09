package com.android.developer.feedingindia.pojos;

import java.util.HashMap;

public class DeliveryDetails {

    private String donationId,hungerSpotId;
    private HashMap<String,String> donorAddress,hungerSpotAddress;
    private String status;
    private String deliveryImgUrl;

    public DeliveryDetails(){

    }

    public DeliveryDetails(String donationId, String hungerSpotId, HashMap<String, String> donorAddress, HashMap<String, String> hungerSpotAddress, String status) {
        this.donationId = donationId;
        this.hungerSpotId = hungerSpotId;
        this.donorAddress = donorAddress;
        this.hungerSpotAddress = hungerSpotAddress;
        this.status = status;
    }

    public String getDonationId() {
        return donationId;
    }

    public String getHungerSpotId() {
        return hungerSpotId;
    }

    public HashMap<String, String> getDonorAddress() {
        return donorAddress;
    }

    public HashMap<String, String> getHungerSpotAddress() {
        return hungerSpotAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setDeliveryImgUrl(String imgUrl){deliveryImgUrl = imgUrl; }

    public String getDeliveryImgUrl(){return  deliveryImgUrl; }
}
