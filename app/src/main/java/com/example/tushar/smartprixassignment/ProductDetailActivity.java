package com.example.tushar.smartprixassignment;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tushar.smartprixassignment.adapter.StoreAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StoreAdapter mStoreAdapter;
    ArrayList<StoreDetail> storeArrayList;
    private String productID;
    private final int START_INDEX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_activty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_store);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        storeArrayList = new ArrayList<StoreDetail>();
        productID = getIntent().getStringExtra("product_id");
        initializeCollapseToolBar();
        setStoreListUi(START_INDEX, productID);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int curSize = mStoreAdapter.getItemCount();
                setStoreListUi(curSize, productID);
            }
        });

    }

    private void initializeCollapseToolBar() {
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra("product_name"));

        ImageView deviceImageView = (ImageView) findViewById(R.id.product_image);
        Picasso.with(this).load(getIntent().getStringExtra("image_url")).into(deviceImageView);
    }

    private void setStoreListUi(final int start, String productId) {
        String url = "http://api.smartprix.com/simple/v1?type=product_full&key=NVgien7bb7P5Gsc8DWqc&id=" + productId + "&indent=1";
        JsonObjectRequest productsRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("request_status").equalsIgnoreCase("fail")) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.server_response_error),
                                Toast.LENGTH_LONG).show();
                    } else {
                        parseJsonAndAddStore(response, start);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        productsRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getBaseContext()).add(productsRequest);
    }


    private void parseJsonAndAddStore(JSONObject response, int start) {
        try {
            JSONObject result = response.getJSONObject("request_result");
            JSONArray resultArray = result.getJSONArray("prices");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject storeJson = resultArray.getJSONObject(i);
                StoreDetail storeEntry = new StoreDetail();
                storeEntry.setStoreName(storeJson.getString("store_name"));
                storeEntry.setPrice(storeJson.getString("price"));
                storeEntry.setBuyLink(storeJson.getString("link"));
                storeEntry.setDeliveryTime(storeJson.getString("store_delivery"));
                storeEntry.setLogo(storeJson.getString("logo"));
                storeEntry.setProductName(storeJson.getString("name"));
                storeEntry.setPos(storeJson.getString("pos"));
                storeEntry.setDeliveryCharges(storeJson.getString("shipping_cost"));
                storeEntry.setStock(storeJson.getString("stock"));
                storeEntry.setDeliveryStock(storeJson.getString("delivery"));
                storeEntry.setStoreRating(storeJson.getString("store_rating"));
                storeEntry.setStoreUrl(storeJson.getString("store_url"));
                storeArrayList.add(storeEntry);
            }
            if (storeArrayList.size() == 0) {
                Toast.makeText(this, getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
            }
            if (mStoreAdapter == null) {
                mStoreAdapter = new StoreAdapter(storeArrayList, this);
                mRecyclerView.setAdapter(mStoreAdapter);
            } else {
                mStoreAdapter.notifyItemRangeInserted(start, storeArrayList.size() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
