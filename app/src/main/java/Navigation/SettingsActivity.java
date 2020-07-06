package Navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.clowneon1.speex.Authentication.DataBaseRef;
import com.clowneon1.speex.Authentication.SignInActivity;
import com.clowneon1.speex.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton imageButton;
    DataBaseRef dataBaseRef = new DataBaseRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        imageButton = (ImageButton)findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBaseRef.Auth.signOut();
                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}