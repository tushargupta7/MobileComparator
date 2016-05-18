package com.example.tushar.smartprixassignment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        listView = (ListView) findViewById(R.id.mobile_list);
        setCategoryList();
        listView.setOnItemClickListener(this);

    }

    private void setCategoryList() {
        String url = "http://api.smartprix.com/simple/v1?type=categories&key=NVgien7bb7P5Gsc8DWqc&indent=1";
        JsonObjectRequest productsRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("request_status").equalsIgnoreCase("fail")) {
                        Toast.makeText(getBaseContext(),
                                getString(R.string.server_response_error),
                                Toast.LENGTH_LONG).show();
                    } else {
                        parseJsonAndAddCategories(response);
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

    private void parseJsonAndAddCategories(JSONObject response) {
        ArrayList<String> categoryList = new ArrayList<String>();
        try {
            JSONArray result = response.getJSONArray("request_result");
            for (int i = 0; i < result.length(); i++) {
                categoryList.add(result.getString(i));
            }
            ArrayAdapter mAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, categoryList);
            listView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent launchProdListActivity = new Intent(this, ProductListActivity.class);
        launchProdListActivity.putExtra("category", (String) parent.getAdapter().getItem(position));
        startActivity(launchProdListActivity);
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
}
