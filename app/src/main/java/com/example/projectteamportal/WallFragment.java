package com.example.projectteamportal;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WallFragment extends Fragment {

    private RecyclerView wallRV;
    private DatabaseReference walldb;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> WallRVAdapter;
    private TextView defaultText;

    private View view;
    public WallFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        walldb = FirebaseDatabase.getInstance().getReference("Posts");
        walldb.keepSynced(true);
        Query dbpost = walldb.orderByChild("createdDate");
        view = inflater.inflate(R.layout.wall_fragment,container,false);

        defaultText = (TextView) view.findViewById(R.id.wall_default);




        wallRV = (RecyclerView) view.findViewById(R.id.RVwall);
        wallRV.hasFixedSize();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        //WallRecyclerViewAdapter recyclerAdapter = new WallRecyclerViewAdapter(getContext(), postList);
        wallRV.setLayoutManager(manager);
        dbpost.keepSynced(true);

        dbpost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    defaultText.setVisibility(View.VISIBLE);
                    wallRV.setVisibility(View.GONE);

                }else {
                    defaultText.setVisibility(View.GONE);
                    wallRV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions postOptions = new FirebaseRecyclerOptions.Builder<Post>().setQuery(dbpost, Post.class).build();
        WallRVAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(postOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull Post model) {

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
                        intent.putExtra("postid",getRef(holder.getAdapterPosition()).getKey());
                        startActivity(intent);

                    }
                });






            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_card_row, viewGroup, false);


                return new PostViewHolder(view);
            }
        };

        wallRV.setAdapter(WallRVAdapter);


        //wallPostList.setAdapter(recyclerAdapter);
        return view;



    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onStart() {
        super.onStart();
        WallRVAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        WallRVAdapter.stopListening();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle, tvStartDate, tvDesc, createdate, creatorname;
        private CircleImageView IVpostImage;

        public PostViewHolder(@NonNull View itemView) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            Toast.makeText(getActivity(),data.getStringExtra("message"),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), CreatePost.class), 2);

            }


        });





    }
}
