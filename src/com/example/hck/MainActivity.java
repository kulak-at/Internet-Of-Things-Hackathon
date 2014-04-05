package com.example.hck;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	  private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	  private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, 6664, null);

	  BeaconManager beaconManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		beaconManager  = new BeaconManager(this);
		
		setupBeacons();
		
	}
	
	private void setupBeacons() {
		
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
				// TODO Auto-generated method stub
				Log.i("kulak", "Beacons discovered: " + beacons);
				final TextView infoView = ((TextView)findViewById(R.id.info));
				final ImageView imageView = ((ImageView)findViewById(R.id.img));
				
				runOnUiThread(new Runnable() {
					public void run() {

						if(beacons.size() == 1) {
							// we're in range of 1 estimote
							int min =beacons.get(0).getMinor();
							if(min == 1 || min == 2) {
								// displaying offer
								infoView.setText("Offer " + min);
								// setting img
								if(min == 1)
									imageView.setBackgroundColor(Color.GREEN);
								else
									imageView.setBackgroundColor(Color.CYAN);
								
							} else {
								// displaying no-offer
								infoView.setText("Wrong offers.");
								imageView.setBackgroundColor(Color.WHITE);
							}
						} else if(beacons.size() > 1) {
							infoView.setText(beacons.size() + " offers available.");
							imageView.setBackgroundColor(Color.WHITE);
						} else {
							infoView.setText("No offers");
							imageView.setBackgroundColor(Color.WHITE);
						}
					}
				});
				
				
				String txt = "";
				String sep = "";
				for(Beacon b : beacons) {
					txt += sep + "<" + b.getMajor() + "," + b.getMinor() + ">";
					sep = ", ";
				}
				
//				((TextView)findViewById(R.id.info)).setText(txt);
				
			}
		  });
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			
			@Override
			public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
				} catch(RemoteException e) {
					Log.i("kulak", "Cannot start ranging", e);
				}
				
			}
		});
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch(RemoteException e) {
			Log.i("kulak", "Cannot stop ranging");
		}
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		beaconManager.disconnect();
		
	}

}
