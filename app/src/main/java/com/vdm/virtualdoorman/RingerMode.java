package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RingerMode extends Activity{
    private RadioGroup ringerRadioGroup;
    private RadioButton radioButton;
    private RadioButton ringAndVibrate;
    private RadioButton vibrator;
    private TextView apply;
    SharedPreferences ringerPref;
    public static final String RingerPREFS = "RingerPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // code to hide notification bar
    	/* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    	            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringer_mode);
        RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ArrivalsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ringerRadioGroup=(RadioGroup)findViewById(R.id.radioRinger);
        ringAndVibrate=(RadioButton)findViewById(R.id.ringer);
        vibrator=(RadioButton)findViewById(R.id.vibrator);
        ringerPref = getSharedPreferences(RingerPREFS,
                Context.MODE_PRIVATE);
        String ringer_mode = (ringerPref.getString("Ringer", ""));
        if(ringer_mode.equalsIgnoreCase("Ring and Vibrate")){
            ringAndVibrate.setChecked(true);
        }
        if(ringer_mode.equalsIgnoreCase("vibrate")){
            vibrator.setChecked(true);

        }
        apply = (TextView) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int selectedId=ringerRadioGroup.getCheckedRadioButtonId();
                radioButton=(RadioButton)findViewById(selectedId);
                if(radioButton.getText().toString().equalsIgnoreCase("Ring and Vibrate")){
                    ringerPref = getSharedPreferences(RingerPREFS,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = ringerPref.edit();
                    editor.putString("Ringer", "ring and vibrate");
                    editor.commit();
                    Toast.makeText(RingerMode.this, radioButton.getText()+" mode has been activated", Toast.LENGTH_SHORT).show();
                }
                if(radioButton.getText().toString().equalsIgnoreCase("Vibrate")){
                    ringerPref = getSharedPreferences(RingerPREFS,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = ringerPref.edit();
                    editor.putString("Ringer", "vibrate");
                    editor.commit();
                    Toast.makeText(RingerMode.this, radioButton.getText()+" mode has been activated", Toast.LENGTH_SHORT).show();
                }
                finish();

            }
        });
//
//        Button btnaddBusiness = (Button) findViewById(R.id.addbusiness);
//        btnaddBusiness.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent time = new Intent(getApplicationContext(), AddBusiness.class);
//                startActivity(time);
//            }
//        });
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
