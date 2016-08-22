package org.string_db.jdbc;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostgresqlDbManager implements DbManager {
    private static final Logger log = Logger.getLogger("org.string_db.jdbc.DbManager");
    public static final String PAXDB_PROPERTIES = "/opt/paxdb/v4.0/hibernate.properties";

    private PostgresConnector connector;

    public PostgresqlDbManager() {
        try {
            log.info("creating DbManager");
            connector = loadConnector();
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void shutdown() throws SQLException {
        log.info("shuting down DbManager");
        connector.shutdown();//TODO add a shutdown hook
    }

    @Override
    public PostgresConnector getConnector() {
        return connector;
    }

    /**
     * @param sql query
     * @return tab separated list of records
     */
    @Override
    public String execute(String sql) throws SQLException {
        try {
            return connector.executeToTsv(sql);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return connector.execute(sql);
    }

    @Override
    public ResultSet getCursorBasedResultSet(String query, int fetchSize) throws SQLException {
        return connector.getCursorBasedResultSet(query, fetchSize);
    }

    private final List<String> readFile(InputStream istream) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(istream)));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            lines.add(strLine.trim());
        }
        if (lines.isEmpty()) {
            throw new IOException("empty file: " + istream);
        }
        return lines;
    }

    private PostgresConnector loadConnector() throws Exception {
        Properties props = new Properties();
        final FileInputStream inStream = new FileInputStream(PAXDB_PROPERTIES);
        props.load(inStream);
        inStream.close();

        String[] url = props.getProperty("hibernate.connection.url").replace("jdbc:postgresql://", "").split("/");
        final String host = url[0];
        final String database = url[1];

        final String username = props.getProperty("hibernate.connection.username");
        final String password = props.getProperty("hibernate.connection.password");
        log.info("host=" + host + ", db=" + database + ", user=" + username);
        return new PostgresConnector(host, database, username, password);
    }

    public static void main(String[] args) throws SQLException {
        final PostgresqlDbManager m = new PostgresqlDbManager();
        System.out.println(m.execute("select count(*) from items.species;"));
        m.shutdown();
    }
}
