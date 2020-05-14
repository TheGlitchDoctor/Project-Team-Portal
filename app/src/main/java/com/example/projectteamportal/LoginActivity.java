package com.example.projectteamportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private Button signInButton;
    private Button regButton;
    private EditText emailText;
    private EditText passText;
    private FirebaseAuth firebaseAuth;
    private TextInputLayout tlEmail, tlpassword;
    private ProgressDialog dialog;
    private TextView forgotP;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=\\S+$)" +
            ".{6,}" +
            "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = (Button) findViewById(R.id.btSignIn);
        regButton = (Button) findViewById(R.id.btRegister);
        emailText = (EditText) findViewById(R.id.etEmailLogin);
        passText = (EditText) findViewById(R.id.etPasswordLogin);
        tlEmail = findViewById(R.id.TLemail);
        tlpassword = findViewById(R.id.TLpassword);
        forgotP = findViewById(R.id.tv_forgot_pass);

        forgotP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("Signing In");
        dialog.setMessage("Attempting to sign in...");
        dialog.setCancelable(false);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Registration.class));
            }
        });

        emailText.addTextChangedListener(new TextWatcher() {
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


        passText.addTextChangedListener(new TextWatcher() {
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

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String Email = emailText.getText().toString().trim();
                    String Password = passText.getText().toString().trim();
                    dialog.show();
                    firebaseAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser User = firebaseAuth.getCurrentUser();
                                if (User.isEmailVerified()){

                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + User.getUid());
                                    String token = FirebaseInstanceId.getInstance().getToken().toString();
                                    //userRef.child("device_token").child(token).setValue(true);
                                    userRef.child("device_token").setValue(token);
                                    Intent Lintent = new Intent(LoginActivity.this, Home.class);
                                    Lintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Lintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(Lintent);
                                    finish();
                                    dialog.dismiss();
                                }else{
                                    firebaseAuth.signOut();
                                    dialog.dismiss();
                                    Toast.makeText(LoginActivity.this,"Please Verify Your Email",Toast.LENGTH_SHORT).show();
                                }



                            }else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Login Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private Boolean validate(){
        if(passwordValidate() && emailValidate()){
            return true;
        }else{
            return false;
        }
    }


    private boolean emailValidate(){

        String email = emailText.getText().toString();
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
        String password = passText.getText().toString();
        if(password.isEmpty()){
            tlpassword.setError("Field cannot be empty.");
            return false;
        }else if (!PASSWORD_PATTERN.matcher(password).matches()){
            tlpassword.setError("Password should be min 6 char & no white spaces.");
            return false;
        }else{
            tlpassword.setError(null);
            return true;
        }
    }

    private void forgotPassword(){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(LoginActivity.this);
        final View mView = layoutInflaterAndroid.inflate(R.layout.email_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String emailAddress;
                        // ToDo get user input here
                        EditText et_email = mView.findViewById(R.id.userInputDialog);
                        emailAddress = et_email.getText().toString().trim();
                        if (Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                            firebaseAuth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this,"Password reset mail has been sent.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(LoginActivity.this,"Invalid email address",Toast.LENGTH_SHORT).show();

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

}
