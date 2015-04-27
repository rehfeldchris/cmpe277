package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
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
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_fragment_container_album_item);
//        setContentView(R.layout.layout_gridlayout_view_picture);
        mItems = new String[5];
        for (int i = 0; i < 5; i++)
            mItems[i] = new String("Menu Item" + (new Integer(i+1)).toString());

        setContentView(R.layout.layout_drawerlayout_menu);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer_menu);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);

//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_drawerlayout_menu_item, mItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
//
//        mDrawerToggle = new ActionBarDrawerToggle(
//                this,
//                mDrawerLayout,
//                R.drawable.ic_launcher,
//                R.string.txt_firstname,
//                R.string.txt_lastname
//        ){
//            public void onDrawerClosed(View view)
//            {
//                getActionBar().setTitle("Test menu");
//                invalidateOptionsMenu();
//            }
//
//            public void onDrawerOpen(View view)
//            {
//                getActionBar().setTitle("Test menu");
//                invalidateOptionsMenu();
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);

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

    }

}
