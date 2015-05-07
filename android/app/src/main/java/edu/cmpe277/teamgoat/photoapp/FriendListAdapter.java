package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Friend;

public class FriendListAdapter extends ArrayAdapter<Friend> {
    private List<Friend> friendList;
    private List<ViewHolder> viewHolders = new ArrayList<>();

    private class ViewHolder {
        TextView textView;
        CheckBox checkBox;
        Friend friend;
    }

    public FriendListAdapter(Context context, int textViewResourceId, List<Friend> countryList) {
        super(context, textViewResourceId, countryList);
        this.friendList = new ArrayList<>(countryList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.layout_fragment_friendlist, null);

            holder = new ViewHolder();
            viewHolders.add(holder);
            holder.textView = (TextView) convertView.findViewById(R.id.txtView_friend_name);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.grant_album_access_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Friend friend = friendList.get(position);
        holder.textView.setText(friend.getName());
        holder.friend = friend;

        return convertView;
    }

    public List<Friend> getFriendsWhoCanViewAlbum() {
        List<Friend> friends = new ArrayList<>();
        for (ViewHolder viewHolder : viewHolders) {
            if (viewHolder.checkBox.isChecked()) {
                friends.add(viewHolder.friend);
            }
        }
        return friends;
    }
}