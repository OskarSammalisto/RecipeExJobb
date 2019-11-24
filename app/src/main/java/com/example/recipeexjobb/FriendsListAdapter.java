package com.example.recipeexjobb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.SignInButton;

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

        List<Recipe> shared = ((MainActivity) context).getSharedRecipeList();
        List<String> usernames = new ArrayList<>();

        for(Recipe recipe : shared){
            usernames.add(recipe.getSharedBy());
        }


        if(displayList != null){

            String name = displayList.get(position).get("username").toString();
            holder.friendName.setText(name);
            holder.backgroundLayout.setSelected(selectedFriends.get(position, false));

            //String yes = "yes";

            try{
                if(usernames.contains(name)){  //displayList.get(position).get("sharing").equals(yes)
                    selectedFriends.put(holder.getAdapterPosition(), true);
                    holder.backgroundLayout.setSelected(true);
                    Log.d("!!!!", "yes");
                }
                else {
                    Log.d("!!!!", name +usernames.get(0));
                }


            }catch (Exception e){
                Log.d("is sharing", "no such value");
            }






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

                        if(selectedFriends.get(getAdapterPosition(), false)){
                            AlertDialog.Builder sharedRecipesDialog = new AlertDialog.Builder(context);
                            final String username = displayList.get(getAdapterPosition())
                                    .get("username").toString();

                            sharedRecipesDialog.setTitle( username +" Would like to share recipes with you.");

                            List<Recipe> shared = ((MainActivity) context).getSharedRecipeList();

                            final ArrayAdapter<String> recipeNames = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
                                for(Recipe recipe : shared){
                                    if(username.equals(recipe.getSharedBy())){
                                        recipeNames.add(recipe.getRecipeTitle());
                                    }
                                }
                            ListView listView = new ListView(context);

                            listView.setAdapter(recipeNames);

                            sharedRecipesDialog.setView(listView);

                            sharedRecipesDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ((MainActivity) context).saveSharedRecipes(username);

                                    selectedFriends.delete(getAdapterPosition());
                                    backgroundLayout.setSelected(false);
                                }
                            });

                            sharedRecipesDialog.setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            sharedRecipesDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ((MainActivity) context).deleteSharedRecipes(username);

                                    selectedFriends.delete(getAdapterPosition());
                                    backgroundLayout.setSelected(false);
                                }
                            });

                            sharedRecipesDialog.show();


                        }

//                        else {
//                            //removing friend would go here
//                        }

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
