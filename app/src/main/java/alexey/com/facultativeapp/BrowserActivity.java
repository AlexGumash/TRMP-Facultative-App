package alexey.com.facultativeapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import alexey.com.facultativeapp.R;

public class BrowserActivity extends AppCompatActivity {

    /*
    Класс для WebView
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        WebView webView = findViewById(R.id.webView);
        Uri data = getIntent().getData();
        assert data != null;
        webView.loadUrl(data.toString());

    }
}
