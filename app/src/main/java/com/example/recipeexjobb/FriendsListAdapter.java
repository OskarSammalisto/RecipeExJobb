package com.example.recipeexjobb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {

    Context context;
    List<Map> displayList;




    public FriendsListAdapter(Context context, List<Map> list){
        this.context = context;
        this.displayList = list;

    }


    @NonNull
    @Override
    public FriendsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_list_item_layout, null);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(displayList != null){

            String name = displayList.get(position).get("username").toString();
            holder.friendName.setText(name);
        }

    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;

        public MyViewHolder(View view) {

            super(view);

            friendName = view.findViewById(R.id.friendName);
        }



    }
}
