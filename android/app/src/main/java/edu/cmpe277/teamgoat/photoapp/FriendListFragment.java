package edu.cmpe277.teamgoat.photoapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by squall on 4/29/15.
 */
public class FriendListFragment extends ListFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private class FriendListAdapter extends ArrayAdapter
    {
        public FriendListAdapter()
        {
            super(getActivity(), 0, getActivity().getResources().getStringArray(R.array.friend_list_test));
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
