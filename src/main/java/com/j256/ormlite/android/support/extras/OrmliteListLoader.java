package com.j256.ormlite.android.support.extras;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

public class OrmliteListLoader<T, ID> extends AsyncTaskLoader<List<T>>
{
    private Dao<T, ID> mDao = null;
    private PreparedQuery<T> mQuery = null;
    private List<T> mData = null;

    public OrmliteListLoader(Context context, Dao<T, ID> dao, PreparedQuery<T> query)
    {
        super(context);
        mDao = dao;
        mQuery = query;
    }

    @Override
    public List<T> loadInBackground()
    {
        List<T> result = null;

        try
        {
            if (mQuery != null)
            {
                result = mDao.query(mQuery);
            } else
            {
                result = mDao.queryForAll();
            }

        } catch (SQLException e)
        {
            result = Collections.emptyList();
        }

        return result;
    }

    @Override
    public void deliverResult(List<T> datas)
    {
        if (isReset())
        {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (datas != null)
            {
                onReleaseResources(datas);
            }
        }

        List<T> oldDatas = mData;
        mData = datas;

        if (isStarted())
        {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(datas);
        }

        if (oldDatas != null && !oldDatas.isEmpty())
        {
            onReleaseResources(oldDatas);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading()
    {
        if (mData != null)
        {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mData);
        } else
        {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading()
    {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<T> datas)
    {
        super.onCanceled(datas);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(datas);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset()
    {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null)
        {
            onReleaseResources(mData);
            mData = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an
     * actively loaded data set.
     */
    protected void onReleaseResources(List<T> datas)
    {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

}
