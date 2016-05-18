package com.example.tushar.smartprixassignment.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tushar.smartprixassignment.R;
import com.example.tushar.smartprixassignment.StoreDetail;
import com.example.tushar.smartprixassignment.WebViewActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tushar on 18/5/16.
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    private static List<StoreDetail> storeEntries;
    private static Context context;

    public StoreAdapter(List<StoreDetail> storeEntry, Context context) {
        this.storeEntries = storeEntry;
        this.context = context;
    }

    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.price_cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoreAdapter.ViewHolder holder, int position) {
        StoreDetail storeEntry = storeEntries.get(position);
        holder.productPrice.setText("Rs. " + storeEntry.getPrice() + "/-");
        holder.storeName.setText(storeEntry.getStoreName());
        holder.deliveryCharges.setText("Delivery Charges:Rs. " + storeEntry.getDeliveryCharges() + "/-");
        holder.deliveryTime.setText("Estimated delivery:" + storeEntry.getDeliveryTime() + " days");
        holder.storeRating.setRating(Integer.parseInt(storeEntry.getStoreRating()) / 20);
        // holder.productImage.setImageResource(R.drawable.smart);
        Picasso.with(context).load(storeEntry.getLogo()).into(holder.storeImage);
    }

    @Override
    public int getItemCount() {
        return storeEntries.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView storeImage;
        public TextView storeName;
        public TextView productPrice;
        public TextView productBrand;
        public RatingBar storeRating;
        public TextView deliveryTime;
        public TextView deliveryCharges;
        public Button productBuyButton;


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.product_buy) {
                Intent webViewIntent = new Intent(context, WebViewActivity.class);
                webViewIntent.putExtra("page_url", storeEntries.get(getPosition()).getBuyLink());
                context.startActivity(webViewIntent);
            }
        }

        public ViewHolder(View view) {
            super(view);
            storeImage = (ImageView) view.findViewById(R.id.store_image);
            productPrice = (TextView) view.findViewById(R.id.product_price);
            storeName = (TextView) view.findViewById(R.id.store_name);
            productBrand = (TextView) view.findViewById(R.id.product_brand);
            storeRating = (RatingBar) view.findViewById(R.id.ratingBar1);
            deliveryTime = (TextView) view.findViewById(R.id.delivery_time);
            deliveryCharges = (TextView) view.findViewById(R.id.delivery_charge);
            productBuyButton = (Button) view.findViewById(R.id.product_buy);
            productBuyButton.setOnClickListener(this);
        }
    }
}
