package com.homework.java.fi;

import java.sql.SQLException;

public interface SQLConsumer<T> {
    void accept(T t) throws SQLException;
}
