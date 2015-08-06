package giftair.co.giftair_android03;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserInfoActivity extends AppCompatActivity {

    private Button Pwd_Change, SaveButton;
    private EditText UserFirstName, UserLastName, UserEmail;
    private ImageView imgView;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_PHOTO_ALBUMS = 2;

    private CharSequence[] items = {"카메라", "사진"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        UserFirstName = (EditText) findViewById(R.id.userFirstName);
        UserLastName = (EditText) findViewById(R.id.userLastName);
        UserEmail = (EditText) findViewById(R.id.userEmail);
        Pwd_Change = (Button) findViewById(R.id.Pwd_Change);
        SaveButton = (Button) findViewById(R.id.SaveButton);
        imgView = (ImageView)findViewById(R.id.userPhoto);

        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();

        RealmResults<UserDatabase> query = realm.where(UserDatabase.class).equalTo("Email", MainActivity.MailPanel.getText().toString()).findAll();

        for (int i = 0; i < query.size(); i++) {
            UserFirstName.setText(query.get(i).getUserFirstName());
            UserLastName.setText(query.get(i).getUserLastName());
        }

        realm.commitTransaction();
        realm.close();

        UserEmail.setText(MainActivity.MailPanel.getText().toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.UserInfo));
        setSupportActionBar(toolbar);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setEnabled(false);

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Pwd_Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChangePwdActivity.class));
            }
        });

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserFirstName.getText().length() > 0) {
                    if (UserLastName.getText().length() > 0) {

                        // 데이터베이스 호출.
                        Realm realm = Realm.getInstance(getApplicationContext());
                        realm.beginTransaction();

                        UserDatabase db = realm.createObject(UserDatabase.class);
                        db.setUserFirstName(UserFirstName.getText().toString());
                        db.setUserLastName(UserLastName.getText().toString());
                        db.setEmail(UserEmail.getText().toString());
                        realm.commitTransaction();

                        realm.close();

                        MainActivity.NamePanel.setText(UserFirstName.getText().toString() + UserLastName.getText().toString());
                        UserInfoActivity.this.finish();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.UserLastInput, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.UserFirstInput, Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
                builder.setTitle(getString(R.string.Select)).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        if(items[position].equals(getString(R.string.Camera))) {
                            Intent CameraIn = new Intent();
                            CameraIn.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(CameraIn, REQUEST_CAMERA);
                        }else if(items[position].equals(getString(R.string.Photo))) {
                            Intent Photo = new Intent(Intent.ACTION_PICK);
                            Photo.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            Photo.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Photo, REQUEST_PHOTO_ALBUMS);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA :
                Uri currImageURI = data.getData();
                Glide.with(UserInfoActivity.this).loadFromMediaStore(currImageURI).asBitmap().into(imgView);
                break;
            case REQUEST_PHOTO_ALBUMS :
                if(data == null) {
                    break;
                }
                Uri filePath = data.getData();
                Glide.with(UserInfoActivity.this).loadFromMediaStore(filePath).asBitmap().into(imgView);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                UserInfoActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
