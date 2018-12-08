package com.tolet.sajib.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class Foram extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mUser;
    private FirebaseUser mCurrentUser;
    private TextView textViewName;
    private TextView address;
    private SpotsDialog progressDialog;
    private ProgressBar progressBar;
    private LinearLayout forum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Foram.this,CreatePost.class);
                startActivity(intent);
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Forum");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);

        mCurrentUser = mAuth.getCurrentUser();
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(Foram.this, Registration.class));
                    finish();
                }
            }
        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);

    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        progressDialog.dismiss();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);

        FirebaseRecyclerAdapter<HelpModel, Foram.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HelpModel,
                Foram.ViewHolder>(HelpModel.class, R.layout.helplist_post, Foram.ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(Foram.ViewHolder viewHolder, final HelpModel model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setDetails(model.getDetails());
                viewHolder.setDate(model.getDate());
                viewHolder.setAddress(model.getAddress());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setPro_pic(getApplicationContext(),model.getPro_pic());
                viewHolder.setPost_image(getApplicationContext(),model.getPost_image());
                progressDialog.dismiss();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ShowFullPostActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("post_key", post_key);
                        startActivity(intent);
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }




    public  static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView nameof = (TextView) mView.findViewById(R.id.name);
            nameof.setText(name);
        }

        public void setAddress(String address){
            TextView rollif = (TextView) mView.findViewById(R.id.address);
            rollif.setText(address);
        }
        public void setTitle(String title){
            TextView titleof = (TextView) mView.findViewById(R.id.title);
            titleof.setText(title);
        }
        public void setDetails(String details){
            TextView detailsof = (TextView) mView.findViewById(R.id.details);
            detailsof.setText(details);
        }
        public void setDate(String date){
            TextView dateof = (TextView) mView.findViewById(R.id.date);
            dateof.setText(date);
        }
        public void setPro_pic(final Context context, final String pro_pic){
            final ImageView propic = (ImageView) mView.findViewById(R.id.pro_image);

            Picasso.with(context).load(pro_pic).networkPolicy(NetworkPolicy.OFFLINE).into(propic, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(pro_pic).into(propic);
                }
            });
        }
        public void setPost_image(final Context context, final String post_image){
            final ImageView postimage = (ImageView) mView.findViewById(R.id.Post_image);

            Picasso.with(context).load(post_image).networkPolicy(NetworkPolicy.OFFLINE).into(postimage, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(post_image).into(postimage);
                }
            });
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
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
        getMenuInflater().inflate(R.menu.forum, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_forum) {
//tttttttttttttttttttttttttttttttttttttttttttttttttttt

        }
        else if (id == R.id.nav_logout) {



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
