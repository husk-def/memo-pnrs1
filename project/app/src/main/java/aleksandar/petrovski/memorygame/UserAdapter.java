package aleksandar.petrovski.memorygame;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    public ArrayList<User>          mUsers;
    private final Context           mContext;
    private final PlayerDBHelper    playerDBHelper;
    private final String            mSQLiteName = "memory_game.db";
    private final String            URL = "http://192.168.43.148:3000";


    private static class ViewHolder {
        TextView    username;
        TextView    email;
        TextView    bestscore;
        TextView    worstscore;
        Button      button;
    }

    public UserAdapter(Context mContext) {
        this.mUsers = new ArrayList<>();
        this.mContext = mContext;
        playerDBHelper = new PlayerDBHelper(mContext, mSQLiteName, null, 1);
    }


    public ArrayList<User> getUsers() {
        return this.mUsers;
    }


    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        Object rv = null;
        try {
            rv = mUsers.get(i);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return rv;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void removeUserByValue(User user) {
        mUsers.remove(user);
        notifyDataSetChanged();
    }

    public void removeUserByIndex(int index) {
        mUsers.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            /* inflate the layout for each list row */
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.element_row, null);
            viewHolder = new ViewHolder();
            viewHolder.bestscore = view.findViewById(R.id.bestscore);
            viewHolder.worstscore = view.findViewById(R.id.worstscore);
            viewHolder.username = view.findViewById(R.id.username);
            viewHolder.email = view.findViewById(R.id.email);
            viewHolder.button = view.findViewById(R.id.removemebutton);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        /* get current item to be displayed */
        User user = (User) getItem(i);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.email.setText(user.getmUserEmail());
        holder.username.setText(user.getmUserName());
        holder.worstscore.setText(String.valueOf(user.getmWorstScore()));
        holder.bestscore.setText(String.valueOf(user.getmBestScore()));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // should use PlayerDBHelper.delete(user.getmUserName()); instead.
                playerDBHelper.delete(user.getmUserName());
                removeUserByValue(user);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpHelper httpHelper = new HttpHelper();
                            boolean del = httpHelper.httpDelete(URL + "/score/?username=" + user.getmUserName());
                            Log.i("debag", "run: bool = " + del);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        holder.button.setEnabled(StatisticsActivity.getMe().equals(user.getmUserName()));
        return view;
    }

    public void addUser(User user) {
        try {
            mUsers.add(user);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void removeUser(User user) {
        mUsers.remove(user);
        notifyDataSetChanged();
    }

    public void notifyDSC() {
        notifyDataSetChanged();
    }
}
