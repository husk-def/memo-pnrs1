package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button          mLoginButton;
    private Button          mRegisterButton;
    private EditText        mUser, mPass, mEmail;
    private final String    mSQLiteName = "memory_game.db";
    private PlayerDBHelper  playerDBHelper;
    private User            currentUser;
    private HttpHelper      httpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = findViewById(R.id.loginbutton);
        mRegisterButton = findViewById(R.id.registerbutton);
        mUser = findViewById(R.id.usernameedit);
        mPass = findViewById(R.id.passwordedit);
        httpHelper = new HttpHelper();
        // not used

        playerDBHelper = new PlayerDBHelper(this, mSQLiteName, null, 1);
        playerDBHelper.setId();

        playerDBHelper.fInsert(new User("ime", "email", 4));
        playerDBHelper.fInsert(new User("ime", "email", 2));
        playerDBHelper.fInsert(new User("ime", "email", 4));
        playerDBHelper.fInsert(new User("ime", "email", 6));
        playerDBHelper.fInsert(new User("ime", "email", 8));
        playerDBHelper.fInsert(new User("ime", "email", 10));
        playerDBHelper.fInsert(new User("ime", "email", 42));
        playerDBHelper.fInsert(new User("ime", "email", 44));
        playerDBHelper.fInsert(new User("ime", "email", 14));
        playerDBHelper.fInsert(new User("ime", "email", 24));
        playerDBHelper.fInsert(new User("ime", "email", 34));
        playerDBHelper.fInsert(new User("ime", "email", 44));
        playerDBHelper.fInsert(new User("ime", "email", 46));
        playerDBHelper.fInsert(new User("ime", "email", 47));
        playerDBHelper.fInsert(new User("ime", "email", 13));
        playerDBHelper.fInsert(new User("ime", "email", 466));
        playerDBHelper.fInsert(new User("ime", "email", 41));
        playerDBHelper.fInsert(new User("mane", "efag", 4));
        playerDBHelper.fInsert(new User("mane", "efag", 2));
        playerDBHelper.fInsert(new User("mane", "efag", 4));
        playerDBHelper.fInsert(new User("mane", "efag", 6));
        playerDBHelper.fInsert(new User("mane", "efag", 8));
        playerDBHelper.fInsert(new User("mane", "efag", 10));
        playerDBHelper.fInsert(new User("mane", "efag", 42));
        playerDBHelper.fInsert(new User("mane", "efag", 44));
        playerDBHelper.fInsert(new User("mane", "efag", 14));
        playerDBHelper.fInsert(new User("mane", "efag", 24));
        playerDBHelper.fInsert(new User("mane", "efag", 34));
        playerDBHelper.fInsert(new User("mane", "efag", 44));
        playerDBHelper.fInsert(new User("mane", "efag", 46));
        playerDBHelper.fInsert(new User("mane", "efag", 47));
        playerDBHelper.fInsert(new User("mane", "efag", 13));
        playerDBHelper.fInsert(new User("mane", "efag", 222));
        playerDBHelper.fInsert(new User("mane", "efag", 41));

        //playerDBHelper.restore();

        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginbutton) {
            if (mUser.getText().toString().equals("")) {
                mUser.setError(getString(R.string.error_empty_username));
                return;
            }
            if (mPass.getText().toString().equals("")) {
                mPass.setError(getString(R.string.error_empty_password));
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", mUser.getText().toString());
                        jsonObject.put("password", mPass.getText().toString());
                        Log.i("moje", jsonObject.getString("username") + jsonObject.getString("password"));

                        Integer i = httpHelper.postJSONObjectFromURL("http://192.168.43.148:3000/auth/signin", jsonObject);
                        if (i == 400) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "DATA_INCORRECT", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (i == 201) {
                            Intent intent = new Intent(MainActivity.this, GameActivity.class);
                            intent.putExtra("username", mUser.getText().toString());
                            startActivity(intent);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "CONNECTION_FAILED", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (view.getId() == R.id.registerbutton) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
