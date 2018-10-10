package com.android.developer.feedingindia.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.adapters.DonationAdapter;
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MyDonationsFragment extends Fragment {

    private DatabaseReference donationDatabaseReference;
    private ChildEventListener mChildEventListener;
    private long userDonationCount,readCount;
    private ProgressBar progressBar;
    private ArrayList<DonationDetails> userDonationList;
    private RecyclerView mRecyclerView;
    private DonationAdapter mAdapter;
    private ArrayList<String> donationIdList;
    private android.support.v7.app.AlertDialog mAlertDialog;
    private android.support.v7.app.AlertDialog.Builder mBuilder;

    public MyDonationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        userDonationList = new ArrayList<>();
        donationIdList = new ArrayList<>();
        userDonationCount = readCount = 0;
        donationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations").
                        child(FirebaseAuth.getInstance().getUid());

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                DonationDetails donationDetails = dataSnapshot.getValue(DonationDetails.class);
                userDonationList.add(donationDetails);
                donationIdList.add(dataSnapshot.getKey());

                readCount++;

                if(readCount == userDonationCount)
                    enableUserInteraction();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder = new android.support.v7.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        else
            mBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_donations, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.donations_container);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        if(userDonationList.size() == 0) {

            donationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    userDonationCount = readCount = 0;
                    userDonationList = new ArrayList<>();
                    donationIdList = new ArrayList<>();

                    userDonationCount = dataSnapshot.getChildrenCount();

                    if (userDonationCount == 0)
                        enableUserInteraction();
                    else
                        donationDatabaseReference.addChildEventListener(mChildEventListener);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    makeToast(databaseError.getMessage());

                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mChildEventListener != null)
            donationDatabaseReference.removeEventListener(mChildEventListener);


        if(mAlertDialog!=null)
            if(mAlertDialog.isShowing())
                mAlertDialog.cancel();

    }

    private void enableUserInteraction()
    {

        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        buildRecyclerView();

    }

    private void buildRecyclerView(){

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new DonationAdapter(userDonationList);
        mRecyclerView.setAdapter(mAdapter);
        attachListenerToRecyclerView();

    }

    private void attachListenerToRecyclerView() {

        mAdapter.setOnItemClickListener(new DonationAdapter.OnClickListener() {
            @Override
            public void onClick(final int position) {

                if(userDonationList.get(position).getStatus().equals("pending")){

                    mAlertDialog = mBuilder.setMessage("Do you want to cancel the donation?").
                            setTitle("Cancel Donation").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    donationDatabaseReference.child(donationIdList.get(position)).removeValue();
                                    userDonationList.remove(position);
                                    donationIdList.remove(position);
                                    mAdapter.notifyItemRemoved(position);

                                }
                            }).setNegativeButton("No",null).create();

                    mAlertDialog.show();

                }
            }

            @Override
            public void onClickImage(ImageView view, int position) {

                //Intent to Enlarged ImageView Activity

            }
        });

    }

    private void makeToast(String message){

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }
}
