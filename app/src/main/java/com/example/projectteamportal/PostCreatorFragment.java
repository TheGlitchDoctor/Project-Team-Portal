package com.example.projectteamportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class PostCreatorFragment extends Fragment {

    private View view;
    private String postid, title, description, creatorid, creatorname, startdate, createdate, userid,ndescription;
    private Button btsave, btdelete;
    private EditText etdesc;
    private DatabaseReference Postdb;
    private ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private CircleImageView IVpostimage;

    public PostCreatorFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.post_creator_fragment,container,false);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        creatorid = getArguments().getString("creatorid");
        postid = getArguments().getString("postid");
        title = getArguments().getString("title");
        description = getArguments().getString("description");
        creatorid = getArguments().getString("creatorid");
        creatorname = getArguments().getString("creatorname");
        startdate = getArguments().getString("startdate");
        createdate = getArguments().getString("createdate");
        etdesc = view.findViewById(R.id.etpostDesc);
        ((TextView)view.findViewById(R.id.tvpostTitle)).setText(title);
        etdesc.setText(description);
        ((TextView)view.findViewById(R.id.tvpostCreator)).setText(creatorname);
        ((TextView)view.findViewById(R.id.tvpoststartDate)).setText(startdate);
        ((TextView)view.findViewById(R.id.tvpostcreateDate)).setText(createdate);

        IVpostimage = view.findViewById(R.id.IVpostimage);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"+creatorid);
        userRef.keepSynced(true);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final User user = dataSnapshot.getValue(User.class);
                    if (user.getProfilePic() != null){
                        try{

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

        Postdb = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        Postdb.keepSynced(true);
        etdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btsave.setVisibility(View.VISIBLE);
            }
        });

        btdelete = view.findViewById(R.id.BTdelete);
        btsave = view.findViewById(R.id.BTsave);

        btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (descValidate()){
                    btsave.setVisibility(View.GONE);
                    dialog.setTitle("Edit Post");
                    dialog.setMessage("Saving changes...");
                    dialog.show();
                    ndescription = etdesc.getText().toString();
                    Postdb.child("description").setValue(ndescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Toast.makeText(getContext(),"Post updated successfully",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });


        btdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
                final View mView = layoutInflaterAndroid.inflate(R.layout.delete_confirm_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
                alertDialogBuilderUserInput.setView(mView);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialog.setTitle("Deleting Post");
                                dialog.setMessage("Attempting to delete post...");
                                dialog.show();
                                joinedDelete();
                                notificationDelete();
                                joinRequestdelete();
                                dialog.dismiss();



                                try {

                                    Postdb.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                try {
                                                    Toast.makeText(getActivity(),"Post deleted successfully",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getActivity(), Home.class);
                                                    startActivity(intent);
                                                    getActivity().finish();

                                                }catch (Exception e){

                                                }

                                            }
                                        }
                                    });
                                }catch (Exception e){

                                }

                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();






            }
        });

        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private boolean descValidate(){
        String desc = etdesc.getText().toString();
        if (desc.isEmpty()){
            Toast.makeText(getActivity(),"Description cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }


    public void joinedDelete(){
        final DatabaseReference JP_Ref = FirebaseDatabase.getInstance().getReference("Joined_projects");
        JP_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot user : dataSnapshot.getChildren()){
                        final String key = user.getKey();
                        Log.d("USERID: ",key);
                        JP_Ref.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot e: dataSnapshot.getChildren()){
                                        String keyx = e.getKey();
                                        Log.d("POSTID",postid);
                                        if (keyx.equals(postid)){
                                            JP_Ref.child(key).child(keyx).removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void notificationDelete(){
        final DatabaseReference noti_Ref = FirebaseDatabase.getInstance().getReference("Notifications");
        noti_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (final DataSnapshot notif : dataSnapshot.getChildren()){
                        final String key1 = notif.getKey();
                        noti_Ref.child(key1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (final DataSnapshot notification : dataSnapshot.getChildren() ){
                                        final String key2 = notification.getKey();
                                        noti_Ref.child(key1).child(key2).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    if (dataSnapshot.child("projectID").getValue().equals(postid)){
                                                        noti_Ref.child(key1).child(key2).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void joinRequestdelete(){

        final DatabaseReference JR_Ref = FirebaseDatabase.getInstance().getReference("Join_Requests");
        JR_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (final DataSnapshot rec : dataSnapshot.getChildren()){
                        final String key1 = rec.getKey();
                        JR_Ref.child(key1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for(DataSnapshot e : dataSnapshot.getChildren()){
                                        String key = e.getKey();
                                        Request request = e.getValue(Request.class);
                                        if (request.getProjectID().equals(postid)){
                                            JR_Ref.child(key1).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
