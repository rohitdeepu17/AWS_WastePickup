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

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.example.rohitsingla.scrapman.amazonaws.mobile.AWSMobileClient;
import com.example.rohitsingla.scrapman.amazonaws.models.nosql.UserDO;

import java.sql.SQLException;


public class ChangePassword extends Activity {
    String TAG = "ChangePassword";
    ScrapDatabaseAdapter mScrapDatabaseAdapter;

    EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword;
    Button buttonUpdatePassword;
    Context mCtx = null;
    DynamoDBMapper myDynamoDbMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mCtx = ChangePassword.this;
        //AmazonAWS
        initializeApplication();
        myDynamoDbMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mScrapDatabaseAdapter = new ScrapDatabaseAdapter(this);

        editTextCurrentPassword = (EditText) findViewById(R.id.edit_text_current_password);
        editTextNewPassword = (EditText) findViewById(R.id.edit_text_new_password);
        editTextConfirmNewPassword = (EditText) findViewById(R.id.edit_text_confirm_new_password);

        buttonUpdatePassword = (Button)findViewById(R.id.button_update_password);
        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStateInfo.isOnline(mCtx)){
                    //update the password in database with proper error handling
                    String currentPassword = editTextCurrentPassword.getText().toString();
                    String newPassword = editTextNewPassword.getText().toString();
                    String confirmNewPassword = editTextConfirmNewPassword.getText().toString();

                    boolean flag = true;

                    //check if any field is empty or filled with only spaces
                    if(currentPassword.length() == 0 || newPassword.length() == 0 || confirmNewPassword.length() == 0 ||
                            currentPassword.trim().length() == 0|| newPassword.trim().length() == 0 || confirmNewPassword.trim().length() == 0){
                        Toast.makeText(ChangePassword.this, "All the above fields are mandatory", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }

                    //verify current password, using verifyLoginCredentials function
                    try {
                        if(flag && !currentPassword.contentEquals(HandleSharedPrefs.getSharedPrefValue(ChangePassword.this, "passwd").toString())){
                            Log.d(TAG, "current password from shared pref = "+HandleSharedPrefs.getSharedPrefValue(ChangePassword.this, "passwd")+", size = "+HandleSharedPrefs.getSharedPrefValue(ChangePassword.this, "passwd").toString().length());
                            Log.d(TAG, "current password from user entered= "+currentPassword+", size = "+currentPassword.length());
                            Toast.makeText(ChangePassword.this, "Sorry, You have entered wrong current password", Toast.LENGTH_SHORT).show();
                            flag = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //verify if both password and confirmNewPassword match
                    if(flag && !newPassword.equals(confirmNewPassword)){
                        Toast.makeText(ChangePassword.this, "Sorry, Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }

                    //if no error so far, update password for this user
                    if(flag){
                        try {
                            String username = HandleSharedPrefs.getSharedPrefValue(ChangePassword.this, "username");
                            updatePassword(username, newPassword);
                            //update shared pref also
                            HandleSharedPrefs.saveUsernameSharedPref(ChangePassword.this, "username",username , "passwd", newPassword);
                            Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT);
                            Intent intent = new Intent(ChangePassword.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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

    void updatePassword(final String username ,final String newPassword) throws SQLException {
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                // Retrieve the item.
                UserDO itemRetrieved = myDynamoDbMapper.load(UserDO.class, username);

                // Update the item.
                itemRetrieved.setPasswd(newPassword);
                myDynamoDbMapper.save(itemRetrieved);
                Log.d(TAG, "Updated account details for user : " + username);
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.change_password, menu);
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
