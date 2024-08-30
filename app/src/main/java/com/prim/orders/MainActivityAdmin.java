package com.prim.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.prim.orders.databinding.ActivityMainAdminBinding;
import com.prim.orders.databinding.ActivityMainBinding;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MainActivityAdmin extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    ActivityMainAdminBinding binding;

    private Users users = new Users();

    private boolean doubleBackToExitPressedOnce = false;
    private Handler backPressHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_admin);
        binding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            //organizations = (Organizations) extras.getSerializable("organizations");
        }

        replaceFragment(new HomeAdminFragment());
        binding.bottomNavbarView.setBackground(null);

        binding.bottomNavbarView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeAdminFragment());
            }
            else if (item.getItemId() == R.id.dishes) {
                replaceFragment(new DishAdminFragment());
            }
            else if (item.getItemId() == R.id.report) {
                replaceFragment(new ReportAdminFragment());
            }
            else if (item.getItemId() == R.id.shop) {
                replaceFragment(new ShopProfileFragment());
            }
            else if (item.getItemId() == R.id.logout) {
                loginPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                loginPrefsEditor = loginPreferences.edit();

                loginPrefsEditor.putBoolean("saveLogin", false);
                loginPrefsEditor.putString("email", "");
                loginPrefsEditor.putString("password", "");
                loginPrefsEditor.commit();

                finish();
                call_logout_API();
                Intent intent = new Intent(MainActivityAdmin.this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_MainActivityAdmin")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_MainActivityAdmin"), RECEIVER_EXPORTED);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        backPressHandler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void call_logout_API() {
        LoadingDialog loadingDialog = new LoadingDialog(MainActivityAdmin.this);
        loadingDialog.startDialog();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.logout(users.getId()).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-logout()", "Response: API Response, Success. " + response.body());
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-logout()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-logout()", "Response: API failed, " + t.getMessage());
            }
        });
    }
}