package com.prim.orders;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;

public class OrderCartReceiptActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Users users = new Users();
    private int transaction_id = 0;
    private WebView webViewReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cart_receipt);

        Toolbar toolbar = findViewById(R.id.toolbarReceipt);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().setTitle("Receipt");

        webViewReceipt = findViewById(R.id.wv_receiptpage);
        setupWebView();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //users = (Users) extras.getSerializable("users");
            transaction_id = extras.getInt("transaction_id");
            loadReceipt();

        }
    }

    private void setupWebView() {
        WebSettings webSettings = webViewReceipt.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webViewReceipt.setWebViewClient(new WebViewClient());
    }

    private void loadReceipt() {
        String url = baseURL + "/cart/receipt/" + transaction_id;
        webViewReceipt.loadUrl(url);
    }
}
