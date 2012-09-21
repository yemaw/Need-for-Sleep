package me.yemaw.needforsleep;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class Activity_Main extends Activity {

	/* UI */
	Button btnSleepOnOff;
	
	/* System */
	boolean isSleeping;
	SharedPreferences sleepPreference;
	SharedPreferences.Editor sleepPreferenceEditor;

	/* Helpers */ 
	Date date;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnSleepOnOff();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void btnSleepOnOff(){
    	btnSleepOnOff = (Button) findViewById(R.id.btnSleepOnOff);
    	
    	//Setting Button Text and UI
		if (isSleeping) {
			btnSleepOnOff.setText(R.string.txt_SleepOff);
		} else {
			btnSleepOnOff.setText(R.string.txt_SleepOn);
		}
    	
		//Setting Button OnClickListener
		btnSleepOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				sleepPreference  = getSharedPreferences("sleepPreference", 0);
				sleepPreferenceEditor = sleepPreference.edit();
				isSleeping = sleepPreference.getBoolean("sleeping", false);
				
				date = new Date();
				/*EditText debug = (EditText) findViewById(R.id.debug);
				debug.setText(Boolean.toString(sleepPreference.getBoolean("sleeping", false)));*/
				
				if (isSleeping) { //'wake up from sleeping' logic
					sleepPreferenceEditor.putBoolean("sleeping", false);
					sleepPreferenceEditor.putLong("end_sleep", date.getTime());
					btnSleepOnOff.setText(R.string.txt_SleepOn);
					sleepPreferenceEditor.commit();
					
					//Calculating Data to show in dialog box
					Long _s = sleepPreference.getLong("start_sleep", 0);
					Long _e = sleepPreference.getLong("end_sleep"  , 0);
					String stS = new SimpleDateFormat("hh:mm:ss").format(_s);
					String stE = new SimpleDateFormat("hh:mm:ss").format(_e);
					
					//Prompt up a dialog box and confirm slept time
					AlertDialog dialog_box = new AlertDialog.Builder(Activity_Main.this)
					.setTitle("Creating sleep entry.").setMessage("Woo! You slept from "+stS+" to "+stE+".\n")
					.setIcon(R.drawable.ic_action_search)
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//TODO::Store to DB
							String msg = "Pressed Yes." + Integer.toString(which);
							EditText debug = (EditText) findViewById(R.id.debug);
							debug.setText(msg);
							//TODO::end
						}
					})
					.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//TODO::Go to Edit View
							String msg = "Pressed No." + Integer.toString(which);
							EditText debug = (EditText) findViewById(R.id.debug);
							debug.setText(msg);
							//TODO::end
						}
					})
					.create(); //create the dialog box
					dialog_box.show(); //show the dialog box
				
				} else { //'go to sleep' logic
					sleepPreferenceEditor.putBoolean("sleeping", true);
					sleepPreferenceEditor.putLong("start_sleep", date.getTime());
					btnSleepOnOff.setText(R.string.txt_SleepOff);
					sleepPreferenceEditor.commit();
					
					//Showing good night status
					String st = new SimpleDateFormat("HH").format(date.getTime()); //generate current hour in 24 hour format
					int i = Integer.parseInt(st);
					if(i>18 || i<3){ //if current hour is between 6PM and 3AM
						st = "Good night. Sweet dream.";
					} else {
						st = "Have a well sleep.";
					}
					Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG).show();
				}
				
			}
		});
    }
}
