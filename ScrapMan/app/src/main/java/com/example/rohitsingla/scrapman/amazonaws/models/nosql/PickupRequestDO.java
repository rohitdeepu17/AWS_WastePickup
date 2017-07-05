package com.example.rohitsingla.scrapman.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "appforscrap-mobilehub-1250419665-PickupRequest")

public class PickupRequestDO {
    private String _requestId;
    private String _day;
    private int _status;
    private String _timeSlot;
    private String _username;

    @DynamoDBHashKey(attributeName = "RequestId")
    @DynamoDBAttribute(attributeName = "RequestId")
    public String getRequestId() {
        return _requestId;
    }

    public void setRequestId(final String _requestId) {
        this._requestId = _requestId;
    }
    @DynamoDBAttribute(attributeName = "Day")
    public String getDay() {
        return _day;
    }

    public void setDay(final String _day) {
        this._day = _day;
    }
    @DynamoDBAttribute(attributeName = "Status")
    public int getStatus() {
        return _status;
    }

    public void setStatus(final int _status) {
        this._status = _status;
    }
    @DynamoDBAttribute(attributeName = "TimeSlot")
    public String getTimeSlot() {
        return _timeSlot;
    }

    public void setTimeSlot(final String _timeSlot) {
        this._timeSlot = _timeSlot;
    }

    @DynamoDBIndexHashKey(attributeName = "Username", globalSecondaryIndexName = "usernameindex")
    public String getUsername() {
        return _username;
    }

    public void setUsername(final String _username) {
        this._username = _username;
    }

}
