/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.account;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import xyz.rhysevans.taxe.R;

/**
 * AboutActivity.java
 *
 * Controller for the app info screen to display verison number, author etc.
 *
 * @author Rhys Evans
 * @verison 0.1
 */
public class AboutActivity extends AppCompatActivity {

    private final String PROJECT_BLOG_URL = "http://blog.rhysevans.xyz";

    ImageButton closeBtn;
    ImageButton submitBtn;
    TextView versionText;
    Button learnMoreBtn;

    /**
     * Called when activity is created to initialize everything
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Initialize all the views
     */
    private void initViews(){
        versionText = findViewById(R.id.app_version);
        learnMoreBtn = findViewById(R.id.learn_more_btn);

        // Initialize Toolbar
        closeBtn = findViewById(R.id.close_btn);
        submitBtn = findViewById(R.id.submit_btn);
        TextView toolbarTitle = findViewById(R.id.title);
        toolbarTitle.setText(getString(R.string.account_action_about));
        submitBtn.setVisibility(View.GONE);
        submitBtn.setEnabled(false);

        // Initialize Version Label
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = packageInfo.versionName;
            versionText.setText(getString(R.string.version_name, version));

        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        closeBtn.setOnClickListener(v -> finish());
        learnMoreBtn.setOnClickListener(v -> loadWebsite());
    }

    /**
     * Load an external website (project blog)
     * with a browser intent
     */
    private void loadWebsite(){
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PROJECT_BLOG_URL));
        startActivity(webIntent);
    }
}
