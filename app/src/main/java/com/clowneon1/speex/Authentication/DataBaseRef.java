package com.clowneon1.speex.Authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;

public class DataBaseRef {
    public FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseUser CurrentUser;
    private String tempString;


    private DatabaseReference UserDataRef = FirebaseDatabase.getInstance().getReference().child("User");
    private DatabaseReference UserInfo = FirebaseDatabase.getInstance().getReference().child("Users").child("Userinfo");
    private DatabaseReference UserList = FirebaseDatabase.getInstance().getReference().child("Users").child("Userlist");

    public DatabaseReference getUserDataRef(){
        return UserDataRef;
    }
    public DatabaseReference getUserList(){
        return UserList;
    }
    public DatabaseReference getUserInfo(){
        return UserInfo;
    }
    public FirebaseUser getCurrentUser(){
        this.CurrentUser = Auth.getCurrentUser();
       return CurrentUser;
    }
    public String getCurrentUserId (){
        if (this.getCurrentUser() != null) {
            this.tempString = this.CurrentUser.getUid();
        }
        return this.tempString;
    }
}
