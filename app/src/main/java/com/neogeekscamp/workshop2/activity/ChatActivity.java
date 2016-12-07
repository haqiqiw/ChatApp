package com.neogeekscamp.workshop2.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.neogeekscamp.workshop2.R;
import com.neogeekscamp.workshop2.adapter.ChatAdapter;
import com.neogeekscamp.workshop2.helper.RealmHelper;
import com.neogeekscamp.workshop2.manager.AppPrefManager;
import com.neogeekscamp.workshop2.model.MessageModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.neogeekscamp.workshop2.model.MessageModel.active;
import static com.neogeekscamp.workshop2.model.MessageModel.inactive;

public class ChatActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_message)
    ImageButton btnMessage;
    @BindView(R.id.btn_image)
    ImageButton btnImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Realm realm;
    private RealmHelper realmHelper;
    private DatabaseReference dbRef;

    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter adapter;
    private ArrayList<MessageModel> messageList = new ArrayList<>();

    private AppPrefManager appPrefManager;
    private String username;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Date dateNow = Calendar.getInstance().getTime();

    protected static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        appPrefManager = new AppPrefManager(this);
        appPrefManager = new AppPrefManager(this);
        realm = Realm.getDefaultInstance();
        realmHelper = new RealmHelper(ChatActivity.this, realm);

        realmHelper.clearMessage();

        username = appPrefManager.getUser().get("username");

        initToolbar();
        initFirebase();
        initRecyclerView();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.chat));
    }


    @OnClick({R.id.btn_image, R.id.btn_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_image:
                openGallery();
                break;
            case R.id.btn_message:
                sendMessage();
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri imageUri = data.getData();
                        Log.d("imageUri", imageUri.toString());
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            String image = encodeImage(bitmap);
                            sendImage(image);
                            Log.d("encodeImage", encodeImage(bitmap));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Cancelled",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return encodedImage;
    }

    private void sendMessage() {
        String textMessage = etMessage.getText().toString().trim();

        if (!textMessage.isEmpty()) {
            DatabaseReference ref = dbRef.child("message").push();

            MessageModel message = new MessageModel();
            message.setUsername(username);
            message.setMessage(textMessage);
            message.setImage("");
            message.setTime(dateFormat.format(dateNow));
            ref.setValue(message);

            etMessage.setText(null);
            recyclerview.smoothScrollToPosition(messageList.size());
        }
    }

    private void sendImage(String image) {
        if (!image.isEmpty()) {
            DatabaseReference ref = dbRef.child("message").push();

            MessageModel message = new MessageModel();
            message.setUsername(username);
            message.setMessage("");
            message.setImage(image);
            message.setTime(dateFormat.format(dateNow));
            ref.setValue(message);

            recyclerview.smoothScrollToPosition(messageList.size());
        }
    }

    private void initFirebase() {
        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    MessageModel message = dataSnapshot.getValue(MessageModel.class);
                    message.setId(dataSnapshot.getKey());
                    if (message.getUsername().equals(username)) {
                        message.setType(active);
                    } else {
                        message.setType(inactive);
                    }
                    realmHelper.addMessage(message);

                    refreshView();
                }
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
    }

    private void refreshView() {
        messageList.clear();
        messageList.addAll(realmHelper.getListMessage());
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        recyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.scrollToPosition(messageList.size());
        recyclerview.setNestedScrollingEnabled(true);
        recyclerview.setVerticalScrollBarEnabled(false);
        adapter = new ChatAdapter(messageList, this);
        recyclerview.setAdapter(adapter);
    }

    private void doLogout() {
        appPrefManager.logout();
        launchLogin();
        Toast.makeText(ChatActivity.this, "Out talks", Toast.LENGTH_SHORT).show();
    }

    private void launchLogin() {
        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) item.getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(ChatActivity.this);
            return true;
        }
        else if (id == R.id.action_signout) {
            doLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }
}
