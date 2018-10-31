package com.waynegames.motiondarts;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AndroidLauncher extends AndroidApplication implements AdInterface {

	// Other
	private InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Advert
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId("ca-app-pub-9534514644294711/4698134701");
		interstitialAd.setAdListener(new AdListener() {

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				loadInterstitialAd();
			}
		});

		loadInterstitialAd();

		// Config
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// Allow the use of the phone's accelerometer and gyroscope
		config.useAccelerometer = true;
		config.useGyroscope = true;

		config.numSamples = 2;

		// Begins the application
		initialize(new MotionDarts(this), config);
	}

	private void loadInterstitialAd(){
		AdRequest interstitialRequest = new AdRequest.Builder().addTestDevice("D1AF290609AD50DE7CB9BA7C73EAB914").build();
		interstitialAd.loadAd(interstitialRequest);
	}

	public void showInterstitial() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				} else {
					loadInterstitialAd();
				}
			}
		});
	}
}
