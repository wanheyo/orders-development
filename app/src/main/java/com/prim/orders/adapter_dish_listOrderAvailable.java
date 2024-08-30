package com.prim.orders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;
import com.prim.orders.models.Users;
import com.prim.orders.utilities.IconSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class adapter_dish_listOrderAvailable extends RecyclerView.Adapter<ListOrderAvailableViewHolder> {
    private Context context;

    private Users users;
    private Organizations organizations;
    private Dishes dishes;
    private ArrayList<Order_Available> orderAvailableList;
    private Order_Cart orderCart;
    private ArrayList<Order_Available_Dish> orderAvailableDishList;
    private Order_Available_Dish orderAvailableDish;


    private List<Date> dateList;
    private IconSetup iconSetup;
    private OnItemsClickListener listener = null;


    public adapter_dish_listOrderAvailable(Context context, ArrayList<Order_Available> orderAvailableList, Dishes dishes, Users users, Organizations organizations , Order_Cart orderCart, ArrayList<Order_Available_Dish> orderAvailableDishList) {
        this.context = context;
        this.orderAvailableList = orderAvailableList;
        this.dishes = dishes;
        this.users = users;
        this.organizations = organizations;
        this.orderCart = orderCart;
        this.orderAvailableDishList = orderAvailableDishList;

        Log.d("order_cart", "Response: order_cart Response, Success." + orderCart.getId());
//        this.orderAvailableDishList = orderAvailableDishList;
//        orderAvailableDishList = new ArrayList<>();
//
//        for (Order_Available oa : orderAvailableList) {
//            orderAvailableDish = new Order_Available_Dish();
//            orderAvailableDish.setOrder_cart_id(orderCart.getId());
//            orderAvailableDish.setOrder_available_id(oa.getId());
//            orderAvailableDish.setQuantity(oa.getQuantity_order());
//            orderAvailableDish.setTotalprice(oa.getTotalPrice_order());
//
//            orderAvailableDishList.add(orderAvailableDish);
//        }

//        this.orderAvailableDishList = orderAvailableDishList;
    }

    public void setWhenClickListener(OnItemsClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListOrderAvailableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dish_listorderavailable, parent, false);
        return new ListOrderAvailableViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrderAvailableViewHolder holder, int position) {
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

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy (EEEE)");
        SimpleDateFormat formatterDate2 = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatterDate.format(orderAvailableList.get(position).getDelivery_date());
        String date2 = formatterDate2.format(orderAvailableList.get(position).getDelivery_date());
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");
        String time = formatterTime.format(orderAvailableList.get(position).getDelivery_date());

        holder.tv_dateTime.setText(date2 + " | " + time);
        holder.tv_deliveryAddress.setText(Html.fromHtml("<b>Pickup Location : </b>" + orderAvailableList.get(position).getDelivery_address()));
        holder.tv_deliveryDate.setText(Html.fromHtml("<b>Pickup Date : </b>" + date));
        holder.tv_deliveryTime.setText(Html.fromHtml("<b>Pickup Time : </b>" + time));

        String openDate = formatterDate2.format(orderAvailableList.get(position).getOpen_date());
        String openTime = formatterTime.format(orderAvailableList.get(position).getOpen_date());
        String closeDate = formatterDate2.format(orderAvailableList.get(position).getClose_date());
        String closeTime = formatterTime.format(orderAvailableList.get(position).getClose_date());

        holder.tv_openCloseDateTime.setText(Html.fromHtml("Open order from <b>" + openDate + " (" + openTime + ")</b> to <b>" + closeDate + " (" + closeTime + ")</b>. Total dishes left : <b>" + orderAvailableList.get(position).getQuantity() + "</b> pcs."));

//        holder.tv_deliveryTime.setText(dishAvailableList.get(position).getTime());
//        SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm:ss a");
//        SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss");
//        try {
//            Date date12 = sdf24.parse(orderAvailableList.get(position).getTime());
//            holder.tv_deliveryTime.setText(sdf12.format(date12) + " | " + orderAvailableList.get(position).getDate());
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        orderAvailableDish = new Order_Available_Dish();
//        orderAvailableDish
//        orderCart = new Order_Cart();

        Log.d("orderAvailableList", "Response: orderAvailableList ID: " + orderAvailableList.get(position).getId());
        for(Order_Available_Dish oad : orderAvailableDishList) {
            if (oad.getOrder_cart_id() == orderCart.getId() && oad.getOrder_available_id() == orderAvailableList.get(position).getId()) {
                orderAvailableList.get(position).setQuantity_order(oad.getQuantity());
                orderAvailableList.get(position).setTotalPrice_order(oad.getTotalprice());
            }
        }

        holder.tv_orderQuantity.setText(String.valueOf(orderAvailableList.get(position).getQuantity_order()));
        holder.tv_totalPrice.setText(String.format("RM %.2f", orderAvailableList.get(position).getTotalPrice_order()));

        if(orderAvailableList.get(position).getQuantity_order() != 0) {
            holder.cl_expandable.setVisibility(View.VISIBLE);
            holder.ib_expand.setImageResource(R.drawable.round_expand_less_24);
        } else {
            holder.cl_expandable.setVisibility(View.GONE);
            holder.ib_expand.setImageResource(R.drawable.round_expand_more_24);
        }

        holder.ib_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.cl_expandable.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.cl_expandable, new AutoTransition());
                    holder.cl_expandable.setVisibility(View.GONE);
                    holder.ib_expand.setImageResource(R.drawable.round_expand_more_24);
                } else {
                    TransitionManager.beginDelayedTransition(holder.cl_expandable, new AutoTransition());
                    holder.cl_expandable.setVisibility(View.VISIBLE);
                    holder.ib_expand.setImageResource(R.drawable.round_expand_less_24);
                }
            }
        });

        holder.ib_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order() < orderAvailableList.get(holder.getAdapterPosition()).getQuantity()) {

                    orderAvailableList.get(holder.getAdapterPosition()).setQuantity_order(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order() + 1);
                    holder.tv_orderQuantity.setText(String.valueOf(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order()));
                    orderAvailableList.get(holder.getAdapterPosition()).setTotalPrice_order(orderAvailableList.get(holder.getAdapterPosition()).getTotalPrice_order() + dishes.getPrice());
                    holder.tv_totalPrice.setText(String.format("RM %.2f", orderAvailableList.get(holder.getAdapterPosition()).getTotalPrice_order()));

                    for (Order_Available_Dish oad : orderAvailableDishList) {
                        if (oad.getOrder_cart_id() == orderCart.getId() && oad.getOrder_available_id() == orderAvailableList.get(holder.getAdapterPosition()).getId()) {
                            oad.setQuantity(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order());
                            oad.setTotalprice(orderAvailableList.get(holder.getAdapterPosition()).getTotalPrice_order());

                            Log.d("orderAvailableDishList", "Response: Cart ID: " + oad.getOrder_cart_id() + " | Order Available ID: " + oad.getOrder_available_id() + " | Quantity: " + oad.getQuantity() + " | Price: " + oad.getTotalprice());
                            break;
                        }
                    }

                    if(listener != null) {
                        listener.onAddBtnClick(dishes, orderAvailableList.get(holder.getAdapterPosition()), orderAvailableDishList);
                    }
                }
            }
        });

        holder.ib_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order() != 0) {

                    orderAvailableList.get(holder.getAdapterPosition()).setQuantity_order(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order() - 1);
//                    orderAvailableList.get(holder.getAdapterPosition()).setQuantity(orderAvailableList.get(holder.getAdapterPosition()).getQuantity() + 1);
                    holder.tv_orderQuantity.setText(String.valueOf(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order()));
                    orderAvailableList.get(holder.getAdapterPosition()).setTotalPrice_order(orderAvailableList.get(holder.getAdapterPosition()).getTotalPrice_order() - dishes.getPrice());
                    holder.tv_totalPrice.setText(String.format("RM %.2f", orderAvailableList.get(holder.getAdapterPosition()).getTotalPrice_order()));

                    for (Order_Available_Dish oad : orderAvailableDishList) {
                        if (oad.getOrder_cart_id() == orderCart.getId() && oad.getOrder_available_id() == orderAvailableList.get(holder.getAdapterPosition()).getId()) {
                            oad.setQuantity(orderAvailableList.get(holder.getAdapterPosition()).getQuantity_order());
                            oad.setTotalprice(orderAvailableList.get(holder.getAdapterPosition()).getTotalPrice_order());

                            Log.d("orderAvailableDishList", "Response: Cart ID: " + oad.getOrder_cart_id() + " | Order Available ID: " + oad.getOrder_available_id() + " | Quantity: " + oad.getQuantity() + " | Price: " + oad.getTotalprice());
                            break;
                        }
                    }

                    if(listener != null){
                        listener.onMinusBtnClick(dishes, orderAvailableList.get(holder.getAdapterPosition()), orderAvailableDishList);
                    }
                }
            }
        });



//        holder.b_addToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(holder.b_addToCart.getText() != "Remove") {
//                    holder.b_addToCart.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_darker));
//                    holder.b_addToCart.setText("Remove");
//                }
//                else {
//                    holder.b_addToCart.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
//                    holder.b_addToCart.setText("Add to cart");
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return orderAvailableList.size();
    }
}

class ListOrderAvailableViewHolder extends RecyclerView.ViewHolder{

    CardView cv_date, cv_cart;
    TextView tv_dateTime, tv_deliveryAddress, tv_deliveryDate, tv_deliveryTime, tv_orderQuantity, tv_openCloseDateTime, tv_totalPrice;
    ImageButton ib_expand, ib_minus, ib_add;
//    Button b_addToCart;
    ConstraintLayout cl_expandable;

    public ListOrderAvailableViewHolder(@NonNull View v) {
        super(v);

        cv_date = v.findViewById(R.id.cvOrderAvailableDish);
        cv_cart = v.findViewById(R.id.cardViewCart);
        tv_dateTime = v.findViewById(R.id.textViewDateTime);
        tv_deliveryAddress = v.findViewById(R.id.tvPickupLocation);
        tv_deliveryDate = v.findViewById(R.id.tvPickupDate);
        tv_deliveryTime = v.findViewById(R.id.tvPickupTime);
        tv_openCloseDateTime = v.findViewById(R.id.tvReminder);
        tv_orderQuantity = v.findViewById(R.id.textViewOrderQuantity);
        tv_totalPrice = v.findViewById(R.id.tvTotalPrice);
        ib_expand = v.findViewById(R.id.buttonExpand);
        ib_minus = v.findViewById(R.id.buttonMinus);
        ib_add = v.findViewById(R.id.buttonAdd);
//        b_addToCart = v.findViewById(R.id.buttonAddToCart);
        cl_expandable = v.findViewById(R.id.ConstraintLayoutExpandable);
    }
}

interface OnItemsClickListener{
    void onAddBtnClick(Dishes dishes ,Order_Available orderAvailable, ArrayList<Order_Available_Dish> orderAvailableDishList);
    void onMinusBtnClick(Dishes dishes ,Order_Available orderAvailable, ArrayList<Order_Available_Dish> orderAvailableDishList);
}
