package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    TextView userName;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        listView = findViewById(R.id.detailslist);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        String name = intent.getStringExtra("userName");
        Bundle arguments = intent.getBundleExtra("nBestResults");

        ArrayList<Integer> results = (ArrayList<Integer>) arguments.getSerializable("arraylist");

        userName = findViewById(R.id.detailsusername);
        userName.setText(name);
        for (Integer integer : results) {
            adapter.add(integer);
        }

    }
}