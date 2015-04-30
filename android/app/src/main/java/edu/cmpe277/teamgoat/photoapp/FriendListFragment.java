package edu.cmpe277.teamgoat.photoapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by squall on 4/29/15.
 */
public class FriendListFragment extends ListFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        String[] friends = getResources().getStringArray(R.array.friend_test);
//        ArrayList<String> friends_array = new ArrayList<>();
//        for (int i = 0; i < friends.length; i++) friends_array.add(friends[i]);
        FriendListAdapter adapter = new FriendListAdapter();
        setListAdapter(adapter);
    }


    private class FriendListAdapter extends ArrayAdapter
    {
        public FriendListAdapter()
        {
            super(getActivity(), 0, getActivity().getResources().getStringArray(R.array.friend_test));
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.layout_fragment_friendlist, null);

            // set the item
            TextView friendName = (TextView)convertView.findViewById(R.id.txtView_friend_name);
            friendName.setText((String)getItem(position));

            return convertView;
        }
    }
}
