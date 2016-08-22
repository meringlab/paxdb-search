package org.string_db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface DbManager {
    void shutdown() throws SQLException;

    PostgresConnector getConnector();

    String execute(String sql) throws SQLException;

    ResultSet executeQuery(String sql) throws SQLException;

    ResultSet getCursorBasedResultSet(String query, int fetchSize) throws SQLException;
}
