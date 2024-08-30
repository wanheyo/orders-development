package com.prim.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class adapter_admin_dishes_listDish extends RecyclerView.Adapter<Admin_ListDishViewHolder> {
    private Context context;
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private Users users;
    private Organizations organizations;
    private Dishes dishes;
    private Order_Cart orderCart;
    private ArrayList<Order_Available> orderAvailableList;
    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private ArrayList<Dishes> dishesList;
    private ArrayList<Dishes> dishesListAll;
    private ArrayList<Dishes> dishCount;

    public void setSearchList(ArrayList<Dishes> dishes) {
        this.dishesList = dishes;
        notifyDataSetChanged();
    }

    public adapter_admin_dishes_listDish(Context context, ArrayList<Dishes> dishesList, Users users, Organizations organizations) {
        this.context = context;
        this.dishesList = dishesList;
        this.users = users;
        this.organizations = organizations;
//        this.orderCart = orderCart;
//        this.orderAvailableList = orderAvailableList;
//        this.orderAvailableDishList = orderAvailableDishList;
//        this.dishesListAll = dishesListAll;
//        this.dishCount = dishCount;

        //Log.d("dishesListAll", "Response: dishesListAll size = " + dishesListAll.size() + ", dishCount size = " + dishCount.size());
    }

    @NonNull
    @Override
    public Admin_ListDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_admin_dishes_listdish, parent, false);
        return new Admin_ListDishViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_ListDishViewHolder holder, int position) {
        if(context != null) {
            Glide.with(context)
                    .load(baseURL + "/dish-image/" + dishesList.get(position).getDish_image())
                    .placeholder(R.drawable.no_picture_icon)
                    //.load(dishesList.get(position).getDish_image())
                    .into(holder.iv_dishimage);
        }
        holder.tv_dishname.setText(dishesList.get(position).getName());
        holder.tv_dishprice.setText(String.format("RM %.2f", dishesList.get(position).getPrice()));

        holder.tv_orderAvailableCount.setText(Html.fromHtml("Current Order Slot : <b>" + dishesList.get(position).getTotalOrderAvailable() + "</b>"));

        holder.cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DishAdminActivity.class);
                intent.putExtra("users", (Serializable) users);
                intent.putExtra("organizations", (Serializable) organizations);
                dishes = new Dishes();
                dishes = dishesList.get(holder.getAdapterPosition());
                intent.putExtra("dishes", (Serializable) dishes);
                context.startActivity(intent);

                //((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishesList.size();
    }
}

class Admin_ListDishViewHolder extends RecyclerView.ViewHolder{

    CardView cv_main, cv_image, cv_availability;
    LinearLayout ll_noti;
    ImageView iv_dishimage;
    TextView tv_dishname, tv_dishprice, tv_orderAvailableCount, tv_upcomingOrderCount;


    public Admin_ListDishViewHolder(@NonNull View v) {
        super(v);

        cv_main = v.findViewById(R.id.cvOrderAvailableDish);
        iv_dishimage = v.findViewById(R.id.ivDishImage_Admin);
        tv_dishname = v.findViewById(R.id.tvDishName_Admin);
        tv_dishprice = v.findViewById(R.id.tvDishPrice_Admin);
        tv_orderAvailableCount = v.findViewById(R.id.tvOrderAvailable_Admin);
        ll_noti = v.findViewById(R.id.ll_noti);
        tv_upcomingOrderCount = v.findViewById(R.id.tvUpcomingOrder);
    }
}
