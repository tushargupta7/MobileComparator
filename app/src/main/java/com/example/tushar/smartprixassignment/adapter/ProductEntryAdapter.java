package com.example.tushar.smartprixassignment.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tushar.smartprixassignment.ProductDetailActivity;
import com.example.tushar.smartprixassignment.ProductEntry;
import com.example.tushar.smartprixassignment.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by tushar on 16/5/16.
 */
public class ProductEntryAdapter extends RecyclerView.Adapter<ProductEntryAdapter.ViewHolder> {
    private static ArrayList<ProductEntry> productEntries;
    private static Context context;

    public ProductEntryAdapter(ArrayList<ProductEntry> ProductEntry, Context context) {
        this.productEntries = ProductEntry;
        this.context = context;
    }

    @Override
    public ProductEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductEntryAdapter.ViewHolder holder, int position) {
        ProductEntry productEntry = productEntries.get(position);
        holder.productPrice.setText(productEntry.getPrice());
        holder.productName.setText(productEntry.getName());
        holder.productBrand.setText(productEntry.getBrand());
        // holder.productImage.setImageResource(R.drawable.smart);
        Picasso.with(context).load(productEntry.getPicUrl()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productEntries.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView productImage;
        public TextView productName;
        public TextView productPrice;
        public TextView productBrand;

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            //intent.putExtra("ProductEntry", (Serializable)productEntries.get(v.getVerticalScrollbarPosition()));
            intent.putExtra("product_id", productEntries.get(getPosition()).getProdId());
            intent.putExtra("product_name", productEntries.get(getPosition()).getName());
            intent.putExtra("image_url", productEntries.get(getPosition()).getPicUrl());
            context.startActivity(intent);
        }

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            productImage = (ImageView) view.findViewById(R.id.product_image);
            productPrice = (TextView) view.findViewById(R.id.product_price);
            productName = (TextView) view.findViewById(R.id.product_name);
            productBrand = (TextView) view.findViewById(R.id.product_brand);


        }
    }
}
