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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class JoinedFragment extends Fragment {
    View view;

    private String userid;
    private RecyclerView joinedRV;
    private DatabaseReference joineddb, postdb;
    private FirebaseRecyclerAdapter<Post, JPViewHolder> joinedRVAdapter;
    private TextView defaultText;
    public JoinedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userid = FirebaseAuth.getInstance().getUid();
        joineddb = FirebaseDatabase.getInstance().getReference("Joined_Projects/" + userid);

        postdb = FirebaseDatabase.getInstance().getReference("Posts");


        view = inflater.inflate(R.layout.joined_fragment,container,false);

        defaultText = (TextView) view.findViewById(R.id.join_default);
        joinedRV = (RecyclerView) view.findViewById(R.id.RVjoined);
        joinedRV.hasFixedSize();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        joinedRV.setLayoutManager(manager);
        joineddb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    defaultText.setVisibility(View.VISIBLE);
                    joinedRV.setVisibility(View.GONE);
                }else {
                    defaultText.setVisibility(View.GONE);
                    joinedRV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions joinedOptions = new FirebaseRecyclerOptions.Builder<Post>().setIndexedQuery(joineddb, postdb, Post.class).build();




        joinedRVAdapter = new FirebaseRecyclerAdapter<Post, JPViewHolder>(joinedOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final JPViewHolder holder, int position, @NonNull Post model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvDesc.setText(model.getDescription());
                holder.tvStartDate.setText(model.getStartdate());
                holder.creatorname.setText(model.getCreatorname());
                holder.createdate.setText(model.getCreatedate());

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + model.getCreatorid());

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
                        intent.putExtra("postid",getRef(holder.getAdapterPosition()).getKey());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public JPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card_row, parent, false);


                return new JPViewHolder(view);
            }
        };


        joinedRV.setAdapter(joinedRVAdapter);
        return view;
    }



    public static class JPViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle, tvStartDate, tvDesc, createdate, creatorname;
        private CircleImageView IVpostImage;
        public JPViewHolder(@NonNull View itemView) {
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
        joinedRVAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        joinedRVAdapter.stopListening();
    }
}
