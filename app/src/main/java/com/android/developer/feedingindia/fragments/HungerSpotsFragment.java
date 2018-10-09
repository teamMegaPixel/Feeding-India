package com.android.developer.feedingindia.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.pojos.HungerSpot;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HungerSpotsFragment extends Fragment {

    private ProgressBar progressBar;
    private DatabaseReference mDatabaseReference;
    private Query hungerSpotQuery;
    private ChildEventListener childEventListener;
    private String userName;
    private ArrayList<Location> mHungerSpots;
    private long hungerSpotCount,readHungerSpots = 0;
    private LinearLayout mLinearLayout;

    public HungerSpotsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        readHungerSpots = hungerSpotCount = 0;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.android.developer.feedingindia", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("name","");
        mHungerSpots = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("HungerSpots");
        hungerSpotQuery = FirebaseDatabase.getInstance().getReference().
                        child("HungerSpots").orderByChild("addedBy").equalTo(userName);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    HungerSpot hungerSpot = dataSnapshot.getValue(HungerSpot.class);
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(hungerSpot.getLatitude());
                    location.setLongitude(hungerSpot.getLongitude());
                    mHungerSpots.add(location);
                    readHungerSpots++;

                if(readHungerSpots == hungerSpotCount)
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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hunger_spots, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mLinearLayout = view.findViewById(R.id.hunger_spot_container);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);

        hungerSpotQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hungerSpotCount = dataSnapshot.getChildrenCount();

                if(hungerSpotCount == 0)
                    enableUserInteraction();
                else
                    hungerSpotQuery.addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if(childEventListener!=null)
            hungerSpotQuery.removeEventListener(childEventListener);
        mHungerSpots.clear();
        readHungerSpots = 0;
    }

    private void enableUserInteraction()
    {
        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    private void addHungerSpot(double latitude, double longitude)
    {
        HungerSpot hungerSpot = new HungerSpot(userName,"pending",latitude,longitude);
        mDatabaseReference.push().setValue(hungerSpot);
        makeToast("Success! HungerSpot added");
    }

    private void makeToast(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

}
