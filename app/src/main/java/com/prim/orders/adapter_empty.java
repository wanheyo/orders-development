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

public class adapter_empty extends RecyclerView.Adapter<EmptyViewHolder> {
    private Context context;
    private String txt;
    private int activity;


    public adapter_empty(Context context, String txt, int activity) {
        this.context = context;
        this.txt = txt;
        this.activity = activity;
    }

    @NonNull
    @Override
    public EmptyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_empty, parent, false);
        return new EmptyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyViewHolder holder, int position) {
        if(activity == 0) {
            holder.tv_context.setText(Html.fromHtml("There are no <b>" + txt + "</b> available"));
        } else if(activity == 1) {
            holder.tv_context.setText(Html.fromHtml("There are no <b>" + txt + "</b> added"));
        } else {
            holder.tv_context.setText(Html.fromHtml("There are no <b>" + txt + "</b> being made"));
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}

class EmptyViewHolder extends RecyclerView.ViewHolder{
    TextView tv_context;

    public EmptyViewHolder(@NonNull View v) {
        super(v);

        tv_context = v.findViewById(R.id.tvContext);
    }
}