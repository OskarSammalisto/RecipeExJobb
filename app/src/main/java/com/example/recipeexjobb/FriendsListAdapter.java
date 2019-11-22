package com.example.recipeexjobb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {

    private Context context;
    private List<Map> displayList;
    private Boolean isFriendsList;
    private Boolean isSharing;
    private SparseBooleanArray selectedFriends = new SparseBooleanArray();




    public FriendsListAdapter(Context context, List<Map> list, boolean isFriendsList, boolean isSharing){
        this.context = context;
        this.displayList = list;
        this.isFriendsList = isFriendsList;
        this.isSharing = isSharing;
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
            holder.backgroundLayout.setSelected(selectedFriends.get(position, false));
        }

    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;
        LinearLayout backgroundLayout;

        public MyViewHolder(View view) {

            super(view);

            friendName = view.findViewById(R.id.friendName);
            backgroundLayout = view.findViewById(R.id.friendListLayout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(isFriendsList && !isSharing){
                        //do friend thing
                    }

                    else if(isSharing){
                            if(selectedFriends.get(getAdapterPosition(), false)){
                                selectedFriends.delete(getAdapterPosition());
                                backgroundLayout.setSelected(false);
                            }
                            else {
                                selectedFriends.put(getAdapterPosition(), true);
                                backgroundLayout.setSelected(true);
                            }


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

    public List<Integer> getFriendsToShareWith(){

        List<Integer> temp = new ArrayList<>();

        if(selectedFriends != null && selectedFriends.size() > 0){
            for(int i = 0; i < selectedFriends.size(); i++){
                temp.add(selectedFriends.keyAt(i));
            }
        }

        return temp;
    }


}
