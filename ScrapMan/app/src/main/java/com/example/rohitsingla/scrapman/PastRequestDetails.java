package com.example.rohitsingla.scrapman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.ScanResultPage;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.PickupRequestDO;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.PickupRequestItemsDO;

import java.util.ArrayList;
import java.util.List;


public class PastRequestDetails extends Activity {
    String TAG = "PastRequestDetails";
    TextView textViewRequestId, textViewDay, textViewTimeSlot, textViewStatus;
    ListView listViewWeights ;
    Button buttonCancelRequest;

    ArrayList<PickupRequestItemsDO> mPriceListPairs = new ArrayList<PickupRequestItemsDO>();
    ArrayList<String> mPriceList  = new ArrayList<String>();

    String requestIdAWS = null;
    DynamoDBMapper myDynamoDbMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AmazonAWS
        initializeApplication();
        setContentView(R.layout.activity_past_request_details);

        textViewRequestId = (TextView)findViewById(R.id.text_view_request_id);
        textViewDay = (TextView)findViewById(R.id.text_view_day);
        textViewTimeSlot = (TextView)findViewById(R.id.text_view_time_slot);
        textViewStatus = (TextView)findViewById(R.id.text_view_status);

        listViewWeights = (ListView)findViewById(R.id.list_view_weight_details);
        buttonCancelRequest = (Button)findViewById(R.id.button_cancel_request);

        myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        //getting extras from intent
        final String day, timeSlot;
        final int requestId;
        String status;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                day = "Default";
                timeSlot = "Default";
                status = "Cancelled";
                requestId = -1;
            } else {
                day = extras.getString("day");
                timeSlot = extras.getString("timeSlot");
                status = extras.getString("status");
                requestId = extras.getInt("requestId");
                requestIdAWS = extras.getString("requestId_aws");
            }
        } else {
            day = (String) savedInstanceState.getSerializable("day");
            timeSlot = (String) savedInstanceState.getSerializable("timeSlot");
            status = (String) savedInstanceState.getSerializable("status");
            requestId = (Integer) savedInstanceState.getSerializable("requestId");
            requestIdAWS = (String) savedInstanceState.getSerializable("requestId_aws");
        }

        textViewRequestId.setText(""+requestId);
        textViewDay.setText(day);
        textViewTimeSlot.setText(timeSlot);
        textViewStatus.setText(""+status);

        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                DynamoDBMapper myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                try {
                    FindAllScrapItemsInRequest(myDynamoDbMapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();

        buttonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest();
                Toast.makeText(PastRequestDetails.this, "Pickup request cancelled successfully", Toast.LENGTH_SHORT);
                Intent intent = new Intent(PastRequestDetails.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void cancelRequest() {
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                // Retrieve the item.
                PickupRequestDO itemRetrieved = myDynamoDbMapper.load(PickupRequestDO.class, requestIdAWS);

                // Update the item.
                itemRetrieved.setStatus(3);
                myDynamoDbMapper.save(itemRetrieved);
                Log.d(TAG, "request Id : "+requestIdAWS+" updated");
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

    private void FindAllScrapItemsInRequest(DynamoDBMapper mapper)
            throws Exception {
        Log.d(TAG, "Find all scrap categories:-");

        /*Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(requestId));

        PickupRequestItemsDO myObject = new PickupRequestItemsDO();
        myObject.setRequestIdCategoryName(requestId+"__Paper");           //it should be LIKE expression

        DynamoDBQueryExpression<PickupRequestItemsDO> queryExpression = new DynamoDBQueryExpression<PickupRequestItemsDO>()
                .withHashKeyValues(myObject)
                .withFilterExpression("contains(FindAllScrapItemsInRequest, :val1)")
                .withExpressionAttributeValues(eav);


        List<PickupRequestItemsDO> latestReplies = mapper.query(PickupRequestItemsDO.class, queryExpression);
        Log.d(TAG, "Number of items = "+latestReplies.size());
        for (PickupRequestItemsDO reply : latestReplies) {
            mPriceListPairs.add(reply);
            mPriceList.add(reply.getRequestIdCategoryName()+" "+reply.getWeight());
            Log.d(TAG, "category name = " + reply.getRequestIdCategoryName() + ", weight = " + reply.getWeight());
        }*/




        ScanResultPage<PickupRequestItemsDO> yourModelClassResultPage = null;
        do {
            //DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            if (yourModelClassResultPage != null) {
                scanExpression.setExclusiveStartKey(yourModelClassResultPage.getLastEvaluatedKey());
            }
            yourModelClassResultPage = mapper.scanPage(PickupRequestItemsDO.class, scanExpression);
            Log.d(TAG,"Hey, "+yourModelClassResultPage.getResults().size());
            List<PickupRequestItemsDO> myList = yourModelClassResultPage.getResults();
            int n = myList.size();
            Log.d(TAG,"Size of PickupRequestItemsDo = "+n);
            for(int i=0;i<n;i++){
                if(myList.get(i).getRequestIdCategoryName().startsWith(requestIdAWS)){
                    mPriceListPairs.add(myList.get(i));
                    String fullreqcat = myList.get(i).getRequestIdCategoryName();
                    int index = fullreqcat.lastIndexOf('_');
                    mPriceList.add(fullreqcat.substring(index+1)+"       "+myList.get(i).getWeight()+" Kgs");
                    Log.d(TAG, "category name = " + myList.get(i).getRequestIdCategoryName() + ", weight = " + myList.get(i).getWeight());
                }
            }

        } while (yourModelClassResultPage.getLastEvaluatedKey() != null);


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

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PastRequestDetails.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, mPriceList);

                listViewWeights = (ListView)findViewById(R.id.list_view_weight_details);

                // Assign adapter to ListView
                listViewWeights.setAdapter(adapter);
                buttonCancelRequest.setEnabled(true);
            }
        };
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.past_request_details, menu);
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
