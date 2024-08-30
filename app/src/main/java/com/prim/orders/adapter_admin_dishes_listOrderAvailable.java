package com.prim.orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Organizations;
import com.prim.orders.utilities.ApiSetup;
import com.prim.orders.utilities.IconSetup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class adapter_admin_dishes_listOrderAvailable extends RecyclerView.Adapter<Admin_ListOrderAvailableViewHolder> {
    private Context context;
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private IconSetup iconSetup;

    private Organizations organizations;
    private Dishes dishes;
    private ArrayList<Order_Available> orderAvailableList;


    public adapter_admin_dishes_listOrderAvailable(Context context, ArrayList<Order_Available> orderAvailableList, Dishes dishes) {
        this.context = context;
        this.orderAvailableList = orderAvailableList;
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public Admin_ListOrderAvailableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_admin_dishes_listorderavailable, parent, false);
        return new Admin_ListOrderAvailableViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_ListOrderAvailableViewHolder holder, int position) {
        //Log.d("Admin_ListOrderAvailableViewHolder", "Response: " + orderAvailableList.size() + orderAvailableList.get(position).getId());
        int margin = 30;

        Drawable icon_date = context.getResources().getDrawable(R.drawable.round_calendar_today_24);
        icon_date = iconSetup.addMarginToDrawable(icon_date, margin);
        icon_date.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom
        holder.tv_deliveryDate.setCompoundDrawables(icon_date, null, null, null);

        Drawable icon_time = context.getResources().getDrawable(R.drawable.round_access_time_24);
        icon_time = iconSetup.addMarginToDrawable(icon_time, margin);
        icon_time.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom
        holder.tv_deliveryTime.setCompoundDrawables(icon_time, null, null, null);

        Drawable icon_location = context.getResources().getDrawable(R.drawable.baseline_location_on_24);
        icon_location = iconSetup.addMarginToDrawable(icon_location, margin);
        icon_location.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom
        holder.tv_deliveryAddress.setCompoundDrawables(icon_location, null, null, null);

        Drawable icon_quantity = context.getResources().getDrawable(R.drawable.baseline_local_dining_24);
        icon_quantity = iconSetup.addMarginToDrawable(icon_quantity, margin);
        icon_quantity.setBounds(0, 0, 50 + margin, 50); // Left, Top, Right, Bottom
        holder.tv_quantityLeft.setCompoundDrawables(icon_quantity, null, null, null);

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatterDate.format(orderAvailableList.get(position).getDelivery_date());
        String date2 = formatterDate2.format(orderAvailableList.get(position).getDelivery_date());
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");
        String time = formatterTime.format(orderAvailableList.get(position).getDelivery_date());

        holder.tv_dateTime.setText(date2 + " | " + time);
        holder.tv_orderQuantity.setText(String.valueOf(orderAvailableList.get(position).getTotalOrderDishAvailable()));
        holder.tv_deliveryAddress.setText(Html.fromHtml("<b>Delivery Location : </b>" + orderAvailableList.get(position).getDelivery_address()));
        holder.tv_deliveryDate.setText(Html.fromHtml("<b>Delivery Date : </b>" + date));
        holder.tv_deliveryTime.setText(Html.fromHtml("<b>Delivery Time : </b>" + time));
        holder.tv_quantityLeft.setText(Html.fromHtml("<b>Quantity Left : </b>" + orderAvailableList.get(position).getQuantity() + " pcs"));

        String openDate = formatterDate2.format(orderAvailableList.get(position).getOpen_date());
        String openTime = formatterTime.format(orderAvailableList.get(position).getOpen_date());
        String closeDate = formatterDate2.format(orderAvailableList.get(position).getClose_date());
        String closeTime = formatterTime.format(orderAvailableList.get(position).getClose_date());

        holder.tv_openCloseDateTime.setText(Html.fromHtml("Open order from <b>" + openDate + " (" + openTime + ")</b> to <b>" + closeDate + " (" + closeTime + ")</b>. Total dishes left : <b>" + orderAvailableList.get(position).getQuantity() + "</b> pcs."));

        Date currentTime = Calendar.getInstance().getTime();
        if(orderAvailableList.get(position).getClose_date().getTime() <= currentTime.getTime()) {
            holder.tv_orderStatus.setText("Order Slot Closed");
        } else if(orderAvailableList.get(position).getQuantity() <= 0) {
            holder.tv_orderStatus.setText("Order Slot Closed (Out of stock)");
        } else {
            holder.tv_orderStatus.setText("Order Slot Open");
        }

        holder.cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderAvailableDishAdminActivity.class);
                //intent.putExtra("organizations", (Serializable) organizations);

                intent.putExtra("order_available", (Serializable) orderAvailableList.get(holder.getAdapterPosition()));
                intent.putExtra("dishes", (Serializable) dishes);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderAvailableList.size();
    }
}

class Admin_ListOrderAvailableViewHolder extends RecyclerView.ViewHolder {

    CardView cv_main;
    TextView tv_dateTime, tv_deliveryAddress, tv_deliveryDate, tv_deliveryTime, tv_quantityLeft, tv_openCloseDateTime, tv_orderQuantity, tv_orderStatus;


    public Admin_ListOrderAvailableViewHolder(@NonNull View v) {
        super(v);

        cv_main = v.findViewById(R.id.cvOrderAvailableDish);
        tv_dateTime = v.findViewById(R.id.textViewDateTime);
        tv_deliveryAddress = v.findViewById(R.id.tvDeliveryLocation);
        tv_deliveryDate = v.findViewById(R.id.tvDeliveryDate);
        tv_deliveryTime = v.findViewById(R.id.tvDeliveryTime);
        tv_quantityLeft = v.findViewById(R.id.tvQuantityLeft);
        tv_openCloseDateTime = v.findViewById(R.id.tvReminder);
        tv_orderQuantity = v.findViewById(R.id.tvOrderQuantity);
        tv_orderStatus = v.findViewById(R.id.tvOrderStatus);
    }
}
