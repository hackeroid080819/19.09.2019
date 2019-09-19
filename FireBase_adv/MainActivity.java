package com.example.itaykan.myfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();

    ArrayAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.messageList);

        final ValueEventListener valueEventListener = mDatabase.getReference().
                child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ///////////// new data is committed to the DB
                //Toast.makeText(getBaseContext(), "Firebase updated", Toast.LENGTH_SHORT).show();
                StringBuilder sb = new StringBuilder();
                int index = 0;
                for(DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Message message = snapShot.getValue(Message.class);
                    //sb.append(message.sender + " : " + message.text + "\n");

                    String addThisToLV = message.sender + " : " + message.text;
                    if (index >=adapter.getCount()) {
                        adapter.add(addThisToLV);
                    }
                    index++;
                }
                //Toast.makeText(getBaseContext(),sb, Toast.LENGTH_SHORT).show();
                //Log.d("======", FirebaseInstanceId.getInstance().getToken());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ////////// new data committed - but something went wrong...
                Toast.makeText(getBaseContext(), "Firebase error", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, new ArrayList<String>() );
        lv.setAdapter(adapter);

        // adding message
        //Message m = new Message();
        //m.sender = "Itay";
        //m.text = "hello auto message";
        //mDatabase.getReference().child("messages").push().setValue(m);

        final EditText msgET = findViewById(R.id.msgET);
        msgET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // i is the code of the pressed key in the keyboard
                // A -- 65
                // 0 -- 48
                // Esc -- 27
                // Enter -- 13
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            && i == KeyEvent.KEYCODE_ENTER) {
                    // adding message
                    Message m = new Message();
                    m.sender = "Itay";
                    m.text = msgET.getText().toString();
                    mDatabase.getReference().child("messages").push().setValue(m);
                    msgET.setText("");
                    return true;
                }
                return false;
            }
        });

    }
}
