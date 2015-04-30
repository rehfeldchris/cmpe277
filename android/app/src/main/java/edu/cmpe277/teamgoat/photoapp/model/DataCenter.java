package edu.cmpe277.teamgoat.photoapp.model;

/**
 * Created by squall on 4/29/15.
 */
public class DataCenter
{
    private User currUser;

    private static DataCenter singletonDataCenter = null;

    public static DataCenter getSingletonDataCenter()
    {
        if (singletonDataCenter == null)
            singletonDataCenter = new DataCenter();

        return singletonDataCenter;
    }
}
