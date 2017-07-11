package com.example.rohitsingla.scrapman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.UserDO;

import java.sql.SQLException;


public class AccountSettings extends Activity {
    String TAG = "AccountSettings";
    EditText editTextName, editTextPhone, editTextAddress;
    Button buttonUpdate, buttonChangePassword;

    TextView textViewUsername;
    DynamoDBMapper myDynamoDbMapper;
    Context mCtx = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AmazonAWS
        initializeApplication();
        setContentView(R.layout.activity_account_settings);
        mCtx = AccountSettings.this;

        textViewUsername = (TextView)findViewById(R.id.text_view_username_email);
        editTextName = (EditText)findViewById(R.id.edit_text_name);
        editTextPhone = (EditText)findViewById(R.id.edit_text_phone);
        editTextAddress = (EditText)findViewById(R.id.edit_text_address);
        buttonUpdate = (Button)findViewById(R.id.button_update);
        buttonChangePassword = (Button)findViewById(R.id.button_change_password);

        textViewUsername.setText(HandleSharedPrefs.getSharedPrefValue(mCtx,"username"));

        try {
            Runnable runnable = new Runnable(){

                @Override
                public void run() {
                    //AWSMobileClient.defaultMobileClient().getDynamoDBMapper().save(myScrapCategoryObj);
                    //ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class,"Plastic");

                    myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                    UserDO myObj = myDynamoDbMapper.load(UserDO.class, HandleSharedPrefs.getSharedPrefValue(mCtx,"username"));

                    Message messageToParent = new Message();
                    messageToParent.what = 0;

                    Bundle messageData = new Bundle();
                    messageData.putString("name",myObj.getName());
                    messageData.putString("phone",myObj.getPhone());
                    messageData.putString("address",myObj.getAddress());
                    messageToParent.setData(messageData);

                    // send message to mainThread
                    mainHandler.sendMessage(messageToParent);
                }
            };
            Thread myThread = new Thread(runnable);
            myThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStateInfo.isOnline(mCtx)){
                    try {
                        updateProfile(HandleSharedPrefs.getSharedPrefValue(mCtx, "username"), editTextName.getText().toString(), editTextPhone.getText().toString(), editTextAddress.getText().toString());
                        Intent intent = new Intent(mCtx, HomePage.class);
                        startActivity(intent);
                        finish();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(mCtx, "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStateInfo.isOnline(mCtx)) {
                    Intent intent = new Intent(mCtx, ChangePassword.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(mCtx,"Sorry! Not connected to internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void updateProfile(final String username, final String name, final String phone, final String address) throws SQLException {
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                // Retrieve the item.
                UserDO itemRetrieved = myDynamoDbMapper.load(UserDO.class, username);

                // Update the item.
                itemRetrieved.setName(name);
                itemRetrieved.setPhone(phone);
                itemRetrieved.setAddress(address);
                myDynamoDbMapper.save(itemRetrieved);
                Log.d(TAG, "Updated account details for user : "+username);
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    /** The main handler. */
    public Handler mainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //Log.d(TAG,"Message received by handler of MainActivity");
            if (msg.what == 0) {
                editTextName.setText(msg.getData().getString("name"));
                editTextPhone.setText(msg.getData().getString("phone"));
                editTextAddress.setText(msg.getData().getString("address"));
            }
        };
    };

    //For AmazonAWS
    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ... Put any application-specific initialization logic here ...
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_settings, menu);
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
        else if(id == R.id.logout){
            //logout from the session and go to login page
            Intent intent = new Intent(this,MyActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
