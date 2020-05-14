package com.example.projectteamportal;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileMain extends AppCompatActivity {

    private TextView TVname, TVemail, TVcollege;
    private EditText ETabout, ETskills, oldPass, newPass, newCPass;
    private CircleImageView IVprofilepic;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private Button save,changePassword,confirmChangePassword;
    private String userID, downloadURI, profilePic,email;
    private Uri imageUri;
    private ProgressDialog dialog;
    private LinearLayout changeLayout;
    private TextInputLayout TLold, TLnew,TLCnew;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=\\S+$)" +
            ".{6,}" +
            "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_main);
        firebaseAuth = FirebaseAuth.getInstance();
        TVname = (TextView) findViewById(R.id.TVname);
        TVemail = (TextView) findViewById(R.id.TVemail);
        TVcollege = (TextView) findViewById(R.id.TVcollegeName);
        ETabout = (EditText) findViewById(R.id.ETabout);
        ETskills = (EditText) findViewById(R.id.ETskills);
        IVprofilepic = (CircleImageView) findViewById(R.id.IVprofileimage);
        save = (Button) findViewById(R.id.BTsaveProfile);
        save.setVisibility(View.GONE);
        userID = getIntent().getExtras().getString("uid");
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("Users");
        storageRef = FirebaseStorage.getInstance().getReference("Profile");

        oldPass = findViewById(R.id.et_old_Password);
        newPass = findViewById(R.id.et_new_Password);
        newCPass = findViewById(R.id.et_new_cPassword);

        TLold = findViewById(R.id.TL_old_password);
        TLnew = findViewById(R.id.TL_new_password);
        TLCnew =findViewById(R.id.TL_new_cpassword);

        changeLayout = findViewById(R.id.change_password_layout);

        changePassword = findViewById(R.id.BTPassword);
        confirmChangePassword = findViewById(R.id.BT_confirm_Password);

        dialog = new ProgressDialog(UserProfileMain.this);
        dialog.setTitle("Edit Profile");
        dialog.setMessage("Attempting to save changes...");
        dialog.setCancelable(false);

        profilePic = getIntent().getExtras().getString("profilePic");
        if (profilePic != null){
            try {

                Picasso.with(getApplicationContext()).load(Uri.parse(profilePic)).networkPolicy(NetworkPolicy.OFFLINE).into(IVprofilepic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getApplicationContext()).load(Uri.parse(profilePic)).into(IVprofilepic);

                    }
                });
            }catch (Exception e){

            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                save.setVisibility(View.GONE);
            }
        });



        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword.setVisibility(View.GONE);
                changeLayout.setVisibility(View.VISIBLE);
            }
        });

        confirmChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_Password();

            }
        });




        dbRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null){
                    User user = dataSnapshot.getValue(User.class);
                    TVname.setText(user.getName());
                    TVemail.setText(user.getEmail());
                    email = user.getEmail();
                    TVcollege.setText(user.getCollege());

                    if (user.getAbout() != null){
                        ETabout.setText(user.getAbout());

                    }

                    if (user.getSkills() != null){
                        ETskills.setText(user.getSkills());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                ETabout.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        save.setVisibility(View.VISIBLE);
                    }
                });

                ETskills.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        save.setVisibility(View.VISIBLE);
                    }
                });

                oldPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        old_passwordValidate();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                newPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        new_passwordValidate();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                newCPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        passValidate();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        }, 1000);








        IVprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,100);
            }
        });

    }

    private boolean old_passwordValidate(){
        String password = oldPass.getText().toString();
        if(password.isEmpty()){
            TLold.setError("Field cannot be empty.");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(password).matches()){
            TLold.setError("Password should be min 6 char & no white spaces.");
            return false;
        }else{
            TLold.setError(null);
            return true;
        }
    }

    private boolean new_passwordValidate(){
        String password = newPass.getText().toString();
        if(password.isEmpty()){
            TLnew.setError("Field cannot be empty.");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(password).matches()){
            TLnew.setError("Password should be min 6 char & no white spaces.");
            return false;
        }else{
            TLnew.setError(null);
            return true;
        }
    }

    private boolean passValidate(){
        String password = newPass.getText().toString().trim();
        String cpassword = newCPass.getText().toString().trim();
        if (password.equals(cpassword)){
            TLCnew.setError(null);
            return true;
        }else {
            TLCnew.setError("Passwords don't match.");
            return false;
        }
    }


    private void change_Password(){

        final ProgressDialog dialog_p = new ProgressDialog(UserProfileMain.this);
        dialog_p.setTitle("Change Password");
        dialog_p.setMessage("Attempting to change password...");
        dialog_p.setCancelable(false);

        if (old_passwordValidate() && new_passwordValidate() && passValidate()){
            dialog_p.show();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email,oldPass.getText().toString().trim());

            firebaseAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        TLold.setError(null);
                        firebaseAuth.getCurrentUser().updatePassword(newPass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialog_p.dismiss();
                                    changeLayout.setVisibility(View.GONE);
                                    changePassword.setVisibility(View.VISIBLE);
                                    Toast.makeText(UserProfileMain.this,"Password update successully.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        dialog_p.dismiss();
                        Toast.makeText(UserProfileMain.this,"Current Password is incorrect",Toast.LENGTH_SHORT).show();
                        TLold.setError("Incorrect Password");
                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Log.d("Picture Upload message","Entered activity result");
            imageUri = data.getData();
            uploadFile();



        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void  uploadFile(){
        if(imageUri != null){
            final StorageReference fileReference = storageRef.child(userID + "." + getFileExtention(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURI = uri.toString();
                            profilePic = downloadURI;
                            dbRef.child(userID).child("profilePic").setValue(downloadURI).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    try{

                                        Picasso.with(getApplicationContext()).load(Uri.parse(profilePic)).networkPolicy(NetworkPolicy.OFFLINE).into(IVprofilepic, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(getApplicationContext()).load(Uri.parse(profilePic)).into(IVprofilepic);
                                            }
                                        });
                                    }catch (Exception e){

                                    }
                                    Toast.makeText(UserProfileMain.this, "Profile Pic Updated",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfileMain.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public void saveData(){
        dialog.show();
        String about = ETabout.getText().toString().trim();
        String skills = ETskills.getText().toString().trim();

        dbRef.child(userID).child("about").setValue(about);
        dbRef.child(userID).child("skills").setValue(skills).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                Toast.makeText(UserProfileMain.this, "User Profile updated!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
