package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    String postURL = "http://192.168.43.69:3000/auth/signup";


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
                    jsonObject.put("password", mPass.getText().toString());
                    jsonObject.put("email", mEmail.getText().toString());

                    int i = httpHelper.postJSONObjectFromURL(postURL, jsonObject);
                    if (i == 201) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (i == -1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "CONNECTION_FAILED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (i == 400) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "DATA_INCORRECT", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}