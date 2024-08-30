package com.prim.orders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    Button buttonlogout;
    private Boolean saveLogin;

    private Users users = new Users();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        TextView tvGreeting = v.findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hi, Welcome!");

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
//            users.setEmail(extras.getString("user_email"));
//            users.setPassword(extras.getString("user_pass"));
//            users.setId(extras.getInt("user_id"));
//            users.setName(extras.getString("user_name"));
            users = (Users) extras.getSerializable("users");
            tvGreeting.setText("Hi, " + users.getName());
            //((TextView)v.findViewById(R.id.textView3)).setText(users.getName());
        }

        callRandomDishesAPI(v);
        callListDishesAPI(v);

        Button buttonbuy = v.findViewById(R.id.buttonBuy);
        Button buttonbuy2 = v.findViewById(R.id.buttonBuy2);
        Button buttonbuy3 = v.findViewById(R.id.buttonBuy3);
        Button buttonbuy4 = v.findViewById(R.id.buttonBuy4);
        Button buttonbuy5 = v.findViewById(R.id.buttonBuy5);

        buttonbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DishActivity.class);
                intent.putExtra("dish_name", ((TextView)v.findViewById(R.id.textViewDishName)).getText());
                startActivity(intent);
            }
        });

        buttonbuy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DishActivity.class);
                intent.putExtra("dish_name", ((TextView)v.findViewById(R.id.textViewDishName2)).getText());
                startActivity(intent);
            }
        });

        buttonbuy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DishActivity.class);
                intent.putExtra("dish_name", ((TextView)v.findViewById(R.id.textViewDishName3)).getText());
                startActivity(intent);
            }
        });

        buttonbuy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DishActivity.class);
                intent.putExtra("dish_name", ((TextView)v.findViewById(R.id.textViewDishName4)).getText());
                startActivity(intent);
            }
        });

        buttonbuy5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DishActivity.class);
                intent.putExtra("dish_name", ((TextView)v.findViewById(R.id.textViewDishName5)).getText());
                startActivity(intent);
            }
        });
        return v;
    }

    public void callRandomDishesAPI(View v) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.randomDishes().enqueue(new Callback<ArrayList<Dishes>>() {
            @Override
            public void onResponse(Call<ArrayList<Dishes>> call, Response<ArrayList<Dishes>> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-randomDishes()", "Response: API Response, Success.");
                    getRandomDish(v, response.body());
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-randomDishes()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(getActivity(), "Response Failed", Toast.LENGTH_SHORT).show();
                    //getRandomDish(v, response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dishes>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-randomDishes()", "Response: API failed, " + t.getMessage());
                Toast.makeText(getActivity(), "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callListDishesAPI(View v) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.listDishes().enqueue(new Callback<ArrayList<Dishes>>() {
            @Override
            public void onResponse(Call<ArrayList<Dishes>> call, Response<ArrayList<Dishes>> response) {

                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishes()", "Response: API Response, Success.");
                    getListDish(v, response.body());
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishes()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(getActivity(), "Response Failed", Toast.LENGTH_SHORT).show();
                    //getListDish(v, response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dishes>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listDishes()", "Response: API failed, " + t.getMessage());
                Toast.makeText(getActivity(), "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getRandomDish(View v, ArrayList<Dishes> dishList) {
        if(getContext() != null) {
            Glide.with(getContext())
                    .load(baseURL + "/dish-image/" + dishList.get(0).getDish_image())
                    .apply(RequestOptions.bitmapTransform(new ColorFilterTransformation(ContextCompat.getColor(getContext(), R.color.overlay_dark))))
                    .placeholder(R.drawable.registerlogin_bg_pic)
                    //.load(dishList.get(0).getDish_image())
                    .into((ImageView)v.findViewById(R.id.imageViewRandomDish));
        }
        ((TextView)v.findViewById(R.id.textView1)).setText(dishList.get(0).getName());
        ((TextView)v.findViewById(R.id.textView2)).setText(dishList.get(0).getO_nama());
        //((TextView)v.findViewById(R.id.textViewDishPrice)).setText(String.format("RM %.2f", dishList.get(i).getPrice()));
    }

    public void getListDish(View v, ArrayList<Dishes> dishList) {

        for(int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    if(getContext() != null) {
                        Glide.with(getContext())
                                .load(dishList.get(i).getDish_image())
                                .into((ImageView)v.findViewById(R.id.ivDishImage));
                    }
                    ((TextView)v.findViewById(R.id.textViewDishName)).setText(dishList.get(i).getName());
                    ((TextView)v.findViewById(R.id.textViewShopAddress)).setText(dishList.get(i).getO_nama());
                    ((TextView)v.findViewById(R.id.textViewDishPrice)).setText(String.format("RM %.2f", dishList.get(i).getPrice()));
                    break;
                case 1:
                    if(getContext() != null) {
                        Glide.with(getContext())
                                .load(dishList.get(i).getDish_image())
                                .into((ImageView)v.findViewById(R.id.imageViewDish2));
                    }
                    ((TextView)v.findViewById(R.id.textViewDishName2)).setText(dishList.get(i).getName());
                    ((TextView)v.findViewById(R.id.textViewShopName2)).setText(dishList.get(i).getO_nama());
                    ((TextView)v.findViewById(R.id.textViewDishPrice2)).setText(String.format("RM %.2f", dishList.get(i).getPrice()));
                    break;
                case 2:
                    if(getContext() != null) {
                        Glide.with(getContext())
                                .load(dishList.get(i).getDish_image())
                                .into((ImageView)v.findViewById(R.id.imageViewDish3));
                    }
                    ((TextView)v.findViewById(R.id.textViewDishName3)).setText(dishList.get(i).getName());
                    ((TextView)v.findViewById(R.id.textViewShopName3)).setText(dishList.get(i).getO_nama());
                    ((TextView)v.findViewById(R.id.textViewDishPrice3)).setText(String.format("RM %.2f", dishList.get(i).getPrice()));
                    break;
                case 3:
                    if(getContext() != null) {
                        Glide.with(getContext())
                                .load(dishList.get(i).getDish_image())
                                .into((ImageView)v.findViewById(R.id.imageViewDish4));
                    }
                    ((TextView)v.findViewById(R.id.textViewDishName4)).setText(dishList.get(i).getName());
                    ((TextView)v.findViewById(R.id.textViewShopName4)).setText(dishList.get(i).getO_nama());
                    ((TextView)v.findViewById(R.id.textViewDishPrice4)).setText(String.format("RM %.2f", dishList.get(i).getPrice()));
                    break;
                case 4:
//                    Glide.with(getContext())
//                            .load(dishList.get(i).getDish_image())
//                            .into((ImageView)v.findViewById(R.id.imageViewDish5));
                    ((TextView)v.findViewById(R.id.textViewDishName5)).setText(dishList.get(i).getName());
                    ((TextView)v.findViewById(R.id.textViewShopName5)).setText(dishList.get(i).getO_nama());
                    ((TextView)v.findViewById(R.id.textViewDishPrice5)).setText(String.format("RM %.2f", dishList.get(i).getPrice()));
                    break;
            }
        }
    }
}