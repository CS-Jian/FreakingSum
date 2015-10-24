package com.mrandmrsjian.freakingsum.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.google.example.games.basegameutils.GameHelper;
//import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import com.mrandmrsjian.freakingsum.DirectedGame;
import com.mrandmrsjian.freakingsum.IActivityRequestHandler;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
    Tracker globalTracker;
    private static final String AD_UNIT_ID = "NOT APPLY YET!";
    AdView adView;

    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOW_ADS:
                {
                    adView.setVisibility(View.VISIBLE);
                    break;
                }
                case HIDE_ADS:
                {
                    adView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    };

    //private GameHelper gameHelper;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer=false;
        config.useCompass=false;
        //initialize(new ThrustCopter(this), config);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        RelativeLayout layout = new RelativeLayout(this);

        View gameView = initializeForView(new DirectedGame(this), config);
        layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        startAdvertising();

        layout.addView(adView, adParams);

        setContentView(layout);

        // Set GoogleAnalytics Tracker
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        globalTracker=analytics.newTracker(R.xml.global_tracker);
	}

    private void startAdvertising() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    @Override
    public void setTrackerScreenName(String path) {
        globalTracker.setScreenName(path);
        globalTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        //gameHelper.onStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        //gameHelper.onStop();
    }

    @Override
    public void showAds(boolean show) {
        handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
    }

    @Override
    public void login() {

    }

    @Override
    public void logOut() {

    }

    @Override
    public boolean isSignedIn() {
        return false;
    }

    @Override
    public void submitScore(int score) {

    }

    @Override
    public void unlockAchievement(int stars) {

    }

    @Override
    public void showScores() {

    }

    @Override
    public void showAchievements() {

    }
}
