package com.j256.ormlite.android.extras;

import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CursorAdapter;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.stmt.PreparedQuery;

public abstract class OrmliteCursorAdapter<T> extends CursorAdapter
{
    PreparedQuery<T> mQuery;

    public OrmliteCursorAdapter(Context context, Cursor c, PreparedQuery<T> query)
    {
        super(context, c, false);
        mQuery = query;
    }

    @Override
    public void bindView(View itemView, Context context, Cursor cursor)
    {
        try
        {
            T item = mQuery.mapRow(new AndroidDatabaseResults(cursor, null)) ;
            bindView(itemView, context, item);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void setQuery(PreparedQuery<T> query)
    {
        mQuery = query;
    }
    
    abstract public void bindView(View itemView, Context context, T item);
}
