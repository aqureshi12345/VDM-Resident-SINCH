package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DaysofWeek extends Activity {
	public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
	SharedPreferences Addpersonpref;
	 private CheckBox monday,tuesday,wednesday,thursday,friday,saturday,sunday;
	 String DaysofWeek ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    // code to hide notification bar
    	/* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    	            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.days_of_week);
        monday=(CheckBox) findViewById(R.id.monday);
        tuesday=(CheckBox) findViewById(R.id.tuesday);
        wednesday=(CheckBox) findViewById(R.id.wednesday);
        thursday=(CheckBox) findViewById(R.id.thursday);
        friday=(CheckBox) findViewById(R.id.friday);
        saturday=(CheckBox) findViewById(R.id.saturday);
        sunday=(CheckBox) findViewById(R.id.sunday);
		RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), PermissionType.class);
				startActivity(intent);
				finish();
			}
		});

        
       TextView btnDaysofWeek = (TextView) findViewById(R.id.daysofweek_next);
       
        btnDaysofWeek.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
						Context.MODE_PRIVATE);
				Editor editor = Addpersonpref.edit();
				editor.putString("permission_type","days_of_week");
				if(monday.isChecked())
				{
				DaysofWeek="mon,";
				}
				if(tuesday.isChecked())
				{
					DaysofWeek= DaysofWeek +"tue,";
				}
				if(wednesday.isChecked())
				{
					DaysofWeek= DaysofWeek +"wed,";
				}
				if(thursday.isChecked())
				{
					DaysofWeek= DaysofWeek +"thu,";
				}
				if(friday.isChecked())
				{
					DaysofWeek= DaysofWeek +"fri,";
				}
				if(saturday.isChecked())
				{
					DaysofWeek= DaysofWeek +"sat,";
				}
				if(sunday.isChecked())
				{
					DaysofWeek= DaysofWeek +"sun,";
				}
				DaysofWeek = DaysofWeek.replaceAll(" ,$", "");
				editor.putString("days_of_week",DaysofWeek);	
				editor.commit();
				Intent time = new Intent(getApplicationContext(),
						TimeofDay.class);
				startActivity(time);
			}
		});

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
