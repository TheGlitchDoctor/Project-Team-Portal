package com.example.projectteamportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView goback;
    private EditText Name, Email, Password, CPassword, College;
    private Button btRegister;
    private FirebaseDatabase database;
    private TextInputLayout tlName, tlEmail, tlPassword, tlCpassword, tlCollege;
    private ProgressDialog dialog;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=\\S+$)" +
            ".{6,}" +
            "$");

    private static final Pattern NAME_PATTERN = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{1,}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        goback = (TextView) findViewById(R.id.tvalreadyreg);
        Name = (EditText) findViewById(R.id.etNameReg);
        Email = (EditText) findViewById(R.id.etEmailReg);
        Password = (EditText) findViewById(R.id.etPasswordReg);
        CPassword = (EditText) findViewById(R.id.etCPasswordReg);
        College = (EditText) findViewById(R.id.etcollegeReg);
        btRegister = (Button) findViewById(R.id.btRegister2);

        tlName = findViewById(R.id.TL_r_name);
        tlEmail = findViewById(R.id.TL_r_email);
        tlPassword = findViewById(R.id.TL_r_password);
        tlCpassword = findViewById(R.id.TL_r_cpassword);
        tlCollege = findViewById(R.id.TL_r_college);

        dialog = new ProgressDialog(Registration.this);
        dialog.setTitle("Registration");
        dialog.setMessage("Attempting to Register...");
        dialog.setCancelable(false);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,LoginActivity.class));
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){

                    dialog.show();
                    final DatabaseReference dbref = database.getReference();
                    String name = Name.getText().toString().trim();
                    String email = Email.getText().toString().trim();
                    String pass = Password.getText().toString().trim();
                    String college = College.getText().toString().trim();
                    final User user = new User(name, email, college);

                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                String uid = firebaseAuth.getUid();
                                assert uid != null;
                                dbref.child("Users").child(uid).setValue(user);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        dialog.dismiss();
                                        sendVerification();
                                        Toast.makeText(Registration.this, "Registration Successful! Please verify your Email.",Toast.LENGTH_LONG).show();
                                        firebaseAuth.signOut();
                                        Intent intent = new Intent(Registration.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }, 2000);

                            }else{
                                dialog.dismiss();
                                Toast.makeText(Registration.this, "Registration Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameValidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailValidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordValidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        CPassword.addTextChangedListener(new TextWatcher() {
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

        College.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                collegeValidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private Boolean validate(){


        if (nameValidate() && emailValidate() && passwordValidate() && collegeValidate() && passValidate()){
            return true;
        }else{
            return false;
        }
    }

    private void sendVerification(){
        FirebaseUser User = firebaseAuth.getCurrentUser();
        User.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    private boolean emailValidate(){

        String email = Email.getText().toString();
        if (email.isEmpty()){
            tlEmail.setError("Field cannot be empty.");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            tlEmail.setError("Please enter a valid Email address.");
            return false;
        }else{
            tlEmail.setError(null);
            return true;
        }

    }

    private boolean passwordValidate(){
        String password = Password.getText().toString();
        if(password.isEmpty()){
            tlPassword.setError("Field cannot be empty.");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(password).matches()){
            tlPassword.setError("Password should be min 6 char & no white spaces.");
            return false;
        }else{
            tlPassword.setError(null);
            return true;
        }
    }



    private boolean collegeValidate(){
        String college = College.getText().toString().trim();
        if (college.isEmpty()){
            tlCollege.setError("Field cannot be empty.");
            return false;
        }else if (!NAME_PATTERN.matcher(college).matches()){
            tlCollege.setError("Invalid characters");
            return false;
        }else {
            tlCollege.setError(null);
            return true;
        }
    }
    private boolean nameValidate(){
        String name = Name.getText().toString().trim();
        if (name.isEmpty()){
            tlName.setError("Field cannot be empty.");
            return false;
        }else if (!NAME_PATTERN.matcher(name).matches()){
            tlName.setError("Invalid characters");
            return false;
        }else {
            tlName.setError(null);
            return true;
        }
    }
    private boolean passValidate(){
        String password = Password.getText().toString().trim();
        String cpassword = CPassword.getText().toString();
        if (password.equals(cpassword)){
            tlCpassword.setError(null);
            return true;
        }else {
            tlCpassword.setError("Passwords don't match.");
            return false;
        }
    }

}


