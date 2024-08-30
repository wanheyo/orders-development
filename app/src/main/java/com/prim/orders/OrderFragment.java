package com.prim.orders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderFragment extends Fragment {

    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    RecyclerView recyclerView;
    List<Organizations> organizationsList;
    adapter_order_listShop adapter;
    adapter_empty adapterEmpty;
    Organizations organizations;
    SearchView searchView;

    Users users = new Users();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
//            users.setEmail(extras.getString("user_email"));
//            users.setPassword(extras.getString("user_pass"));
//            users.setId(extras.getInt("user_id"));
//            users.setName(extras.getString("user_name"));
            users = (Users) extras.getSerializable("users");

            Log.d("OrderFragment", "Response: " + users.getDevice_token());
        }

        callListShopsAPI(v);

        // Inflate the layout for this fragment
        return v;
    }

    public void callListShopsAPI(View v) {
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

        apiService.listShops().enqueue(new Callback<ArrayList<Organizations>>() {
            @Override
            public void onResponse(Call<ArrayList<Organizations>> call, Response<ArrayList<Organizations>> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listShops()", "Response: API Response, Success.");
                    getListShops(v, response.body());
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listShops()", "Response: API Response, Unsuccessful.");
                    Toast.makeText(getActivity(), "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Organizations>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listShops()", "Response: API failed, " + t.getMessage());
                Toast.makeText(getActivity(), "Server Unresponded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getListShops(View v, ArrayList<Organizations> organizationsListFromAPI) {
        recyclerView = v.findViewById(R.id.RecyclerViewListShop);
        searchView = v.findViewById(R.id.SearchViewShopName);

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


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        
        organizationsList = new ArrayList<>();

        for (Organizations o: organizationsListFromAPI) {
            organizations = new Organizations();

//            organizations.setId(o.getId());
//            organizations.setOrganization_picture(o.getOrganization_picture());
//            organizations.setNama(o.getNama());
//            organizations.setAddress(o.getAddress());
            organizationsList.add(o);
        }

//        organizations = new Organizations();
//        organizations.setNama("Kopok Lekor");
//        organizations.setAddress("UTeM");
//        organizationsList.add(organizations);

        if(organizationsList.size() <= 0) {
            adapterEmpty = new adapter_empty(getActivity(), "Shop", 0);
            recyclerView.setAdapter(adapterEmpty);
        } else {
            adapter = new adapter_order_listShop(getActivity(), organizationsList, users);
            recyclerView.setAdapter(adapter);
        }
    }

    private void searchList(String text) {
        List<Organizations> organizationsSearchList = new ArrayList<>();
        for(Organizations org : organizationsList) {
            if(org.getNama().toLowerCase().contains(text.toLowerCase())) {
                organizationsSearchList.add(org);
            }
        }

        if(organizationsSearchList.isEmpty()) {
            Toast.makeText(getActivity(), "Not found...", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(organizationsSearchList);
        }
    }
}