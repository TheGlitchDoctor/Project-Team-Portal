package com.example.projectteamportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class CreatePost extends AppCompatActivity {


    private EditText etTitle, etDescription;
    private Button btAddPost;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbref;
    private String userID, userName;
    private TextView etStartDate;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private Toolbar mTopToolbar;
    private TextInputLayout tlTitle;
    private ProgressDialog Ldialog;

    private static final Pattern NAME_PATTERN = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{1,}");



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CreatePost.this,Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        etTitle = (EditText) findViewById(R.id.ETtitle);
        etDescription = (EditText) findViewById(R.id.ETdescription);
        etStartDate = (TextView) findViewById(R.id.ETstartDate);
        tlTitle = findViewById(R.id.TL_title);
        btAddPost = (Button) findViewById(R.id.BTaddPost);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);




        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        database = FirebaseDatabase.getInstance();

        dbref = database.getReference("Posts");
        dbref.keepSynced(true);
        DatabaseReference dbuser = database.getReference("Users").child(userID);
        dbuser.keepSynced(true);
        dbuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 userName = dataSnapshot.getValue(User.class).getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Ldialog = new ProgressDialog(CreatePost.this);
        Ldialog.setTitle("Create Post");
        Ldialog.setMessage("Attempting to create post...");
        Ldialog.setCancelable(false);

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleValidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.BTaddPost) {

            if (validate()){
                Ldialog.show();
                Post post = (Post) genPost();
                dbref.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Ldialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("message","Post added successfully");
                            setResult(2,intent);
                            finish();
                        }else {
                            Ldialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra("message","Unable to add Post");
                            setResult(2,intent);
                            finish();
                        }
                    }
                });
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog dialog =  new DatePickerDialog(this,
                    myDateListener, year, month, day);

            dialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            return dialog;
        }


        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    showDate(arg1, arg2+1, arg3);
                }

            };


    private void showDate(int year, int month, int day) {
        etStartDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    private Post genPost(){
        String title, description, createdate;
        String date, uid;
        uid = userID;
        title = etTitle.getText().toString();
        description = etDescription.getText().toString();
        date = etStartDate.getText().toString();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        createdate = formattedDate;

        Post post = new Post(uid, userName, title, description, date, createdate, ServerValue.TIMESTAMP);
        return post;
    }

    private boolean titleValidate(){
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()){
            tlTitle.setError("Field is required");
            return false;
        }else if (!NAME_PATTERN.matcher(title).matches()){
            tlTitle.setError("Invalid characters");
            return false;
        }else {
            tlTitle.setError(null);
            return true;
        }
    }

    private boolean descValidate(){
        String desc = etDescription.getText().toString();
        if (desc.isEmpty()){
            Toast.makeText(CreatePost.this,"Description cannot be empty!",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private boolean validate(){
        if (titleValidate() && descValidate()){
            return true;
        }else {
            return false;
        }
    }

}

