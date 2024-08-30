package com.prim.orders.notificationhandler;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.prim.orders.ReminderReceiver;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MyJobIntentService extends JobIntentService {

    private static final String ACTION_SCHEDULE_REMINDER = "com.prim.orders.action.SCHEDULE_REMINDER";
    private static final String ACTION_CANCEL_REMINDER = "com.prim.orders.action.CANCEL_REMINDER";
    //private static int jobCounter = 0;

    public MyJobIntentService() {
        super();
    }

    private static String generateJobID() {
        return UUID.randomUUID().toString();
    }

    public static void scheduleReminder(Context context, long createdTimeMillis, String deviceToken, ArrayList<Order_Available> orderAvailableList, ArrayList<Dishes> dishesList) {
        Intent intent = new Intent(context, MyJobIntentService.class);
        intent.setAction(ACTION_SCHEDULE_REMINDER);
        intent.putExtra("createdTimeMillis", createdTimeMillis);
        intent.putExtra("deviceToken", deviceToken);
        intent.putExtra("orderAvailableList", (Serializable) orderAvailableList);
        intent.putExtra("dishesList", (Serializable) dishesList);

        // Automatically generate a unique JOB_ID
//        int jobID = generateJobID().hashCode();
        int jobID = UniqueIdRegistry.generateScheduleId();

        PendingIntent pendingIntent = PendingIntent.getService(context, jobID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        enqueueWork(context, MyJobIntentService.class, jobID, intent);
    }

    public static void cancelReminderForUser(Context context, int userId) {
        Intent intent = new Intent(context, MyJobIntentService.class);
        intent.setAction(ACTION_CANCEL_REMINDER);
        intent.putExtra("userId", userId);

        // Automatically generate a unique JOB_ID for cancellation
//        int jobID = generateJobID().hashCode();
        int jobID = UniqueIdRegistry.generateCancelId();

        // Use the same flags as in scheduleReminder
        PendingIntent pendingIntent = PendingIntent.getService(context, jobID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Cancel existing reminders
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


//    private static AtomicInteger jobCounter = new AtomicInteger(0);
//
//    private static int generateJobID() {
//        // Using AtomicInteger for thread safety
//        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE) + jobCounter.getAndIncrement();
//    }



    @Override
    protected void onHandleWork(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SCHEDULE_REMINDER.equals(action)) {
                handleScheduleReminder(
                        intent.getLongExtra("createdTimeMillis", 0),
                        intent.getStringExtra("deviceToken"),
                        (ArrayList<Order_Available>) intent.getSerializableExtra("orderAvailableList"),
                        (ArrayList<Dishes>) intent.getSerializableExtra("dishesList")
                );
            }
        }
    }

    private void handleScheduleReminder(long createdTimeMillis, String deviceToken, ArrayList<Order_Available> orderAvailableList, ArrayList<Dishes> dishesList) {
        // Your background work here
        // You can use createdTimeMillis, deviceToken, orderAvailableList, dishesList to schedule reminders

        for(Order_Available oa : orderAvailableList) {
            for(Dishes d : dishesList) {
                if(d.getId() == oa.getDish_id()) {
                    // Schedule 10 and 20 second after current time
                    scheduleNotification(this, createdTimeMillis + 10 * 1000, deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());

                    // Schedule 1 hour before pickup time
                    scheduleNotification(this, oa.getDelivery_date().getTime() - 60 * 60 * 1000, deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());

                    // Schedule at pickup time
                    scheduleNotification(this, oa.getDelivery_date().getTime(), deviceToken, d.getName(), oa.getDelivery_address(), oa.getDelivery_date());
                }
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Context context, long triggerTimeMillis, String deviceToken, String dishname, String deliveryPlace, Date deliveryDate) {
        // Implement your notification scheduling logic here
        // You can use AlarmManager, WorkManager, or any other suitable method
        // For example, you can use AlarmManager as in your original code
        // ...

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);

        // You can pass additional data to the BroadcastReceiver using intent extras if needed

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");

        String date = formatterDate2.format(deliveryDate);
        String time = formatterTime.format(deliveryDate);

        intent.putExtra("deviceToken", deviceToken);
        intent.putExtra("message", "Reminder to pick up your " + dishname + " at " + deliveryPlace + " on " + date + ", " + time);
        int uniqueRequestCode = (int) triggerTimeMillis;

        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Add FLAG_IMMUTABLE to the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
    }
}
