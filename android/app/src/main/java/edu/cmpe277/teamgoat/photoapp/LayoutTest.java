package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by squall on 4/16/15.
 */
public class LayoutTest extends Activity
{
    private String [] mItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
//    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_fragment_container_album_item);
//        setContentView(R.layout.layout_gridlayout_view_album);

        setContentView(R.layout.layout_drawerlayout);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer_menu);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mItems = getResources().getStringArray(R.array.menu_item);

//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_drawerlayout_menu_item, mItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());




        if (savedInstanceState == null)
            selectItem(0);

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position);
        }

    }

    private void selectItem(int position)
    {

        FragmentManager fm = getFragmentManager();
        Fragment frag;

        frag = fm.findFragmentById(R.id.content_frame);
        if (frag == null)
        {
            if (position == 0)
                frag = new ProfileFragment();
            else if (position == 1)
                frag = new AlbumListFragment();
            else if (position == 2)
                frag = new FriendListFragment();
        }
        fm.beginTransaction().add(R.id.content_frame, frag).commit();
    }

}
