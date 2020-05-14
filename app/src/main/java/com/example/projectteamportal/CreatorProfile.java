package com.example.projectteamportal;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class CreatorProfile extends AppCompatActivity {

    private TextView TVname, TVemail, TVcollege, TVLabout,TVabout,TVLskills,TVskills;
    private CircleImageView IVprofilepic;
    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dbRef, JR_ref, JP_ref;
    private Button btaccept, btdecline;

    private String userID, profilePic, current_UID, projectid, current_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_profile);
        TVname = (TextView) findViewById(R.id.TVnameg);
        TVemail = (TextView) findViewById(R.id.TVemailg);
        TVcollege = (TextView) findViewById(R.id.TVcollegeNameg);
        TVabout = (TextView) findViewById(R.id.TVabout);
        TVskills = (TextView) findViewById(R.id.TVskills);
        TVLabout = (TextView) findViewById(R.id.TVLabout);
        TVLskills = (TextView) findViewById(R.id.TVLskills);
        IVprofilepic = (CircleImageView) findViewById(R.id.IVprofileimageg);
        current_UID = FirebaseAuth.getInstance().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("Users");
        userID = getIntent().getExtras().getString("creatorid");
        JR_ref = firebaseDatabase.getReference("Join_Requests/" + current_UID);
        JP_ref =firebaseDatabase.getReference("Joined_Projects/" + userID);

        btaccept = (Button) findViewById(R.id.BT_accept);
        btdecline = (Button) findViewById(R.id.BT_decline);




        dbRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final User user = dataSnapshot.getValue(User.class);
                    if (user.getProfilePic() != null){
                        try{

                            Picasso.with(getApplicationContext()).load(Uri.parse(user.getProfilePic())).networkPolicy(NetworkPolicy.OFFLINE).into(IVprofilepic, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(Uri.parse(user.getProfilePic())).into(IVprofilepic);

                                }
                            });
                        }catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference current_Ref = FirebaseDatabase.getInstance().getReference("Users/" + current_UID);
        current_Ref.keepSynced(true);
        current_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    current_name = user.getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










        String ver = getIntent().getExtras().getString("ver");

        if (ver.equals("1")){

            projectid = getIntent().getExtras().getString("projectID");



            JR_ref.orderByChild("projectID").equalTo(projectid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot e : dataSnapshot.getChildren()){
                            String key = e.getKey();
                            Request request = e.getValue(Request.class);
                            Log.d("STATUS",request.getStatus());
                            Log.d("PROJECT_ID",request.getProjectID());
                            Log.d("SENDER",request.getSender());
                            if (request.getStatus().equals("0") && request.getProjectID().equals(projectid) && request.getSender().equals(userID)){
                                btaccept.setVisibility(View.VISIBLE);
                                btaccept.setEnabled(true);
                                btdecline.setVisibility(View.VISIBLE);
                                btaccept.setEnabled(true);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else{
            btaccept.setVisibility(View.GONE);
            btaccept.setEnabled(false);
            btdecline.setVisibility(View.GONE);
            btaccept.setEnabled(false);
        }




        profilePic = getIntent().getExtras().getString("profilePic");
        if (profilePic != null){
            Picasso.with(getApplicationContext()).load(Uri.parse(profilePic)).into(IVprofilepic);
        }



        dbRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null){
                    User user = dataSnapshot.getValue(User.class);
                    TVname.setText(user.getName());
                    TVemail.setText(user.getEmail());
                    TVcollege.setText(user.getCollege());

                    if (user.getAbout() != null){
                        TVLabout.setVisibility(View.VISIBLE);
                        TVabout.setVisibility(View.VISIBLE);
                        TVabout.setText(user.getAbout());

                    }

                    if (user.getSkills() != null){
                        TVLskills.setVisibility(View.VISIBLE);
                        TVskills.setVisibility(View.VISIBLE);
                        TVskills.setText(user.getSkills());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JR_ref.orderByChild("projectID").equalTo(projectid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot e : dataSnapshot.getChildren()){
                                String key = e.getKey();
                                Request request = e.getValue(Request.class);
                                if (request.getSender().equals(userID) && request.getProjectID().equals(projectid)){
                                    JR_ref.child(key).child("status").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            JP_ref.child(projectid).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
                                                    HashMap<String,String> notificationData = new HashMap<String, String>();
                                                    notificationData.put("from", current_UID);
                                                    notificationData.put("type","accept");
                                                    notificationData.put("projectID",projectid);
                                                    notificationData.put("sender_name", current_name);

                                                    notificationRef.child(userID).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(CreatorProfile.this, "Request accepted",Toast.LENGTH_SHORT).show();
                                                            btaccept.setEnabled(false);
                                                            btaccept.setVisibility(View.GONE);
                                                            btdecline.setEnabled(false);
                                                            btdecline.setVisibility(View.GONE);
                                                        }
                                                    });

                                                }
                                            });


                                        }
                                    });
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });

        btdecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JR_ref.orderByChild("projectID").equalTo(projectid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot e : dataSnapshot.getChildren()){
                                String key = e.getKey();
                                Request request = e.getValue(Request.class);
                                if (request.getSender().equals(userID) && request.getProjectID().equals(projectid)){

                                    JR_ref.child(key).child("status").setValue("2").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
                                            HashMap<String,String> notificationData = new HashMap<String, String>();
                                            notificationData.put("from", current_UID);
                                            notificationData.put("type","decline");
                                            notificationData.put("projectID",projectid);
                                            notificationData.put("sender_name", current_name);

                                            notificationRef.child(userID).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(CreatorProfile.this, "Request declined",Toast.LENGTH_SHORT).show();
                                                    btaccept.setEnabled(false);
                                                    btaccept.setVisibility(View.GONE);
                                                    btdecline.setEnabled(false);
                                                    btdecline.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    });
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }


}
