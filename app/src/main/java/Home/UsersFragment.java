package Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clowneon1.speex.Authentication.DataBaseRef;
import com.clowneon1.speex.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserAdapter;
import UserModel.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {
    DataBaseRef dataBaseRef = new DataBaseRef();
    private Query Users_query;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseRecyclerOptions<User> options;
    //private FirebaseRecyclerAdapter adapter ;
    

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        //query
        Users_query = dataBaseRef.getUserInfo().limitToLast(50);
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(Users_query,User.class).build();

        recyclerView = view.findViewById(R.id.users_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(options,getContext());
        recyclerView.setAdapter(userAdapter);
        mUsers = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        userAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userAdapter.stopListening();
    }
}