package com.prim.orders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the notification here (e.g., show a notification)
        Log.d("ReminderReceiver", "Received reminder notification");
        // Add code to show notification
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String deviceToken = extras.getString("deviceToken", "");
            String message = extras.getString("message", "");
//            Dishes dishes = (Dishes) extras.getSerializable("dishes");
//            Order_Available orderAvailable = (Order_Available) extras.getSerializable("order_available");
            //String message = extras.getString("message", "");

            // Handle the notification here (e.g., show a notification)
            Log.d("ReminderReceiver", "Received reminder notification for device: " + deviceToken + ", message: ");
            // Add code to show notification
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();

            SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
            SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");

//            String date = formatterDate2.format(orderAvailable.getDelivery_date());
//            String time = formatterTime.format(orderAvailable.getDelivery_date());
            try {
                notificationObj.put("title", "Your Order Reminder");
//                notificationObj.put("body", "Reminder to pickup your " + dishes.getName() + " at " + orderAvailable.getDelivery_address() + " on " + date + ", " + time);
                notificationObj.put("body", message);
                JSONObject dataObj = new JSONObject();
                dataObj.put("user_name", "test");
                jsonObject.put("notification", notificationObj);
                jsonObject.put("data", dataObj);
                jsonObject.put("to", deviceToken);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", "Bearer AAAA0iek96I:APA91bFcrD0aiRmZSMTrR-CMi6jbyFimEoiTMDPCl9HwCInnTcVl2NEq9zO3WtyODaacGhKHoKJRLAu6t2Tyougf9Z6Y7PJZp80jY4NKxl2ayHcp83fXbDhkVhgiBN4zI4_5PttfvGUG")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {

                }
            });
        }
    }
}

