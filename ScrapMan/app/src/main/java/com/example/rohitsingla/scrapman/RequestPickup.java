package com.example.rohitsingla.scrapman;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.ScanResultPage;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.PickupRequestDO;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.PickupRequestItemsDO;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.ScrapCategoryDO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


public class RequestPickup extends Activity {
    ScrapDatabaseAdapter mScrapDatabaseAdapter;

    Button buttonSubmitRequest;
    Spinner spinnerTimeSlot;

    TextView textViewSelectedDate;

    private double[] arrTemp;
    private String[] categoryNames;
    double weights[];

    ListView listViewPriceList ;
    ArrayList<PriceListPairs> mPriceListPairs = new ArrayList<PriceListPairs>();
    ArrayList<String> mPriceList  = new ArrayList<String>();
    DynamoDBMapper myDynamoDbMapper;

    private Calendar calendar;
    private int year, month, day;
    private static final String TAG = "RequestPickUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AmazonAWS
        initializeApplication();

        setContentView(R.layout.activity_request_pickup);
        listViewPriceList = (ListView) findViewById(R.id.list_view_request_pickup);
        textViewSelectedDate = (TextView) findViewById(R.id.text_view_date_selected);

        mScrapDatabaseAdapter = new ScrapDatabaseAdapter(this);

        buttonSubmitRequest = (Button)findViewById(R.id.button_submit_request);
        spinnerTimeSlot = (Spinner)findViewById(R.id.spinner_time_slot);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        textViewSelectedDate.setText(day+"/"+month+"/"+year);


        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                try {
                    FindAllScrapCategories(myDynamoDbMapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();

        buttonSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, textViewSelectedDate.getText().toString()+spinnerTimeSlot.getSelectedItem().toString());
                    Log.d(TAG, "The number of categories = "+categoryNames.length);
                    double sum = 0.0;
                    for(int i=0;i<categoryNames.length;i++) {
                        Log.d(TAG, "The weight at index " + i + " = " + arrTemp[i]);
                        sum += arrTemp[i];
                    }
                    if(sum > 0.0) {
                        requestPickup(textViewSelectedDate.getText().toString(), spinnerTimeSlot.getSelectedItem().toString(), HandleSharedPrefs.getSharedPrefValue(RequestPickup.this, "username"), categoryNames, arrTemp, categoryNames.length);
                        Intent intent = new Intent(RequestPickup.this, CheckPastRequests.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(RequestPickup.this, "Sorry, you have to request pickup for at least one category", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        textViewSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    changeDate(arg1, arg2 + 1, arg3);
                }


            };

    private void changeDate(int arg1, int arg2, int arg3) {
        year = arg1;
        month = arg2;
        day = arg3;
        textViewSelectedDate.setText(day+"/"+month+"/"+year);
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
                //Log.d(TAG,"CategoryName = "+myList.get(i).getCategoryName()+"UnitPrice = "+myList.get(i).getUnitPrice());
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
                arrTemp = new double[mPriceList.size()];
                categoryNames = new String[mPriceList.size()];
                for(int i=0;i<mPriceList.size();i++)
                    categoryNames[i] = mPriceListPairs.get(i).key();
                MyListAdapter myListAdapter = new MyListAdapter();
                // Assign adapter to ListView
                listViewPriceList.setAdapter(myListAdapter);
            }
        };
    };






    //Creating Custom Adapter
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(mPriceListPairs != null){
                return mPriceListPairs.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mPriceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //ViewHolder holder = null;
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = RequestPickup.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_request_pickup, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.text_view_category_unit_price);
                holder.editText1 = (EditText) convertView.findViewById(R.id.edit_text_quantity);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            holder.textView1.setText(mPriceList.get(position));
            Log.d(TAG, "value to holder at " + position + " = " + mPriceList.get(position));
            holder.editText1.setText(String.valueOf(arrTemp[position]));
            holder.editText1.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    if(arg0.length()>0)
                        arrTemp[holder.ref] = Double.parseDouble(arg0.toString());      //Bug here : in case we erase entire double value 0.0
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            int ref;
        }
    }


    void requestPickup(String day, String timeSlot, String username, String[] categoryNames, double[] weights, int n) throws SQLException {
        String requestId = insertNewPickupRequest(day, timeSlot, username);
        Log.d(TAG,"requestid = "+requestId);
        insertPickupRequestItems(requestId, categoryNames, weights, n);
    }

    /**
     * Function to create a new pickup request
     *
     * @param day
     * @param timeSlot
     * @return requestId for the inserted row/entry
     */
    String insertNewPickupRequest(String day, String timeSlot, String username) throws SQLException {
        final PickupRequestDO myObj = new PickupRequestDO();
        myObj.setDay(day);
        myObj.setTimeSlot(timeSlot);
        myObj.setStatus(0);
        myObj.setUsername(username);
        String requestId = UUID.randomUUID().toString();
        myObj.setRequestId(requestId);
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                myDynamoDbMapper.save(myObj);
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
        return requestId;
    }

    /**
     * This function inserts approximate weights corresponding to different categories for a particular requestId
     *
     * @param requestId     request id
     * @param categoryNames category names
     * @param weights        weight for a category
     * @param n             number of categories
     */
    void insertPickupRequestItems(String requestId, String[] categoryNames, double[] weights, int n) throws SQLException {
        for(int i=0;i<n;i++){
            if(weights[i]>0){
                final PickupRequestItemsDO myObj = new PickupRequestItemsDO();
                myObj.setWeight(weights[i]);
                myObj.setRequestIdCategoryName(requestId+"__"+categoryNames[i]);
                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        myDynamoDbMapper.save(myObj);
                    }
                };
                Thread myThread = new Thread(runnable);
                myThread.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.request_pickup, menu);
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
