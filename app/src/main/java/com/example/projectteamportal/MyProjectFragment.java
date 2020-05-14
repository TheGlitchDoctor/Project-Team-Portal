package com.example.projectteamportal;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class MyProjectFragment extends Fragment {

    private String uid;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mpRV;
    private DatabaseReference mpdb;
    private FirebaseRecyclerAdapter<Post, mpPostViewHolder> mpRVAdapter;
    private View view;
    private TextView defaultText;
    public MyProjectFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        mpdb = FirebaseDatabase.getInstance().getReference("Posts");
        Query post = mpdb.orderByChild("creatorid").equalTo(uid);

        mpdb.keepSynced(true);
        view = inflater.inflate(R.layout.my_project_fragment,container,false);
        defaultText = (TextView) view.findViewById(R.id.my_default);

        mpRV = (RecyclerView) view.findViewById(R.id.RVmp);
        mpRV.hasFixedSize();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        //WallRecyclerViewAdapter recyclerAdapter = new WallRecyclerViewAdapter(getContext(), postList);
        mpRV.setLayoutManager(manager);

        post.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    defaultText.setVisibility(View.VISIBLE);
                    mpRV.setVisibility(View.GONE);
                }else {
                    defaultText.setVisibility(View.GONE);
                    mpRV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions postOptions = new FirebaseRecyclerOptions.Builder<Post>().setQuery(post, Post.class).build();
        mpRVAdapter = new FirebaseRecyclerAdapter<Post, mpPostViewHolder>(postOptions) {

            @NonNull
            @Override
            public mpPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card_row, parent, false);
                return new mpPostViewHolder(view);


            }

            @Override
            protected void onBindViewHolder(@NonNull final mpPostViewHolder holder, final int position, @NonNull Post model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvDesc.setText(model.getDescription());
                holder.tvStartDate.setText(model.getStartdate());
                holder.creatorname.setText(model.getCreatorname());
                holder.createdate.setText(model.getCreatedate());

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + model.getCreatorid());
                userRef.keepSynced(true);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        final String postPic = user.getProfilePic();
                        if (postPic != null ){
                            try {

                                Picasso.with(getActivity()).load(Uri.parse(postPic)).networkPolicy(NetworkPolicy.OFFLINE).into(holder.IVpostImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        Picasso.with(getActivity()).load(Uri.parse(postPic)).into(holder.IVpostImage);

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


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(),PostDetails.class);
                        intent.putExtra("postid",getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        mpRV.setAdapter(mpRVAdapter);


        return view;
    }


    public static class mpPostViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle, tvStartDate,tvDesc, createdate, creatorname;
        private CircleImageView IVpostImage;

        public mpPostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.TV_post_title);
            tvStartDate = (TextView) itemView.findViewById(R.id.TV_post_startdate);
            tvDesc = (TextView) itemView.findViewById(R.id.TV_post_desc);
            createdate = (TextView) itemView.findViewById(R.id.TV_createDate);
            creatorname = (TextView) itemView.findViewById(R.id.TV_creatorName);
            IVpostImage = (CircleImageView) itemView.findViewById(R.id.IV_post_image_id);

        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mpRVAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        mpRVAdapter.stopListening();
    }


}
