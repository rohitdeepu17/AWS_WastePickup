package com.example.rohitsingla.scrapman;

/**
 * This class is to store <categoryName,unitPrice> pairs
 *
 * Created by rohitsingla on 29/08/15.
 */
public class PriceListPairs {
    private final String key;
    private final Double value;

    public PriceListPairs(String aKey, Double aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public String key()   { return key; }
    public Double value() { return value; }
}