package com.google.firebase.quickstart.database.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.quickstart.database.SignInActivity;

/**
 * Created by Jan on 2017-10-20.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,SignInActivity.class);
        startActivity(intent);

        finish();
    }
}
