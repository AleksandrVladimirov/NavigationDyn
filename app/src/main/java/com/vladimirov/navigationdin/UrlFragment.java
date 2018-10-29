package com.vladimirov.navigationdin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class UrlFragment extends Fragment {

    private WebView webView;
    private String param;

    public UrlFragment() {
    }

    public UrlFragment(String param) {
        this.param = param;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.url_item, container, false);

        webView = view.findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        SimpleWebViewClient webViewClient = new SimpleWebViewClient();
        webView.setWebViewClient(webViewClient);

        if(param != null && URLUtil.isValidUrl(param)) {
            webView.loadUrl(param);
        } else if(param != null && !URLUtil.isValidUrl(param)){
            Toast.makeText(getContext(), "Oops... Invalid url format", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public void myOnKeyDown() {
        if(webView.canGoBack()){
            webView.goBack();
        }
    }

    public boolean webCanGoBack() {
        return webView.canGoBack();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
