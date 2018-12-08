package com.tolet.sajib.forum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextPhonenumber;
    private EditText editTextDetailAddress;
    private EditText editTextCountry;
    private EditText editTextCity;
    private ImageView pro_picture;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference mDatabase;

    private ImageButton buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
        editTextPhonenumber = (EditText)findViewById(R.id.editTextPhonenumber);
        editTextDetailAddress = (EditText)findViewById(R.id.editTextDetailAddress);
        editTextCountry = (EditText)findViewById(R.id.editTextCountry);
        editTextCity = (EditText)findViewById(R.id.editTextCity);
        pro_picture = (ImageView) findViewById(R.id.pro_picture);
        buttonSignup = (ImageButton) findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();

            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), Foram.class));
        }
        progressDialog = new ProgressDialog(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String confirmpass = editTextConfirmPassword.getText().toString().trim();
        final String phonenumber = editTextPhonenumber.getText().toString().trim();
        final String addresses = editTextDetailAddress.getText().toString().trim();
        final String countries = editTextCountry.getText().toString().trim();
        final String cities = editTextCity.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(confirmpass)){
            Toast.makeText(this,"Please confirm password",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(addresses)){
            Toast.makeText(this,"Please enter address",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(countries)){
            Toast.makeText(this,"Please enter country name",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(cities)){
            Toast.makeText(this,"Please enter city name",Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        if(password.matches(confirmpass)){
            firebaseAuth.createUserWithEmailAndPassword(email, confirmpass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();

                                String user_id = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(user_id);
                                current_user_db.child("name").setValue(name);
                                current_user_db.child("email").setValue(email);
                                current_user_db.child("userID").setValue(user_id);
                                current_user_db.child("phone").setValue(phonenumber);
                                current_user_db.child("address").setValue(addresses);
                                current_user_db.child("country").setValue(countries);
                                current_user_db.child("city").setValue(cities);

                                finish();
                                Intent intent = new Intent(getApplicationContext(), Foram.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(Registration.this,"Registration Error",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show();
        }


    }

    public void signin(View view) {
    }
}
