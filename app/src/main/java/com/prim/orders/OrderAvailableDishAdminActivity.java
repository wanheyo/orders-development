package com.prim.orders;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.prim.orders.models.api_respond.API_Resp;
import com.prim.orders.models.api_respond.API_Resp_listOADAdmin;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.pois.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderAvailableDishAdminActivity extends AppCompatActivity {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    RecyclerView recyclerView;
    adapter_admin_dishes_listOAD adapter;
    adapter_empty adapterEmpty;
    androidx.appcompat.widget.SearchView searchView;

    private Organizations organizations;
    private Order_Available orderAvailable = new Order_Available();
    private Dishes dishes = new Dishes();
    private ArrayList<Order_Available_Dish> orderAvailableDishList = new ArrayList<>();
    private ArrayList<Users> usersList = new ArrayList<>();
    private ArrayList<Order_Cart> orderCartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_available_dish_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAdminOAD);
        setSupportActionBar(toolbar);
//        MenuItem cart = (MenuItem) toolbar.findViewById(R.id.cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                cart.setIcon(ContextCompat.getDrawable(DishActivity.this, R.drawable.round_shopping_cart_checkout_24));
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            organizations = (Organizations) extras.getSerializable("organizations");
            dishes = (Dishes) extras.getSerializable("dishes");
            orderAvailable = (Order_Available) extras.getSerializable("order_available");

            SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
            SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
            String date = formatterDate.format(orderAvailable.getDelivery_date());
            String date2 = formatterDate2.format(orderAvailable.getDelivery_date());
            SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");
            String time = formatterTime.format(orderAvailable.getDelivery_date());

            getSupportActionBar().setTitle(date2 + " | " + time);
        }

        call_listOADAdmin_API();
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_OrderAvailableDishAdminActivity")) {
                    finish();
                    // DO WHATEVER YOU WANT.
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_OrderAvailableDishAdminActivity"), RECEIVER_EXPORTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.Edit) {
//            Intent intentf = new Intent("finish_DishActivity");
//            sendBroadcast(intentf);
//            Intent intentf2 = new Intent("finish_ShopActivity");
//            sendBroadcast(intentf2);
//            finish();
            Intent intent = new Intent(OrderAvailableDishAdminActivity.this, UpdateOrderAvailableAdminActivity.class);
            intent.putExtra("organizations", (Serializable) organizations);
            intent.putExtra("dishes", (Serializable) dishes);
            intent.putExtra("order_available", (Serializable) orderAvailable);
            startActivity(intent);
        } else if(id == R.id.Delete) {
            if(orderAvailable.getTotalOrderDishAvailable() > 0) {

                Toast.makeText(OrderAvailableDishAdminActivity.this, "Slot must not have any order", Toast.LENGTH_SHORT).show();
            } else {
                call_deleteOrderAvailable_API();
            }
        } else if(id == R.id.ExcelReport) {
            createExcel();
        }
        return true;
    }

    public void call_listOADAdmin_API() {
        LoadingDialog loadingDialog = new LoadingDialog(OrderAvailableDishAdminActivity.this);
        loadingDialog.startDialog();
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
                    usersList = response.body().getUsersList();
                    orderCartList = response.body().getOrderCartList();

                    getListOAD();
//                    Intent intent = new Intent(OrderAvailableDishAdminActivity.this, MainActivityAdmin.class);
//                    //intent.putExtra("users", (Serializable) users);
//                    intent.putExtra("organizations", (Serializable) organizations);
//                    startActivity(intent);
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-listOADAdmin()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp_listOADAdmin> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-listOADAdmin()", "Response: API failed. " + t.getMessage());
                Toast.makeText(OrderAvailableDishAdminActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getListOAD() {
        recyclerView = findViewById(R.id.rv_OAD);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderAvailableDishAdminActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        searchView = findViewById(R.id.SearchViewOrderID);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

        //Collections.sort(orderAvailableList);

//        Date currentTime = Calendar.getInstance().getTime();
//        ArrayList<Order_Available> orderAvailableListAfterOption = new ArrayList<>();
//        for (Order_Available oa : orderAvailableList) {
//            if(option == 0) {
//                if(oa.getDelivery_date().getTime() >= currentTime.getTime()) {
//                    orderAvailableListAfterOption.add(oa);
//                }
//            } else {
//                if(oa.getDelivery_date().getTime() < currentTime.getTime()) {
//                    orderAvailableListAfterOption.add(oa);
//                }
//            }
//        }

        if(orderAvailableDishList.size() <= 0) {
            adapterEmpty = new adapter_empty(OrderAvailableDishAdminActivity.this, "Orders", 3);
            recyclerView.setAdapter(adapterEmpty);
        } else {
            adapter = new adapter_admin_dishes_listOAD(OrderAvailableDishAdminActivity.this, orderAvailableDishList, usersList, orderCartList, orderAvailable);
            recyclerView.setAdapter(adapter);
        }
    }

    public void call_deleteOrderAvailable_API() {
        LoadingDialog loadingDialog = new LoadingDialog(OrderAvailableDishAdminActivity.this);
        loadingDialog.startDialog();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.deleteOrderAvailable(orderAvailable.getId()).enqueue(new Callback<API_Resp>() {
            @Override
            public void onResponse(Call<API_Resp> call, Response<API_Resp> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-deleteOrderAvailable()", "Response: API Response, Success. " + response.body().getResponse());

//                    Intent intentf = new Intent("finish_OrderAvailableDishAdminActivity");
//                    sendBroadcast(intentf);
                    Intent intentf2 = new Intent("finish_DishAdminActivity");
                    sendBroadcast(intentf2);

//                    Intent intent = new Intent(OrderAvailableDishAdminActivity.this, MainActivityAdmin.class);
//                    //intent.putExtra("users", (Serializable) users);
//                    intent.putExtra("organizations", (Serializable) organizations);
//                    intent.putExtra("order_available", (Serializable) orderAvailable);
//                    intent.putExtra("dishes", (Serializable) dishes);
//                    startActivity(intent);

                    Toast.makeText(OrderAvailableDishAdminActivity.this, "Slot successfully deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-deleteOrderAvailable()", "Response: API Response, Unsuccessful.");
                }
            }

            @Override
            public void onFailure(Call<API_Resp> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-addOrderAvailable()", "Response: API failed. " + t.getMessage());
                Toast.makeText(OrderAvailableDishAdminActivity.this, "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchList(String text) {
        ArrayList<Order_Available_Dish> orderAvailableDishSearchList = new ArrayList<>();
        for(Order_Available_Dish oad : orderAvailableDishList) {
            if(String.valueOf(oad.getId()).contains(text.toLowerCase())) {
                orderAvailableDishSearchList.add(oad);
            }
        }

        if(orderAvailableDishSearchList.isEmpty()) {
            Toast.makeText(this, "Not found...", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(orderAvailableDishSearchList);
        }
    }

    private void createExcel() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Order Data " + dishes.getName());

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("No.");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Total Price(RM)");
        headerRow.createCell(4).setCellValue("Status");

        int rowNum = 1;
        for (int i = 0; i < orderAvailableDishList.size(); i++) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(usersList.get(i).getName());
            row.createCell(2).setCellValue(orderAvailableDishList.get(i).getQuantity());
            row.createCell(3).setCellValue(orderAvailableDishList.get(i).getTotalprice());
            row.createCell(4).setCellValue(orderAvailableDishList.get(i).getDelivery_status());
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            int maxColumnWidth = getMaxColumnWidth(sheet, i);
            sheet.setColumnWidth(i, maxColumnWidth);
        }
//        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
//            sheet.autoSizeColumn(i);
//        }

        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String baseFileName = dishes.getName() + "_slot_" + orderAvailable.getId() + "_order";
            String fileExtension = ".xls";
            int fileCount = 1;

            // Check if the file already exists, and create a new name if needed
            File file;
            do {
                String fileName = (fileCount == 1) ? baseFileName + fileExtension : baseFileName + " (" + fileCount + ")" + fileExtension;
                file = new File(downloadFolder, fileName);
                fileCount++;
            } while (file.exists());

            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            Log.d("ExcelUtils", "Excel file created successfully: " + file.getAbsolutePath());
            Toast.makeText(this, "Excel file successfully downloaded", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ExcelUtils", "Error creating Excel file: " + e.getMessage());
            Toast.makeText(this, "Excel file download failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to get the maximum content length in a column
    private int getMaxColumnWidth(Sheet sheet, int columnIndex) {
        int maxColumnWidth = 0;
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    int cellContentLength = cell.toString().length();
                    maxColumnWidth = Math.max(maxColumnWidth, cellContentLength);
                }
            }
        }
        return maxColumnWidth * 256; // Multiply by 256 to convert to Excel units
    }
}