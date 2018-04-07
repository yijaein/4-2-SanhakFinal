package com.Andong.sanhak.realfinal.Jan.Notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.quickstart.Jan.R;


/**
 * Created by Jan on 2017-10-11.
 *
 */

/*
    2017_10_14 이재인 FCM 푸시 알림 앱 서버 시작
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "InsIDService";

    @Override
    public void onTokenRefresh() {
        //Get Update token

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN),refreshedToken);
        editor.commit();

        Log.d(TAG,"new Token"+refreshedToken);


    }

}
