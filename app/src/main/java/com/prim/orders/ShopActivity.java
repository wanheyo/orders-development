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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dish_Available;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp_getOrderCart;
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

public class ShopActivity extends AppCompatActivity {

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    RecyclerView recyclerView;
    Button buttonAvailableDish;
    Button buttonAllDish;
    CardView cardViewCart;
    Boolean buttonAvailableDishClicked;
    Boolean buttonAllDishClicked;
    private IconSetup iconSetup;

    private List<Dishes> dishesList;
    private ArrayList<Dishes> dishesListAll = new ArrayList<>();
    private ArrayList<Dishes> dishCount = new ArrayList<>();
    private Dishes dishes;
    adapter_shop_listDish adapter;
    adapter_empty adapterEmpty;

    private Users users = new Users();
    private Organizations organizations = new Organizations();
    private ArrayList<Order_Available> orderAvailableList = new ArrayList<>();
    private Order_Available_Dish orderAvailableDish;
    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private Order_Cart orderCart = new Order_Cart();


    public interface totalAvailableDishCallBack {
        void onSuccess(int response);
        void onError(int error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this, MainActivity.class);

                intent.putExtra("users", (Serializable) users);

                startActivity(intent);
                Intent intentf = new Intent("finish_DishActivity");
                sendBroadcast(intentf);
                Intent intentf2 = new Intent("finish_ShopActivity");
                sendBroadcast(intentf2);
                Intent intentf3 = new Intent("finish_CartActivity");
                sendBroadcast(intentf3);
                finish();
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
            orderAvailableList = (ArrayList<Order_Available>) extras.getSerializable("order_available");
            orderAvailableDishList = (ArrayList<Order_Available_Dish>) extras.getSerializable("order_available_dish");
            dishesListAll = (ArrayList<Dishes>) extras.getSerializable("all-dish");
            dishCount = (ArrayList<Dishes>) extras.getSerializable("dish-count");
            orderCart = (Order_Cart) extras.getSerializable("order_cart");

            ((TextView) findViewById(R.id.textViewShopName6)).setText(organizations.getNama());
            ((TextView) findViewById(R.id.textViewShopAddress2)).setText(organizations.getAddress() + ", " + organizations.getCity() + ", " + organizations.getPostcode() + " " + organizations.getDistrict() + ", " + organizations.getState());
            if(this != null) {
                Glide.with(this)
                        .load(baseURL + "/organization-picture/" + organizations.getOrganization_picture())
                        .placeholder(R.drawable.no_picture_icon)
                        .into((ImageView) findViewById(R.id.ivDishImage));
            }
            //getSupportActionBar().setTitle(organizations.getNama() + " (" + organizations.getId() + ")");
        }

        if(dishesListAll == null || dishCount == null) {
            callGetOrderCartAPI();
            callListDishAvailableAPI();
        } else {
            buttonAvailableDishClicked = true;
            buttonAllDishClicked = false;
            getListDishesByShop(dishesListAll, dishCount);
        }

//        callGetOrderCartAPI();
//        callListDishAvailableAPI();

        buttonAvailableDishClicked = true;
        buttonAllDishClicked = false;

        //callListDishesByShopAPI();

        buttonAvailableDish = findViewById(R.id.buttonAvailableDish);
        buttonAllDish = findViewById(R.id.buttonAllDishes);
        cardViewCart = findViewById(R.id.cardViewCart);

        buttonAvailableDish.setBackgroundColor(ContextCompat.getColor(ShopActivity.this, R.color.primary_darker));

        buttonAvailableDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAvailableDish.setBackgroundColor(ContextCompat.getColor(ShopActivity.this, R.color.primary_darker));
                buttonAllDish.setBackgroundColor(ContextCompat.getColor(ShopActivity.this, R.color.primary));

                buttonAvailableDishClicked = true;
                buttonAllDishClicked = false;
                //callListDishesByShopAPI();
                callGetOrderCartAPI();
                callListDishAvailableAPI();
            }
        });

        buttonAllDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAvailableDish.setBackgroundColor(ContextCompat.getColor(ShopActivity.this, R.color.primary));
                buttonAllDish.setBackgroundColor(ContextCompat.getColor(ShopActivity.this, R.color.primary_darker));

                buttonAvailableDishClicked = false;
                buttonAllDishClicked = true;
                //callListDishesByShopAPI();
                callGetOrderCartAPI();
                callListDishAvailableAPI();
            }
        });

        cardViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this, CartActivity.class);
                intent.putExtra("users", (Serializable) users);
                intent.putExtra("organizations", (Serializable) organizations);
                intent.putExtra("all-dish", (Serializable) dishesListAll);
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
                if (action.equals("finish_ShopActivity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_ShopActivity"), Context.RECEIVER_EXPORTED);
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
            Intent intentf = new Intent("finish_DishActivity");
            sendBroadcast(intentf);
            Intent intentf2 = new Intent("finish_ShopActivity");
            sendBroadcast(intentf2);
            Intent intentf3 = new Intent("finish_CartActivity");
            sendBroadcast(intentf3);
            finish();
            Intent intent = new Intent(ShopActivity.this, MainActivity.class);
            intent.putExtra("users", (Serializable) users);
            startActivity(intent);
        } else if(id == R.id.cart) {
            Intent intent = new Intent(ShopActivity.this, CartActivity.class);
            intent.putExtra("users", (Serializable) users);
            intent.putExtra("organizations", (Serializable) organizations);
            intent.putExtra("all-dish", (Serializable) dishesListAll);
            intent.putExtra("order_cart", (Serializable) orderCart);
            intent.putExtra("order_available", (Serializable) orderAvailableList);
            intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ShopActivity.this, MainActivity.class);

        intent.putExtra("users", (Serializable) users);
//        intent.putExtra("organizations", (Serializable) organizations);
//        intent.putExtra("dishes", (Serializable) dishes);
//        //intent.putExtra("order_cart", (Serializable) orderCart);
//        //intent.putExtra("order_available", (Serializable) orderAvailableList);
//        intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
        startActivity(intent);
        Intent intentf = new Intent("finish_DishActivity");
        sendBroadcast(intentf);
        Intent intentf2 = new Intent("finish_ShopActivity");
        sendBroadcast(intentf2);
        Intent intentf3 = new Intent("finish_CartActivity");
        sendBroadcast(intentf3);
        finish();
        //super.onBackPressed();
    }

    public void callGetOrderCartAPI() {
        LoadingDialog loadingDialog = new LoadingDialog(ShopActivity.this);
        loadingDialog.startDialog();
//        Gson gson = new GsonBuilder()
//                .setDateFormat("HH:mm:ss")
//                .create();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getOrderCart(users.getId(), organizations.getId()).enqueue(new Callback<API_Resp_getOrderCart>() {
            @Override
            public void onResponse(Call<API_Resp_getOrderCart> call, Response<API_Resp_getOrderCart> response) {
                if(response.isSuccessful()) {
                    API_Resp_getOrderCart resp = response.body();
                    //orderAvailableDishList = resp.getOrderAvailableDish();
                    orderCart = resp.getOrderCart();
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-getOrderCart()", "Response: API Response, Success. " + orderCart.getId());
//                    getListOrderAvailable(response.body());
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-getOrderCart()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<API_Resp_getOrderCart> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-getOrderCart()", "Response: API failed, " + t.getMessage());
                Toast.makeText(ShopActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callListDishAvailableAPI() {
        LoadingDialog loadingDialog = new LoadingDialog(ShopActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.listOrderAvailable(organizations.getId()).enqueue(new Callback<ArrayList<Order_Available>>() {
            @Override
            public void onResponse(Call<ArrayList<Order_Available>> call, Response<ArrayList<Order_Available>> response) {
                if(response.isSuccessful()) {
                    //loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOrderAvailable()", "Response: API Response, Success. ");
                    orderAvailableList = response.body();
                    callListDishesByShopAPI(loadingDialog);
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOrderAvailable()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Order_Available>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listOrderAvailable()", "Response: API failed, " + t.getMessage());
                Toast.makeText(ShopActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callListDishesByShopAPI(LoadingDialog loadingDialog) {
        //LoadingDialog loadingDialog = new LoadingDialog(ShopActivity.this);
        //loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.listDishesByShop(organizations.getId()).enqueue(new Callback<ArrayList<Dishes>>() {
            @Override
            public void onResponse(Call<ArrayList<Dishes>> call, Response<ArrayList<Dishes>> response) {
                if(response.isSuccessful()) {
                    //API_Resp_listDishesByShop resp = response.body();
//                    ArrayList<Dishes> data = resp.getData();
                    ArrayList<Dishes> data = response.body();
                    dishesListAll = data;
//                    ArrayList<Dishes> count = resp.getCount();
                    ArrayList<Dishes> count = response.body();
                    dishCount = count;
                    //Log.d("responseBody", "Response: " + data.get(0).getName()  + ", " + count.get(0).getTotalDishAvailable());
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Success.");
                    getListDishesByShop(data, count);
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dishes>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listDishesByShop()", "Response: API failed, " + t.getMessage());
                Toast.makeText(ShopActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });

//        apiService.listDishesByShop(organizations.getId()).enqueue(new Callback<ArrayList<Dishes>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Dishes>> call, Response<ArrayList<Dishes>> response) {
//
//                if(response.isSuccessful()) {
//                    ArrayList<Dishes> responseBody = response.body();
//                    Log.d("responseBody", "Response: " + response.body());
//                    if (responseBody != null) {
//
//                    }
//
//                    loadingDialog.dismissDialog();
//                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Success.");
//                    //getListDishesByShop(response.body());
//                } else {
//                    loadingDialog.dismissDialog();
//                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Unsuccessful.");
//                    Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Dishes>> call, Throwable t) {
//                loadingDialog.dismissDialog();
//                Log.d("OrderS_API-listDishesByShop()", "Response: API failed, " + t.getMessage());
//                Toast.makeText(ShopActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void getListDishesByShop(ArrayList<Dishes> dishListFromAPI_getData, ArrayList<Dishes> dishListFromAPI_getCount) {
        recyclerView = findViewById(R.id.RecycleViewListDish);


//        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShopActivity.this, 2, GridLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShopActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(ShopActivity.this));

        dishesList = new ArrayList<>();
        int i = 0;
        for (Dishes d : dishListFromAPI_getData) {
            dishes = new Dishes();
            dishes.setId(d.getId());
            dishes.setName(d.getName());
            dishes.setPrice(d.getPrice());
            dishes.setDish_image(d.getDish_image());
            dishes.setTotalOrderAvailable(dishListFromAPI_getCount.get(i).getTotalOrderAvailable());
            i++;

            if(buttonAvailableDishClicked) {
                if(dishes.getTotalOrderAvailable() > 0) {
                    for (Order_Available oad : orderAvailableList) {
                        if(oad.getDish_id() == dishes.getId()) {
                            if(oad.getQuantity() <= 0) {
                                break;
                            } else {
                                dishesList.add(dishes);
                                break;
                            }
                        }
                    }
//                    dishesList.add(dishes);
                }
            }

            if(buttonAllDishClicked) {
                dishesList.add(dishes);
            }
//            for (Dishes d_count : dishListFromAPI_getCount) {
//                if(d_count.getId() == d.getId()) {
//                    dishes.setTotalDishAvailable(d_count.getTotalDishAvailable());
//                }
//            }
//            callListDishAvailableAPI(d.getId(), new totalAvailableDishCallBack() {
//                @Override
//                public void onSuccess(int response) {
//                    Log.d("Callback()", "Response: API Response, Success. dish_id = " + d.getId() + ", " + response);
//                    dishes.setTotalDishAvailable(response);
//                }
//
//                @Override
//                public void onError(int error) {
//                    Log.d("Callback()", "Response: API Response, Success. dish_id = " + d.getId() + ", " + error);
//                    dishes.setTotalDishAvailable(error);
//                }
//            });

        }

        Log.d("orderAvailableDishList", "Response: orderAvailableDishList Success,Size Before = " + orderAvailableDishList.size());

        if(orderAvailableDishList.isEmpty()) {

            for (Order_Available oa : orderAvailableList) {
                orderAvailableDish = new Order_Available_Dish();
                orderAvailableDish.setOrder_cart_id(orderCart.getId());
                orderAvailableDish.setOrder_available_id(oa.getId());
                orderAvailableDish.setQuantity(oa.getQuantity_order());
                orderAvailableDish.setTotalprice(oa.getTotalPrice_order());

                orderAvailableDishList.add(orderAvailableDish);
            }
        }

        Log.d("orderAvailableDishList", "Response: orderAvailableDishList Success,Size After = " + orderAvailableDishList.size());

        if(dishesList.size() <= 0) {
            adapterEmpty = new adapter_empty(ShopActivity.this, "Dish", 0);
            recyclerView.setAdapter(adapterEmpty);
        } else {
            adapter = new adapter_shop_listDish(ShopActivity.this, dishesList, users, organizations, orderCart, orderAvailableList, orderAvailableDishList, dishesListAll, dishCount);
            recyclerView.setAdapter(adapter);
        }

        int margin = 30;
        Drawable icon_cart = getResources().getDrawable(R.drawable.round_shopping_cart_24);
        icon_cart = iconSetup.addMarginToDrawable(icon_cart, margin);
        icon_cart.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom

        ((TextView) findViewById(R.id.textViewCart)).setCompoundDrawables(icon_cart, null, null, null);

        double totalPriceCart = 0;
        int orderCount = 0;
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
    }

    public void callListDishAvailableAPI(int dish_id, totalAvailableDishCallBack callBack) {
        LoadingDialog loadingDialog = new LoadingDialog(ShopActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.listDishAvailable(dish_id).enqueue(new Callback<ArrayList<Dish_Available>>() {
            @Override
            public void onResponse(Call<ArrayList<Dish_Available>> call, Response<ArrayList<Dish_Available>> response) {

                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishAvailable()", "Response: API Response, Success. dish_id = " + dish_id + ", " + response.body().size());
                    callBack.onSuccess(response.body().size());
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishAvailable()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                    callBack.onError(0);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dish_Available>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listDishAvailable()", "Response: API failed, " + t.getMessage());
                Toast.makeText(ShopActivity.this, "Server Unresponded", Toast.LENGTH_SHORT).show();
                callBack.onError(0);
            }
        });
    }

}