package com.example.projectteamportal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbUsersRef;
    private String userID,imageuri;
    private TextView tvdspName, tvdspEmail;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private CircleImageView IVprofile;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        dbUsersRef = FirebaseDatabase.getInstance().getReference("Users");
        dbUsersRef.keepSynced(true);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);

        HomePageAdapter adapter = new HomePageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new WallFragment(), "Wall");
        adapter.AddFragment(new MyProjectFragment(), "My Projects");
        adapter.AddFragment(new JoinedFragment(),"Joined");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userID = firebaseAuth.getUid();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            tvdspName = (TextView) headerView.findViewById(R.id.tvdspName);
            tvdspEmail = (TextView) headerView.findViewById(R.id.tvdspEmail);
            IVprofile = (CircleImageView) headerView.findViewById(R.id.imageView);
            dbUsersRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = (User) dataSnapshot.getValue(User.class);
                    if (tvdspName != null && tvdspEmail != null){
                        tvdspName.setText(user.getName());
                        tvdspEmail.setText(user.getEmail());

                    }
                    if (user.getProfilePic() != null){
                        imageuri = user.getProfilePic();
                        try{

                            Picasso.with(getApplicationContext()).load(Uri.parse(imageuri)).networkPolicy(NetworkPolicy.OFFLINE).into(IVprofile, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(Uri.parse(imageuri)).into(IVprofile);

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
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return false;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_profile) {
            // Handle the camera action
            Intent intent = new Intent(Home.this, UserProfileMain.class);
            intent.putExtra("profilePic",imageuri);
            intent.putExtra("uid",userID);
            startActivity(intent);

        } else if (id == R.id.nav_requests) {

            Intent intent = new Intent(Home.this, RequestPage.class);
            startActivity(intent);


        } else if (id == R.id.signOut){

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + userID);
            userRef.child("device_token").removeValue();


            firebaseAuth.signOut();
            Intent Exintent = new Intent(Home.this,LoginActivity.class);
            Exintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Exintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(Exintent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
