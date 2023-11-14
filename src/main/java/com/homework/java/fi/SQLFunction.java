package com.homework.java.fi;

import java.sql.SQLException;

public interface SQLFunction<T, R> {
    R apply(T t) throws SQLException;
}
