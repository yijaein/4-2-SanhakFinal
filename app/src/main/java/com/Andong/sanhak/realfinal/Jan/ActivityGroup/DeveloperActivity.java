package com.Andong.sanhak.realfinal.Jan.ActivityGroup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.quickstart.Jan.R;

/**
 * Created by Jan on 2017-11-13.
 */

public class DeveloperActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_setting);
        TextView jungmingText  = (TextView)findViewById(R.id.jungmingTextview);
        jungmingText.setText(R.string.jungming);
    }
}
