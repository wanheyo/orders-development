package com.prim.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DishAdminFragment extends Fragment {

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    RecyclerView recyclerView;
    List<Organizations> organizationsList;
    adapter_admin_dishes_listDish adapter;
    adapter_empty adapterEmpty;
    SearchView searchView;
    Button buttonAddNewDish;

    private ArrayList<Dishes> dishesList;
    private Users users = new Users();
    private Organizations organizations = new Organizations();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dish_admin, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
        }

        call_listDishesByShop_API(v);

        buttonAddNewDish = v.findViewById(R.id.btnAddNewDish);
        buttonAddNewDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateDishAdminActivity.class);
                intent.putExtra("organizations", (Serializable) organizations);
                startActivity(intent);
            }
        });

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
                    loadingDialog.dismissDialog();
                    dishesList = response.body();
                    //ArrayList<Dishes> count = response.body();
                    Log.d("OrderS_API-listDishesByShop()", "Response: API Response, Success. " + dishesList.size());
                    getDishesList(v);
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

    public void getDishesList(View v) {
        recyclerView = v.findViewById(R.id.RecyclerViewDish);
        searchView = v.findViewById(R.id.sv_searchDishName);

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShopActivity.this, 2, GridLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(ShopActivity.this));

        if(dishesList.size() <= 0) {
            adapterEmpty = new adapter_empty(getActivity(), "Dish", 1);
            recyclerView.setAdapter(adapterEmpty);
        } else {
            adapter = new adapter_admin_dishes_listDish(getActivity(), dishesList, users, organizations);
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