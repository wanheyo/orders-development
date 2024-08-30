package com.prim.orders;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Organization_User;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    //String baseURL = "https://prim.my";
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private Users users = new Users();
    private Organizations organizations = new Organizations();

    private String TokenDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button letStart = findViewById(R.id.buttonLetstart);
        callSharedPreferences();
//        askNotificationPermission();

        letStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    public void callSharedPreferences() {
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        //if remember me clicked before
        if(saveLogin == true) {
            users.setEmail(loginPreferences.getString("email", ""));
            users.setPassword(loginPreferences.getString("password", ""));
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String token) {
                    Log.d("Get Token", "Response: Get Token Response, Success. " + token);
                    users.setDevice_token(token);
                    TokenDevice = token;
                    callLoginAPI();
                }
            }).addOnFailureListener(e -> {
               Toast.makeText(this, "Failed to get Token", Toast.LENGTH_SHORT).show();
            });
            //callLoginAPI();
            //Log.d("users.getId()", "Response: user_id, " + users.getDevice_token());
            //call_isUserOrderSAdmin_API(users.getId());
        }
    }

    public void callLoginAPI() {
        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startDialog();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        //loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.login(users.getEmail(), users.getPassword(), users.getDevice_token()).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-login()", "Response: API Response, Success. " + users.getDevice_token());

                    if(response.body().getOrganizations() != null) {
                        finish();
                        Intent intent = new Intent(LoginActivity.this, MainActivityAdmin.class);

                        users = response.body();
                        organizations = response.body().getOrganizations();
                        if(TokenDevice != null) {
                            users.setDevice_token(TokenDevice);
                        }

                        intent.putExtra("users", (Serializable) users);
                        intent.putExtra("organizations", (Serializable) organizations);
                        startActivity(intent);
                        //call_isUserOrderSAdmin_API(response.body().getId());
                    } else {
                        finish();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        users = response.body();
                        if(TokenDevice != null) {
                            users.setDevice_token(TokenDevice);
                        }
                        intent.putExtra("users", (Serializable) users);
                        startActivity(intent);
                        //call_isUserOrderSAdmin_API(response.body().getId());
                    }
                    //Toast.makeText(LoginActivity.this, "Login Success " + response.body().getId(), Toast.LENGTH_SHORT).show();


                }
                else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-login()", "Response: API Response, Unsuccessful (Wrong email or password).");
                    Toast.makeText(LoginActivity.this, "Login Failed, Wrong Email or Password", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-login()", "Response: API Failed, " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Server Unresponded, " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void call_isUserOrderSAdmin_API(int userid) {
        Log.d("userid", "Response: userid, " + userid);
        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startDialog();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.isUserOrderSAdmin(userid).enqueue(new Callback<Organization_User>() {
            @Override
            public void onResponse(Call<Organization_User> call, Response<Organization_User> response) {;
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();

                    if(response.body() != null) {
                        Log.d("OrderS_API-isUserOrderSAdmin()", "Response: API Response, Success. " + userid + " Admin");
                        //Toast.makeText(LoginActivity.this, "Login Success " + response.body().getId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivityAdmin.class);
                        intent.putExtra("users", (Serializable) users);
                        startActivity(intent);
                    } else {
                        Log.d("OrderS_API-isUserOrderSAdmin()", "Response: API Response, Success. " + userid + " Not Admin");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("users", (Serializable) users);
                        startActivity(intent);
                    }
                }
                else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-isUserOrderSAdmin()", "Response: API Response.");
                    Toast.makeText(LoginActivity.this, "Check User Type Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Organization_User> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-isUserOrderSAdmin()", "Response: API Failed, " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Server Unresponded, " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.auth_bottom_dialog_layout);

        Button loginButton = dialog.findViewById(R.id.buttonLogin);
        Button registerButton = dialog.findViewById(R.id.buttonRegister);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showBottomDialogCont();
                //Toast.makeText(LoginActivity.this, "Login", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://prim.my/register"));
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Register", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void showBottomDialogCont() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login_bottom_dialog_layout);

        Button loginContLogin = dialog.findViewById(R.id.buttonContLogin);

        loginContLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText EditTextemail = (EditText) dialog.findViewById(R.id.editTextEmail);
                EditText EditTextpass = (EditText) dialog.findViewById(R.id.editTextPass);
                CheckBox RememberMeCheckBox = (CheckBox) dialog.findViewById(R.id.checkBoxRememberMe);
                loginConfirmation(EditTextemail, EditTextpass, RememberMeCheckBox, dialog);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void loginConfirmation(EditText EditTextemail, EditText EditTextpass, CheckBox RememberMeCheckBox, Dialog dialog) {

        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        String email = EditTextemail.getText().toString();
        String pass = EditTextpass.getText().toString();

        if(RememberMeCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("email", email);
            loginPrefsEditor.putString("password", pass);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }

        if(!email.isEmpty() && !pass.isEmpty()) {
            users.setEmail(email);
            users.setPassword(pass);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String token) {
                    Log.d("Get Token", "Response: Get Token Response, Success. " + token);
                    users.setDevice_token(token);
                    TokenDevice = token;
                    callLoginAPI();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to get Token", Toast.LENGTH_SHORT).show();
            });
            //callLoginAPI();
            Log.d("users.getId()", "Response: user_id, " + users.getDevice_token());
            //call_isUserOrderSAdmin_API(users.getId());
        }
        else {
            if(email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email cannot be blank.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            else if(pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Password cannot be blank.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }
}