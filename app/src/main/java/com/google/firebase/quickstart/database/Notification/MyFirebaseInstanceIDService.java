package com.google.firebase.quickstart.database.Notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Jan on 2017-10-11.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInsIDService";

    @Override
    public void onTokenRefresh() {
        //Get Update token

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"new Token"+refreshedToken);


    }
}
