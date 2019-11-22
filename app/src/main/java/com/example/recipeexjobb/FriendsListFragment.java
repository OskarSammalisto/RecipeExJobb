package com.example.recipeexjobb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class FriendsListFragment extends Fragment {

    List<Map> friendRequestList;
    List<Map> friendsList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);


        friendRequestList = ((MainActivity) getActivity()).getFriendRequestsList();
        friendsList = ((MainActivity) getActivity()).getFriendsList();




        //set adapter for friend requests
        RecyclerView friendRequestRW = view.findViewById(R.id.friendReqRW);
        FriendsListAdapter friendReqAdapter = new FriendsListAdapter(getContext(), friendRequestList);

        friendRequestRW.setHasFixedSize(true);
        friendRequestRW.setLayoutManager(new LinearLayoutManager(view.getContext()));

        friendRequestRW.setAdapter(friendReqAdapter);

        //set adapter for existing friends list
        RecyclerView friendsRW = view.findViewById(R.id.friendsListRW);
        FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getContext(), friendsList);

        friendsRW.setHasFixedSize(true);
        friendsRW.setLayoutManager(new LinearLayoutManager(view.getContext()));

        friendsRW.setAdapter(friendsListAdapter);





        return view;

    }





}
