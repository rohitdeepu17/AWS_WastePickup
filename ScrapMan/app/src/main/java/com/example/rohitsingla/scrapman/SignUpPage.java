package com.example.rohitsingla.scrapman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.UserDO;

import java.sql.SQLException;


public class SignUpPage extends Activity {

    Button buttonSubmit;
    EditText editTextUsername, editTextPassword, editTextConfirmPassword, editTextName, editTextPhone, editTextAddress;

    ScrapDatabaseAdapter mScrapDatabaseAdapter;
    Context mCtx = null;

    private static final String TAG = "SignUpPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mCtx = SignUpPage.this;
        //AmazonAWS
        initializeApplication();
        mScrapDatabaseAdapter = new ScrapDatabaseAdapter(this);

        editTextUsername = (EditText) findViewById(R.id.edit_text_username_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        editTextConfirmPassword = (EditText) findViewById(R.id.edit_text_confirm_password);
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextPhone = (EditText) findViewById(R.id.edit_text_phone);
        editTextAddress = (EditText) findViewById(R.id.edit_text_address);



        buttonSubmit = (Button)findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStateInfo.isOnline(mCtx)){
                    boolean flag = true;

                    final String username = editTextUsername.getText().toString();
                    final String passwd = editTextPassword.getText().toString();
                    String confirmPasswd = editTextConfirmPassword.getText().toString();
                    final String name = editTextName.getText().toString();
                    final String phone = editTextPhone.getText().toString();
                    final String address = editTextAddress.getText().toString();

                    //check if any field is empty or any field is just filled with spaces
                    if(username.length()==0 || passwd.length()==0 || confirmPasswd.length()==0 ||
                            name.length()==0 || phone.length()==0 || address.length()==0 ||
                            username.trim().length()==0 || passwd.trim().length()==0 || confirmPasswd.trim().length()==0 ||
                            name.trim().length()==0 || phone.trim().length()==0 || address.trim().length()==0) {
                    /*Log.d(TAG,""+username.length()+passwd.length()+confirmPasswd.length()+name.length()+phone.length()+address.length()+
                    username.trim().length()+passwd.trim().length()+confirmPasswd.trim().length()+name.trim().length()+phone.trim().length()+address.trim().length());
                    Toast.makeText(mCtx, "All the above fields are mandatory", Toast.LENGTH_SHORT).show();*/
                        Toast.makeText(mCtx,"All fields are compulsory", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }

                    //check if user with given username already exists, only if no problem so far, i.e, flag is still set
                    try {
                        if(flag && mScrapDatabaseAdapter.checkIfUserAlreadyExists(username)) {
                            Toast.makeText(mCtx, "User already exists", Toast.LENGTH_SHORT).show();
                            flag = false;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //check if both password and confirm password are same
                    if(flag && !passwd.equals(confirmPasswd)) {
                        Toast.makeText(mCtx, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }

                    //if all checks are done successfully, register new user
                    //For the time, commenting this code to make PriceList work
                    if(flag) {
                        try {
                            Log.d(TAG, "Username : " + username);
                            Log.d(TAG, "Password : "+passwd);
                            Log.d(TAG, "Name : "+name);
                            Log.d(TAG, "Phone : "+phone);
                            Log.d(TAG, "Address : "+address);

                            Runnable runnable = new Runnable(){

                                @Override
                                public void run() {
                                    UserDO myObj = new UserDO();
                                    myObj.setUsername(username);
                                    myObj.setPasswd(passwd);
                                    myObj.setName(name);
                                    myObj.setPhone(phone);
                                    myObj.setAddress(address);
                                    AWSMobileClient.defaultMobileClient().getDynamoDBMapper().save(myObj);
                                }
                            };
                            Thread myThread = new Thread(runnable);
                            myThread.start();

                            HandleSharedPrefs.saveUsernameSharedPref(mCtx, "username", username, "passwd", passwd);
                            Toast.makeText(mCtx, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(mCtx, HomePage.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(mCtx,"Sorry! Not connected to internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //For AmazonAWS
    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ... Put any application-specific initialization logic here ...
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up_page, menu);
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
