package ru.androidacademy.msk.lists;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;

import androidx.appcompat.app.AppCompatActivity;

public class WebRequest extends AppCompatActivity {

    static String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        Bundle arguments = getIntent().getExtras();
        url = arguments.get("url").toString();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.web_container, new CustomWebViewFragment())
                    .commit();
        }
    }

    public static class CustomWebViewFragment extends WebViewFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View result= super.onCreateView(inflater, container, savedInstanceState);
            getWebView().getSettings().setJavaScriptEnabled(true);
            // настройка масштабирования
            getWebView().getSettings().setSupportZoom(true);
            getWebView().getSettings().setBuiltInZoomControls(true);
            getWebView().loadUrl(url);
            return(result);
        }
        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }
    }
}
