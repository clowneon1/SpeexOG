package UserModel;

import androidx.annotation.NonNull;

import com.clowneon1.speex.Authentication.DataBaseRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import Home.PagerAdapter;

public class User {
    private String id;
    private String Username;
    private  String ImageThumbURL;

    public User(String id, String username, String imageURL) {
        this.id = id;
        this.Username = username;
        this.ImageThumbURL = imageURL;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getImageThumbURL() {
        return ImageThumbURL;
    }

    public void setImageThumbURL(String imageThumbURL) {
        ImageThumbURL = imageThumbURL;
    }
}
