package edu.cmpe277.teamgoat.photoapp;

import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AlbumListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlbumListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TODO: Change Adapter to display your content
        String [] friends = getActivity().getResources().getStringArray(R.array.friend_list_test);
        ArrayList<String> albums = new ArrayList<String>();
        for (int i = 0; i < friends.length; i++) albums.add(friends[i]);

        AlbumListAdapter adapter = new AlbumListAdapter(albums);
        setListAdapter(adapter);
    }


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private class AlbumListAdapter extends ArrayAdapter
    {
        public AlbumListAdapter(ArrayList<String> albums)
        {
            super(getActivity(), 0, albums);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.layout_fragment_container_album_item, null);

            TextView txtTitle = (TextView)convertView.findViewById(R.id.txt_album_title);
            txtTitle.setText((String)getItem(position));

            return convertView;
        }
    }

}
