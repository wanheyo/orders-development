package com.prim.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.prim.orders.models.api_respond.API_Resp_listDishesByShop;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;
import com.prim.orders.utilities.IconSetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DishActivity extends AppCompatActivity {

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private IconSetup iconSetup;

    RecyclerView recyclerView;
    private Users users = new Users();
    private Organizations organizations = new Organizations();
    private ArrayList<Order_Available_Dish> orderAvailableDishList = new ArrayList<>();
    private Order_Cart orderCart = new Order_Cart();
    private Dishes dishes = new Dishes();
    private ArrayList<Dishes> dishesListAll = new ArrayList<>();
    private ArrayList<Dishes> dishCount = new ArrayList<>();

    private Dish_Available dishAvailable = new Dish_Available();
    private Order_Available orderAvailable = new Order_Available();
    private ArrayList<Order_Available> orderAvailableList;
    private double totalPriceCart = 0;

    List<Dish_Available> dish_availableList;

    List<Order_Available_Dish> order_available_dishList;

    adapter_dish_listOrderAvailable adapter;
    CardView cardViewCart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        MenuItem cart = (MenuItem) toolbar.findViewById(R.id.cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishActivity.this, ShopActivity.class);

                intent.putExtra("users", (Serializable) users);
                intent.putExtra("organizations", (Serializable) organizations);
                //intent.putExtra("dishes", (Serializable) dishes);
                //intent.putExtra("order_cart", (Serializable) orderCart);
                intent.putExtra("order_available", (Serializable) orderAvailableList);
                intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
                intent.putExtra("all-dish", (Serializable) dishesListAll);
                intent.putExtra("dish-count", (Serializable) dishCount);
                intent.putExtra("order_cart", (Serializable) orderCart);
                startActivity(intent);
//                cart.setIcon(ContextCompat.getDrawable(DishActivity.this, R.drawable.round_shopping_cart_checkout_24));
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            users.setId(extras.getInt("user_id"));
//            users.setName(extras.getString("user_name"));
//            dishes.setName(extras.getString("dish_name"));
//            dishes.setPrice(extras.getDouble("dish_price"));
//            dishes.setId(extras.getInt("dish_id"));
//            orderAvailable.setDish_id(extras.getInt("dish_id"));

            users = (Users) extras.getSerializable("users");
            dishes = (Dishes) extras.getSerializable("dishes");
            organizations = (Organizations) extras.getSerializable("organizations");
            orderCart = (Order_Cart) extras.getSerializable("order_cart");
            dishesListAll = (ArrayList<Dishes>) extras.getSerializable("all-dish");
            dishCount = (ArrayList<Dishes>) extras.getSerializable("dish-count");
            orderAvailableList = (ArrayList<Order_Available>) extras.getSerializable("order_available");
            orderAvailableDishList = (ArrayList<Order_Available_Dish>) extras.getSerializable("order_available_dish");
            orderAvailable.setDish_id(dishes.getId());
            getSupportActionBar().setTitle(dishes.getName());
//            ((TextView) findViewById(R.id.textViewPrice)).setText("Available Order (" + String.format("RM %.2f", dishes.getPrice()) + "/each)");
        }
        getListOrderAvailable();
//        callListDishAvailableAPI();

        cardViewCart = findViewById(R.id.cardViewCart);

        cardViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishActivity.this, CartActivity.class);
                intent.putExtra("users", (Serializable) users);
                intent.putExtra("organizations", (Serializable) organizations);
                intent.putExtra("all-dish", (Serializable) dishesListAll);
                //intent.putExtra("dishes", (Serializable) dishes);
                intent.putExtra("order_cart", (Serializable) orderCart);
                intent.putExtra("order_available", (Serializable) orderAvailableList);
                intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
                startActivity(intent);
            }
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_DishActivity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_DishActivity"), Context.RECEIVER_EXPORTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.home) {
            Intent intent = new Intent(DishActivity.this, MainActivity.class);
            intent.putExtra("users", (Serializable) users);
            startActivity(intent);
        } else if(id == R.id.cart) {
            Intent intent = new Intent(DishActivity.this, CartActivity.class);
            intent.putExtra("users", (Serializable) users);
            intent.putExtra("organizations", (Serializable) organizations);
            intent.putExtra("all-dish", (Serializable) dishesListAll);
            //intent.putExtra("dishes", (Serializable) dishes);
            intent.putExtra("order_cart", (Serializable) orderCart);
            intent.putExtra("order_available", (Serializable) orderAvailableList);
            intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DishActivity.this, ShopActivity.class);

        intent.putExtra("users", (Serializable) users);
        intent.putExtra("organizations", (Serializable) organizations);
        //intent.putExtra("dishes", (Serializable) dishes);
        //intent.putExtra("order_cart", (Serializable) orderCart);
        intent.putExtra("order_available", (Serializable) orderAvailableList);
        intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
        intent.putExtra("all-dish", (Serializable) dishesListAll);
        intent.putExtra("dish-count", (Serializable) dishCount);
        intent.putExtra("order_cart", (Serializable) orderCart);
        startActivity(intent);
        //super.onBackPressed();
    }

    //    public void callListDishAvailableAPI() {
//        LoadingDialog loadingDialog = new LoadingDialog(DishActivity.this);
//        loadingDialog.startDialog();
//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                .create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseURL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        ApiService apiService = retrofit.create(ApiService.class);
//
//        apiService.listOrderAvailable(orderAvailable.getDish_id()).enqueue(new Callback<ArrayList<Order_Available>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Order_Available>> call, Response<ArrayList<Order_Available>> response) {
//                if(response.isSuccessful()) {
//                    loadingDialog.dismissDialog();
//                    Log.d("OrderS_API-listOrderAvailable()", "Response: API Response, Success. ");
//                    getListOrderAvailable(response.body());
//                } else {
//                    loadingDialog.dismissDialog();
//                    Log.d("OrderS_API-listOrderAvailable()", "Response: API Response, Unsuccessful.");
//                    Toast.makeText(DishActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Order_Available>> call, Throwable t) {
//                loadingDialog.dismissDialog();
//                Log.d("OrderS_API-listOrderAvailable()", "Response: API failed, " + t.getMessage());
//                Toast.makeText(DishActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void getListOrderAvailable() {
        recyclerView = findViewById(R.id.RecycleViewDate);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(DishActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        ArrayList<Order_Available> order_availableList = new ArrayList<>();
        for (Order_Available o : orderAvailableList) {
            if(o.getDish_id() == dishes.getId()) {
                orderAvailable = new Order_Available();
                orderAvailable.setId(o.getId());
                orderAvailable.setOpen_date(o.getOpen_date());
                orderAvailable.setClose_date(o.getClose_date());
                orderAvailable.setDelivery_date(o.getDelivery_date());
                orderAvailable.setDelivery_address(o.getDelivery_address());
                orderAvailable.setQuantity(o.getQuantity());
                orderAvailable.setDiscount(o.getDiscount());
                orderAvailable.setDish_id(o.getDish_id());
                orderAvailable.setLongitude(o.getLongitude());
                orderAvailable.setLatitude(o.getLatitude());
                orderAvailable.setDelivery_place_pic(o.getDelivery_place_pic());

                order_availableList.add(orderAvailable);
            }
        }
        adapter = new adapter_dish_listOrderAvailable(DishActivity.this, order_availableList, dishes, users, organizations, orderCart, orderAvailableDishList);
        recyclerView.setAdapter(adapter);

        int orderCount = 0;
        adapter.setWhenClickListener(new OnItemsClickListener() {
            @Override
            public void onAddBtnClick(Dishes dishes, Order_Available orderAvailable, ArrayList<Order_Available_Dish> orderAvailableDishList_fromAdapter) {

                int orderCount = 0;
                totalPriceCart = 0;
                for(Order_Available_Dish oad : orderAvailableDishList_fromAdapter) {
                    totalPriceCart = totalPriceCart + oad.getTotalprice();

                    if(oad.getQuantity() > 0) {
                        orderCount = orderCount + oad.getQuantity();
                    }
                }
                orderAvailableDishList = orderAvailableDishList_fromAdapter;

                if(totalPriceCart == 0 && orderCount == 0) {
                    TransitionManager.beginDelayedTransition(findViewById(R.id.cardViewCart), new AutoTransition());
                    findViewById(R.id.cardViewCart).setVisibility(View.GONE);
                } else {
                    TransitionManager.beginDelayedTransition(findViewById(R.id.cardViewCart), new AutoTransition());
                    findViewById(R.id.cardViewCart).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textViewCart)).setText(String.format("RM %.2f", totalPriceCart) + " (" + orderCount + "x)");
                }
                orderCart.setTotalamount(totalPriceCart);
            }

            @Override
            public void onMinusBtnClick(Dishes dishes, Order_Available orderAvailable, ArrayList<Order_Available_Dish> orderAvailableDishList_fromAdapter) {

                int orderCount = 0;
                totalPriceCart = 0;
                for(Order_Available_Dish oad : orderAvailableDishList) {
                    totalPriceCart = totalPriceCart + oad.getTotalprice();

                    if(oad.getQuantity() > 0) {
                        orderCount = orderCount + oad.getQuantity();
                    }
                }
                orderAvailableDishList = orderAvailableDishList_fromAdapter;

                if(totalPriceCart == 0 && orderCount == 0) {
                    TransitionManager.beginDelayedTransition(findViewById(R.id.cardViewCart), new AutoTransition());
                    findViewById(R.id.cardViewCart).setVisibility(View.GONE);
                } else {
                    TransitionManager.beginDelayedTransition(findViewById(R.id.cardViewCart), new AutoTransition());
                    findViewById(R.id.cardViewCart).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textViewCart)).setText(String.format("RM %.2f", totalPriceCart) + " (" + orderCount + "x)");
                }
                orderCart.setTotalamount(totalPriceCart);
                //((TextView) findViewById(R.id.textViewCart)).setText(String.format("RM %.2f", totalPriceCart) + " (" + orderCount + "x)");
            }
        });

        int margin = 30;
        Drawable icon_cart = getResources().getDrawable(R.drawable.round_shopping_cart_24);
        icon_cart = iconSetup.addMarginToDrawable(icon_cart, margin);
        icon_cart.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom

        ((TextView) findViewById(R.id.textViewCart)).setCompoundDrawables(icon_cart, null, null, null);

        for(Order_Available_Dish oad : orderAvailableDishList) {
            totalPriceCart = totalPriceCart + oad.getTotalprice();

            if(oad.getQuantity() > 0) {
                orderCount = orderCount + oad.getQuantity();
            }
        }

        if(totalPriceCart == 0 && orderCount == 0) {
            TransitionManager.beginDelayedTransition(findViewById(R.id.cardViewCart), new AutoTransition());
            findViewById(R.id.cardViewCart).setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(findViewById(R.id.cardViewCart), new AutoTransition());
            findViewById(R.id.cardViewCart).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textViewCart)).setText(String.format("RM %.2f", totalPriceCart) + " (" + orderCount + "x)");
        }
        orderCart.setTotalamount(totalPriceCart);

        //((TextView) findViewById(R.id.textViewCart)).setText(String.format("RM %.2f", totalPriceCart) + " (" + orderCount + "x)");
    }
}