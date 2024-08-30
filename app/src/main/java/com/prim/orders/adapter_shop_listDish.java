package com.prim.orders;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class adapter_shop_listDish extends RecyclerView.Adapter<ListDishViewHolder> {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Context context;
    private Users users;
    private Organizations organizations;
    private Dishes dishes;
    private Order_Cart orderCart;
    private ArrayList<Order_Available> orderAvailableList;
    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private List<Dishes> dishesList;
    private ArrayList<Dishes> dishesListAll;
    private ArrayList<Dishes> dishCount;

    public adapter_shop_listDish(Context context, List<Dishes> dishesList, Users users, Organizations organizations, Order_Cart orderCart, ArrayList<Order_Available> orderAvailableList, ArrayList<Order_Available_Dish> orderAvailableDishList, ArrayList<Dishes> dishesListAll, ArrayList<Dishes> dishCount) {
        this.context = context;
        this.dishesList = dishesList;
        this.users = users;
        this.organizations = organizations;
        this.orderCart = orderCart;
        this.orderAvailableList = orderAvailableList;
        this.orderAvailableDishList = orderAvailableDishList;
        this.dishesListAll = dishesListAll;
        this.dishCount = dishCount;

        //Log.d("dishesListAll", "Response: dishesListAll size = " + dishesListAll.size() + ", dishCount size = " + dishCount.size());
    }

    @NonNull
    @Override
    public ListDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_shop_listdish, parent, false);
        return new ListDishViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDishViewHolder holder, int position) {
        if(context != null) {
            Glide.with(context)
                    .load(baseURL + "/dish-image/" + dishesList.get(position).getDish_image())
                    .placeholder(R.drawable.no_picture_icon)
                    //.load(dishesList.get(position).getDish_image())
                    .into(holder.iv_dishimage);
        }
        holder.tv_dishname.setText(dishesList.get(position).getName());
        holder.tv_dishprice.setText(String.format("RM %.2f", dishesList.get(position).getPrice()));

        Log.d("LogicCheck-getTotalDishAvailable_in_onBindViewHolder", "Response: For dish_id = " + dishesList.get(position).getId() + ", " + dishesList.get(position).getTotalOrderAvailable());

        String status = "Unavailable";
        if(dishesList.get(position).getTotalOrderAvailable() > 0) {

            for(Order_Available oa : orderAvailableList) {
                if(dishesList.get(position).getId() == oa.getDish_id()) {
                    if(oa.getQuantity() > 0) {
                        //holder.tv_dishavailabilty.setText("Available " + dishesList.get(position).getTotalOrderAvailable());
                        holder.cv_main.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, DishActivity.class);
                                intent.putExtra("users", (Serializable) users);
                                intent.putExtra("organizations", (Serializable) organizations);
                                dishes = new Dishes();
                                dishes.setId(dishesList.get(holder.getAdapterPosition()).getId());
                                dishes.setName(dishesList.get(holder.getAdapterPosition()).getName());
                                dishes.setPrice(dishesList.get(holder.getAdapterPosition()).getPrice());
                                intent.putExtra("dishes", (Serializable) dishes);
                                intent.putExtra("order_cart", (Serializable) orderCart);
                                intent.putExtra("order_available", (Serializable) orderAvailableList);
                                intent.putExtra("order_available_dish", (Serializable) orderAvailableDishList);
                                intent.putExtra("all-dish", (Serializable) dishesListAll);
                                intent.putExtra("dish-count", (Serializable) dishCount);
                                intent.putExtra("order_cart", (Serializable) orderCart);
                                context.startActivity(intent);
                            }
                        });
                    }
                    holder.tv_dishavailabilty.setText(Html.fromHtml("Current Order Slot : <b>" + dishesList.get(position).getTotalOrderAvailable() + "</b>"));
                }
            }

        }
        else {
            holder.tv_dishavailabilty.setText(Html.fromHtml("Current Order Slot : <b>" + dishesList.get(position).getTotalOrderAvailable() + "</b>"));
        }
    }

    @Override
    public int getItemCount() {
        return dishesList.size();
        //return dishesListSize;
    }
}

class ListDishViewHolder extends RecyclerView.ViewHolder{

    CardView cv_main, cv_image, cv_availability;
    ImageView iv_dishimage;
    TextView tv_dishname, tv_dishprice, tv_dishavailabilty;


    public ListDishViewHolder(@NonNull View v) {
        super(v);

        cv_main = v.findViewById(R.id.cvOrderAvailableDish);
        cv_image = v.findViewById(R.id.CardViewImage);
        //cv_availability = v.findViewById(R.id.card);
        iv_dishimage = v.findViewById(R.id.ivDishImage);
        tv_dishname = v.findViewById(R.id.textViewDishName);
        tv_dishprice = v.findViewById(R.id.textViewDishPrice);
        tv_dishavailabilty = v.findViewById(R.id.textViewDishAvailability);


    }
}
