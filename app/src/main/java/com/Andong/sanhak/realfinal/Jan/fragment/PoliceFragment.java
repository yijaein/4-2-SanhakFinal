package com.Andong.sanhak.realfinal.Jan.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        View v = inflater.inflate(R.layout.police_fragment, container, false);
        mWebView =(WebView)v.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebView.loadUrl("https://m.lost112.go.kr/");





        return v;
    }
}
