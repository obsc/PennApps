package com.example.aircanvas_sensortest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SensorTest extends Activity implements SensorEventListener {
	
	private static final String TAG = "SensorTest";
	
	private static final float ALPHA = 0.05f;		// Low-pass filter smoothing constant
	private static final float THRESHA = 0.15f;		// Hard threshold
	private static final float THRESHB = 0.3f;		// Soft threshold
	private static final float THRESHC = 0.5f;		// Motion threshold
	
	private SensorManager sm;
	private Sensor accel;
	private float[] accel_data = new float[3];
	private float[] vel_data = new float[3];
	private float[] pos_data = new float[3];
	
	private boolean[] past_moves = new boolean[10];
	private int buffer_index = 0;
	
	private boolean inMotion = false;
	
	private boolean zero = true;
	
	private long accelTime = System.currentTimeMillis();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_test);
		
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accel = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

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
		sm.registerListener(this, accel, 10000);
	}
	
	public void onSensorChanged(SensorEvent event) {
		long t = System.currentTimeMillis();
		Log.i(TAG, "accel time: " + (t - accelTime));
		accelTime = t;
		// past value: acce_data; measured value: event.values
		// low pass filter
		if (zero) {
			accel_data = event.values;
			zero = false;
		} else {
			for (int i = 0; i < accel_data.length; i++)
				event.values[i] = ALPHA * event.values[i] - (1.0f - ALPHA) * accel_data[i];
			accel_data = event.values;
		}
		
		// Thresholding based on magnitude
		float mag = 0.0f;
		for (int i = 0; i < event.values.length; i++) {
			mag += (event.values[i] * event.values[i]);
		}
		mag = (float) Math.sqrt(mag);
		float ratio = (mag - THRESHA) / (THRESHB - THRESHA);
		ratio = Math.max(0.0f, ratio);
		ratio = Math.min(1.0f, ratio);
		
		if (ratio == 0) {
			past_moves[buffer_index++] = false;
		}
		else {
			past_moves[buffer_index++] = true;
		}
		if (buffer_index >= 10) {
			buffer_index = 0;
		}
		
		if (!inMotion && mag > THRESHC) {
			inMotion = true;
		}
		else {
			int stops = 0;
			for (int i = 0; i < 10; i++) {
				if (!past_moves[i]) {
					stops++;
				}
			}
			if (stops > 5) {
				inMotion = false;
			}
		}
		
		for (int i = 0; i < event.values.length; i++) {
			event.values[i] = event.values[i] * ratio;	
		}
		
		// velocity
		
		for (int i = 0; i < vel_data.length; i++) {
			if (inMotion) {
				vel_data[i] += event.values[i] / 100;
			}
			else {
				vel_data[i] = 0;
			}
		}
			
		for (int i = 0; i < pos_data.length; i++) {
			pos_data[i] += vel_data[i] / 100;
		}
		String a = "Accel Data:\nX: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2] + "\n\n";
		String v = "Velocity Data:\nX: " + vel_data[0] + "\nY: " + vel_data[1] + "\nZ: " + vel_data[2] + "\n\n";
		String p = "Position Data:\nX: " + pos_data[0] + "\nY: " + pos_data[1] + "\nZ: " + pos_data[2] + "\n\n";
		((TextView) this.findViewById(R.id.accel_data)).setText(a+v+p);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}
