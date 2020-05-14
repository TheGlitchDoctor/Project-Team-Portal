package com.example.projectteamportal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.params.BlackLevelPattern;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestPage extends AppCompatActivity {

    private RecyclerView requestRV;
    private DatabaseReference requestdb;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter<Request, RequestViewHolder> RequestRVAdapter;
    private String userID;
    private TextView defaultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_page);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        defaultText = (TextView) findViewById(R.id.request_default);


        requestdb = FirebaseDatabase.getInstance().getReference("Join_Requests").child(userID);
        Query requestQuery = requestdb.orderByChild("date");
        requestdb.keepSynced(true);

        requestRV = (RecyclerView) findViewById(R.id.RV_request);
        requestRV.hasFixedSize();
        LinearLayoutManager manager = new LinearLayoutManager(RequestPage.this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        requestRV.setLayoutManager(manager);


        requestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    defaultText.setVisibility(View.VISIBLE);
                    requestRV.setVisibility(View.GONE);
                }else {
                    defaultText.setVisibility(View.GONE);
                    requestRV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        FirebaseRecyclerOptions requestOptions = new FirebaseRecyclerOptions.Builder<Request>().setQuery(requestQuery, Request.class).build();
        RequestRVAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(requestOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull final Request model) {

                holder.TVname.setText(model.getSenderName());
                holder.TVprojectTitle.setText(model.getProjectName());
                holder.TVsentDate.setText(model.getDate());

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + model.getSender());
                userRef.keepSynced(true);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        final String postPic = user.getProfilePic();
                        if (postPic != null ){
                            try {

                                Picasso.with(getApplicationContext()).load(Uri.parse(postPic)).networkPolicy(NetworkPolicy.OFFLINE).into(holder.IVpostImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getApplicationContext()).load(Uri.parse(postPic)).into(holder.IVpostImage);
                                    }
                                });
                            }catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if (model.getStatus().equals("0")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(RequestPage.this, CreatorProfile.class);
                            intent.putExtra("ver","1");
                            intent.putExtra("creatorid",model.getSender());
                            intent.putExtra("projectID",model.getProjectID());
                            startActivity(intent);

                        }
                    });
                }else if (model.getStatus().equals("1")) {
                    holder.TVstatus.setVisibility(View.VISIBLE);
                    holder.TVstatus.setText("ACCEPTED!");
                    holder.TVstatus.setTextColor(Color.parseColor("#FF14488A"));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(RequestPage.this,"Action is already taken!",Toast.LENGTH_SHORT).show();

                        }
                    });
                }else {
                    holder.TVstatus.setVisibility(View.VISIBLE);
                    holder.TVstatus.setText("DECLINED!");
                    holder.TVstatus.setTextColor(Color.parseColor("#FFAF1623"));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(RequestPage.this,"Action is already taken!",Toast.LENGTH_SHORT).show();

                        }
                    });

                }



            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);

                return new RequestViewHolder(view);
            }
        };



        requestRV.setAdapter(RequestRVAdapter);

    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);


    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestRVAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestRVAdapter.stopListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        private ImageView IVsenderImage;
        private TextView TVname, TVsentDate, TVprojectTitle,TVstatus;
        private CircleImageView IVpostImage;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            TVname = (TextView) itemView.findViewById(R.id.TV_sender_name);
            TVsentDate = (TextView) itemView.findViewById(R.id.TV_sentDate);
            IVsenderImage = (ImageView) itemView.findViewById(R.id.IV_sender_image);
            TVprojectTitle = (TextView) itemView.findViewById(R.id.TV_project_title);
            TVstatus = (TextView) itemView.findViewById(R.id.TV_status);
            IVpostImage = (CircleImageView) itemView.findViewById(R.id.IV_sender_image);

        }

    }
}


