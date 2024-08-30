package com.prim.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.prim.orders.databinding.ActivityMainBinding;
import com.prim.orders.models.Users;
import com.prim.orders.notificationhandler.PickupNotification;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    ActivityMainBinding binding;

    private Users users = new Users();

    private boolean doubleBackToExitPressedOnce = false;
    private Handler backPressHandler = new Handler(Looper.getMainLooper());
    //Test commit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
        }

        boolean openMyOrderFragment = extras != null && extras.getBoolean("openMyOrderFragment", false);

        if (openMyOrderFragment) {
            replaceFragment(new MyOrderFragment());
            binding.bottomNavbarView.setSelectedItemId(R.id.myorder);
        } else {
            replaceFragment(new HomeFragment());
        }

        binding.bottomNavbarView.setBackground(null);

        binding.bottomNavbarView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.order) {
                replaceFragment(new OrderFragment());
            } else if (item.getItemId() == R.id.myorder) {
                replaceFragment(new MyOrderFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.logout) {
                cancelPendingReminders();
                loginPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                loginPrefsEditor = loginPreferences.edit();

                loginPrefsEditor.putBoolean("saveLogin", false);
                loginPrefsEditor.putString("email", "");
                loginPrefsEditor.putString("password", "");
                loginPrefsEditor.commit();

                finish();
                call_logout_API();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
        });
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
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
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
                    if (!isFinishing() && !isDestroyed()) {
                        loadingDialog.dismissDialog();
                    }
                    Log.d("OrderS_API-logout()", "Response: API Response, Success. " + response.body());
                } else {
                    if (!isFinishing() && !isDestroyed()) {
                        loadingDialog.dismissDialog();
                    }
                    Log.d("OrderS_API-logout()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                if (!isFinishing() && !isDestroyed()) {
                    loadingDialog.dismissDialog();
                }
                Log.d("OrderS_API-logout()", "Response: API failed, " + t.getMessage());
            }
        });
    }

    private void cancelPendingReminders() {
        // Implement logic to cancel pending reminders based on the current user
        // You may need to use the same logic you used to set reminders
        // ...

        // For example, you can use MyJobIntentService.cancelReminderForUser(users.getId());
        PickupNotification.cancelPendingReminders(MainActivity.this, users);
    }
}
