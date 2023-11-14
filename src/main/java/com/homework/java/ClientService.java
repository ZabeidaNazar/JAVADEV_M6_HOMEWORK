package com.homework.java;

import com.homework.java.db.Database;
import com.homework.java.fi.SQLConsumer;
import com.homework.java.fi.SQLFunction;

import java.sql.*;
import java.util.*;

public class ClientService {

    private static String insertClient = "INSERT INTO client (name) VALUES (?)";
    private static String selectByIdClient = "SELECT * FROM client WHERE id = ?";
    private static String updateClient = "UPDATE client SET name = ? WHERE id = ?";
    private static String deleteClient = "DELETE FROM client WHERE id = ?";
    private static String selectAllClient = "SELECT * FROM client";

    private Map<String, PreparedStatement> preparedStatements = new HashMap<>();

    public long create(String name) {
        checkNameLength(name);
        return execute(insertClient, (preparedStatement) -> preparedStatement.setString(1, name),
            (preparedStatement) -> {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                generatedKeys.next();
                return generatedKeys.getLong("id");
            });
    }

    public String getById(long id) {
        return execute(selectByIdClient, (preparedStatement) -> preparedStatement.setLong(1, id), (preparedStatement) -> {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                return rs.getString("name");
            } else {
                throw new IllegalArgumentException("Id відсутня в таблиці!");
            }
        });
    }
    public void setName(long id, String name) {
        checkNameLength(name);
        executeUpdate(updateClient, (preparedStatement) -> {
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, id);
        });
    }
    public void deleteById(long id) {
        executeUpdate(deleteClient, (preparedStatement) -> preparedStatement.setLong(1, id));
    }
    public List<Client> listAll() {
        return executeQuery(selectAllClient, resultSet -> {
            List<Client> clients = new ArrayList<>();
            while (resultSet.next()) {
                clients.add(new Client(resultSet.getInt("id"), resultSet.getString("name")));
            }
            return clients;
        });
    }

    private <T> T execute(String query, SQLConsumer<PreparedStatement> fill, SQLFunction<PreparedStatement, T> work) {
        try {
            PreparedStatement preparedStatement = getPreparedStatement(query);

            fill.accept(preparedStatement);

            boolean i = preparedStatement.execute();

            return work.apply(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException("SQLException.", e);
        } finally {
            //System.out.println("Updated rows: " + actualCount + "/" + elementsCount + " - " + query.split("VALUES")[0]);
        }
    }
    private void executeUpdate(String query, SQLConsumer<PreparedStatement> fill) {
        try {
            PreparedStatement preparedStatement = getPreparedStatement(query);

            fill.accept(preparedStatement);

            boolean i = preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException.", e);
        } finally {
            //System.out.println("Updated rows: " + actualCount + "/" + elementsCount + " - " + query.split("VALUES")[0]);
        }
    }
    private <T> T executeQuery(String query, SQLFunction<ResultSet, T> work) {
        try {
            PreparedStatement preparedStatement = getPreparedStatement(query);

            ResultSet rs = preparedStatement.executeQuery();

            return work.apply(rs);
        } catch (SQLException e) {
            throw new RuntimeException("SQLException.", e);
        } finally {
            //System.out.println("Updated rows: " + actualCount + "/" + elementsCount + " - " + query.split("VALUES")[0]);
        }
    }
    private static void checkNameLength(String name) {
        if (name == null || name.length() < 2 || name.length() > 1000) {
            throw new IllegalArgumentException("Довжина імені має бути від 2 до 1000 символів включно!");
        }
    }
    private PreparedStatement getPreparedStatement(String query) throws SQLException {
        PreparedStatement preparedStatement;
        if (preparedStatements.containsKey(query)) {
            preparedStatement = preparedStatements.get(query);
        } else {
            preparedStatement = Database.getInstance().getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatements.put(query, preparedStatement);
        }
        return preparedStatement;
    }
    public void close() {
        try {
            for (PreparedStatement preparedStatement : preparedStatements.values()) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
