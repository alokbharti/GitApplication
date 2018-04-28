package com.example.bhart.gitapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhart on 4/28/2018.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<user> ListItem;
    private Context context;

    public Adapter(List<user> listItem, Context context) {
        ListItem = listItem;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_list,parent,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user listItem = ListItem.get(position);
        holder.mUser.setText(listItem.getName());
        holder.mScore.setText(listItem.getScore());

    }

    @Override
    public int getItemCount() {
        return ListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mUser;
        public TextView mScore;
        public ViewHolder(View itemView) {
            super(itemView);
            mUser = (TextView)itemView.findViewById(R.id.username);
            mScore = (TextView) itemView.findViewById(R.id.score);
        }
    }
}
