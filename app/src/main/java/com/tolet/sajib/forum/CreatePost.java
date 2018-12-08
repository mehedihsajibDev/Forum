package com.tolet.sajib.forum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CreatePost extends AppCompatActivity {
    private ImageView imageView;
    private EditText title;
    private EditText details;
    private TextView moment_date;

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    private static final int PICK_IMAGE_REQUEST = 234;

    private StorageReference mStorage;

    private Uri uri;

    boolean gallery = false;

    private SpotsDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
//        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
     toolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(), Foram.class);
             startActivity(intent);
             finish();
         }
     });
        progressDialog = new SpotsDialog(this, R.style.Custom_Progress_Bar);
        progressDialog.setMessage("please wait..");

        imageView = (ImageView) findViewById(R.id.image);
        title = (EditText) findViewById(R.id.postTitle);
        details = (EditText) findViewById(R.id.details);
        moment_date = (TextView) findViewById(R.id.moment_date);
        String mDate = DateFormat.getDateTimeInstance().format(new Date());
        moment_date.setText(mDate);

        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Forum");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mStorage = FirebaseStorage.getInstance().getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(CreatePost.this, Registration.class));
                    finish();
                }
            }
        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryImageTake();
                gallery = true;
            }
        });

    }

    private void GalleryImageTake() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void saveInformation() {
        final String fromTitle = title.getText().toString().trim();
        final String fromdetails = details.getText().toString().trim();
        final String moments_date = moment_date.getText().toString().trim();

        if (!TextUtils.isEmpty(fromTitle) && !TextUtils.isEmpty(fromdetails)) {
            progressDialog.show();

            final DatabaseReference newPost = mDatabase.push();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("title").setValue(fromTitle);
                    newPost.child("details").setValue(fromdetails);
                    newPost.child("userID").setValue(mCurrentUser.getUid());
                    newPost.child("date").setValue(moments_date);
                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                    newPost.child("address").setValue(dataSnapshot.child("address").getValue());
                    newPost.child("post_image").setValue("link");
                    newPost.child("pro_pic").setValue(dataSnapshot.child("image").getValue())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        title.setText("");
                                        details.setText("");
                                        Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(CreatePost.this, Foram.class));
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Post Uploading error ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }
        if (TextUtils.isEmpty(fromTitle)) {
            Toast.makeText(this, "Please enter title", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(fromdetails)) {
            Toast.makeText(this, "Please enter details", Toast.LENGTH_LONG).show();
            return;
        }

    }
    private void saveInformationwithImge() {
        final String fromTitle = title.getText().toString().trim();
        final String fromdetails = details.getText().toString().trim();
        final String moments_date = moment_date.getText().toString().trim();
        if (!TextUtils.isEmpty(fromTitle) && !TextUtils.isEmpty(fromdetails) && uri != null) {
            progressDialog.show();

            StorageReference filePath = mStorage.child("post_image").child(uri.getLastPathSegment());
            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUri = taskSnapshot.getDownloadUrl();
                            final DatabaseReference newPost = mDatabase.push();

                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("title").setValue(fromTitle);
                                    newPost.child("details").setValue(fromdetails);
                                    newPost.child("userId").setValue(mCurrentUser.getUid());
                                    newPost.child("date").setValue(moments_date);
                                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());
                                    newPost.child("address").setValue(dataSnapshot.child("address").getValue());
                                    newPost.child("pro_pic").setValue(dataSnapshot.child("image").getValue());
                                    newPost.child("post_image").setValue(downloadUri.toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(CreatePost.this, Foram.class));
                                                        finish();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Post Uploading error ", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });

        }
        if (TextUtils.isEmpty(fromTitle)) {
            Toast.makeText(this, "Please enter title", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(fromdetails)) {
            Toast.makeText(this, "Please enter details", Toast.LENGTH_LONG).show();
            return;
        }
        if (uri==null) {
            Toast.makeText(this, "Please enter image", Toast.LENGTH_LONG).show();
            return;
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            try {
                uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Foram.class);
        startActivity(intent);
        finish();
    }



    public void submit(View view) {
        if(gallery == false){
            saveInformation();
        } else if (gallery == true){
            saveInformationwithImge();
        }
    }
}
