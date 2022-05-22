package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mUser, mPass, mEmail;
    private Button mRegisterButton;
    private HttpHelper httpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUser = findViewById(R.id.usernameedit_reg);
        mPass = findViewById(R.id.passwordedit_reg);
        mEmail = findViewById(R.id.emailedit_reg);
        httpHelper = new HttpHelper();

        mRegisterButton = findViewById(R.id.registerbutton_reg);

        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", mUser.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject.put("password", mPass.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject.put("email", mEmail.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    boolean bool = httpHelper.postJSONObjectFromURL("http://192.168.43.148:3000/auth/signup", jsonObject);
                    if (!bool) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "username, passwor or email is wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}