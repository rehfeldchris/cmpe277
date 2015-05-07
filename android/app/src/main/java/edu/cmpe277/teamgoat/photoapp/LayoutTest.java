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
//        setContentView(R.layout.layout_gridlayout_view_image);

        setContentView(R.layout.drawerlayout_left_panel);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mItems = getResources().getStringArray(R.array.menu_item);

//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.layout_drawerlayout_menu_item, mItems));
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

        frag = fm.findFragmentById(R.id.layout_drawer_fragment_container_main);

        // set a flag to check if fragment exist, if it does, replacing fragment instead of adding
        boolean isExisted = false;

        if (frag != null)
            isExisted = true;

        if (position == 0)
            frag = new ProfileFragment();
        else if (position == 1)
            frag = new AlbumListFragment();
        else if (position == 2)
            frag = new FriendListFragment();

        if (!isExisted && position < 3)
            fm.beginTransaction().add(R.id.layout_drawer_fragment_container_main, frag).commit();
        else
            fm.beginTransaction().replace(R.id.layout_drawer_fragment_container_main, frag).commit();

    }

}
