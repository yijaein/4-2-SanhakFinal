package com.Andong.sanhak.realfinal.Jan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.quickstart.Jan.R;

/**
 * Created by Jan on 2017-11-13.
 */

public class PoliceFragment extends android.support.v4.app.Fragment {


    public PoliceFragment(){

    }
    private WebView mWebView;
    private WebSettings mWebSetting;



    @Nullable
    @Override


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_police, container, false);
        mWebView =(WebView)v.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebView.loadUrl("https://m.lost112.go.kr/");
        /*
        2017_11_13 이재인 웹뷰 뒤로가기 버튼
         */
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;


                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();

                    } else {

                        (getActivity()).onBackPressed();
                    }

                    return true;


                }
                return false;
            }
        });





        return v;
    }








}
