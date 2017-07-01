package com.example.rohitsingla.scrapman;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomePage extends Activity {
    Button buttonPriceList, buttonRequestPickup, buttonAccountSettings, buttonCheckPastRequests, buttonTnCs, buttonContactUs, buttonAboutUs, buttonLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Drawable img;

        buttonPriceList = (Button)findViewById(R.id.button_price_list);
        img = getApplicationContext().getResources().getDrawable( R.drawable.price_list );
        img.setBounds( 0, 30, 230, 230 );
        buttonPriceList.setCompoundDrawables(null, img, null, null);

        buttonRequestPickup = (Button) findViewById(R.id.button_request_pickup);
        img = getApplicationContext().getResources().getDrawable( R.drawable.request_pickup );
        img.setBounds( 0, 30, 230, 230 );
        buttonRequestPickup.setCompoundDrawables(null, img, null, null);

        buttonAccountSettings = (Button) findViewById(R.id.button_account_settings);
        img = getApplicationContext().getResources().getDrawable( R.drawable.account_settings );
        img.setBounds( 0, 30, 230, 230 );
        buttonAccountSettings.setCompoundDrawables(null, img, null, null);

        buttonCheckPastRequests = (Button) findViewById(R.id.button_check_past_requests);
        img = getApplicationContext().getResources().getDrawable( R.drawable.past_requests );
        img.setBounds( 0, 30, 230, 230 );
        buttonCheckPastRequests.setCompoundDrawables(null, img, null, null);

        buttonTnCs = (Button) findViewById(R.id.button_terms_and_conditions);
        img = getApplicationContext().getResources().getDrawable( R.drawable.terms_and_conditions );
        img.setBounds( 0, 30, 230, 230 );
        buttonTnCs.setCompoundDrawables(null, img, null, null);

        buttonContactUs = (Button) findViewById(R.id.button_contact_us);
        img = getApplicationContext().getResources().getDrawable( R.drawable.contact_us );
        img.setBounds( 0, 30, 230, 230 );
        buttonContactUs.setCompoundDrawables(null, img, null, null);

        buttonAboutUs = (Button)findViewById(R.id.button_about_us);
        img = getApplicationContext().getResources().getDrawable( R.drawable.about_us );
        img.setBounds( 0, 30, 230, 230 );
        buttonAboutUs.setCompoundDrawables(null, img, null, null);

        buttonLogOut = (Button)findViewById(R.id.button_log_out);
        img = getApplicationContext().getResources().getDrawable( R.drawable.log_out );
        img.setBounds( 0, 30, 230, 230 );
        buttonLogOut.setCompoundDrawables(null, img, null, null);

        buttonPriceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, PriceList.class);
                startActivity(intent);
            }
        });

        buttonRequestPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, RequestPickup.class);
                startActivity(intent);
            }
        });

        buttonAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, AccountSettings.class);
                startActivity(intent);
            }
        });

        buttonCheckPastRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, CheckPastRequests.class);
                startActivity(intent);
            }
        });

        buttonTnCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, TermsAndConditions.class);
                startActivity(intent);
            }
        });

        buttonContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ContactUs.class);
                startActivity(intent);
            }
        });

        buttonAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, AboutUs.class);
                startActivity(intent);
            }
        });

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make the shared preferences null
                HandleSharedPrefs.saveUsernameSharedPref(HomePage.this, "username", null, "passwd", null);
                //go to login page
                Intent intent = new Intent(HomePage.this, MyActivity.class);
                startActivity(intent);
            }
        });
    }

     // TODO : this to be replaced by activity stack clearance.
    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
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
