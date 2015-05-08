package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Friend;
import edu.cmpe277.teamgoat.photoapp.model.User;

public class FriendListAdapter extends ArrayAdapter<User> {
    private List<User> friendList;
    private List<ViewHolder> viewHolders = new ArrayList<>();

    private class ViewHolder {
        TextView textView;
        CheckBox checkBox;
        User friend;
        ImageView imageView;
    }

    public FriendListAdapter(Context context, int textViewResourceId, List<User> countryList) {
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
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgView_friend_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User friend = friendList.get(position);
        holder.textView.setText(friend.getName());
        if (friend.getProfilePhotoUrl() != null) {
            ImageLoader.getInstance().displayImage(
                friend.getProfilePhotoUrl(),
                holder.imageView
            );
        }

        holder.textView.setText(friend.getName());
        holder.friend = friend;

        return convertView;
    }

    public List<User> getFriendsWhoCanViewAlbum() {
        List<User> friends = new ArrayList<>();
        for (ViewHolder viewHolder : viewHolders) {
            if (viewHolder.checkBox.isChecked()) {
                friends.add(viewHolder.friend);
            }
        }
        return friends;
    }
}