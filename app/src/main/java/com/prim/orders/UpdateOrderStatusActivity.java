package com.prim.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dish_Type;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateOrderStatusActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    TextView tv_name, tv_email, tv_phone, tv_date, tv_qty, tv_totalAmount;
    AutoCompleteTextView ac_orderStatus;
    TableRow tr_1, tr_2;
    EditText et_orderId;
    Button btn_update;

    private Users users;
    private Order_Available_Dish orderAvailableDish;
    private Order_Cart orderCart;
    private Order_Available orderAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            orderAvailableDish = (Order_Available_Dish) extras.getSerializable("order_available_dish");
            orderCart = (Order_Cart) extras.getSerializable("order_cart");
            orderAvailable = (Order_Available) extras.getSerializable("order_available");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_name = findViewById(R.id.tvCustName);
        tv_email = findViewById(R.id.tvEmail);
        tv_phone = findViewById(R.id.tvTelNum);
        tv_date = findViewById(R.id.tvDateCreated);
        tv_qty = findViewById(R.id.tvQty);
        tv_totalAmount = findViewById(R.id.tvTotalAmount);
        ac_orderStatus = findViewById(R.id.ac_orderStatus);
        tr_1 = findViewById(R.id.tr1);
        tr_2 = findViewById(R.id.tr2);
        et_orderId = findViewById(R.id.et_orderId);
        btn_update = findViewById(R.id.btnUpdateStatus);

        //TransitionManager.beginDelayedTransition(ll_updateOrder, new AutoTransition());
        tr_1.setVisibility(View.GONE);
        tr_2.setVisibility(View.GONE);

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");

        tv_name.setText(Html.fromHtml("<b>Name : </b>" + users.getName()));
        tv_email.setText(Html.fromHtml("<b>Email : </b>" + users.getEmail()));
        if(users.getTelno() != null) {
            tv_phone.setText(Html.fromHtml("<b>Phone/IC Num : </b>" + users.getTelno()));
        } else {
            tv_phone.setText(Html.fromHtml("<b>Phone/IC Num : </b>" + users.getIcno()));
        }
        String date = formatterDate2.format(orderCart.getCreated_at());
        String time = formatterTime.format(orderCart.getCreated_at());
        tv_date.setText(Html.fromHtml("<b>Date Created : </b>" + date + " (" + time + ")"));
        tv_qty.setText(Html.fromHtml("<b>Quantity : </b>" + String.valueOf(orderAvailableDish.getQuantity()) + " pcs"));
        tv_totalAmount.setText(Html.fromHtml("<b>Total : </b>" + String.format("RM %.2f", orderAvailableDish.getTotalprice())));
        getOrderStatus();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ac_orderStatus.getText().toString().equals("order-completed")) {
                    if(et_orderId.getText().toString().equals(String.valueOf(orderAvailableDish.getId()))) {
                        call_updateOADAdmin_API(ac_orderStatus.getText().toString());
                    } else {
                        Toast.makeText(UpdateOrderStatusActivity.this, "Wrong Order ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    call_updateOADAdmin_API(ac_orderStatus.getText().toString());
                }
            }
        });

        et_orderId.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_orderId.setTransformationMethod(new UpdateOrderStatusActivity.NumericKeyBoardTransformationMethod());
    }

    public void getOrderStatus() {
        ArrayList<String> item = new ArrayList<>();
        if(orderAvailableDish.getDelivery_status().equals("order-preparing")) {
            ac_orderStatus.setText("order-preparing", false);
        } else if(orderAvailableDish.getDelivery_status().equals("order-completed")) {
            ac_orderStatus.setText("order-completed", false);
        } else if(orderAvailableDish.getDelivery_status().equals("order-abandon")) {
            ac_orderStatus.setText("order-abandon", false);
        } else {
            ac_orderStatus.setText("order-unknown", false);
        }

        item.add("order-preparing");
        item.add("order-completed");
        item.add("order-abandon");

//        for(Dish_Type dt : dishTypeList) {
//            item.add(dt.getName());
//        }
        ArrayAdapter<String> adapterItem = new ArrayAdapter<>(UpdateOrderStatusActivity.this, R.layout.list_item, item);
        ac_orderStatus.setAdapter(adapterItem);

        ac_orderStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).toString() == "order-completed" && !orderAvailableDish.getDelivery_status().equals(adapterView.getItemAtPosition(i).toString())) {
                    TransitionManager.beginDelayedTransition(tr_1, new AutoTransition());
                    tr_1.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(tr_2, new AutoTransition());
                    tr_2.setVisibility(View.VISIBLE);
                } else if(orderAvailableDish.getDelivery_status().equals(adapterView.getItemAtPosition(i).toString())) {
                    TransitionManager.beginDelayedTransition(tr_1, new AutoTransition());
                    tr_1.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(tr_2, new AutoTransition());
                    tr_2.setVisibility(View.GONE);
                } else {
                    TransitionManager.beginDelayedTransition(tr_1, new AutoTransition());
                    tr_1.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(tr_2, new AutoTransition());
                    tr_2.setVisibility(View.VISIBLE);
                }
                //Toast.makeText(UpdateOrderStatusActivity.this, "Status: " + adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void call_updateOADAdmin_API(String status) {
        LoadingDialog loadingDialog = new LoadingDialog(UpdateOrderStatusActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.updateOADAdmin(orderAvailableDish.getId(), orderCart.getId(), status).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateOADAdmin()", "Response: API Response, Success. " + response.body().getResponse());
                    Intent intent = new Intent(UpdateOrderStatusActivity.this, OrderAvailableDishAdminActivity.class);
                    //intent.putExtra("users", (Serializable) users);
                    intent.putExtra("order_available", (Serializable) orderAvailable);
                    Intent intentf = new Intent("finish_OrderAvailableDishAdminActivity");
                    sendBroadcast(intentf);
                    startActivity(intent);
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateOADAdmin()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-updateOADAdmin()", "Response: API failed. " + t.getMessage());
                Toast.makeText(UpdateOrderStatusActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}