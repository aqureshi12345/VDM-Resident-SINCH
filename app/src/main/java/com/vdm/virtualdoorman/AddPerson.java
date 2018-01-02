package com.vdm.virtualdoorman;



import com.vdm.virtualdoorman.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class AddPerson extends Activity {

	// Progress Dialog

	EditText first_name;
	EditText last_name;
	EditText primary_phone;
	EditText secret_question;
	EditText passcode;
	TextView quest, ans;
	RelativeLayout backbutton;
	Switch check;
	public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
	SharedPreferences Addpersonpref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_person);

		// Edit Text
		first_name = (EditText) findViewById(R.id.first_name);
		last_name = (EditText) findViewById(R.id.last_name);
		primary_phone = (EditText) findViewById(R.id.phone);
		secret_question = (EditText) findViewById(R.id.secret_question);
		passcode = (EditText) findViewById(R.id.passcode_answer);
		quest=(TextView) findViewById(R.id.question);
		ans=(TextView) findViewById(R.id.answer);
		backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), ArrivalsActivity.class);
				startActivity(intent);
				finish();
			}
		});
		check=(Switch) findViewById(R.id.Switch);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {

		        if ( !isChecked )
		        {
					check.setChecked(false);
		        	quest.setVisibility(View.GONE);
		        	ans.setVisibility(View.GONE);
		        	secret_question.setVisibility(View.GONE);
					passcode.setVisibility(View.GONE);
		        }
		        else {
		        	check.setChecked(true);
		        	quest.setVisibility(View.VISIBLE);
		        	ans.setVisibility(View.VISIBLE);
		        	secret_question.setVisibility(View.VISIBLE);
					passcode.setVisibility(View.VISIBLE);
		        }

		    }
		});
		
		// Create button
		TextView btnCreateguest = (TextView) findViewById(R.id.addperson_next);

		// button click event
		btnCreateguest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if(!check.isChecked()) {
					
					if (first_name.getText().toString().length() == 0)
						first_name.setError("First name is required!");
					else if (last_name.getText().toString().length() == 0)
						last_name.setError("Last name is required!");
					else {
						String fname = first_name.getText().toString();
						String lname = last_name.getText().toString();
						String Phone = primary_phone.getText().toString();
						String SecQuest = secret_question.getText().toString();
						String SecAns = passcode.getText().toString();

						Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
								Context.MODE_PRIVATE);
						Editor editor = Addpersonpref.edit();
						editor.putString("guest_id", "");
						editor.putString("first_name", fname);
						editor.putString("last_name", lname);
						editor.putString("primary_phone", Phone);
						editor.putString("secret_question", SecQuest);
						editor.putString("passcode", SecAns);
						editor.putString("is_company", "0");
						editor.commit();
						Intent permission = new Intent(getApplicationContext(),
								PermissionType.class);
						String context=getApplicationContext().toString();


						startActivity(permission);
					}
					
				}
				else{
					if (first_name.getText().toString().length() == 0)
						first_name.setError("First name is required!");
					else if (last_name.getText().toString().length() == 0)
						last_name.setError("Last name is required!");
					else if (secret_question.getText().toString().length() == 0)
						secret_question.setError("Secret Question is required!");
					else if (passcode.getText().toString().length() == 0)
						passcode.setError("Passcode is required!");
					else {
						String fname = first_name.getText().toString();
						String lname = last_name.getText().toString();
						String Phone = primary_phone.getText().toString();
						String SecQuest = secret_question.getText().toString();
						String SecAns = passcode.getText().toString();

						Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
								Context.MODE_PRIVATE);
						Editor editor = Addpersonpref.edit();
						editor.putString("guest_id", "");
						editor.putString("first_name", fname);
						editor.putString("last_name", lname);
						editor.putString("primary_phone", Phone);
						editor.putString("secret_question", SecQuest);
						editor.putString("passcode", SecAns);
						editor.putString("is_company", "0");
						editor.commit();
						Intent permission = new Intent(getApplicationContext(),
								PermissionType.class);
						startActivity(permission);
				
					} 
				}
				}
			
		});
	}

}
