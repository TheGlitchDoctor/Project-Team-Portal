package com.example.projectteamportal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostNormalFragment extends Fragment {


    private View view;
    private String postid, title, description, creatorid, creatorname, startdate, createdate;
    private TextView tvcreatorLink, TVstatus;
    private Button btjoin;
    private DatabaseReference userRef;
    private DatabaseReference JoinRequestRef1, JoinRequestRef2;
    private FirebaseAuth firebaseAuth;
    private String current_state, userID ;
    private boolean flag = false;
    private CircleImageView IVpostimage;
    public PostNormalFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.post_normal_fragment,container,false);
        postid = getArguments().getString("postid");
        title = getArguments().getString("title");
        description = getArguments().getString("description");
        creatorid = getArguments().getString("creatorid");
        creatorname = getArguments().getString("creatorname");
        startdate = getArguments().getString("startdate");
        createdate = getArguments().getString("createdate");

        btjoin = (Button) view.findViewById(R.id.BTjoin);
        TVstatus = (TextView) view.findViewById(R.id.TV_status);
        TVstatus.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        IVpostimage = view.findViewById(R.id.IVpostimage);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"+creatorid);
        userRef.keepSynced(true);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final User user = dataSnapshot.getValue(User.class);
                    if (user.getProfilePic() != null){
                        try {
                            Picasso.with(getActivity().getApplicationContext()).load(Uri.parse(user.getProfilePic())).networkPolicy(NetworkPolicy.OFFLINE).into(IVpostimage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getActivity().getApplicationContext()).load(Uri.parse(user.getProfilePic())).into(IVpostimage);
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



        JoinRequestRef1 = FirebaseDatabase.getInstance().getReference().child("Join_Requests").child(creatorid);
        JoinRequestRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
//                    btjoin.setEnabled(false);
//                    btjoin.setVisibility(View.GONE);

                    for (DataSnapshot e : dataSnapshot.getChildren()) {
                        String key = e.getKey();
                        Request request = e.getValue(Request.class);
                        if (request.getSender().equals(userID) && request.getProjectID().equals(postid)){
                            String status = request.getStatus();
                            if (status.equals("0")){
                                TVstatus.setText("Join request pending");
                                TVstatus.setVisibility(View.VISIBLE);
                                btjoin.setEnabled(false);
                                btjoin.setVisibility(View.GONE);
                            }else if (status.equals("1")){
                                TVstatus.setText("You have joined this project");
                                TVstatus.setVisibility(View.VISIBLE);
                                btjoin.setEnabled(false);
                                btjoin.setVisibility(View.GONE);
                            }else {
                                TVstatus.setText("Join request declined");
                                TVstatus.setVisibility(View.VISIBLE);
                                btjoin.setEnabled(false);
                                btjoin.setVisibility(View.GONE);
                            }
                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ((TextView)view.findViewById(R.id.tvpostTitle)).setText(title);
        ((TextView)view.findViewById(R.id.tvpostDesc)).setText(description);
        ((TextView)view.findViewById(R.id.tvpostCreator)).setText(creatorname);
        ((TextView)view.findViewById(R.id.tvpoststartDate)).setText(startdate);
        ((TextView)view.findViewById(R.id.tvpostcreateDate)).setText(createdate);
        tvcreatorLink = view.findViewById(R.id.tvcreatorLink);

        tvcreatorLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreatorProfile.class);
                intent.putExtra("creatorid", creatorid);
                intent.putExtra("ver","0");
                startActivity(intent);
            }
        });


        current_state = "not_joined";

        btjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                btjoin.setEnabled(false);
                btjoin.setVisibility(View.GONE);

            }
        });




        return view;
    }



    private void sendRequest() {

        String imageuri;
        userRef = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    User user = dataSnapshot.getValue(User.class);
                    final String name = user.getName();
                    String imageuri = null ;
                    if (user.getProfilePic() != null){
                        imageuri = user.getProfilePic();
                    }


                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c);
                    String createdate = formattedDate;

                    Request request = new Request(userID, name, postid, title, "0", createdate, imageuri);
                    JoinRequestRef2 = FirebaseDatabase.getInstance().getReference().child("Join_Requests");

                    JoinRequestRef2.child(creatorid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                for (DataSnapshot req : dataSnapshot.getChildren()){
                                    Request request = req.getValue(Request.class);
                                    if (request.getSender().equals(userID) && request.getProjectID().equals(postid)){
                                        flag = true;
                                    }

                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if (!flag){
                        JoinRequestRef2.child(creatorid).push().setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
                                HashMap<String,String> notificationData = new HashMap<String, String>();
                                notificationData.put("from", userID);
                                notificationData.put("type","request");
                                notificationData.put("projectID",postid);
                                notificationData.put("title",title);
                                notificationData.put("sender_name", name);

                                notificationRef.child(creatorid).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        try{

                                            Toast.makeText(getActivity(),"Request sent successfully!",Toast.LENGTH_SHORT).show();
                                        }catch (Exception e){
                                            return;
                                        }
                                    }
                                });


                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
