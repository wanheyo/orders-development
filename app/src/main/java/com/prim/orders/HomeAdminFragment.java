package com.prim.orders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp_listOADAdmin;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeAdminFragment extends Fragment {

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    RecyclerView recyclerView;
    List<Organizations> organizationsList;
    adapter_order_listShop adapter;
    Organizations organizations;
    SearchView searchView;

    private Users users = new Users();
    private ArrayList<Dishes> dishesList = new ArrayList<>();
    private ArrayList<Order_Available> orderAvailableList = new ArrayList<>();
    private ArrayList<Order_Available_Dish> orderAvailableDishList = new ArrayList<>();
    int count = 0;

    TextView tvUncompletedOrder;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_admin, container, false);

        TextView tvGreeting = v.findViewById(R.id.tvGreeting);
        tvUncompletedOrder = v.findViewById(R.id.tvUncompletedOrder);
        tvGreeting.setText("Hi, Welcome!");

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
//            users.setEmail(extras.getString("user_email"));
//            users.setPassword(extras.getString("user_pass"));
//            users.setId(extras.getInt("user_id"));
//            users.setName(extras.getString("user_name"));
            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
            tvGreeting.setText("Hi, " + users.getName());

            call_listDishesByShop_API(v);

            //tvUncompletedOrder.setText(String.valueOf(count));
        }

        // Inflate the layout for this fragment
        return v;
    }

    public void call_listDishesByShop_API(View v) {
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

        apiService.listDishesByShopAdmin(organizations.getId()).enqueue(new Callback<ArrayList<Dishes>>() {
            @Override
            public void onResponse(Call<ArrayList<Dishes>> call, Response<ArrayList<Dishes>> response) {
                if (response.isSuccessful()) {
                    //loadingDialog.dismissDialog();
                    dishesList = response.body();

                    if(dishesList.size() <= 0) {
                        loadingDialog.dismissDialog();
                    } else {
                        for(Dishes d : dishesList) {
                            call_listOrderAvailableAdmin_API(d, v, loadingDialog);
                        }
                    }

                    //ArrayList<Dishes> count = response.body();
                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Success. " + dishesList.size());

                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(getActivity(), "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dishes>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listDishesByShop()", "Response: API failed, " + t.getMessage());
                Toast.makeText(getActivity(), "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void call_listOrderAvailableAdmin_API(Dishes dishes, View v, LoadingDialog loadingDialog) {
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
                    //loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOrderAvailableAdmin()", "Response: API Response, Success. " + orderAvailableList.size());

                    if(orderAvailableList.size() <= 0) {
                        loadingDialog.dismissDialog();
                    } else {
                        for(Order_Available oa : orderAvailableList) {
                            call_listOADAdmin_API(oa, v, loadingDialog);
                        }
                    }



                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOrderAvailableAdmin()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(getActivity(), "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Order_Available>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listOrderAvailableAdmin()", "Response: API failed, " + t.getMessage());
                Toast.makeText(getActivity(), "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void call_listOADAdmin_API(Order_Available orderAvailable, View v, LoadingDialog loadingDialog) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.listOADAdmin(orderAvailable.getId()).enqueue(new Callback<API_Resp_listOADAdmin>() {
            @Override
            public void onResponse(Call<API_Resp_listOADAdmin> call, Response<API_Resp_listOADAdmin> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOADAdmin()", "Response: API Response, Success. ");
                    orderAvailableDishList = response.body().getOrderAvailableDishList();

                    for(Order_Available_Dish oad : orderAvailableDishList) {
                        if(oad.getDelivery_status().equals("order-preparing")) {
                            count++;
                        }
                    }

                    tvUncompletedOrder.setText(String.valueOf(count));

                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOADAdmin()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp_listOADAdmin> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listOADAdmin()", "Response: API failed. " + t.getMessage());
                Toast.makeText(getActivity(), "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}