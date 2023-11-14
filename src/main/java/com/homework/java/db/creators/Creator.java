package com.homework.java.db.creators;

import org.flywaydb.core.Flyway;

import java.sql.Connection;

public interface Creator {
    Connection createConnection();

    static void flywayMigration(String connectionUrl, String username, String password) {
        Flyway flyway = Flyway.configure().schemas("public").dataSource(connectionUrl, username, password).load();
        flyway.migrate();
    }
}
