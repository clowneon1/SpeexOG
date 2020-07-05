package com.clowneon1.speex.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clowneon1.speex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import Home.MainActivity;


public class SignInActivity extends AppCompatActivity {
    private Button sign_up;
    private Button sign_in;
    private TextView txt_click;
    private EditText email_sig_in;
    private EditText password_sign_in;
    private ProgressDialog progressDialog;
    DataBaseRef dataBaseRef = new DataBaseRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        sign_up = (Button)findViewById(R.id.new_sign_up);
        sign_in = (Button)findViewById(R.id.sign_in);
        txt_click = (TextView)findViewById(R.id.sign_up_txt);
        email_sig_in = (EditText)findViewById(R.id.Email_sign_in);
        password_sign_in = (EditText)findViewById(R.id.Pass_sign_in);

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing in");

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        txt_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EMAIL = email_sig_in.getText().toString();
                String PASSWORD = password_sign_in.getText().toString();

                if(TextUtils.isEmpty(EMAIL)){
                    Toast.makeText(SignInActivity.this,"Email address cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(PASSWORD)){
                    Toast.makeText(SignInActivity.this,"Password cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if(PASSWORD.length() < 6){
                    Toast.makeText(SignInActivity.this,"Password must be at least 6 letters",Toast.LENGTH_LONG).show();
                }
                else{
                    progressDialog.show();
                    sign_in.setEnabled(false);
                    signInAccount(EMAIL ,PASSWORD);
                }
            }
        });
    }
    private void signInAccount(String txt_Email , String txt_Password){
        dataBaseRef.Auth.signInWithEmailAndPassword(txt_Email ,txt_Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                        if(dataBaseRef.getCurrentUser().isEmailVerified()) {
                            progressDialog.dismiss();
                            returnToMain();
                        }
                        else{
                            progressDialog.dismiss();
                            resendVerificationMail();
                        }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this, "Invalid email address or wrong password", Toast.LENGTH_SHORT).show();
                sign_in.setEnabled(true);
            }
        });
    }

    private void returnToMain(){
        Intent ToMain = new Intent(SignInActivity.this, MainActivity.class);
        ToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ToMain);
        finish();
    }
    private void resendVerificationMail(){
        dataBaseRef.getCurrentUser().sendEmailVerification();
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Verification");
        builder.setMessage("Verification email sent. Please verify and sign in again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog Alert = builder.create();
        Alert.show();
    }
}