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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.UserDO;

import java.sql.SQLException;


public class MyActivity extends Activity {
    EditText editTextUsername, editTextPassword;
    Button buttonLogin, buttonSignUp;

    ScrapDatabaseAdapter mScrapDatabaseAdapter;

    String username, passwd;
    String TAG = "MyActivity";

    String[] categoryNames = new String[]{
            "Paper",
            "Cardbox",
            "Iron",
            "Tin",
            "Plastic"
    };

    double[] prices = new double[]{
            9.00,
            7.00,
            18.00,
            50.00,
            12.50,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AmazonAWS
        initializeApplication();
        setContentView(R.layout.activity_my);

        /* TODO : Why onCreate is being called for an activity instance already on the stack?
         * Reason : new intent launched
          * But where does prev instance of this activity goes from activity stack?*/
        /*if(getIntent().getBooleanExtra("EXIT",false)){
            finish();
        }*/

        Log.d(TAG, "Inside onCreate of MyActivity");
        mScrapDatabaseAdapter = new ScrapDatabaseAdapter(this);

        //For the time being, create price list on the user end
        try {
            mScrapDatabaseAdapter.createPriceList(categoryNames, prices, categoryNames.length);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //check the values of shared preferences username and password :
        //If they are not null and matches some user credentials in the database(need to check this also,
        //because user might have changed his credentials through website), directly launch HomePage with sharedPref credentials
        username = HandleSharedPrefs.getSharedPrefValue(MyActivity.this, "username");
        passwd = HandleSharedPrefs.getSharedPrefValue(MyActivity.this, "passwd");

        try {
            if(username != null && username.length()!=0 && passwd != null && passwd.length()!=0 && mScrapDatabaseAdapter.verifyLoginCredentials(username, passwd)){
                Intent intent = new Intent(MyActivity.this,HomePage.class);
                startActivity(intent);
                finish();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        editTextUsername = (EditText)findViewById(R.id.edit_text_username);
        editTextPassword = (EditText)findViewById(R.id.edit_text_password);

        buttonLogin = (Button)findViewById(R.id.button_login);
        buttonSignUp = (Button)findViewById(R.id.button_sign_up);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login with proper error handling
                //if correct credentials, take user to HomePage
                try {
                    username = editTextUsername.getText().toString();
                    passwd = editTextPassword.getText().toString();
                    Log.d(TAG, "username : "+username+", password : "+passwd);
                    if(username != null && username.length()!=0 && passwd != null && passwd.length()!=0)
                    {
                        verifyLoginCredentials(username, passwd);
                    }
                    else
                    {
                        Toast.makeText(MyActivity.this,"Incorrect username/password",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //else show toast
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyActivity.this, SignUpPage.class);
                startActivity(mIntent);
            }
        });

        /*final ScrapCategoryDO myScrapCategoryObj = new ScrapCategoryDO();
        for(int i=0;i<categoryNames.length;i++){
            myScrapCategoryObj.setCategoryName(categoryNames[i]);
            myScrapCategoryObj.setUnitPrice(prices[i]);
            Runnable runnable = new Runnable(){

                @Override
                public void run() {
                    //AWSMobileClient.defaultMobileClient().getDynamoDBMapper().save(myScrapCategoryObj);
                    //ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class,"Plastic");
                    ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class);
                    if(myScrapCategoryObj1!=null)
                        Log.d(TAG, "Scrap Category = "+myScrapCategoryObj1.getUnitPrice());
                    else
                        Log.d(TAG, "Scrap Category = null");
                }
            };
            Thread myThread = new Thread(runnable);
            myThread.start();
        }*/

        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                /*AWSMobileClient.defaultMobileClient().getDynamoDBMapper().save(myScrapCategoryObj);
                ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class,"Plastic");
                Log.d(TAG,"Rohit category name = "+myScrapCategoryObj1.getCategoryName()+", unit price = "+myScrapCategoryObj1.getUnitPrice());*/

                DynamoDBMapper myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                try {
                    //FindAllScrapCategories(myDynamoDbMapper);
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

    private void verifyLoginCredentials(final String username, final String passwd) {
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                //AWSMobileClient.defaultMobileClient().getDynamoDBMapper().save(myScrapCategoryObj);
                //ScrapCategoryDO myScrapCategoryObj1 = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(ScrapCategoryDO.class,"Plastic");
                UserDO myObj = AWSMobileClient.defaultMobileClient().getDynamoDBMapper().load(UserDO.class, username);
                if(myObj!=null){
                    if(myObj.getPasswd().equals(passwd)){
                        Message messageToParent = new Message();
                        messageToParent.what = 0;

                        Bundle messageData = new Bundle();
                        messageData.putBoolean("success",true);
                        messageToParent.setData(messageData);

                        // send message to mainThread
                        mainHandler.sendMessage(messageToParent);
                    }else{
                        Message messageToParent = new Message();
                        messageToParent.what = 0;

                        Bundle messageData = new Bundle();
                        messageData.putBoolean("success",false);
                        messageToParent.setData(messageData);

                        // send message to mainThread
                        mainHandler.sendMessage(messageToParent);
                    }
                }
                else{
                    Message messageToParent = new Message();
                    messageToParent.what = 0;

                    Bundle messageData = new Bundle();
                    messageData.putBoolean("success",false);
                    messageToParent.setData(messageData);

                    // send message to mainThread
                    mainHandler.sendMessage(messageToParent);
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


    /** The main handler. */
    public Handler mainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //Log.d(TAG,"Message received by handler of MainActivity");
            if (msg.what == 0) {
                if(msg.getData().getBoolean("success")){
                    //successfully login the user
                    HandleSharedPrefs.saveUsernameSharedPref(MyActivity.this, "username", username, "passwd", passwd);
                    Intent intent = new Intent(MyActivity.this,HomePage.class);
                    //intent.putExtra("username", editTextUsername.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MyActivity.this,"Incorrect username/password",Toast.LENGTH_SHORT).show();
                }
            }
        };
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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
