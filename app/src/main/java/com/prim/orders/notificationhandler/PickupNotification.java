package com.prim.orders.notificationhandler;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.prim.orders.ReminderReceiver;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Users;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PickupNotification {
    public static void scheduleReminder(Context context, long createdTimeMillis, String deviceToken, ArrayList<Order_Available> orderAvailableList, ArrayList<Dishes> dishesList) {

        Log.d("scheduleReminder", "Response: API Response, Success. " + deviceToken);
//        MyJobIntentService.scheduleReminder(context, createdTimeMillis, deviceToken, orderAvailableList, dishesList);

        for(Order_Available oa : orderAvailableList) {
            for(Dishes d : dishesList) {
                if(d.getId() == oa.getDish_id()) {
                    // Schedule 10 and 20 second after current time
                    scheduleNotification(context, createdTimeMillis + 10 * 1000, deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());
                    //scheduleNotification(context, createdTimeMillis + 20 * 1000, deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());

                    // Schedule 1 hour before pickup time
                    scheduleNotification(context, oa.getDelivery_date().getTime() - 60 * 60 * 1000, deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());

                    // Schedule at pickup time
                    scheduleNotification(context, oa.getDelivery_date().getTime(), deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());
                }
            }
            //scheduleNotification(context, pickupTimeMillis + 10 * 1000, deviceToken);
//            // Schedule 1 hour before pickup time
//            scheduleNotification(context, pickupTimeMillis - 60 * 60 * 1000, deviceToken);
//
//            // Schedule at pickup time
//            scheduleNotification(context, pickupTimeMillis, deviceToken);
        }

//        scheduleNotification(context, createdTimeMillis + 60 * 1000, deviceToken, orderAvailable, dishes);
//
//        // Schedule 1 hour before pickup time
//        scheduleNotification(context, pickupTimeMillis - 60 * 60 * 1000, deviceToken, orderAvailable, dishes);
//
//        // Schedule at pickup time
//        scheduleNotification(context, pickupTimeMillis, deviceToken, orderAvailable, dishes);
    }

    public static void cancelPendingReminders(Context context, Users users) {
        // Implement logic to cancel pending reminders based on the current user
        // You may need to use the same logic you used to set reminders
        // ...

        // For example, you can use MyJobIntentService.cancelReminderForUser(users.getId());
        MyJobIntentService.cancelReminderForUser(context, users.getId());
    }

    @SuppressLint("ScheduleExactAlarm")
    private static void scheduleNotification(Context context, long triggerTimeMillis, String deviceToken, String dishname, String deliveryPlace, Date deliveryDate) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);

        // You can pass additional data to the BroadcastReceiver using intent extras if needed

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");

        String date = formatterDate2.format(deliveryDate);
        String time = formatterTime.format(deliveryDate);

        intent.putExtra("deviceToken", deviceToken);
        intent.putExtra("message", "Reminder to pickup your " + dishname + " at " + deliveryPlace + " on " + date + ", " + time);
        int uniqueRequestCode = (int) triggerTimeMillis;
//        intent.putExtra("order_available", (Serializable) orderAvailable);
//        intent.putExtra("dishes", (Serializable) dishes);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
    }
}
