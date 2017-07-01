package com.example.rohitsingla.scrapman.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "appforscrap-mobilehub-1250419665-PickupRequestItems")

public class PickupRequestItemsDO {
    private String _requestIdCategoryName;
    private Double _weight;

    @DynamoDBHashKey(attributeName = "RequestIdCategoryName")
    @DynamoDBAttribute(attributeName = "RequestIdCategoryName")
    public String getRequestIdCategoryName() {
        return _requestIdCategoryName;
    }

    public void setRequestIdCategoryName(final String _requestIdCategoryName) {
        this._requestIdCategoryName = _requestIdCategoryName;
    }
    @DynamoDBAttribute(attributeName = "Weight")
    public Double getWeight() {
        return _weight;
    }

    public void setWeight(final Double _weight) {
        this._weight = _weight;
    }

}
