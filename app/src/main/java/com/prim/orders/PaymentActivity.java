package com.prim.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Users users = new Users();
    private Organizations organizations = new Organizations();
    private Order_Cart orderCart = new Order_Cart();

    WebView wv_paymentpage;
    private Handler handler;
    private Runnable countdownRunnable;
    private int countdownTime = 5; // 5 seconds
    private boolean isCountdownComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbarPayment);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                intent.putExtra("users", (Serializable) users);
                startActivity(intent);
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            orderCart = (Order_Cart) extras.getSerializable("order_cart");
        }

        getSupportActionBar().setTitle(String.format("Payment | RM %.2f", orderCart.getTotalamount()));

        wv_paymentpage = findViewById(R.id.wv_paymentpage);
        wv_paymentpage.getSettings().setJavaScriptEnabled(true);
        wv_paymentpage.getSettings().setDomStorageEnabled(true);
        wv_paymentpage.getSettings().setDatabaseEnabled(true);
        wv_paymentpage.getSettings().setAllowFileAccess(true);
        wv_paymentpage.getSettings().setAllowContentAccess(true);
        wv_paymentpage.setWebViewClient(new MyWebViewClient());

        // Prepare headers
        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("order_cart_id", String.valueOf(orderCart.getId()));
        extraHeaders.put("user_id", String.valueOf(orderCart.getUser_id()));
        extraHeaders.put("organ_id", String.valueOf(orderCart.getOrgan_id()));
        extraHeaders.put("totalamount", String.valueOf(orderCart.getTotalamount()));

        // Load URL with headers
        wv_paymentpage.loadUrl(baseURL + "/cart/mobilepayment", extraHeaders);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Load the URL with headers
            String url = request.getUrl().toString();
            Map<String, String> headers = new HashMap<>();
            headers.put("order_cart_id", String.valueOf(orderCart.getId()));
            headers.put("user_id", String.valueOf(orderCart.getUser_id()));
            headers.put("organ_id", String.valueOf(orderCart.getOrgan_id()));
            headers.put("totalamount", String.valueOf(orderCart.getTotalamount()));
            view.loadUrl(url, headers);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Log.d("OrderS_API-onPageFinished()", "Response: API Response = " + url);
            // Check if the URL indicates a successful transaction
            if (url.contains("/directpayReceipt")) {
                startCountdown();
            }
        }
    }

    private void startCountdown() {
        handler = new Handler();
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (countdownTime > 0) {
                    getSupportActionBar().setTitle(String.format("Redirecting in %d seconds", countdownTime));
                    countdownTime--;
                    handler.postDelayed(this, 1000); // 1 second delay
                } else {
                    // Redirect to MainActivity
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.putExtra("users", (Serializable) users);
                    intent.putExtra("openMyOrderFragment", true);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.post(countdownRunnable);
    }

    @Override
    public void onBackPressed() {
        showTransactionWarning(); // Show a warning when the back button is pressed
    }

    private void showTransactionWarning() {
        if (!isCountdownComplete) {
            Toast.makeText(this, "Please wait for the transaction to complete. Pressing back will fail the transaction.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed(); // Allow the default behavior if the transaction is complete
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
    }
}
