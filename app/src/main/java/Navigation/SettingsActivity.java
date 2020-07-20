package Navigation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.clowneon1.speex.Authentication.DataBaseRef;
import com.clowneon1.speex.Authentication.SignInActivity;
import com.clowneon1.speex.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import User.EditBioActivity;
import User.EditUsernameActivity;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ImageButton imageButton;
    DataBaseRef dataBaseRef = new DataBaseRef();
    TextView display_name;
    public TextView display_status;
    public TextView display_username;
    private String tempString;
    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private TextView networkStatus;
    public TextView display_bio;
    //profilepic
    private ImageView ProfileImg;
    private ImageView edit_profile;
    //progeress dialog
    ProgressDialog uploadingImg;
    //edits
    private ImageButton editUsername;
    private ImageButton editBio;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        imageButton = (ImageButton)findViewById(R.id.imageButton);
        display_name = (TextView)findViewById(R.id.display_name);
        display_status = (TextView)findViewById(R.id.online_status);
        display_username = (TextView)findViewById(R.id.display_username);
        display_bio = (TextView)findViewById(R.id.display_bio);

        //profile pic
        ProfileImg = (ImageView)findViewById(R.id.imageView_);
        edit_profile = (ImageView)findViewById(R.id.edit_pic);
        //toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //edits
        editBio = (ImageButton)findViewById(R.id.edit_Bio);
        editUsername = (ImageButton)findViewById(R.id.edit_username2);

        editBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditBioActivity.class);
                startActivity(intent);
            }
        });
        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditUsernameActivity.class);
                startActivity(intent);
            }
        });

        dataBaseRef.getUserInfo().child(dataBaseRef.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String ThumbImage = Objects.requireNonNull(snapshot.child("ImageThumbURL").getValue()).toString();
                if(!ThumbImage.equals("default")){
                    Glide.with(SettingsActivity.this).load(ThumbImage).into(ProfileImg);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dataBaseRef.getUserInfo().child(dataBaseRef.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempString = snapshot.child("Name").getValue().toString() + " " + snapshot.child("Surname").getValue().toString();
                display_name.setText(tempString);
                display_username.setText(snapshot.child("Username").getValue().toString());
                display_bio.setText(snapshot.child("Bio").getValue().toString());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBaseRef.Auth.signOut();
                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(GalleryIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri Input_uri = data.getData();
            CropImage.activity(Input_uri).setAspectRatio(1,1).start(SettingsActivity.this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                uploadingImg = new ProgressDialog(SettingsActivity.this);
                uploadingImg.setTitle("Updating Image");
                uploadingImg.setMessage("Please wait while updating image...");
                uploadingImg.show();

                File ThumbImgFile = new File(resultUri.getPath());
                try{
                    Bitmap compressedImageBitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(ThumbImgFile);

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.PNG,50,bao);
                    byte[] Thumb_Image_byte = bao.toByteArray();

                    UploadTask uploadTask = dataBaseRef.getUsersStorageRef().child(dataBaseRef.getCurrentUserId()).child("Profile_Img").child("profile.jpg").putBytes(Thumb_Image_byte);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return dataBaseRef.getUsersStorageRef().child(dataBaseRef.getCurrentUserId()).child("Profile_Img").child("profile.jpg").getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                dataBaseRef.getUserInfo().child(dataBaseRef.getCurrentUserId()).child("ImageThumbURL").setValue(downloadUri.toString());
                                uploadingImg.dismiss();
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                }catch (IOException ignored){

                }


            }
        }
    }


    @Override
    protected void onDestroy(){
            super.onDestroy();
            unregisterReceiver(receiver);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            //Log.v(LOG_TAG, "Receieved notification about network status");
            isNetworkAvailable(context);

        }


        private boolean isNetworkAvailable(Context context) {
            try {
                String temp = "online";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Status", temp);

                ConnectivityManager connectivity = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivity != null) {
                    NetworkInfo[] info = connectivity.getAllNetworkInfo();
                    if (info != null) {
                        for (int i = 0; i < info.length; i++) {
                            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                                if (!isConnected) {
                                    display_status.setText("online");
                                    isConnected = true;
                                    //do your processing here ---
                                    //if you need to post any data to the server or get status
                                    //update from the server
                                }
                                return true;
                            }
                        }
                    }
                }
                display_status.setText("offline");
                isConnected = false;
                return false;
            }catch (NullPointerException e){
                return false;
            }
        }

    }
}

