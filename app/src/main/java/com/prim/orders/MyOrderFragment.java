package com.prim.orders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp_getOrderAvailableDish;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyOrderFragment extends Fragment {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    RecyclerView recyclerView;
    adapter_myOrder_listOrderAvailableDish adapter;
    adapter_empty adapterEmpty;
    Organizations organizations;
    SearchView searchView;
    int option = 0;

    private Users users = new Users();
    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private ArrayList<Order_Cart> orderCartList;
    private ArrayList<Order_Available> orderAvailableList;
    private ArrayList<Dishes> dishesList;
    private ArrayList<Organizations> organizationsList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_myorder, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
//            users.setEmail(extras.getString("user_email"));
//            users.setPassword(extras.getString("user_pass"));
//            users.setId(extras.getInt("user_id"));
//            users.setName(extras.getString("user_name"));
            users = (Users) extras.getSerializable("users");
        }

        // Inflate the layout for this fragment
        call_getOrderAvailableDish_API(v);

        RadioGroup rg = (RadioGroup) v.findViewById(R.id.radioGroupOption);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonUpcoming) {
                    option = 0;
                    call_getOrderAvailableDish_API(v);
                } else if (checkedId == R.id.radioButtonCompleted) {
                    option = 1;
                    call_getOrderAvailableDish_API(v);
                }
            }
        });

        return v;
    }

    public void call_getOrderAvailableDish_API(View v) {
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

        apiService.getOrderAvailableDish(users.getId(), option).enqueue(new Callback<API_Resp_getOrderAvailableDish>() {
            @Override
            public void onResponse(Call<API_Resp_getOrderAvailableDish> call, Response<API_Resp_getOrderAvailableDish> response) {
                if(response.isSuccessful()) {
                    API_Resp_getOrderAvailableDish resp = response.body();
                    orderAvailableDishList = resp.getOrderAvailableDish();
                    orderCartList = resp.getOrderCart();
                    orderAvailableList = resp.getOrderAvailable();
                    dishesList = resp.getDishes();
                    organizationsList = resp.getOrganizations();

                    //Log.d("responseBody", "Response: " + data.get(0).getName()  + ", " + count.get(0).getTotalDishAvailable());
                    loadingDialog.dismissDialog();
                    getOrderAvailableDishList(v);
                    Log.d("OrderS_API-getOrderAvailableDish()", "Response: API Response, Success.");
                    //getListDishesByShop(data, count);
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-getOrderAvailableDish()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(getActivity(), "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<API_Resp_getOrderAvailableDish> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-getOrderAvailableDish()", "Response: API failed, " + t.getMessage());
                Toast.makeText(getActivity(), "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getOrderAvailableDishList(View v) {
        recyclerView = v.findViewById(R.id.rvOrderAvailableDish);
//        searchView = v.findViewById(R.id.sv_searchDishName);
//
//        searchView.clearFocus();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchList(newText);
//                return false;
//            }
//        });

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShopActivity.this, 2, GridLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        if(orderAvailableDishList.size() <= 0) {
            adapterEmpty = new adapter_empty(getActivity(), "Orders", 3);
            recyclerView.setAdapter(adapterEmpty);
        } else {
            adapter = new adapter_myOrder_listOrderAvailableDish(getActivity(), users, organizationsList, dishesList, orderCartList, orderAvailableList, orderAvailableDishList);
            recyclerView.setAdapter(adapter);
        }
    }

    private void searchList(String text) {
        ArrayList<Dishes> dishesSearchList = new ArrayList<>();

        for(Dishes d : dishesList) {
            if(d.getName().toLowerCase().contains(text.toLowerCase())) {
                dishesSearchList.add(d);
            }
        }

        if(dishesSearchList.isEmpty()) {
            Toast.makeText(getActivity(), "Not found...", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dishesSearchList);
        }
    }
}
