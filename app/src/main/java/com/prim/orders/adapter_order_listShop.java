package com.prim.orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiSetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class adapter_order_listShop extends RecyclerView.Adapter<ListShopViewHolder> {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Context context;
    private Users users;
    private Organizations organizations;
    private List<Organizations> organizationsList;
    private ArrayList<Order_Available_Dish> orderAvailableDishList = new ArrayList<>();

    public void setSearchList(List<Organizations> organizations) {
        this.organizationsList = organizations;
        notifyDataSetChanged();
    }

    public adapter_order_listShop(Context context, List<Organizations> organizationsList, Users users) {
        this.context = context;
        this.organizationsList = organizationsList;
        this.users = users;
    }

    @NonNull
    @Override
    public ListShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_order_listshop, parent, false);
        return new ListShopViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListShopViewHolder holder, int position) {
        if(context != null) {
            Glide.with(context)
                    .load(baseURL + "/organization-picture/" + organizationsList.get(position).getOrganization_picture())
                    .placeholder(R.drawable.no_picture_icon)
                    //.load(organizationsList.get(position).getOrganization_picture())
                    .into(holder.iv_shoplogo);
        }
        holder.tv_shopname.setText(organizationsList.get(position).getNama());
        holder.tv_shopaddress.setText(organizationsList.get(position).getAddress());
//        holder.tv_dishavailabilty.setText(organizationsList.get(position).get);

        holder.cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShopActivity.class);
//                intent.putExtra("user_id", users.getId());
//                intent.putExtra("user_name", users.getName());
//                intent.putExtra("shop_id", organizationsList.get(holder.getAdapterPosition()).getId());
//                intent.putExtra("shop_name", organizationsList.get(holder.getAdapterPosition()).getNama());
//                intent.putExtra("shop_address", organizationsList.get(holder.getAdapterPosition()).getAddress());
//                intent.putExtra("shop_picture", organizationsList.get(holder.getAdapterPosition()).getOrganization_picture());

                intent.putExtra("users", (Serializable) users);
                organizations = new Organizations();
                organizations = organizationsList.get(holder.getAdapterPosition());
                intent.putExtra("organizations", (Serializable) organizations);
                intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return organizationsList.size();
    }
}

class ListShopViewHolder extends RecyclerView.ViewHolder{

    CardView cv_main, cv_image;
    ImageView iv_shoplogo;
    TextView tv_shopname, tv_shopaddress, tv_dishavailabilty;


    public ListShopViewHolder(@NonNull View v) {
        super(v);

        cv_main = v.findViewById(R.id.cvOrderAvailableDish);
        cv_image = v.findViewById(R.id.CardViewImage);
        iv_shoplogo = v.findViewById(R.id.ivDishImage);
        tv_shopname = v.findViewById(R.id.textViewDishName);
        tv_shopaddress = v.findViewById(R.id.textViewShopAddress);
        tv_dishavailabilty = v.findViewById(R.id.textViewDishPrice);


    }
}
