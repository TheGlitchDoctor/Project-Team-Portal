package com.example.projectteamportal;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PostDetails extends AppCompatActivity {

    private DatabaseReference Postdb;
    private FirebaseAuth firebaseAuth;
    private String userID,postid;
    private Query post;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private PostNormalFragment nfragment;
    private PostCreatorFragment cfragment;
    private int flag;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (flag == 0){
            transaction.remove(nfragment);
        }else {
            transaction.remove(cfragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Intent rintent = getIntent();
        postid = rintent.getExtras().getString("postid");
        Log.d("PROJECT_ID",postid);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        Log.d("UID: ",userID);
        Postdb = FirebaseDatabase.getInstance().getReference("Posts");
        Postdb.keepSynced(true);







    }


    @Override
    protected void onStart() {
        super.onStart();
        post = Postdb.child(postid);

        post.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 fm = getSupportFragmentManager();
                 transaction = fm.beginTransaction();
                if (dataSnapshot.getValue() != null){
                    Post POST = dataSnapshot.getValue(Post.class);
                    Bundle data = new Bundle();
                    try {
                        data.putString("postid", postid);
                        data.putString("creatorid", POST.getCreatorid());
                        data.putString("title", POST.getTitle());
                        data.putString("description", POST.getDescription());
                        data.putString("creatorname", POST.getCreatorname());
                        data.putString("startdate", POST.getStartdate());
                        data.putString("createdate", POST.getCreatedate());
                    }catch (NullPointerException n){

                    }
                    Log.d("creatorID: ",POST.getCreatorid());
                    if (!POST.getCreatorid().equals(userID)){
                        nfragment = new PostNormalFragment();
                        nfragment.setArguments(data);
                        transaction.add(R.id.fragmentContainer,nfragment,"");
                        flag = 0;
                    }else{
                        cfragment = new PostCreatorFragment();
                        cfragment.setArguments(data);
                        transaction.add(R.id.fragmentContainer,cfragment,"");
                        flag = 1;
                    }

                    transaction.commitAllowingStateLoss();
                }else {
                    PostDetails.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
