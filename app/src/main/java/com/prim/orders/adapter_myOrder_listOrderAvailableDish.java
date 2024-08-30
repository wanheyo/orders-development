package com.prim.orders;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class adapter_myOrder_listOrderAvailableDish extends RecyclerView.Adapter<ListOrderAvailableDish_MyOrderViewHolder> {
    private ApiSetup apiSetup = new ApiSetup();
    String baseURL = apiSetup.getBaseURL();

    private Context context;
    private Users users;

    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private ArrayList<Order_Cart> orderCartList;
    private ArrayList<Order_Available> orderAvailableList;
    private ArrayList<Dishes> dishesList;
    private ArrayList<Organizations> organizationsList;

    adapter_cart_listOrderAvailableDish adapter;
    RecyclerView recyclerView;

    public void setSearchList(ArrayList<Dishes> dishesList) {
        this.dishesList = dishesList;
        ArrayList<Order_Available_Dish> orderAvailableDishListSort = new ArrayList<>();
        ArrayList<Order_Cart> orderCartListSort;
        ArrayList<Order_Available> orderAvailableListSort;
        ArrayList<Organizations> organizationsListSort;

        for(Dishes d : dishesList) {
            for (Order_Available oa : orderAvailableList) {
                for (Order_Available_Dish oad : orderAvailableDishList) {
                    if(d.getId() == oa.getDish_id()) {
                        if(oa.getId() == oad.getOrder_available_id()) {
                            orderAvailableDishListSort.add(oad);
                        }
                    }
                }
            }
        }

        orderAvailableDishList = orderAvailableDishListSort;


        notifyDataSetChanged();
    }

    public adapter_myOrder_listOrderAvailableDish(Context context, Users users, ArrayList<Organizations> organizationsList, ArrayList<Dishes> dishesList, ArrayList<Order_Cart> orderCartList, ArrayList<Order_Available> orderAvailableList, ArrayList<Order_Available_Dish> orderAvailableDishList) {
        this.context = context;
        this.users = users;
        this.organizationsList = organizationsList;
        this.dishesList = dishesList;
        this.orderCartList = orderCartList;
        this.orderAvailableList = orderAvailableList;
        this.orderAvailableDishList = orderAvailableDishList;
    }

    @NonNull
    @Override
    public ListOrderAvailableDish_MyOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_myorder_listorderavailabledish, parent, false);
        return new ListOrderAvailableDish_MyOrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrderAvailableDish_MyOrderViewHolder holder, int position) {
        if(context != null) {
            Glide.with(context)
                    .load(baseURL + "/dish-image/" + dishesList.get(position).getDish_image())
                    .placeholder(R.drawable.no_picture_icon)
                    //.load(dishesList.get(position).getDish_image())
                    .into(holder.iv_dishImage);
        }
        holder.tv_dishName.setText(dishesList.get(position).getName());
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        holder.tv_shopName.setText(organizationsList.get(position).getNama());
        holder.tv_date1.setText(formatterDate.format(orderAvailableList.get(position).getDelivery_date()));


        holder.tv_dishInfo.setText(Html.fromHtml("<b>Dish : </b>" + dishesList.get(position).getName() + " (" + String.format("RM %.2f", dishesList.get(position).getPrice()) + " each)"));
        holder.tv_pickupDate.setText(Html.fromHtml("<b>Pickup Date : </b>" + formatterDate.format(orderAvailableList.get(position).getDelivery_date())));
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");
        holder.tv_pickupTime.setText(Html.fromHtml("<b>Pickup Time : </b>" + formatterTime.format(orderAvailableList.get(position).getDelivery_date())));
        holder.tv_pickupLocation.setText(Html.fromHtml("<b>Pickup Location : </b>" + orderAvailableList.get(position).getDelivery_address()));
        holder.tv_status.setText(Html.fromHtml("<b>Status : </b>" + orderAvailableDishList.get(position).getDelivery_status()));
        holder.tv_CartTransacInfo.setText(Html.fromHtml("<b>Cart ID : </b>" + orderCartList.get(position).getId() + " | <b>Transaction ID : </b>" + orderCartList.get(position).getTransactions_id()));
        holder.tv_dishTotalPrice.setText(String.format("RM %.2f", orderAvailableDishList.get(position).getTotalprice()) + " (" + orderAvailableDishList.get(position).getQuantity() + "x)");

        holder.iv_pending.setVisibility(View.GONE);
        holder.iv_abandon.setVisibility(View.GONE);
        holder.iv_completed.setVisibility(View.GONE);

        if(orderAvailableDishList.get(position).getDelivery_status().equals("order-completed")) {
            holder.iv_pending.setVisibility(View.GONE);
            holder.iv_abandon.setVisibility(View.GONE);
            holder.iv_completed.setVisibility(View.VISIBLE);
        } else if(orderAvailableDishList.get(position).getDelivery_status().equals("order-preparing")) {
            holder.iv_pending.setVisibility(View.VISIBLE);
            holder.iv_abandon.setVisibility(View.GONE);
            holder.iv_completed.setVisibility(View.GONE);
        } else {
            holder.iv_pending.setVisibility(View.GONE);
            holder.iv_abandon.setVisibility(View.VISIBLE);
            holder.iv_completed.setVisibility(View.GONE);
        }

        holder.cv_oad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog(orderAvailableDishList.get(holder.getAdapterPosition()).getId(), orderAvailableList.get(holder.getAdapterPosition()).getDelivery_date(), orderAvailableDishList.get(holder.getAdapterPosition()).getDelivery_status(), dishesList.get(holder.getAdapterPosition()), orderCartList.get(holder.getAdapterPosition()).getTransactions_id());
            }
        });

//        holder.ib_expand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(holder.cl_expandable.getVisibility() == View.VISIBLE) {
//                    TransitionManager.beginDelayedTransition(holder.cl_expandable, new AutoTransition());
//                    holder.cl_expandable.setVisibility(View.GONE);
//                    holder.ib_expand.setImageResource(R.drawable.round_expand_more_24);
//                } else {
//                    TransitionManager.beginDelayedTransition(holder.cl_expandable, new AutoTransition());
//                    holder.cl_expandable.setVisibility(View.VISIBLE);
//                    holder.ib_expand.setImageResource(R.drawable.round_expand_less_24);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return orderAvailableDishList.size();
    }

    public void showBottomDialog(int orderID, Date deliveryDate, String status, Dishes dishes, int transaction_id) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_order_bottom_dialog_layout);
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");

        TextView tv_OrderID = dialog.findViewById(R.id.tvOrderID);
        TextView tv_OrderInfo = dialog.findViewById(R.id.tvOrderInfo);

        if(!status.equals("order-completed")) {
            tv_OrderID.setText(String.valueOf(orderID));
            tv_OrderInfo.setText(Html.fromHtml("Please shows this number to seller to pickup your <b>" + dishes.getName() + "</b>. Dish suppose to be pickup at <b>" + formatterDate.format(deliveryDate) + "</b>, <b>" + formatterTime.format(deliveryDate) + "</b>."));
        } else {
            //tv_OrderID.setText("--");
            tv_OrderID.setText(String.valueOf(orderID));
            tv_OrderInfo.setText(Html.fromHtml("Your <b>" + dishes.getName() + "</b> already completed and supposedly has been pickup at <b>" + formatterDate.format(deliveryDate) + "</b>, <b>" + formatterTime.format(deliveryDate) + "</b>."));
        }

        Button receiptButton = dialog.findViewById(R.id.buttonReceipt);
        receiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, OrderCartReceiptActivity.class);
                intent.putExtra("users", (Serializable) users);
                intent.putExtra("transaction_id", transaction_id);
                context.startActivity(intent);
                //Toast.makeText(LoginActivity.this, "Login", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}

class ListOrderAvailableDish_MyOrderViewHolder extends RecyclerView.ViewHolder {

    ImageView iv_dishImage, iv_pending, iv_abandon, iv_completed;
    ImageButton ib_expand;
    TextView tv_dishName, tv_shopName, tv_date1, tv_dishInfo, tv_pickupDate, tv_pickupTime, tv_pickupLocation, tv_status, tv_CartTransacInfo, tv_dishTotalPrice;
    ConstraintLayout cl_expandable;
    CardView cv_oad;
    RecyclerView rv_orderAvailableDish;

    public ListOrderAvailableDish_MyOrderViewHolder(@NonNull View v) {
        super(v);

        iv_dishImage = v.findViewById(R.id.ivDishImage);
        iv_pending = v.findViewById(R.id.ivPending);
        iv_abandon = v.findViewById(R.id.ivAbandon);
        iv_completed = v.findViewById(R.id.ivCompleted);
        //ib_expand = v.findViewById(R.id.btnExpand);
        tv_dishName = v.findViewById(R.id.tvDishName);
        tv_shopName = v.findViewById(R.id.tvShopName);
        tv_date1 = v.findViewById(R.id.tvDate1);
        tv_dishInfo = v.findViewById(R.id.tvDishInfo);
        tv_pickupDate = v.findViewById(R.id.tvPickupDate);
        tv_pickupTime = v.findViewById(R.id.tvDeliveryTime);
        tv_pickupLocation = v.findViewById(R.id.tvDeliveryLocation);
        tv_status = v.findViewById(R.id.tvStatus);
        tv_CartTransacInfo = v.findViewById(R.id.tvCartTransacInfo);
        tv_dishTotalPrice = v.findViewById(R.id.tvTotalPrice);
        cl_expandable = v.findViewById(R.id.ConstraintLayoutExpandable);
        cv_oad = v.findViewById(R.id.cvOrderAvailableDish);
        //rv_orderAvailableDish = v.findViewById(R.id.RecycleViewOrderAvailableDish);
    }
}
