package com.example.tushar.smartprixassignment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tushar.smartprixassignment.adapter.ProductEntryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductEntryAdapter productsAdapter;
    ArrayList<ProductEntry> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        productArrayList = new ArrayList<ProductEntry>();
        setProductListUi(0);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int curSize = productsAdapter.getItemCount();
                setProductListUi(curSize);
            }
        });

    }

    private void setProductListUi(final int start) {
        String category = getIntent().getStringExtra("category");
        String url = "http://api.smartprix.com/simple/v1?type=search&key=NVgien7bb7P5Gsc8DWqc&category=" + category + "&start=" + start + "indent=1";
        JsonObjectRequest productsRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("request_status").equalsIgnoreCase("fail")) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.server_response_error),
                                Toast.LENGTH_LONG).show();
                    } else {
                        parseJsonAndAddProducts(response, start);
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


    private void parseJsonAndAddProducts(JSONObject response, int start) {
        try {
            JSONObject result = response.getJSONObject("request_result");
            JSONArray resultArray = result.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject productJson = resultArray.getJSONObject(i);
                ProductEntry productEntry = new ProductEntry();
                productEntry.setName(productJson.getString("name"));
                productEntry.setPrice(productJson.getString("price"));
                productEntry.setBrand(productJson.getString("brand"));
                productEntry.setCategory(productJson.getString("category"));
                productEntry.setPicUrl(productJson.getString("img_url"));
                productEntry.setProdId(productJson.getString("id"));
                productArrayList.add(productEntry);
            }
            if (productArrayList.size() == 0) {
                Toast.makeText(this, getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
            }
            if (productsAdapter == null) {
                productsAdapter = new ProductEntryAdapter(productArrayList, this);
                recyclerView.setAdapter(productsAdapter);
            } else {
                productsAdapter.notifyItemRangeInserted(start, productArrayList.size() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        //check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("category", getIntent().getStringExtra("category"));
        }

        super.startActivity(intent);
    }
}
