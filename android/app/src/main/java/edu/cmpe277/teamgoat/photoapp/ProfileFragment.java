package edu.cmpe277.teamgoat.photoapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by squall on 4/29/15.
 */
public class ProfileFragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_profile, container, false);

        ImageView img_Avatar = (ImageView)v.findViewById(R.id.imgView_user_avatar);
        img_Avatar.setImageResource(R.drawable.goat_avatar_no_bg);
        img_Avatar.setMaxWidth(90);
        img_Avatar.setMaxHeight(90);


        return v;
    }
}
