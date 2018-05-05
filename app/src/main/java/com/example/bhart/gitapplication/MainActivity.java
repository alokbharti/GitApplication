package com.example.bhart.gitapplication;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //initialising list for storing score and username
    private List<user> listItem;

    //Recylerview for the layout
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    // Github Url to find Username and score
    private String url="";

    //for page number
    private int page=1;

    private String name="tom";

    //for next and previous button
    private Button mPrev;
    private Button mNext;

    //for Editext
    private EditText userSearch;

    //for search button
    private TextView button;

    //for layout manager
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialising RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.recylerview);
        //setting size of RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //setting Layout for RecyclerView
        manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        //Default url value with Default search name Tom, It gives the score value with highest to lowest score value
        url="https://api.github.com/search/users?q=tom&page=1";

        listItem = new ArrayList<>();

        //Toast to show the name which is searched
        Toast.makeText(this,"Showing result with name Tom",Toast.LENGTH_LONG).show();


        //setting adapter with recylerview
        mAdapter = new Adapter(listItem,getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        //getting default url with name Tom
        getUserData(url);

        //when search button is clicked..

        button = (TextView) findViewById(R.id.search_user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Taking value from Editext
                userSearch = (EditText)findViewById(R.id.editext);
                name = userSearch.getText().toString();

                if(!name.isEmpty()){
                    //updating value of url with searched name
                    url = "https://api.github.com/search/users?q="+name+"&page=1";


                    //clearing previous list
                    listItem.clear();
                    getUserData(url);

                    //notifying dataset has changed
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Showing result with name "+name , Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Please search any Name",Toast.LENGTH_SHORT).show();
                }

               }
        });

        //when prev Button is clicked
        mPrev = (Button)findViewById(R.id.prev);
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page==1){
                    Toast.makeText(getApplicationContext(),"This is first page",Toast.LENGTH_SHORT).show();
                }
                else{
                    page--;
                    url=getUrl(page);
                    listItem.clear();
                    mAdapter.notifyDataSetChanged();
                    Log.e("PrevUrl",url);
                    getUserData(url);
                }
            }
        });

        //when next Button is clicked
        mNext = (Button)findViewById(R.id.next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listItem.size()==0){
                    Toast.makeText(getApplicationContext(),"This is Last Page",Toast.LENGTH_SHORT).show();
                }
                else{
                    page++;
                    url=getUrl(page);
                    listItem.clear();
                    mAdapter.notifyDataSetChanged();
                    Log.e("NextUrl",url);
                    getUserData(url);
                }
            }
        });


    }

    //getting url for a particular page
    public String getUrl(int page){
        String PageUrl;
        PageUrl = "https://api.github.com/search/users?q="+name+"&page="+String.valueOf(page);
        return PageUrl;
    }



    //function to get a list of user with search name
    public void getUserData(String url) {

        //Initialising dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data....");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //when no error has occurred, dismiss the dialog box
                        progressDialog.dismiss();

                        try {

                            //initialising json Object nad array
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            //adding value to listItem
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                user item = new user(object.getString("login"), object.getString("score"));
                                listItem.add(item);
                                mAdapter.notifyDataSetChanged();

                            }
                            if(listItem.size()==0){
                                Toast.makeText(getApplicationContext(),"That was Last Page",Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Oops, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            //when error occurred whiling getting data of users
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Check your Internet", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }

            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

        };

        //Requesting using volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}
