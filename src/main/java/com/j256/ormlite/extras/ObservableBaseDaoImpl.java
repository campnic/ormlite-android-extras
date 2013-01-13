package com.j256.ormlite.extras;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

public class ObservableBaseDaoImpl<T, ID> extends BaseDaoImpl<T, ID>
{

    public ObservableBaseDaoImpl(Class<T> dataClass) throws SQLException {
        super(dataClass);
    }

    public ObservableBaseDaoImpl(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public ObservableBaseDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

}
