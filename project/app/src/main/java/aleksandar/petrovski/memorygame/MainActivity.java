package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLoginButton;
    private EditText mUser, mPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = findViewById(R.id.loginbutton);
        mUser = findViewById(R.id.usernameedit);
        mPass = findViewById(R.id.passwordedit);


        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mUser.getText().toString().equals("")) {
            mUser.setError(getString(R.string.error_empty_username));
            return;
        }
        if (mPass.getText().toString().equals("")) {
            mPass.setError(getString(R.string.error_empty_password));
            return;
        }

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }
}