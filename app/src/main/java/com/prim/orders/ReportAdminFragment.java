package com.prim.orders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.models.api_respond.API_Resp_getReport;
import com.prim.orders.utilities.ApiService;
import com.prim.orders.utilities.ApiSetup;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportAdminFragment extends Fragment {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    RecyclerView recyclerView;
    List<Organizations> organizationsList;
    adapter_admin_dishes_listDish adapter;
    SearchView searchView;
    Button buttonAddNewDish;

    private ArrayList<Dishes> dishesList;
    private Users users = new Users();
    private Organizations organizations = new Organizations();
    private ArrayList<API_Resp_getReport> reportList = new ArrayList<>();

    String filterDate = "All Time";

    Date dateCustom, dateStart, dateEnd;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report_admin, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            users = (Users) extras.getSerializable("users");
            organizations = (Organizations) extras.getSerializable("organizations");
            call_getReport_API(v);
        }
        //call_listDishesByShop_API(v);

        TextView tvTotal = v.findViewById(R.id.tvTotal);
        tvTotal.setText("Total Sale");
        AutoCompleteTextView ac_filter = v.findViewById(R.id.ac_filter);
        AutoCompleteTextView ac_filter_date = v.findViewById(R.id.ac_filter_date);


        LinearLayout ll_customDate = v.findViewById(R.id.ll_CustomDate);
        LinearLayout ll_customDateRange = v.findViewById(R.id.ll_CustomDate_Range);
        EditText et_date_custom = v.findViewById(R.id.et_CustomDate);
        EditText et_date_start = v.findViewById(R.id.et_StartDate);
        EditText et_date_end = v.findViewById(R.id.et_EndDate);
        Button btnSearch = v.findViewById(R.id.btn_Search);
        Button btnExport = v.findViewById(R.id.btn_Export);

        ArrayList<String> item = new ArrayList<>();
        ac_filter.setText("Sale");

        item.add("Sale");
        item.add("Profit");

        ArrayAdapter<String> adapterItem = new ArrayAdapter<>(getActivity(), R.layout.list_item, item);
        ac_filter.setAdapter(adapterItem);

        ArrayList<String> itemDate = new ArrayList<>();

        itemDate.add("All Time");
        itemDate.add("Custom Date");
        itemDate.add("Custom Date Range");

        ll_customDate.setVisibility(View.GONE);
        ll_customDateRange.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);

        ArrayAdapter<String> adapterItemDate = new ArrayAdapter<>(getActivity(), R.layout.list_item, itemDate);
        ac_filter_date.setAdapter(adapterItemDate);

        ac_filter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvTotal.setText("Total " + adapterView.getItemAtPosition(i).toString());
                getChart(v, adapterView.getItemAtPosition(i).toString());

                btnSearch.setVisibility(View.GONE);
                //btnExport.setVisibility(View.GONE);
            }
        });

        ac_filter_date.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_date_custom.setText(null);
                et_date_start.setText(null);
                et_date_end.setText(null);
                btnSearch.setVisibility(View.GONE);
                dateCustom = null;
                dateStart = null;
                dateEnd = null;
                filterDate = adapterView.getItemAtPosition(i).toString();

                if(adapterView.getItemAtPosition(i).toString() == "All Time") {
                    TransitionManager.beginDelayedTransition(ll_customDate, new AutoTransition());
                    TransitionManager.beginDelayedTransition(ll_customDateRange, new AutoTransition());
                    ll_customDate.setVisibility(View.GONE);
                    ll_customDateRange.setVisibility(View.GONE);
                    btnExport.setVisibility(View.VISIBLE);
                    dateCustom = null;
                    dateStart = null;
                    dateEnd = null;
                    call_getReport_API(v);

                } else if(adapterView.getItemAtPosition(i).toString() == "Custom Date") {
                    TransitionManager.beginDelayedTransition(ll_customDate, new AutoTransition());
                    TransitionManager.beginDelayedTransition(ll_customDateRange, new AutoTransition());
                    ll_customDate.setVisibility(View.VISIBLE);
                    ll_customDateRange.setVisibility(View.GONE);
                    btnExport.setVisibility(View.GONE);
                    dateCustom = null;
//                    dateStart = null;
//                    dateEnd = null;
                } else {
                    TransitionManager.beginDelayedTransition(ll_customDate, new AutoTransition());
                    TransitionManager.beginDelayedTransition(ll_customDateRange, new AutoTransition());
                    ll_customDate.setVisibility(View.GONE);
                    ll_customDateRange.setVisibility(View.VISIBLE);
                    btnExport.setVisibility(View.GONE);
//                    dateCustom = null;
                    dateStart = null;
                    dateEnd = null;
                }
            }
        });

        et_date_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.

                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        dateCustom = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTime();
                        et_date_custom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                },year, month, day);

                datePickerDialog.show();
                btnSearch.setVisibility(View.VISIBLE);
            }
        });

        et_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.

                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        dateStart = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTime();
                        et_date_start.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                },year, month, day);

                datePickerDialog.show();
            }
        });

        et_date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.

                        final Calendar c = Calendar.getInstance();

                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        if(dateStart != null) {
                            dateEnd = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTime();
                            et_date_end.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            btnSearch.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getActivity(), "Please select Start Date", Toast.LENGTH_SHORT).show();
                        }

                    }
                },year, month, day);

                datePickerDialog.show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_getReport_API(v);
                btnExport.setVisibility(View.VISIBLE);
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExcel();
            }
        });

        return v;
    }

    public void call_getReport_API(View v) {
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
        apiService.getReport(organizations.getId(), dateCustom, dateStart, dateEnd).enqueue(new Callback<ArrayList<API_Resp_getReport>>() {
            @Override
            public void onResponse(Call<ArrayList<API_Resp_getReport>> call, Response<ArrayList<API_Resp_getReport>> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-getReport()", "Response: API Response, Success. ");
                    reportList = response.body();
                    getChart(v, "Sale");

                } else {
                    loadingDialog.dismissDialog();
                    Log.d("OrderS_API-getReport()", "Response: API Response, Unsuccessful.");
                    //Toast.makeText(ShopActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<API_Resp_getReport>> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.d("OrderS_API-getReport()", "Response: API failed. ");
                Toast.makeText(getActivity(), "Response: Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getChart(View v, String filter) {
        PieChart pieChart = v.findViewById(R.id.pieChart);

        // Sample data for the pie chart
        ArrayList<PieEntry> entries = new ArrayList<>();

        if(filter.equals("Sale")) {
            for(API_Resp_getReport rep : reportList) {
                entries.add(new PieEntry(rep.getQuantity(), rep.getDishName()));
            }
        } else {
            for(API_Resp_getReport rep : reportList) {
                entries.add(new PieEntry((float) rep.getProfit(), rep.getDishName()));
            }
        }

//        entries.add(new PieEntry(30f, "Label 1"));
//        entries.add(new PieEntry(50f, "Label 2"));
//        entries.add(new PieEntry(20f, "Label 3"));

        // Configure the dataset
        PieDataSet dataSet_Quantity = new PieDataSet(entries, "Total " + filter);
        dataSet_Quantity.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet_Quantity.setValueTextSize(16f);

        // Configure the pie chart
        PieData pieData = new PieData(dataSet_Quantity);
        if(filter.equals("Sale")) {
            pieData.setValueFormatter(new IntValueFormatter());
        }
        pieChart.setData(pieData);
        pieChart.getDescription().setText("Total " + filter + " By Dishes");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(30f);

        pieChart.invalidate();
    }

    private void createExcel() {
        HSSFWorkbook workbook = new HSSFWorkbook();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String sheetName = organizations.getNama() + "_Report";
        if(filterDate == "All Time") {
            sheetName = organizations.getNama() + "_Report_All_Time";
        } else if(filterDate == "Custom Date") {
            sheetName = organizations.getNama() + "_Report_At_" + dateFormat.format(dateCustom);
        } else if(filterDate == "Custom Date Range") {
            sheetName = organizations.getNama() + "_Report_Between_" + dateFormat.format(dateStart) + "_to_" + dateFormat.format(dateEnd);
        }
        Sheet sheet = workbook.createSheet(sheetName);

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("No.");
        headerRow.createCell(1).setCellValue("Dish Name");
        headerRow.createCell(2).setCellValue("Price(RM)");
        headerRow.createCell(3).setCellValue("Quantity");
        headerRow.createCell(4).setCellValue("Total Price(RM)");

        int rowNum = 1;
        for (int i = 0; i < reportList.size(); i++) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(reportList.get(i).getDishName());
            row.createCell(2).setCellValue(reportList.get(i).getProfit() / reportList.get(i).getQuantity());
            row.createCell(3).setCellValue(reportList.get(i).getQuantity());
            row.createCell(4).setCellValue(reportList.get(i).getProfit());
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

            String baseFileName = organizations.getNama() + "_Report";
            if(filterDate == "All Time") {
                baseFileName = organizations.getNama() + "_Report_All_Time";
            } else if(filterDate == "Custom Date") {
                baseFileName = organizations.getNama() + "_Report_At_" + dateFormat.format(dateCustom);
            } else if(filterDate == "Custom Date Range") {
                baseFileName = organizations.getNama() + "_Report_Between_" + dateFormat.format(dateStart) + "_to_" + dateFormat.format(dateEnd);
            }

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
            Toast.makeText(getActivity(), "Excel file successfully downloaded", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ExcelUtils", "Error creating Excel file: " + e.getMessage());
            Toast.makeText(getActivity(), "Excel file download failed", Toast.LENGTH_SHORT).show();
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

class IntValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value);
    }
}