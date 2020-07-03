package com.clowneon1.speex.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.clowneon1.speex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.time.Instant;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private EditText Username;
    private EditText Email;
    private EditText Password;
    private Button New_Sign_up;
    private EditText Name;
    private EditText Surname;
    private boolean isExist = false;
    private ProgressDialog progressDialog;

    private DataBaseRef dataBaseRef = new DataBaseRef();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Email = (EditText)findViewById(R.id.sign_up_email);
        Password = (EditText)findViewById(R.id.sign_up_password);
        New_Sign_up = (Button)findViewById(R.id.new_sign_up);
        Username = (EditText)findViewById(R.id.sign_up_username);
        Name = (EditText)findViewById(R.id.sign_up_name);
        Surname = (EditText)findViewById(R.id.sign_up_surname);

        New_Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String USERNAME = Username.getText().toString();
                final String EMAIL = Email.getText().toString();
                final String PASSWORD = Password.getText().toString();
                final String NAME = Name.getText().toString();
                final String SURNAME = Surname.getText().toString();
                final String TEMPSTRING = USERNAME;
                progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setTitle("Signing up");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait...");


                if(TextUtils.isEmpty(USERNAME) || TextUtils.isEmpty(NAME) || TextUtils.isEmpty(SURNAME)){
                    Toast.makeText(SignUpActivity.this,"Credentials cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(EMAIL)){
                    Toast.makeText(SignUpActivity.this,"Email address cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(PASSWORD)){
                    Toast.makeText(SignUpActivity.this,"Password cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if(PASSWORD.length() < 6){
                    Toast.makeText(SignUpActivity.this,"Password must be at least 6 letters",Toast.LENGTH_LONG).show();
                }
                else{
                    dataBaseRef.getUserList().child(USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(SignUpActivity.this,"User with this username already exists",Toast.LENGTH_LONG).show();
                            }
                            else {
                                progressDialog.show();
                                New_Sign_up.setEnabled(false);
                                createAccount(EMAIL,PASSWORD,USERNAME,NAME,SURNAME);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });

    }

    private void createAccount(final String USER_EMAIL, final String USER_PASSWORD, final String USER_USERNAME, final String NAME, final String SURNAME){
        dataBaseRef.Auth.createUserWithEmailAndPassword(USER_EMAIL,USER_PASSWORD).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap <String , String> UserInfo = new HashMap<>();
                UserInfo.put("Name" ,NAME);
                UserInfo.put("Surname",SURNAME);
                UserInfo.put("Username",USER_USERNAME);
                UserInfo.put("Email" ,USER_EMAIL);
                UserInfo.put("Password",USER_PASSWORD);
                UserInfo.put("ImageURL","default");
                UserInfo.put("ImageThumbURL","default");

                /*HashMap<String , String> UserList = new HashMap<>();
                UserList.put(USER_USERNAME,dataBaseRef.getCurrentUser().getUid());*/


                UserProfileChangeRequest NewProfileInfo = new UserProfileChangeRequest.Builder().setDisplayName(USER_USERNAME).build();
                dataBaseRef.Auth.getCurrentUser().updateProfile(NewProfileInfo);

                dataBaseRef.getUserList().child(USER_USERNAME).setValue(dataBaseRef.getCurrentUserId());
                dataBaseRef.getUserInfo().child(dataBaseRef.getCurrentUserId()).setValue(UserInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataBaseRef.getCurrentUser().sendEmailVerification();
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpActivity.this);
                        builder1.setMessage(R.string.Verification_Mail);
                        builder1.setCancelable(false);
                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dataBaseRef.Auth.signOut();
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder1.create();
                        alert.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "You cannot create Account with this email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    New_Sign_up.setEnabled(true);
            }
        });
    }
}