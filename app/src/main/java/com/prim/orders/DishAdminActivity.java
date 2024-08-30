package com.prim.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dish_Available;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;
import com.prim.orders.utilities.IconSetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DishAdminActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private IconSetup iconSetup;
    RecyclerView recyclerView;
    adapter_admin_dishes_listOrderAvailable adapter;
    adapter_empty adapterEmpty;
    int option = 0;

    private Users users = new Users();
    private Organizations organizations = new Organizations();
    private Dishes dishes = new Dishes();
    private ArrayList<Order_Available> orderAvailableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAdminDish);
        setSupportActionBar(toolbar);
//        MenuItem cart = (MenuItem) toolbar.findViewById(R.id.cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                cart.setIcon(ContextCompat.getDrawable(DishActivity.this, R.drawable.round_shopping_cart_checkout_24));
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
            dishes = (Dishes) extras.getSerializable("dishes");
            getSupportActionBar().setTitle(dishes.getName());
//            ((TextView) findViewById(R.id.textViewPrice)).setText("Available Order (" + String.format("RM %.2f", dishes.getPrice()) + "/each)");
        }
        call_listOrderAvailableAdmin_API();

        Button btnAddNewOrderSlot = findViewById(R.id.btnAddNewOrderSlot);
        btnAddNewOrderSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishAdminActivity.this, CreateOrderAvailableAdminActivity.class);
                intent.putExtra("organizations", (Serializable) organizations);
                intent.putExtra("dishes", (Serializable) dishes);
                startActivity(intent);
            }
        });

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupOption);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonUpcoming) {
                    option = 0;
                    //call_getOrderAvailableDish_API(v);
                } else if (checkedId == R.id.radioButtonCompleted) {
                    option = 1;
                    //call_getOrderAvailableDish_API(v);
                }
                getListOrderAvailable();
            }
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_DishAdminActivity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_DishAdminActivity"), RECEIVER_EXPORTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.Edit) {
//            Intent intentf = new Intent("finish_DishActivity");
//            sendBroadcast(intentf);
//            Intent intentf2 = new Intent("finish_ShopActivity");
//            sendBroadcast(intentf2);
//            finish();
            Intent intent = new Intent(DishAdminActivity.this, UpdateDishAdminActivity.class);
            intent.putExtra("organizations", (Serializable) organizations);
            intent.putExtra("dishes", (Serializable) dishes);
            startActivity(intent);
        } else if(id == R.id.Delete) {
            if(dishes.getTotalOrderAvailable() > 0) {

                Toast.makeText(DishAdminActivity.this, "Dish must not have any slot", Toast.LENGTH_SHORT).show();
            } else {
                call_deleteDishes_API();
            }
        }
        return true;
    }

    public void call_listOrderAvailableAdmin_API() {
        LoadingDialog loadingDialog = new LoadingDialog(DishAdminActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.listOrderAvailableAdmin(dishes.getId()).enqueue(new Callback<ArrayList<Order_Available>>() {
            @Override
            public void onResponse(Call<ArrayList<Order_Available>> call, Response<ArrayList<Order_Available>> response) {
                if(response.isSuccessful()) {
                    orderAvailableList = response.body();
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOrderAvailableAdmin()", "Response: API Response, Success. " + orderAvailableList.size());
                    getListOrderAvailable();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOrderAvailableAdmin()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(DishAdminActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Order_Available>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listOrderAvailableAdmin()", "Response: API failed, " + t.getMessage());
                Toast.makeText(DishAdminActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getListOrderAvailable() {
        recyclerView = findViewById(R.id.rv_orderavailable);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DishAdminActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        //Collections.sort(orderAvailableList);

        Date currentTime = Calendar.getInstance().getTime();
        ArrayList<Order_Available> orderAvailableListAfterOption = new ArrayList<>();
        for (Order_Available oa : orderAvailableList) {
            if(option == 0) {
                if(oa.getDelivery_date().getTime() >= currentTime.getTime()) {
                    orderAvailableListAfterOption.add(oa);
                }
            } else {
                if(oa.getDelivery_date().getTime() < currentTime.getTime()) {
                    orderAvailableListAfterOption.add(oa);
                }
            }
        }

        if(option == 0) {
            Collections.sort(orderAvailableListAfterOption);
        } else {
            Collections.sort(orderAvailableListAfterOption);
            Collections.reverse(orderAvailableListAfterOption);

        }

        if(orderAvailableListAfterOption.size() <= 0) {
            adapterEmpty = new adapter_empty(DishAdminActivity.this, "Slot", 1);
            recyclerView.setAdapter(adapterEmpty);
        } else {
            adapter = new adapter_admin_dishes_listOrderAvailable(DishAdminActivity.this, orderAvailableListAfterOption, dishes);
            recyclerView.setAdapter(adapter);
        }


    }

    public void call_deleteDishes_API() {
        LoadingDialog loadingDialog = new LoadingDialog(DishAdminActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.deleteDishes(dishes.getId()).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-deleteDishes()", "Response: API Response, Success. " + response.body().getResponse());

//                    Intent intentf = new Intent("finish_OrderAvailableDishAdminActivity");
//                    sendBroadcast(intentf);
//                    Intent intentf2 = new Intent("finish_DishAdminActivity");
//                    sendBroadcast(intentf2);

//                    Intent intent = new Intent(OrderAvailableDishAdminActivity.this, MainActivityAdmin.class);
//                    //intent.putExtra("users", (Serializable) users);
//                    intent.putExtra("organizations", (Serializable) organizations);
//                    intent.putExtra("order_available", (Serializable) orderAvailable);
//                    intent.putExtra("dishes", (Serializable) dishes);
//                    startActivity(intent);

                    Toast.makeText(DishAdminActivity.this, "Dishes successfully deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-deleteOrderAvailable()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-addOrderAvailable()", "Response: API failed. " + t.getMessage());
                Toast.makeText(DishAdminActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}