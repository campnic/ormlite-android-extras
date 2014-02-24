package com.j256.ormlite.android.support.extras;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;
import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.*;

public abstract class AndroidBaseDaoImpl<T, ID> extends BaseDaoImpl<T, ID>
{

    public AndroidBaseDaoImpl(Class<T> dataClass) throws SQLException {
        super(dataClass);
    }

    public AndroidBaseDaoImpl(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public AndroidBaseDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    public Cursor getCursor(PreparedQuery<T> query) throws SQLException {
        DatabaseConnection readOnlyConn = connectionSource.getReadOnlyConnection();
        AndroidCompiledStatement stmt = (AndroidCompiledStatement) query.compile(readOnlyConn, StatementBuilder.StatementType.SELECT);
        Cursor base = stmt.getCursor();
        String idColumnName = getTableInfo().getIdField()
                                            .getColumnName();
        int idColumnIndex = base.getColumnIndex(idColumnName);
        return new NoIdCursorWrapper(base, idColumnIndex);
    }

    public Loader<List<T>> getResultSetLoader(Context context, PreparedQuery<T> query) throws SQLException {
        OrmliteListLoader<T, ID> loader = new OrmliteListLoader<T, ID>(context, this, query);
        synchronized (mLoaders)
        {
            mLoaders.add(loader);
        }
        return loader;
    }

    public OrmliteCursorLoader<T> getSQLCursorLoader(Context context, PreparedQuery<T> query) throws SQLException {
        OrmliteCursorLoader<T> loader = new OrmliteCursorLoader<T>(context, this, query);
        synchronized (mLoaders)
        {
            mLoaders.add(loader);
        }
        return loader;
    }

    protected final Set<Loader<?>> mLoaders = Collections.newSetFromMap(new WeakHashMap<Loader<?>, Boolean>());

    public void notifyContentChange() {
        final List<Loader<?>> mLoadersCopy;

        synchronized (mLoaders) {
            mLoadersCopy = new ArrayList<Loader<?>>(mLoaders);
        }

        for( Loader<?> loader : mLoadersCopy )
            loader.onContentChanged();
    }


    @Override
    public int create(T arg0) throws SQLException {
        int result = super.create(arg0);
        if (result > 0)
        {
            notifyContentChange();
        }
        return result;
    }

    @Override
    public int updateRaw(String arg0, String... arg1) throws SQLException {
        int result = super.updateRaw(arg0, arg1);
        if (result > 0)
        {
            notifyContentChange();
        }
        return result;
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        int result = super.delete(preparedDelete);
        if (result > 0)
        {
            notifyContentChange();
        }
        return result;
    }
}
