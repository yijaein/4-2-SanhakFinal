package com.google.firebase.quickstart.database.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.quickstart.database.MyAdapter;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.models.Chat;
import com.google.firebase.quickstart.database.models.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Jan on 2017-10-31.
 * 2017_10_31 이재인 채팅기능 추가
 * 
 */

public class ChatFragment extends android.support.v4.app.Fragment {

    public ChatFragment(){

    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    EditText etText;
    Button btnSend;

    String email;
    FirebaseDatabase database;

    List<Chat> mChat;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_chat, container, false);

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url

            email = user.getEmail();
            Toast.makeText(getActivity(),email,Toast.LENGTH_SHORT).show();

        }

        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycle_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mChat = new ArrayList<>();

        mAdapter= new MyAdapter(mChat);
        mRecyclerView.setAdapter(mAdapter);

        etText = (EditText)v.findViewById(R.id.jungminEditText);
        btnSend = (Button)v.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stText = etText.getText().toString();

                if (stText.equals("")||stText.isEmpty()){
                    Toast.makeText(getActivity(),"내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),email+""+stText,Toast.LENGTH_SHORT).show();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());


                    DatabaseReference myRef = database.getReference("chats").child(formattedDate);

                    Hashtable<String, String> chat  = new Hashtable<String, String>();
                    chat.put("email", email);
                    chat.put("text",stText);
                    myRef.setValue(chat);
                    etText.setText("");

                }


            }
        });

        /*
        DB에서 채팅 저장된 값 불러오기
         */

        DatabaseReference myRef = database.getReference("chats");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mChat.add(chat);
                mRecyclerView.scrollToPosition(mChat.size()-1);
                mAdapter.notifyItemInserted(mChat.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }
}
