package com.waynegames.motiondarts;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // Allow the use of the phone's accelerometer and gyroscope
        config.useAccelerometer = true;
        config.useGyroscope = true;

        // Begins the application
		initialize(new MotionDarts(), config);
	}
}
