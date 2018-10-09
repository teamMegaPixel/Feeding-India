package com.android.developer.feedingindia.pojos;

import java.util.HashMap;

public class DonationDetails {

    private String foodDescription,foodPreparedOn,additionalContactNumber,status,userContactNumber,deliverer;
    private boolean hasContainer,canDonate;
    private HashMap<String,Object> donorAddress;
    private String imageUrl;
   private String hungerSpotImgUrl;


    public DonationDetails() {

    }

    public DonationDetails(String foodDescription, String foodPreparedOn, String additionalContactNumber, String status, String userContactNumber, boolean hasContainer, boolean canDonate, String deliverer, HashMap<String, Object> donorAddress) {
        this.foodDescription = foodDescription;
        this.foodPreparedOn = foodPreparedOn;
        this.additionalContactNumber = additionalContactNumber;
        this.status = status;
        this.userContactNumber = userContactNumber;
        this.hasContainer = hasContainer;
        this.canDonate = canDonate;
        this.deliverer = deliverer;
        this.donorAddress = donorAddress;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public String getFoodPreparedOn() {
        return foodPreparedOn;
    }

    public String getAdditionalContactNumber() {
        return additionalContactNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public boolean isHasContainer() {
        return hasContainer;
    }

    public boolean isCanDonate() {
        return canDonate;
    }

    public String getDeliverer() {
        return deliverer;
    }

    public HashMap<String,Object> getDonorAddress() {
        return donorAddress;
    }

    public String getImageUrl(){
        if(imageUrl != null)
            return imageUrl;
        else
            return null;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getHungerSpotImgUrl() {
        return hungerSpotImgUrl;
    }
}
