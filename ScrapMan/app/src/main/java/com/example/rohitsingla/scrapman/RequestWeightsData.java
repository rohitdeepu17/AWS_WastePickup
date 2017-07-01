package com.example.rohitsingla.scrapman;

/**
 * This class is used to store weights details for different categories for a particular request
 *
 * Created by rohitsingla on 30/08/15.
 */
public class RequestWeightsData {
    private final String categoryName;
    private final Double weight;

    public RequestWeightsData(String categoryName, Double weight) {
        this.categoryName = categoryName;
        this.weight = weight;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Double getWeight() {
        return weight;
    }
}
