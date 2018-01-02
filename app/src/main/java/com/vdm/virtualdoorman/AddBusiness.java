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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AddBusiness extends Activity {

	EditText company_name;
	EditText primary_phone;
	EditText secret_question;
	EditText passcode;
	RelativeLayout backbutton;
	Switch check;
	TextView quest, ans;
	public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
	SharedPreferences Addpersonpref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_business);
		company_name = (EditText) findViewById(R.id.company_name);
		primary_phone = (EditText) findViewById(R.id.primary_phone);
		secret_question = (EditText) findViewById(R.id.sec_question);
		passcode = (EditText) findViewById(R.id.pass_answer);
		quest=(TextView) findViewById(R.id.question);
		ans=(TextView) findViewById(R.id.answer);
		check=(Switch) findViewById(R.id.Switch);
		backbutton=(RelativeLayout) findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), ArrivalsActivity.class);
				startActivity(intent);
				finish();
			}
		});
		check.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( !isChecked )
		        {   
		        	quest.setVisibility(View.GONE);
		        	ans.setVisibility(View.GONE);
		        	secret_question.setVisibility(View.GONE);
					passcode.setVisibility(View.GONE);
		        }
		        else {
		        	quest.setVisibility(View.VISIBLE);
		        	ans.setVisibility(View.VISIBLE);
		        	secret_question.setVisibility(View.VISIBLE);
					passcode.setVisibility(View.VISIBLE);
		        }

		    }
		});

		TextView btnaddBusiness = (TextView) findViewById(R.id.addbusiness_next);
		btnaddBusiness.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(!check.isChecked()) {
				if (company_name.getText().toString().length() == 0)
					company_name.setError("Company name is required!");
				else {
					String cname = company_name.getText().toString();
					String Phone = primary_phone.getText().toString();
					String SecQuest = secret_question.getText().toString();
					String SecAns = passcode.getText().toString();

					Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
							Context.MODE_PRIVATE);
					Editor editor = Addpersonpref.edit();
					editor.putString("first_name", "");
					editor.putString("last_name", cname);
					editor.putString("primary_phone", Phone);
					editor.putString("secret_question", SecQuest);
					editor.putString("passcode", SecAns);
					editor.putString("is_company", "1");
					editor.commit();

					Intent permission = new Intent(getApplicationContext(),
							PermissionType.class);
					startActivity(permission);
					
				}
			}
				else {
					if (company_name.getText().toString().length() == 0)
						company_name.setError("Company name is required!");
					else if (secret_question.getText().toString().length() == 0)
						secret_question.setError("Secret Question is required!");
					else if (passcode.getText().toString().length() == 0)
						passcode.setError("Passcode is required!");
					else {
						String cname = company_name.getText().toString();
						String Phone = primary_phone.getText().toString();
						String SecQuest = secret_question.getText().toString();
						String SecAns = passcode.getText().toString();

						Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
								Context.MODE_PRIVATE);
						Editor editor = Addpersonpref.edit();
						editor.putString("first_name", "");
						editor.putString("last_name", cname);
						editor.putString("primary_phone", Phone);
						editor.putString("secret_question", SecQuest);
						editor.putString("passcode", SecAns);
						editor.putString("is_company", "1");
						editor.commit();

						Intent permission = new Intent(getApplicationContext(),
								PermissionType.class);
						startActivity(permission);
						
					}
				}
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



