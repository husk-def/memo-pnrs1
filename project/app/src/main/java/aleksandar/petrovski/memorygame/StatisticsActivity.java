package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Random;

public class StatisticsActivity extends AppCompatActivity {

    private void addRandomScore(User user, int howMany) {
        Random random = new Random();
        for (int i = 1; i <= howMany ; ++i) {
            user.addScore(random.nextInt(i * 9) - 120);
        }
        user.updateBestWorstScore();
    }

    private void addLinearScore(User user, int howMany) {
        for (int i = 0; i < howMany; ++i) {
            user.addScore(i);
        }
        user.updateBestWorstScore();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        UserAdapter userAdapter = new UserAdapter(this);

        /* add random users to userAdapter list */
        userAdapter.addUser(new User("aleksandar", "d@yahoo"));
        userAdapter.addUser(new User("aleksanda", "f@yahoo"));
        userAdapter.addUser(new User("aleksand", "a@yahoo"));
        userAdapter.addUser(new User("aleksan", "f@yahoo"));
        userAdapter.addUser(new User("aleksa", "sdf@yahoo"));
        userAdapter.addUser(new User("aleks", "sdf@yahoo"));
        userAdapter.addUser(new User("alek", "af@yahoo"));
        userAdapter.addUser(new User("ale", "adf@yahoo"));
        userAdapter.addUser(new User("al", "asf@yhoo"));
        userAdapter.addUser(new User("a", "asdf@aho"));
        userAdapter.addUser(new User("andar", "asdf@ahoo"));
        userAdapter.addUser(new User("dar", "asdf@yo"));
        userAdapter.addUser(new User("ar", "asdf@yao"));
        userAdapter.addUser(new User("ksandar", "asdf@yaho"));
        userAdapter.addUser(new User("special", "special"));

        /* generate random/linear scores for each user added to userAdapter */
        int i = 0;
        for (User user : userAdapter.getUsers()) {
            //addLinearScore(user, 20 + i);
            addLinearScore(user, 20 + i);
            ++i;
        }


        ListView listView = findViewById(R.id.listvju);
        listView.setAdapter(userAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) userAdapter.getItem(i);
                Intent intent = new Intent(StatisticsActivity.this, DetailsActivity.class);
                Bundle arguments = new Bundle();
                arguments.putSerializable("arraylist", user.getNBestResults(10));
                intent.putExtra("nBestResults", arguments);
                intent.putExtra("userName", user.getmUserName());
                startActivity(intent);
            }
        });
    }
}