package com.bobocode.pool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PooledDataSource extends PGSimpleDataSource {
    // todo: 1. store a queue of connections (this is a pool)
    // todo: 2. initialize a datasource with 10 physical connection
    // todo: 3. override method getConnection so it uses a pool
    private BlockingQueue<Connection> pool;

    @SneakyThrows
    @Override
    public Connection getConnection() {
        if (pool == null) {
            initializePool();
        }
        return pool.take();
    }

    private void initializePool() throws SQLException, InterruptedException {
        pool = new LinkedBlockingQueue<>();
        for (int i = 0; i < 10; i++) {
            Connection connection = super.getConnection();
            pool.put(new ConnectionProxy(connection, pool));
        }
    }
}
