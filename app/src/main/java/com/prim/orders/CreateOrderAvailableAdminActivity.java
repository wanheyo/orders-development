package com.prim.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateOrderAvailableAdminActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    EditText et_pickupDate, et_pickupTime, et_startDate, et_startTime, et_endDate, et_endTime, et_pickupAddress, et_quantity;
    Date dateDelivery, dateStart, dateEnd;


    private Organizations organizations;
    private Order_Available orderAvailable = new Order_Available();
    private Dishes dishes = new Dishes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order_available_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreateOrderAvailable);
        setSupportActionBar(toolbar);
//        MenuItem cart = (MenuItem) toolbar.findViewById(R.id.cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                cart.setIcon(ContextCompat.getDrawable(DishActivity.this, R.drawable.round_shopping_cart_checkout_24));
            }
        });

        getSupportActionBar().setTitle("Add new order slot");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            organizations = (Organizations) extras.getSerializable("organizations");
            dishes = (Dishes) extras.getSerializable("dishes");
            Log.d("CreateDish", "Response: Success. " + dishes.getName());
//            ((TextView) findViewById(R.id.textViewPrice)).setText("Available Order (" + String.format("RM %.2f", dishes.getPrice()) + "/each)");
        }

        et_pickupDate = findViewById(R.id.et_deliveryDate);
        et_pickupTime = findViewById(R.id.et_deliveryTime);
        et_startDate = findViewById(R.id.et_startDate);
        et_startTime = findViewById(R.id.et_startTime);
        et_endDate = findViewById(R.id.et_endDate);
        et_endTime = findViewById(R.id.et_endTime);
        et_pickupAddress = findViewById(R.id.et_deliveryAddress);
        et_quantity = findViewById(R.id.et_orderId);
        Button btn_AddOrderAvailable = findViewById(R.id.btnAddOrderAvailable);

        et_quantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_quantity.setTransformationMethod(new CreateOrderAvailableAdminActivity.NumericKeyBoardTransformationMethod());

        et_pickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateOrderAvailableAdminActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.

                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        dateDelivery = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTime();
                        et_pickupDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a", Locale.getDefault());
                        String formattedTime = timeFormat.format(dateDelivery);
                        et_pickupTime.setText(formattedTime);
                    }
                },year, month, day);

                long oneDayInMillis = 24 * 60 * 60 * 1000; // number of milliseconds in one day
                long oneDayLater = new Date().getTime() + oneDayInMillis;
                datePickerDialog.getDatePicker().setMinDate(oneDayLater);
                datePickerDialog.show();

                et_startDate.setText(null);
                et_startTime.setText(null);
                et_endDate.setText(null);
                et_endTime.setText(null);
            }
        });

        et_pickupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateDelivery != null) {
                    final Calendar c = Calendar.getInstance();

                    // on below line we are getting our hour, minute.
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // on below line we are initializing our Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateOrderAvailableAdminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // on below line we are setting selected time
                            // in our text view.
                            
                            SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a", Locale.getDefault());
                            Calendar c = Calendar.getInstance();
                            c.setTime(dateDelivery);
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH);
                            int day = c.get(Calendar.DAY_OF_MONTH);
                            dateDelivery = new GregorianCalendar(year, month, day, hourOfDay, minute).getTime();
                            String formattedTime = timeFormat.format(dateDelivery);
                            et_pickupTime.setText(formattedTime);
                        }
                    }, hour, minute, false);
                    // at last we are calling show to
                    // display our time picker dialog.
                    timePickerDialog.show();
                } else {
                    Toast.makeText(CreateOrderAvailableAdminActivity.this, "Pickup date cannot be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        et_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateDelivery != null) {
                    final Calendar c = Calendar.getInstance();

                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(CreateOrderAvailableAdminActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            final Calendar c = Calendar.getInstance();
                            int hour = c.get(Calendar.HOUR_OF_DAY);
                            int minute = c.get(Calendar.MINUTE);
                            dateStart = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTime();
                            et_startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a", Locale.getDefault());
                            String formattedTime = timeFormat.format(dateStart);
                            et_startTime.setText(formattedTime);
                        }
                    },year, month, day);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateDelivery);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    Date dateDeliverySubtract1day = new Date();
                    dateDeliverySubtract1day = calendar.getTime();
                    datePickerDialog.getDatePicker().setMaxDate(dateDeliverySubtract1day.getTime());
                    datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                    datePickerDialog.show();
                    Log.d("new Date().getTime()", "Response: API Response, Success. " + new Date().getTime());

                    et_endDate.setText(null);
                    et_endTime.setText(null);
                } else {
                    Toast.makeText(CreateOrderAvailableAdminActivity.this, "Pickup date cannot be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        et_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateDelivery != null) {
                    final Calendar c = Calendar.getInstance();

                    // on below line we are getting our hour, minute.
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // on below line we are initializing our Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateOrderAvailableAdminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // on below line we are setting selected time
                            // in our text view.
                            SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a", Locale.getDefault());
                            Calendar c = Calendar.getInstance();
                            c.setTime(dateStart);
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH);
                            int day = c.get(Calendar.DAY_OF_MONTH);
                            Date pickTime = new GregorianCalendar(year, month, day, hourOfDay, minute).getTime();
                            if(pickTime.getTime() >= dateDelivery.getTime()) {
                                Toast.makeText(CreateOrderAvailableAdminActivity.this, "Start order time cannot pass the pickup time.", Toast.LENGTH_SHORT).show();
                            } else {
                                dateStart = new GregorianCalendar(year, month, day, hourOfDay, minute).getTime();
                                String formattedTime = timeFormat.format(dateStart);
                                et_startTime.setText(formattedTime);
                            }

                        }
                    }, hour, minute, false);
                    // at last we are calling show to
                    // display our time picker dialog.
                    timePickerDialog.show();
                } else {
                    Toast.makeText(CreateOrderAvailableAdminActivity.this, "Pickup date cannot be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        et_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateStart != null) {
                    final Calendar c = Calendar.getInstance();

                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(CreateOrderAvailableAdminActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            final Calendar c = Calendar.getInstance();
                            int hour = c.get(Calendar.HOUR_OF_DAY);
                            int minute = c.get(Calendar.MINUTE);
                            Date dateNow = c.getTime();

                            Calendar c1 = Calendar.getInstance();
                            c1.setTime(dateDelivery);
                            int h = c1.get(Calendar.HOUR_OF_DAY);
                            int min = c1.get(Calendar.MINUTE);

                            if(hour >= h && minute >= min && !(dateNow.getTime() >= dateStart.getTime())) {
                                dateEnd = new GregorianCalendar(year, monthOfYear, dayOfMonth, h - 1, min).getTime();
                            } else {
                                dateEnd = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour + 1, minute).getTime();
                            }

                            if(dateEnd.getTime() >= dateStart.getTime()) {
                                dateEnd = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour + 1, minute).getTime();
                            }

                            if(dateEnd.getTime() >= dateDelivery.getTime()) {
                                dateEnd = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour - 1, minute).getTime();
                            }

                            et_endDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a", Locale.getDefault());
                            String formattedTime = timeFormat.format(dateEnd);
                            et_endTime.setText(formattedTime);
                        }
                    },year, month, day);

                    datePickerDialog.getDatePicker().setMinDate(dateStart.getTime());
                    datePickerDialog.getDatePicker().setMaxDate(dateDelivery.getTime());
                    datePickerDialog.show();
                } else {
                    Toast.makeText(CreateOrderAvailableAdminActivity.this, "Start order date cannot be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        et_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateDelivery != null) {
                    final Calendar c = Calendar.getInstance();

                    // on below line we are getting our hour, minute.
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // on below line we are initializing our Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateOrderAvailableAdminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            SimpleDateFormat timeFormat = new SimpleDateFormat("KK:mm a", Locale.getDefault());
                            Calendar c = Calendar.getInstance();
                            c.setTime(dateEnd);
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH);
                            int day = c.get(Calendar.DAY_OF_MONTH);
                            Date pickTime = new GregorianCalendar(year, month, day, hourOfDay, minute).getTime();
                            if((pickTime.getTime() <= dateStart.getTime()) || (pickTime.getTime() >= dateDelivery.getTime())) {
                                Toast.makeText(CreateOrderAvailableAdminActivity.this, "End order time cannot pass the start order time.", Toast.LENGTH_SHORT).show();
                            } else {
                                dateEnd = new GregorianCalendar(year, month, day, hourOfDay, minute).getTime();
                                String formattedTime = timeFormat.format(dateEnd);
                                et_endTime.setText(formattedTime);
                            }
                        }
                    }, hour, minute, false);
                    // at last we are calling show to
                    // display our time picker dialog.
                    timePickerDialog.show();
                } else {
                    Toast.makeText(CreateOrderAvailableAdminActivity.this, "Pickup date cannot be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_AddOrderAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrderAvailable();
            }
        });
    }

    public void addOrderAvailable() {
        String deliveryAddress = et_pickupAddress.getText().toString();
        String quantity = et_quantity.getText().toString();


        if(dateDelivery != null && dateStart != null && dateEnd != null && !deliveryAddress.isEmpty() && !quantity.isEmpty()) {

            orderAvailable.setDelivery_date(dateDelivery);
            orderAvailable.setOpen_date(dateStart);
            orderAvailable.setClose_date(dateEnd);
            orderAvailable.setDelivery_address(deliveryAddress);
            orderAvailable.setQuantity(Integer.parseInt(quantity));

            call_addOrderAvailable_API();

        } else {
            Toast.makeText(CreateOrderAvailableAdminActivity.this, "Please complete all the form.", Toast.LENGTH_SHORT).show();
        }
    }

    public void call_addOrderAvailable_API() {
        LoadingDialog loadingDialog = new LoadingDialog(CreateOrderAvailableAdminActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.addOrderAvailable(dishes.getId(), orderAvailable.getOpen_date(), orderAvailable.getClose_date(), orderAvailable.getDelivery_date(), orderAvailable.getDelivery_address(), orderAvailable.getQuantity()).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-addOrderAvailable()", "Response: API Response, Success. " + response.body().getResponse());

//                    Intent intent = new Intent(CreateOrderAvailableAdminActivity.this, MainActivityAdmin.class);
//                    //intent.putExtra("users", (Serializable) users);
//                    intent.putExtra("organizations", (Serializable) organizations);
//                    startActivity(intent);
//
                    Toast.makeText(CreateOrderAvailableAdminActivity.this, "Slot added", Toast.LENGTH_SHORT).show();
                    Intent intentf2 = new Intent("finish_DishAdminActivity");
                    sendBroadcast(intentf2);
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-addOrderAvailable()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-addOrderAvailable()", "Response: API failed. " + t.getMessage());
                Toast.makeText(CreateOrderAvailableAdminActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
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