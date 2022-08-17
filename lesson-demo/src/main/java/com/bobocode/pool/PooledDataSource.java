package com.bobocode.pool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PooledDataSource extends PGSimpleDataSource {

    private BlockingQueue<Connection> pool;

    public PooledDataSource(String url, String user, String password) throws InterruptedException, SQLException {
        this.setURL(url);
        this.setUser(user);
        this.setPassword(password);

        pool = new LinkedBlockingQueue<>();
        for (int i = 0; i < 10; i++) {
            Connection connection = super.getConnection();
            pool.put(new ConnectionProxy(connection, pool));
        }
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        return pool.take();
    }
}
