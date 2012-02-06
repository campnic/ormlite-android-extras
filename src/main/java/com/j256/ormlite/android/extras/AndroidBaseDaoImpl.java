package com.j256.ormlite.android.extras;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;

public abstract class AndroidBaseDaoImpl<T, ID> extends BaseDaoImpl<T, ID>
{

    public AndroidBaseDaoImpl(Class<T> dataClass) throws SQLException
    {
        super(dataClass);
    }

    public AndroidBaseDaoImpl(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public AndroidBaseDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException
    {
        super(connectionSource, tableConfig);
    }

    public Cursor getCursor(PreparedQuery<T> query) throws SQLException
    {
        DatabaseConnection readOnlyConn = connectionSource.getReadOnlyConnection();
        AndroidCompiledStatement stmt = (AndroidCompiledStatement) query.compile(readOnlyConn, StatementBuilder.StatementType.SELECT);
        Cursor base =  stmt.getCursor();
        String idColumnName = getTableInfo().getIdField().getColumnName();
        int idColumnIndex = base.getColumnIndex(idColumnName);
        NoIdCursorWrapper wrapper = new NoIdCursorWrapper(base, idColumnIndex);
        return wrapper;
    }
    
    public Loader<List<T>> getResultSetLoader(Context context, PreparedQuery<T> query) throws SQLException
    {
        return new OrmliteListLoader<T, ID>(context, this, query);
    }
    
    public OrmliteCursorLoader<T> getSQLCursorLoader(Context context, PreparedQuery<T> query) throws SQLException
    {
        return new OrmliteCursorLoader<T>(context, this, query);
    }
}

