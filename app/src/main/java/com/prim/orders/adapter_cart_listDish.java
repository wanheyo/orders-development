package com.prim.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.ApiSetup;

import java.util.ArrayList;

public class adapter_cart_listDish extends RecyclerView.Adapter<ListDish_CartViewHolder> {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();
    private Context context;
    private Users users;
    private Organizations organizations;
    private ArrayList<Dishes> dishOrderList;
    private Order_Cart orderCart;
    private ArrayList<Order_Available> orderAvailableList;
    private ArrayList<Order_Available_Dish> orderAvailableDishList;

    adapter_cart_listOrderAvailableDish adapter;
    RecyclerView recyclerView;

    public adapter_cart_listDish(Context context, Users users, Organizations organizations, ArrayList<Dishes> dishOrderList, Order_Cart orderCart, ArrayList<Order_Available> orderAvailableList, ArrayList<Order_Available_Dish> orderAvailableDishList) {
        this.context = context;
        this.users = users;
        this.organizations = organizations;
        this.dishOrderList = dishOrderList;
        this.orderCart = orderCart;
        this.orderAvailableList = orderAvailableList;
        this.orderAvailableDishList = orderAvailableDishList;
    }

    @NonNull
    @Override
    public ListDish_CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_cart_listdish, parent, false);
        return new ListDish_CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDish_CartViewHolder holder, int position) {
        if(context != null) {
            Glide.with(context)
                    .load(baseURL + "/dish-image/" + dishOrderList.get(position).getDish_image())
                    .placeholder(R.drawable.no_picture_icon)
                    //.load(dishOrderList.get(position).getDish_image())
                    .into(holder.iv_dish);
        }
        holder.tv_dishName.setText(dishOrderList.get(position).getName());
        holder.tv_dishPrice.setText(String.format("RM %.2f", dishOrderList.get(position).getPrice()));

        double totalDishPrice = 0;
        for(Order_Available oa : orderAvailableList) {
            for(Order_Available_Dish oad : orderAvailableDishList) {
                if(oa.getId() == oad.getOrder_available_id()) {
                    if(oa.getDish_id() == dishOrderList.get(position).getId()) {
                        totalDishPrice = totalDishPrice + oad.getTotalprice();
                    }
                }
            }
        }
        holder.tv_dishTotalPrice.setText(String.format("RM %.2f", totalDishPrice));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        holder.rv_orderAvailableDish.setLayoutManager(gridLayoutManager);
        holder.rv_orderAvailableDish.setHasFixedSize(true);

        //gridLayoutManager.setInitialPrefetchItemCount(orderAvailableDishList.size());

        ArrayList<Order_Available_Dish> orderDishList = new ArrayList<>();
        for(Order_Available oa : orderAvailableList) {
            for(Order_Available_Dish oad : orderAvailableDishList) {
                if(oad.getQuantity() != 0 && oad.getOrder_available_id() == oa.getId()) {
                    if(oa.getDish_id() == dishOrderList.get(position).getId()) {
                        Order_Available_Dish newOrder = new Order_Available_Dish();
                        newOrder.setId(oad.getId());
                        newOrder.setQuantity(oad.getQuantity());
                        newOrder.setTotalprice(oad.getTotalprice());
                        newOrder.setDelivery_status(oad.getDelivery_status());
                        newOrder.setDelivery_proof_pic(oad.getDelivery_proof_pic());
                        newOrder.setOrder_desc(oad.getOrder_desc());
                        newOrder.setOrder_available_id(oad.getOrder_available_id());
                        newOrder.setOrder_cart_id(oad.getOrder_cart_id());

                        orderDishList.add(newOrder);
                    }
                }
            }
        }

        adapter = new adapter_cart_listOrderAvailableDish(context, users, organizations, dishOrderList, orderCart, orderAvailableList, orderDishList);
        holder.rv_orderAvailableDish.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dishOrderList.size();
    }
}

class ListDish_CartViewHolder extends RecyclerView.ViewHolder{

    ImageView iv_dish;
    TextView tv_dishName, tv_dishPrice, tv_dishTotalPrice;
    RecyclerView rv_orderAvailableDish;

    public ListDish_CartViewHolder(@NonNull View v) {
        super(v);

        iv_dish = v.findViewById(R.id.ivDishImage);
        tv_dishName = v.findViewById(R.id.textViewNameDish);
        tv_dishPrice = v.findViewById(R.id.textViewPriceDish);
        tv_dishTotalPrice = v.findViewById(R.id.textViewDishTotalPrice);
        rv_orderAvailableDish = v.findViewById(R.id.RecycleViewOrderAvailableDish);
    }
}
