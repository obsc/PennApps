package com.example.aircanvas_sensortest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class SensorTest extends Activity implements SensorEventListener {
	
	private static final float ALPHA = 0.01f;		// Low-pass filter smoothing constant
	private static final float THRESHA = 0.15f;
	private static final float THRESHB = 0.4f;
	
	private SensorManager sm;
	private Sensor accel;
	private float[] accel_data = new float[3];
	private float[] vel_data = new float[3];
	
	private boolean zero = true;
	

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
		sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void onSensorChanged(SensorEvent event) {
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
		
		float mag = 0.0f;
		
		for (int i = 0; i < event.values.length; i++) {
			mag += (event.values[i] * event.values[i]);
		}
		
		mag = (float) Math.sqrt(mag);
		float ratio = (mag - THRESHA) / (THRESHB - THRESHA);
		ratio = Math.max(0.0f, ratio);
		ratio = Math.min(1.0f, ratio);
		
		for (int i = 0; i < event.values.length; i++) {
			event.values[i] = event.values[i] * ratio;
		}
		
		// velocity
		for (int i = 0; i < vel_data.length; i++) {
			vel_data[i] += event.values[i];
		}
		
		String a = "Accel Data:\nX: " + event.values[0] + "\nY: " + event.values[1] + "\nZ: " + event.values[2] + "\n\n";
		String v = "Velocity Data:\nX: " + vel_data[0] + "\nY: " + vel_data[1] + "\nZ: " + vel_data[2] + "\n\n";
		((TextView) this.findViewById(R.id.accel_data)).setText(a+v);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}
