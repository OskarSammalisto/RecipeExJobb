package com.example.recipeexjobb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    private Context context;
    private List<Map> displayList;
    private Boolean isFriendsList;




    public FriendsListAdapter(Context context, List<Map> list, boolean isFriendsList){
        this.context = context;
        this.displayList = list;
        this.isFriendsList = isFriendsList;

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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFriendsList){
                        //do friend thing
                    }
                    else {

                        AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(context);
                        addFriendDialog.setTitle("Add " +displayList.get(getAdapterPosition()).get("username").toString() +" to friends list?");

                        addFriendDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(context instanceof MainActivity){
                                    ((MainActivity) context).addNewFriendToList(displayList.get(getAdapterPosition()), true);
                                    displayList.remove(getAdapterPosition());
                                    notifyDataSetChanged();

                                }
                            }
                        }).setNeutralButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Delete  Request", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) context).addNewFriendToList(displayList.get(getAdapterPosition()), false);
                                displayList.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        });

                        addFriendDialog.show();


                    }
                }
            });




        }



    }


}
