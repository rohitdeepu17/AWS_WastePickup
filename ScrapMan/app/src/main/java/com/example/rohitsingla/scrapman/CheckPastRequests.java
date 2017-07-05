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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.PickupRequestDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckPastRequests extends Activity {
    ListView listViewPastRequests ;
    String TAG = "CheckPastRequests";
    String username;

    ArrayList<PickupRequestDO> mPickupRequestData = new ArrayList<PickupRequestDO>();
    ArrayList<String> mPickupRequestStrings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = HandleSharedPrefs.getSharedPrefValue(CheckPastRequests.this,"username");
        //listViewPastRequests = (ListView) findViewById(R.id.list_view_past_requests);
        setContentView(R.layout.activity_check_past_requests);
        //AmazonAWS
        initializeApplication();

        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                DynamoDBMapper myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                try {
                    FindAllPickupRequests(myDynamoDbMapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void FindAllPickupRequests(DynamoDBMapper mapper)
            throws Exception {
        Log.d(TAG, "Find all past requests:-");

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(username));

        PickupRequestDO myObject = new PickupRequestDO();
        Log.d(TAG,"username in session = "+username);
        myObject.setUsername(username);

        DynamoDBQueryExpression<PickupRequestDO> queryExpression = new DynamoDBQueryExpression<PickupRequestDO>()
                .withIndexName("usernameindex")
                .withConsistentRead(false)
                .withHashKeyValues(myObject);

        List<PickupRequestDO> latestReplies = mapper.query(PickupRequestDO.class, queryExpression);
        Log.d(TAG, "Number of categories = "+latestReplies.size());
        int n = latestReplies.size();
        int c=0;
        for (PickupRequestDO reply : latestReplies) {
            mPickupRequestData.add(reply);
            mPickupRequestStrings.add("" + (c+1) + ". " + reply.getDay() + " " + reply.getTimeSlot() + " " + getStatusString(reply.getStatus()));
            Log.d(TAG,"requestid = "+reply.getRequestId()+", day = "+reply.getDay()+", timeslot = "+ reply.getTimeSlot()+", status="+reply.getStatus());
            c++;
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckPastRequests.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, mPickupRequestStrings);

                listViewPastRequests = (ListView) findViewById(R.id.list_view_past_requests);
                // Assign adapter to ListView
                listViewPastRequests.setAdapter(adapter);

                listViewPastRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(CheckPastRequests.this, PastRequestDetails.class);
                        intent.putExtra("requestId",(position+1));
                        intent.putExtra("requestId_aws",mPickupRequestData.get(position).getRequestId());
                        intent.putExtra("day",mPickupRequestData.get(position).getDay());
                        intent.putExtra("timeSlot",mPickupRequestData.get(position).getTimeSlot());
                        intent.putExtra("status",getStatusString(mPickupRequestData.get(position).getStatus()));
                        startActivity(intent);
                    }
                });
            }
        };
    };

    public String getStatusString(int status)
    {
        if(status == 0)
            return "Requested";
        else if(status == 1)
            return "Accepted";
        else if(status == 2)
            return "Picked";
        else if(status == 3)
            return "Cancelled";
        else
            return "Error";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_past_requests, menu);
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
