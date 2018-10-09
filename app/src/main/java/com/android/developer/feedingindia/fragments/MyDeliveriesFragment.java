package com.android.developer.feedingindia.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.adapters.DeliveriesAdapter;
import com.android.developer.feedingindia.pojos.DeliveryDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;


public class MyDeliveriesFragment extends Fragment {

    private DatabaseReference deliveryDatabaseReference;
    private long deliveryCount = 0;
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private HashMap<String,DeliveryDetails> userDeliveries;
    private ArrayList<DeliveryDetails> deliveriesList;
    private HashMap<String,String> pushIdToUserIdMap;
    private AlertDialog mAlertDialog;
    private AlertDialog.Builder mBuilder;
    private FirebaseAuth mAuth;

//

    private boolean canAddImage,notSelected;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private String imageUrl;
    private View selectedItemView;
    private StorageReference mDeliveryPhotoReference;
    String donationId,donationStatus;
    private int pos;
//

    public MyDeliveriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        deliveryCount = 0;
        userDeliveries = new HashMap<>();
        pushIdToUserIdMap = new HashMap<>();
        deliveriesList = new ArrayList<>();
        deliveryDatabaseReference = FirebaseDatabase.getInstance().getReference().
                child("Deliveries").child(FirebaseAuth.getInstance().getUid());
        mDeliveryPhotoReference = FirebaseStorage.getInstance().getReference().child("delivery_photos");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder = new android.support.v7.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        else
            mBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
//
        canAddImage = false;
//

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_deliveries, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.deliveries_container);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();


        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        deliveryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                deliveryCount = dataSnapshot.getChildrenCount();

                if(deliveryCount == 0)
                    enableUserInteraction();
                else
                {

                    ObjectMapper objectMapper = new ObjectMapper();
                    HashMap<String,HashMap<String,Object>> myList = (HashMap<String,HashMap<String,Object>>)dataSnapshot.getValue();
                    Set mySet = myList.entrySet();
                    Iterator iterator = mySet.iterator();
                    while(iterator.hasNext()){
                        Map.Entry myMapEntry =(Map.Entry) iterator.next();
                        DeliveryDetails deliveryDetails = objectMapper.convertValue(myMapEntry.getValue(), DeliveryDetails.class);
                        userDeliveries.put(myMapEntry.getKey().toString(),deliveryDetails);
                        deliveriesList.add(deliveryDetails);
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

        deliveryCount = 0;
        userDeliveries.clear();
        pushIdToUserIdMap.clear();
        deliveriesList.clear();
    }

    private void enableUserInteraction()
    {

        DeliveriesAdapter mAdapter = new DeliveriesAdapter(deliveriesList);

        mAdapter.setOnItemClickListener(new DeliveriesAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, final View view) {

                final TextView statusTextView = view.findViewById(R.id.status);
                    pos = position;
                if(statusTextView.getText().toString().equals("PENDING")) {

                    mAlertDialog = mBuilder.setMessage("What's the status of delivery?")
                            .setTitle("Status")
                            .setPositiveButton("DELIVERED", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String status = "delivered";
                                    statusTextView.setText(status.toUpperCase());
                                    //changeDonationStatus(view.getTag().toString(),status);
                                    donationId = view.getTag().toString();
                                    donationStatus = status;
                                    ShowAlert();
                                }
                            })

                            .setNegativeButton("CANCEL DELIVERY", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    String status = "cancelled";
                                    statusTextView.setText(status.toUpperCase());
                                    changeDonationStatus(view.getTag().toString(),status);
                                }
                            })
                            .create();
                    mAlertDialog.show();

                }
//

            }
        });
//
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }
//
    private void ShowAlert(){
        mAlertDialog = mBuilder.setMessage("Do You want to add the image of delivery?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openGallery();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        mAlertDialog.show();
    }
    private void openGallery() {
    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    gallery.setType("image/jpeg");
    startActivityForResult(gallery, PICK_IMAGE);
        }
//

    private void changeDonationStatus(final String donationId, final String status){

        Query query = FirebaseDatabase.getInstance().getReference().child("Deliveries").
                child(mAuth.getUid()).orderByChild("status").equalTo("pending");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,Object> pHashMap = (HashMap<String,Object>)dataSnapshot.getValue();
                Set mySet = pHashMap.entrySet();
                Iterator iterator = mySet.iterator();
                Map.Entry myMapEntry = (Map.Entry) iterator.next();
                HashMap<String,Object> cHashMap = (HashMap<String,Object>)myMapEntry.getValue();
                cHashMap.put("status",status);
                cHashMap.put("deliveryImgUrl",imageUrl);
                DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Deliveries").
                        child(mAuth.getUid()).child(myMapEntry.getKey().toString());
                mDatabaseReference.updateChildren(cHashMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("Donations");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,Object> pHashMap = (HashMap<String,Object>) dataSnapshot.getValue();
                Collection<Object> mCollection = pHashMap.values();
                Set<String> uid = pHashMap.keySet();
                Iterator mIterator = uid.iterator();
                for(Object object : mCollection){
                    String user = mIterator.next().toString();
                    HashMap<String,Object> cHashMap = (HashMap<String,Object>)object;
                    Set mySet = cHashMap.entrySet();
                    Iterator iterator = mySet.iterator();
                    while(iterator.hasNext()) {
                        Map.Entry myMapEntry = (Map.Entry) iterator.next();
                        if (myMapEntry.getKey().equals(donationId)) {
                            HashMap<String,Object> donation = (HashMap<String,Object>)myMapEntry.getValue();
                            if(status.equals("cancelled"))
                            donation.put("status","pending");
                            else {
                                donation.put("status", "delivered");
                                donation.put("hungerSpotImgUrl", imageUrl);
                            }
                            databaseReference.child(user).child(donationId).updateChildren(donation);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        onResume();

    }

    private void onClickDelivered(String pushId){

        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().
                getReference().child("Donations").child(pushIdToUserIdMap.get(pushId)).child(pushId);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> myMap = (HashMap<String, Object>) dataSnapshot.getValue();
                myMap.put("status","delivered");
                mDatabaseReference.updateChildren(myMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //
    }

    private void onClickCannotDeliver(String pushId) {

        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().
                getReference().child("Donations").child(pushIdToUserIdMap.get(pushId)).child(pushId);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> myMap = (HashMap<String, Object>) dataSnapshot.getValue();
                myMap.put("deliverer","none");
                myMap.put("status","pending");
                if(myMap.get("canDonate").equals(true))
                    myMap.put("canDonate",false);
                mDatabaseReference.updateChildren(myMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            canAddImage = true;
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference photoRef =
                    mDeliveryPhotoReference.child(imageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "unable to upload image", Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();
                        Log.i("download url",downloadUri.toString());
                        Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
                    }
                    changeDonationStatus(donationId,donationStatus);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        }
    }


