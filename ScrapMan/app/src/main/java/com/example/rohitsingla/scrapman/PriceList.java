package com.example.rohitsingla.scrapman;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.ScrapCategoryDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PriceList extends Activity {
    ListView listViewPriceList ;
    String TAG = "PriceList";

    ArrayList<PriceListPairs> mPriceListPairs = new ArrayList<PriceListPairs>();
    ArrayList<String> mPriceList  = new ArrayList<String>();
    Context mCtx=null;
    ScrapDatabaseAdapter mScrapDatabaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AmazonAWS
        initializeApplication();
        setContentView(R.layout.activity_price_list);
        mCtx = PriceList.this;

        mScrapDatabaseAdapter = new ScrapDatabaseAdapter(this);

        listViewPriceList = (ListView) findViewById(R.id.list_view_price_list);
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                /*AWSMobileClient.defaultMobileClient().getDynamoDBMapper().save(myScrapCategoryObj);
                ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class,"Plastic");
                Log.d(TAG,"Rohit category name = "+myScrapCategoryObj1.getCategoryName()+", unit price = "+myScrapCategoryObj1.getUnitPrice());*/

                DynamoDBMapper myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                try {
                    FindAllScrapCategories(myDynamoDbMapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class);
                final ScrapCategoryDO myHashKeyValues = new ScrapCategoryDO();
                DynamoDBQueryExpression<ScrapCategoryDO> queryExpression = new DynamoDBQueryExpression<ScrapCategoryDO>().setHashKeyValues(myHashKeyValues);
                List<ScrapCategoryDO> allScrapCategories = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().query(ScrapCategoryDO.class, queryExpression);
                Log.d(TAG, "Number of Scrap Categories = "+allScrapCategories.size());*/
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();




    }

    //For AmazonAWS
    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ... Put any application-specific initialization logic here ...
    }

    private void FindAllScrapCategories(DynamoDBMapper mapper)
            throws Exception {
        Log.d(TAG, "Find all scrap categories:-");

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS("12"));

        /*DynamoDBQueryExpression<ScrapCategoryDO> queryExpression = new DynamoDBQueryExpression<ScrapCategoryDO>()
                .withKeyConditionExpression("categoryName = :val1").withExpressionAttributeValues(eav);*/

        //DynamoDBQueryExpression<ScrapCategoryDO> queryExpression = new DynamoDBQueryExpression<ScrapCategoryDO>();

        /*ScrapCategoryDO myObject = new ScrapCategoryDO();
        myObject.setCategoryName("Iron");
        DynamoDBQueryExpression<ScrapCategoryDO> queryExpression = new DynamoDBQueryExpression<ScrapCategoryDO>()
                .withHashKeyValues(myObject)
                .withFilterExpression("UnitPrice < :val1")
                .withExpressionAttributeValues(eav);

        List<ScrapCategoryDO> latestReplies = mapper.query(ScrapCategoryDO.class, queryExpression);
        Log.d(TAG, "Number of categories = "+latestReplies.size());
        for (ScrapCategoryDO reply : latestReplies) {
            Log.d(TAG,"category name = "+reply.getCategoryName()+", unit price = "+ reply.getUnitPrice());
        }*/

        ScanResultPage<ScrapCategoryDO> yourModelClassResultPage = null;
        do {
            //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            if (yourModelClassResultPage != null) {
                scanExpression.setExclusiveStartKey(yourModelClassResultPage.getLastEvaluatedKey());
            }
            yourModelClassResultPage = mapper.scanPage(ScrapCategoryDO.class, scanExpression);
            Log.d(TAG,"Hey, "+yourModelClassResultPage.getResults().size());
            List<ScrapCategoryDO> myList = yourModelClassResultPage.getResults();
            int n = myList.size();
            for(int i=0;i<n;i++){
                mPriceListPairs.add(new PriceListPairs(myList.get(i).getCategoryName(), myList.get(i).getUnitPrice()));
                Log.d(TAG,"CategoryName = "+myList.get(i).getCategoryName()+"UnitPrice = "+myList.get(i).getUnitPrice());
            }

        } while (yourModelClassResultPage.getLastEvaluatedKey() != null);

        long size = mPriceListPairs.size();
        Log.d(TAG, "size of mPriceListPairs = "+mPriceListPairs.size());

        for(int i=0;i<size;i++)
        {
            mPriceList.add("" + mPriceListPairs.get(i).key() + "( Rs." + mPriceListPairs.get(i).value() + "/Kg)");
        }

        Message messageToParent = new Message();
        messageToParent.what = 0;

        Bundle messageData = new Bundle();
        messageData.putString("text","dummy");
        messageToParent.setData(messageData);

        // send message to mainThread
        mainHandler.sendMessage(messageToParent);


    }

    /** The main handler. */
    public Handler mainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //Log.d(TAG,"Message received by handler of MainActivity");
            if (msg.what == 0) {
                // Define a new Adapter
                // First parameter - Context
                // Second parameter - Layout for the row
                // Third parameter - ID of the TextView to which the data is written
                // Forth - the Array of data

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mCtx,
                        android.R.layout.simple_list_item_1, android.R.id.text1, mPriceList);


                // Assign adapter to ListView
                listViewPriceList.setAdapter(adapter);
            }
        };
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.price_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
