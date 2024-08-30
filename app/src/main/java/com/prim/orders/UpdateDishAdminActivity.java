package com.prim.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dish_Type;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;
import com.prim.orders.utilities.MediaHandler;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateDishAdminActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    EditText et_dishname, et_dishprice;
    AutoCompleteTextView ac_dishtype;
    ImageView ivAddDishImage;
    Button btnUpdateDish;

    String dishName, dishPrice, dishType;

    File fileImage;
    MultipartBody.Part filePart;
    MediaHandler mediaHandler;

    private Organizations organizations;
    private ArrayList<Dish_Type> dishTypeList;
    private Dishes dishes = new Dishes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dish_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
//        MenuItem cart = (MenuItem) toolbar.findViewById(R.id.cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                cart.setIcon(ContextCompat.getDrawable(DishActivity.this, R.drawable.round_shopping_cart_checkout_24));
            }
        });

        ivAddDishImage = findViewById(R.id.ivAddDish);
        et_dishname = findViewById(R.id.et_dishname);
        et_dishprice = findViewById(R.id.et_dishprice);
        ac_dishtype = findViewById(R.id.ac_dishtype);
        btnUpdateDish = findViewById(R.id.btnUpdateDish);

        getSupportActionBar().setTitle("Add new dish");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            organizations = (Organizations) extras.getSerializable("organizations");
            dishes = (Dishes) extras.getSerializable("dishes");
            Log.d("CreateDish", "Response: Success. " + organizations.getNama());
//            ((TextView) findViewById(R.id.textViewPrice)).setText("Available Order (" + String.format("RM %.2f", dishes.getPrice()) + "/each)");

            if(this != null) {
                Glide.with(this)
                        .load(baseURL + "/dish-image/" + dishes.getDish_image())
                        .placeholder(R.drawable.no_picture_icon)
                        //.load(dishesList.get(position).getDish_image())
                        .into(ivAddDishImage);
            }
            et_dishname.setText(dishes.getName());
            et_dishprice.setText(String.format("RM%.2f", dishes.getPrice()));
            //ac_dishtype.setText(dishes.getDish_type());
        }



        if(dishes.getTotalOrderAvailable() > 0) {
            et_dishname.setEnabled(false);
            et_dishprice.setEnabled(false);
        }


        ivAddDishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

        //et_dishprice.setText("RM0.00");
        et_dishprice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_dishprice.setTransformationMethod(new UpdateDishAdminActivity.NumericKeyBoardTransformationMethod());
        et_dishprice.addTextChangedListener(new UpdateDishAdminActivity.MoneyTextWatcher(et_dishprice));

        call_getDishType_API();
        //ac_dishtype.setSelection(dishes.getDish_type());
        String[] item = {"Test", "Test2"};

        btnUpdateDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDish();
            }
        });
    }

    public void call_getDishType_API() {
        LoadingDialog loadingDialog = new LoadingDialog(UpdateDishAdminActivity.this);
        loadingDialog.startDialog();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getDishType().enqueue(new Callback<ArrayList<Dish_Type>>() {
            @Override
            public void onResponse(Call<ArrayList<Dish_Type>> call, Response<ArrayList<Dish_Type>> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();

                    if (response.body() != null) {
                        Log.d("OrderS_API-getDishType()", "Response: API Response, Success. ");
                        dishTypeList = response.body();
                        getDishType();

                    } else {
                        Log.d("OrderS_API-getDishType()", "Response: API Response. ");
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Dish_Type>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-getDishType()", "Response: API Response.");
                Toast.makeText(UpdateDishAdminActivity.this, "Receive dish type failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDishType() {
        ArrayList<String> item = new ArrayList<>();
        for(Dish_Type dt : dishTypeList) {
            item.add(dt.getName());
        }
        ArrayAdapter<String> adapterItem = new ArrayAdapter<>(UpdateDishAdminActivity.this, R.layout.list_item, item);
        ac_dishtype.setAdapter(adapterItem);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Set the initial selection to "B"
//                for(Dish_Type dt : dishTypeList) {
//                    if(dishes.getDish_type() == dt.getId()) {
//                        int initialSelectionIndex = item.indexOf(dt.getName());
//                        ac_dishtype.setSelection(initialSelectionIndex);
//                    }
//                }
//            }
//        }, 100);


        //ac_dishtype.setSelection(1);

        ac_dishtype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dishType = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UpdateDishAdminActivity.this, "Item: " + dishType, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addDish() {
        dishName = et_dishname.getText().toString();
        dishPrice = et_dishprice.getText().toString();
        dishType = ac_dishtype.getText().toString();

        Log.d("addDish()", "Response: dishName-" + dishName + ", dishPrice-" + dishPrice + ", dishType-" + dishType + ".");

        if(!dishName.isEmpty() && !"RM0.00".equals(dishPrice) && !"Select".equals(dishType)) {
            dishes.setName(dishName);
            dishes.setPrice(Double.parseDouble(dishPrice.substring(2)));

            for(Dish_Type dt : dishTypeList) {
                if((dt.getName()).equals(dishType)) {
                    dishes.setDish_type(dt.getId());
                    break;
                }
            }
            call_updateDishes_API();
        } else {
            if(dishName.isEmpty()) {
                Toast.makeText(UpdateDishAdminActivity.this, "Dish name cannot be blank.", Toast.LENGTH_SHORT).show();
            } else if(dishPrice.isEmpty() || dishPrice.equals("RM0.00")) {
                Toast.makeText(UpdateDishAdminActivity.this, "Dish price cannot be blank.", Toast.LENGTH_SHORT).show();
            } else if(dishType.isEmpty() || dishType.equals("Select")) {
                Toast.makeText(UpdateDishAdminActivity.this, "Dish type cannot be blank.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void call_updateDishes_API() {
        LoadingDialog loadingDialog = new LoadingDialog(UpdateDishAdminActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //filePart = MultipartBody.Part.createFormData("file", fileImage.getName(), RequestBody.create(MediaType.parse("image/*"), fileImage));
        Log.d("OrderS_API-updateDishes()", "Response: API Response, Success. " + fileImage);
        //RequestBody requestFile = RequestBody.create(MultipartBody.FORM, fileImage);
        MultipartBody.Part filePart = null;
        if(fileImage != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
            filePart = MultipartBody.Part.createFormData("dish_image", fileImage.getName(), requestBody);
        }
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileImage);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("dish_image", fileImage.getName(), requestBody);
        RequestBody dishIdBody = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(dishes.getId()));
        RequestBody organIdBody = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(organizations.getId()));
        RequestBody dishNameBody = RequestBody.create(MediaType.parse("text/plain"), dishes.getName());
        RequestBody dishPriceBody = RequestBody.create(MediaType.parse("text/plain"), Double.toString(dishes.getPrice()));
        RequestBody dishTypeBody = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(dishes.getDish_type()));

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.updateDishes(dishIdBody, organIdBody, dishNameBody, dishPriceBody, dishTypeBody, filePart).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateDishes()", "Response: API Response, Success. " + response.body().getResponse());
                    //Toast.makeText(CartActivity.this, "Response: " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                    Intent intentf2 = new Intent("finish_DishAdminActivity");
                    sendBroadcast(intentf2);

//                    Toast.makeText(UpdateDishAdminActivity.this, "Dish updated.", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(UpdateDishAdminActivity.this, MainActivityAdmin.class);
//                    //intent.putExtra("users", (Serializable) users);
//                    intent.putExtra("organizations", (Serializable) organizations);
//                    startActivity(intent);
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-updateDishes()", "Response: API Response, Unsuccessful.");
                    //Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-updateDishes()", "Response: API failed. " + t.getMessage());
                Toast.makeText(UpdateDishAdminActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
//        apiService.addDishes(organizations.getId()).enqueue(new Callback<Dishes>() {
//            @Override
//            public void onResponse(Call<Dishes> call, Response<Dishes> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<Dishes> call, Throwable t) {
//
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String path = mediaHandler.getPath(UpdateDishAdminActivity.this, selectedImage);
            fileImage = new File(path);
            Log.d("onActivityResult()", "Response: Image Path. " + path);
            //ivAddDish.setImageURI(selectedImage);
            Glide.with(UpdateDishAdminActivity.this)
                    //.load(baseURL + "/organization-picture/" + organizationsList.get(position).getOrganization_picture())
                    .load(selectedImage)
                    .placeholder(R.drawable.no_picture_icon)
                    .into(ivAddDishImage);
        }
    }

    class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            if (s.isEmpty()) return;
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[RM,.]", "");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);

            String formatted = NumberFormat.getCurrencyInstance(new Locale("ms", "MY")).format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }
}