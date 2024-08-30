package com.prim.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class adapter_cart_listOrderAvailableDish extends RecyclerView.Adapter<ListOrderAvailableDish_CartViewHolder> {
    private Context context;
    private Users users;
    private Organizations organizations;
    private ArrayList<Dishes> dishOrderList;
    private Order_Cart orderCart;
    private ArrayList<Order_Available> orderAvailableList;
    private ArrayList<Order_Available_Dish> orderAvailableDishList;

    public adapter_cart_listOrderAvailableDish(Context context, Users users, Organizations organizations, ArrayList<Dishes> dishOrderList, Order_Cart orderCart, ArrayList<Order_Available> orderAvailableList, ArrayList<Order_Available_Dish> orderAvailableDishList) {
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
    public ListOrderAvailableDish_CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_cart_listorderavailabledish, parent, false);
        return new ListOrderAvailableDish_CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrderAvailableDish_CartViewHolder holder, int position) {
        holder.tv_orderPrice.setText(String.format("RM %.2f", orderAvailableDishList.get(position).getTotalprice()));
        //holder.tv_orderPrice.setText(String.format("RM %.2f", orderAvailableDishList.get(position).getTotalprice()));
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");

        String date, time;
        for(Order_Available oa : orderAvailableList) {
            if(oa.getId() == orderAvailableDishList.get(position).getOrder_available_id()) {
                date = formatterDate2.format(oa.getDelivery_date());
                time = formatterTime.format(oa.getDelivery_date());
                holder.tv_num.setText(String.valueOf(position + 1) + ")");
                holder.tv_orderDate.setText(date + " | " + time + " (" + String.valueOf(orderAvailableDishList.get(position).getQuantity()) + " pcs)");
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderAvailableDishList.size();
    }
}

class ListOrderAvailableDish_CartViewHolder extends RecyclerView.ViewHolder{

    ImageView iv_btn_minus, iv_btn_add, iv_btn_info;
    TextView tv_orderDate, tv_orderPrice, tv_orderQuantity, tv_num;

    public ListOrderAvailableDish_CartViewHolder(@NonNull View v) {
        super(v);

//        iv_btn_minus = v.findViewById(R.id.imageViewBtnMinus);
//        iv_btn_add = v.findViewById(R.id.imageViewBtnAdd);
//        iv_btn_info = v.findViewById(R.id.imageViewBtnInfo);
        tv_num = v.findViewById(R.id.textViewNum);
        tv_orderDate = v.findViewById(R.id.textViewOrderAvailableDishDate);
        //tv_orderPrice = v.findViewById(R.id.textViewOrderDishPrice);
        tv_orderPrice = v.findViewById(R.id.textViewOrderDishPrice);
    }
}
