package com.example.bhart.gitapplication;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private String name="";

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
        url="https://api.github.com/search/users?q=tom";

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

                //updating value of url with searched name
                url = "https://api.github.com/search/users?q="+name;

                //clearing previous list
                listItem.clear();
                getUserData(url);

                //notifying dataset has changed
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Showing result with name "+name , Toast.LENGTH_LONG).show();
            }
        });


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

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            //when error occurred whiling getting data of users
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Check your Internet", Toast.LENGTH_SHORT).show();

            }
        });

        //Requesting using volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}
