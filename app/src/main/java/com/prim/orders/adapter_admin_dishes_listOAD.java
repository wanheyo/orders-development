package com.prim.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class adapter_admin_dishes_listOAD extends RecyclerView.Adapter<Admin_ListOADViewHolder> {
    private Context context;
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private ArrayList<Users> usersList;
    private ArrayList<Order_Cart> orderCartList;
    private Order_Available orderAvailable;

    public void setSearchList(ArrayList<Order_Available_Dish> orderAvailableDishList) {
        this.orderAvailableDishList = orderAvailableDishList;
        notifyDataSetChanged();
    }

    public adapter_admin_dishes_listOAD(Context context, ArrayList<Order_Available_Dish> orderAvailableDishList, ArrayList<Users> usersList, ArrayList<Order_Cart> orderCartList, Order_Available orderAvailable) {
        this.context = context;
        this.orderAvailableDishList = orderAvailableDishList;
        this.usersList = usersList;
        this.orderCartList = orderCartList;
        this.orderAvailable = orderAvailable;
    }

    @NonNull
    @Override
    public Admin_ListOADViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_admin_oa_listoad, parent, false);
        return new Admin_ListOADViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_ListOADViewHolder holder, int position) {

        holder.tv_num.setText(String.valueOf(position + 1));
        holder.tv_name.setText(usersList.get(position).getName());
        holder.tv_quantity.setText(String.valueOf(orderAvailableDishList.get(position).getQuantity()));
        holder.tv_price.setText(String.format("%.2f", orderAvailableDishList.get(position).getTotalprice()));
        holder.tv_status.setText(orderAvailableDishList.get(position).getDelivery_status());

        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateOrderStatusActivity.class);
                intent.putExtra("users", (Serializable) usersList.get(holder.getAdapterPosition()));
                intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList.get(holder.getAdapterPosition()));
                intent.putExtra("order_cart", (Serializable) orderCartList.get(holder.getAdapterPosition()));
                intent.putExtra("order_available", (Serializable) orderAvailable);
                context.startActivity(intent);
                //((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderAvailableDishList.size();
    }
}

class Admin_ListOADViewHolder extends RecyclerView.ViewHolder {

    LinearLayout ll_main;
    TextView tv_num, tv_name, tv_quantity, tv_price, tv_status;


    public Admin_ListOADViewHolder(@NonNull View v) {
        super(v);

        ll_main = v.findViewById(R.id.ll_main);
        tv_num = v.findViewById(R.id.tvNum);
        tv_name = v.findViewById(R.id.tvName);
        tv_quantity = v.findViewById(R.id.tvQuantity);
        tv_price = v.findViewById(R.id.tvPrice);
        tv_status = v.findViewById(R.id.tvOadStatus);
    }
}
