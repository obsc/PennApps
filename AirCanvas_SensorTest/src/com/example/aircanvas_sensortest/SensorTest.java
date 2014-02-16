package com.example.aircanvas_sensortest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SensorTest extends Activity {
	
	private static final String TAG = "SensorTest";
	
	private static final float THRESHA = 0.1f;		// Hard threshold
	private static final float THRESHB = 0.2f;		// Soft threshold
	
	private static final float[] SMOOTH = {1, 2, 4, 8, 16, 32};
	private static final float SMOOTHSUM = 31;
	
	private SensorManager sm;
	private Sensor accel;
	private Sensor orientation;
	private float[] accel_data = new float[3];
	private float[] vel_data = new float[3];
	private float[] pos_data = new float[3];
	private float[] orientation_data = new float[3];
	
	private float[][] past_accel = new float[SMOOTH.length][3];
	private int accelIndex = 0;
	
	private long timestamp = System.currentTimeMillis();
	
	private SensorEventListener accelListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
//			if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
				if (accelIndex == 0) {
					timestamp = System.currentTimeMillis();
				}
				
				past_accel[accelIndex] = event.values;
				
//				for (int i = 0; i < event.values.length; i++) {
//					Log.d(TAG, "" + past_accel[accelIndex][i]);
//				}
				
				if (accelIndex == SMOOTH.length - 1) {
					float[] accel = new float[3];
					for (int i = 0; i < SMOOTH.length; i++) {
						accel[0] += SMOOTH[i] * past_accel[i][0];
						Log.i(TAG, ""+ i + " "+ past_accel[i][0]);
						accel[1] += SMOOTH[i] * past_accel[i][1];
						accel[2] += SMOOTH[i] * past_accel[i][2];
					}
					accel[0] = accel[0] / SMOOTHSUM;
					accel[1] = accel[1] / SMOOTHSUM;
					accel[2] = accel[2] / SMOOTHSUM;
					
					// Thresholding based on magnitude
//					float mag = 0.0f;
//					for (int i = 0; i < accel.length; i++) {
//						mag += (accel[i] * accel[i]);
//					}
//					mag = (float) Math.sqrt(mag);
//					float ratio = (mag - THRESHA) / (THRESHB - THRESHA);
//					ratio = Math.max(0.0f, ratio);
//					ratio = Math.min(1.0f, ratio);
		//
//					for (int i = 0; i < accel.length; i++) {
//						accel[i] = accel[i] * ratio;	
//					}
					
					for (int i = 0; i < accel.length; i++) {
						float ratio = (accel[i] - THRESHA) / (THRESHB - THRESHA);
						ratio = Math.max(0.0f, ratio);
						ratio = Math.min(1.0f, ratio);
					}
					
					long delta = System.currentTimeMillis() - timestamp;
					
					for (int i = 0; i < vel_data.length; i++) {
						vel_data[i] += accel[i] * delta / 1000;
					}
					for (int i = 0; i < pos_data.length; i++) {
						pos_data[i] += vel_data[i] * delta / 1000;
					}
				}
				
				accelIndex++;
				if (accelIndex >= SMOOTH.length) {
					accelIndex = 0;
				}

				String a = "Accel Data:\nX: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2] + "\n\n";
				String v = "Velocity Data:\nX: " + vel_data[0] + "\nY: " + vel_data[1] + "\nZ: " + vel_data[2] + "\n\n";
				String p = "Position Data:\nX: " + pos_data[0] + "\nY: " + pos_data[1] + "\nZ: " + pos_data[2] + "\n\n";
				((TextView) findViewById(R.id.accel_data)).setText(a+v+p);
//			}
//			else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
//			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}

	};
	
	private SensorEventListener orientListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			orientation_data = event.values;
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_test);
		
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accel = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		orientation = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_test, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sm.registerListener(accelListener, accel, 1000);
		sm.registerListener(orientListener, orientation, 1000);
	}
	

}
