package org.string_db.jdbc;

import java.sql.*;

/**
 * a copy of JMaintenance/../PostgresConnector because that dependency brings
 * all kinds of unnecessary jars (spring, and commons and whatnot).
 * 
 */
public class PostgresConnector {

    protected String host;
    protected String database;
    protected String username;
    protected String password;

    protected Connection conn = null;
    protected Statement statement;

    public PostgresConnector(String host, String database, String username, String password)
            throws SQLException {
        this.database = database;
        this.host = host;
        this.password = password;
        this.username = username;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
        String url = "jdbc:postgresql://" + this.host + "/" + this.database + "?user="
                + this.username + (this.password != null ? "&password=" + this.password : "");
        conn = DriverManager.getConnection(url);
        statement = conn.createStatement();
    }

    /**
     * For large ResultSets it's better to scroll through results instead of
     * loading them all at once.
     * 
     * Note, make sure to close the ResultSet before using the connection to
     * create another one.
     * 
     * @param query
     * @param fetchSize
     * @return
     * @throws SQLException
     */
    public ResultSet getCursorBasedResultSet(String query, int fetchSize) throws SQLException {
        getConnection().setAutoCommit(false);
        statement.setFetchSize(fetchSize);
        statement.execute(query);
        return statement.getResultSet();
    }
    /**
     * deallocate resources
     */
    public void shutdown() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet execute(String sqlQuery) throws SQLException {
        ResultSet resultSet;
        statement.execute(sqlQuery);
        resultSet = statement.getResultSet();
        return resultSet;
    }

    /**
     * 
     * @param statement
     * @param sql
     * @return tab separated list of records
     * @throws DBException
     * @throws SQLException
     */
    public String executeToTsv(String sql) throws SQLException {
        final ResultSet rs = execute(sql);
        StringBuffer sb = new StringBuffer();
        while (rs.next()) {
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                sb.append(rs.getObject(i).toString());
                if (i < rs.getMetaData().getColumnCount()) {
                    sb.append("\t");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Connection getConnection() {
        return conn;
    }

    public Statement getStatement() {
        return statement;
    }

}
