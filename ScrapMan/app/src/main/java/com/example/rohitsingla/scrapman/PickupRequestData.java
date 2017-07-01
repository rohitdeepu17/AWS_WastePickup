package com.example.rohitsingla.scrapman;

/**
 * Created by rohitsingla on 30/08/15.
 */
public class PickupRequestData {
    private final int mRequestId;
    private final String mDay;
    private final String mTimeSlot;
    private int mStatus;

    public PickupRequestData(int requestId, String day, String timeSlot, int status){
        mRequestId = requestId;
        mDay = day;
        mTimeSlot = timeSlot;
        mStatus = status;
    }

    public int getmRequestId() {
        return mRequestId;
    }

    public String getmDay() {

        return mDay;
    }

    public String getmTimeSlot() {

        return mTimeSlot;
    }

    public int getmStatus() {
        return mStatus;
    }
}
