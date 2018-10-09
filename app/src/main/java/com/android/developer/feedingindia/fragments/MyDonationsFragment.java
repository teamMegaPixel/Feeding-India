package com.android.developer.feedingindia.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.adapters.DeliveriesAdapter;
import com.android.developer.feedingindia.adapters.DonationAdapter;
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyDonationsFragment extends Fragment {

    private DatabaseReference donationDatabaseReference;
    private long userDonationCount = 0;
    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private HashMap<String,DonationDetails> userDonationList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static DeliveriesAdapter.ClickListener clickListener;
    private int pos;

    private ArrayList<String> tags = new ArrayList<>();
    private ArrayList<DonationDetails> userDonations = new ArrayList<>();
    private android.support.v7.app.AlertDialog mAlertDialog;
    private android.support.v7.app.AlertDialog.Builder mBuilder;

    public MyDonationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDonationList = new HashMap<>();
        userDonationCount = 0;
        donationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations").
                        child(FirebaseAuth.getInstance().getUid());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_donations, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.donations_view);
        mLinearLayout = view.findViewById(R.id.my_donations_container);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        donationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userDonationCount = dataSnapshot.getChildrenCount();

                if(userDonationCount == 0)
                    enableUserInteraction();
                else {

                    ObjectMapper objectMapper = new ObjectMapper();
                    HashMap<String,HashMap<String,Object>> myList = (HashMap<String,HashMap<String,Object>>)dataSnapshot.getValue();
                    Set mySet = myList.entrySet();
                    Iterator iterator = mySet.iterator();
                    while(iterator.hasNext()){
                        Map.Entry myMapEntry =(Map.Entry) iterator.next();
                        DonationDetails donationDetails = objectMapper.convertValue(myMapEntry.getValue(), DonationDetails.class);
                            userDonationList.put(myMapEntry.getKey().toString(),donationDetails);
                        Log.i("userDoantion",userDonationList.toString());
                    }

                    enableUserInteraction();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        userDonationCount = 0;
        userDonationList.clear();
    }


    private void onClickDelete(String pushID){

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations").
                child(FirebaseAuth.getInstance().getUid()).child(pushID);
        mDatabaseReference.removeValue();

    }

    private void enableUserInteraction()
    {

        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);

        mAdapter = new DonationAdapter(userDonationList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);


    }
}
