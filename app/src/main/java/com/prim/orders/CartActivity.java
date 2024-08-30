package com.prim.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.compose.ui.window.Notification;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.models.api_respond.API_Resp_getOrderCart;
import com.prim.orders.notificationhandler.PickupNotification;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;
import com.prim.orders.utilities.IconSetup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity {
    private Users users = new Users();
    private Organizations organizations = new Organizations();
    private Dishes dishes = new Dishes();
    private ArrayList<Dishes> dishesListAll = new ArrayList<>();
    private Order_Cart orderCart = new Order_Cart();
    private ArrayList<Order_Available> orderAvailableList = new ArrayList<>();
    private ArrayList<Order_Available_Dish> orderAvailableDishList = new ArrayList<>();
    private Users otherUsers = new Users();

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    adapter_cart_listDish adapter;
    RecyclerView recyclerView;
    IconSetup iconSetup;
    Button buttonCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
            dishesListAll = (ArrayList<Dishes>) extras.getSerializable("all-dish");
            //dishes = (Dishes) extras.getSerializable("dishes");
            orderCart = (Order_Cart) extras.getSerializable("order_cart");
            orderAvailableList = (ArrayList<Order_Available>) extras.getSerializable("order_available");
            orderAvailableDishList = (ArrayList<Order_Available_Dish>) extras.getSerializable("order_available_dish");
//            ((TextView) findViewById(R.id.textViewShopName6)).setText(organizations.getNama());
//            ((TextView) findViewById(R.id.textViewShopAddress2)).setText(organizations.getAddress() + " " + organizations.getId());
//            if (this != null) {
//                Glide.with(this)
//                        .load(organizations.getOrganization_picture())
//                        .into((ImageView) findViewById(R.id.imageViewDish));
//            }
        }
        getSupportActionBar().setTitle(String.valueOf("Cart | " + organizations.getNama()));

        if(orderAvailableDishList.size() <= 0) {

        }

//        int margin = 30;
//        Drawable icon_cart = getResources().getDrawable(R.drawable.round_shopping_cart_24);
//        icon_cart = iconSetup.addMarginToDrawable(icon_cart, margin);
//        icon_cart.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom
//
//        getSupportActionBar().setIcon(R.drawable.round_shopping_cart_24);

        ((TextView) findViewById(R.id.textViewDishTotalPrice)).setText(String.format("RM %.2f", orderCart.getTotalamount()));
        getListDish_Cart();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(CartActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CartActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_CartActivity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_CartActivity"), Context.RECEIVER_EXPORTED);
    }

    public void getListDish_Cart() {
        recyclerView = findViewById(R.id.RecycleViewCartDish);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CartActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        int count;
        ArrayList<Dishes> dishOrderList = new ArrayList<>();
        for(Dishes d : dishesListAll) {
            count = 0;
            for(Order_Available oa : orderAvailableList) {
                for(Order_Available_Dish oad : orderAvailableDishList) {
                    if(oad.getOrder_available_id() == oa.getId() && oad.getQuantity() != 0) {
                        if(oa.getDish_id() == d.getId() && count == 0) {
                            Dishes dishOrder = new Dishes();
                            dishOrder.setId(d.getId());
                            dishOrder.setName(d.getName());
                            dishOrder.setPrice(d.getPrice());
                            dishOrder.setDish_image(d.getDish_image());
                            dishOrder.setCreated_at(d.getCreated_at());
                            dishOrder.setUpdated_at(d.getUpdated_at());
                            dishOrder.setDish_type(d.getDish_type());
                            dishOrder.setOrgan_id(d.getOrgan_id());

                            dishOrderList.add(dishOrder);
                            count++;
                        }
                    }
                }
            }
        }

        for(Dishes d : dishOrderList) {
            Log.d("dishOrderList", "Response: API Response, Dish ID = " + d.getId());
        }

        adapter = new adapter_cart_listDish(CartActivity.this, users, organizations, dishOrderList, orderCart, orderAvailableList, orderAvailableDishList);
        recyclerView.setAdapter(adapter);

        buttonCheckOut = findViewById(R.id.buttonCheckOut);

        buttonCheckOut.setEnabled(false);
        for(Order_Available_Dish oad : orderAvailableDishList) {
            if(oad.getQuantity() != 0) {
                buttonCheckOut.setEnabled(true);
            }
        }

        buttonCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double totalamount = 0;
                ArrayList<Order_Available> orderAvailable_Noti = new ArrayList<>();
                ArrayList<Dishes> dishes_Noti = new ArrayList<>();

                for(Order_Available_Dish oad : orderAvailableDishList) {
                    if(oad.getQuantity() != 0) {
                        for(Order_Available oa : orderAvailableList) {
                            if(oad.getOrder_available_id() == oa.getId()) {
                                for(Dishes d : dishesListAll) {
                                    if(d.getId() == oa.getDish_id()) {
                                        orderAvailable_Noti.add(oa);
                                        dishes_Noti.add(d);
                                    }
                                }
                            }
                        }

                        totalamount = totalamount + oad.getTotalprice();
                        //orderCreatedNotification();
                        //long pickupTimeMillis = System.currentTimeMillis();
                        long createdTimeMillis = System.currentTimeMillis();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // The device is running Android 12 or later
                            // Add your Android 12 specific code here
                            //PickupNotification.scheduleReminder(CartActivity.this, createdTimeMillis, users.getDevice_token(), orderAvailable_Noti, dishes_Noti);
                            Toast.makeText(CartActivity.this, "Currently, notification reminder will only work for Android 11 and below.", Toast.LENGTH_SHORT).show();
                        } else {
                            // The device is running an Android version earlier than 12
                            // Add code for earlier Android versions here if needed
                            PickupNotification.scheduleReminder(CartActivity.this, createdTimeMillis, users.getDevice_token(), orderAvailable_Noti, dishes_Noti);
                        }

//                        PickupNotification.scheduleReminder(CartActivity.this, createdTimeMillis, users.getDevice_token(), orderAvailable_Noti, dishes_Noti);
                        //Log.d("orderAvailableDishList", "Response: ID = " + oad.getQuantity());

                        //test payment
                        callCreateOrderCartAPI(oad, totalamount);
                        call_getUsers_API();
                    }
                }


                //Toast.makeText(CartActivity.this, "Order Successfully made", Toast.LENGTH_SHORT).show();

                Intent intentf = new Intent("finish_DishActivity");
                sendBroadcast(intentf);
                Intent intentf2 = new Intent("finish_ShopActivity");
                sendBroadcast(intentf2);

//                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("users", (Serializable) users);
                intent.putExtra("order_cart", (Serializable) orderCart);

                startActivity(intent);

                finish();
            }
        });
    }

    public void callCreateOrderCartAPI(Order_Available_Dish orderAvailableDish, Double totalamount) {
        LoadingDialog loadingDialog = new LoadingDialog(CartActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.createOrderCart(orderAvailableDish.getQuantity(), orderAvailableDish.getTotalprice(), orderAvailableDish.getOrder_available_id(), orderAvailableDish.getOrder_cart_id(), totalamount).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-createOrderCart()", "Response: API Response, Success. ");
                    //Toast.makeText(CartActivity.this, "Response: " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-createOrderCart()", "Response: API Response, Unsuccessful.");
                    //Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-createOrderCart()", "Response: API failed. ");
                Toast.makeText(CartActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void call_getUsers_API() {
        LoadingDialog loadingDialog = new LoadingDialog(CartActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getUsers(organizations.getId()).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    otherUsers = response.body();
                    Log.d("OrderS_API-getUsers()", "Response: API Response, Success. " + otherUsers.getDevice_token());
                    //sendNotificationToSeller(otherUsers.getDevice_token(), "Order Receive", users.getName() + " has made order on your dish");
                    //Toast.makeText(CartActivity.this, "Response: " + response.body().getResponse(), Toast.LENGTH_SHORT).show();

                    JSONObject jsonObject = new JSONObject();
                    JSONObject notificationObj = new JSONObject();

                    try {
                        notificationObj.put("title", "Order received");
                        notificationObj.put("body", users.getName() + " has made order on your dish");
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("user_name", users.getName());
                        jsonObject.put("notification", notificationObj);
                        jsonObject.put("data", dataObj);
                        jsonObject.put("to", otherUsers.getDevice_token());
                        call_notification_API(jsonObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-getUsers()", "Response: API Response, Unsuccessful.");
                    //Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-getUsers()", "Response: API failed. ");
                Toast.makeText(CartActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void orderCreatedNotification() {
        String chanelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), chanelID);
        builder.setSmallIcon(R.drawable.round_add_circle_24_primary)
                .setContentTitle("Orders Created")
                .setContentText("Your order created.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Data value here");

//        PendingIntent pendingIntent;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        } else {
//            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }

//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
            if(notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID, "Some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());
    }

    private void sendNotificationToSeller(String deviceToken, String title, String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://fcm.googleapis.com/v1/orders-9b781/messages:send");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Authorization", "Bearer AAAA0iek96I:APA91bFcrD0aiRmZSMTrR-CMi6jbyFimEoiTMDPCl9HwCInnTcVl2NEq9zO3WtyODaacGhKHoKJRLAu6t2Tyougf9Z6Y7PJZp80jY4NKxl2ayHcp83fXbDhkVhgiBN4zI4_5PttfvGUG");
                    httpURLConnection.setDoOutput(true);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("token", deviceToken);
                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", title);
                    notificationObject.put("body", body);
                    jsonObject.put("notification", notificationObject);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write(jsonObject.toString());
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    int responseCode = httpURLConnection.getResponseCode();
                    Log.d(Utils.TAG, "sendNotificationToSeller: Response Code - " + responseCode);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void call_notification_API(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAA0iek96I:APA91bFcrD0aiRmZSMTrR-CMi6jbyFimEoiTMDPCl9HwCInnTcVl2NEq9zO3WtyODaacGhKHoKJRLAu6t2Tyougf9Z6Y7PJZp80jY4NKxl2ayHcp83fXbDhkVhgiBN4zI4_5PttfvGUG")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {

            }
        });

    }

}