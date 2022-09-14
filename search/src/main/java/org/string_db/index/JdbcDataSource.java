package org.string_db.index;

import org.apache.log4j.Logger;
import org.string_db.jdbc.DbManager;
import org.string_db.jdbc.PostgresqlDbManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class JdbcDataSource implements DataSource {
    private static final Logger log = Logger.getLogger(JdbcDataSource.class);
    public static final String PAXDB_PROPERTIES = "/opt/paxdb/v5.0/paxdb.properties";
    private final List<Long> species;

    DbManager db;

    public JdbcDataSource() {
        final Long[] speciesIds;
        try {
            Properties props = new Properties();
            final FileInputStream inStream = new FileInputStream(PAXDB_PROPERTIES);
            props.load(inStream);
            final String species_ids = props.getProperty("species_ids");
            final String[] ids = species_ids.split(",");
            speciesIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                speciesIds[i] = Long.valueOf(id);
            }
            inStream.close();
        } catch (IOException e) {
            throw new ExceptionInInitializerError("failed to read species ids: " + e.getMessage());
        }
        species = Collections.unmodifiableList(Arrays.asList(speciesIds));
        db = new PostgresqlDbManager();
    }

    public static void main(String[] args) {
        final JdbcDataSource s = new JdbcDataSource();
        assert (!s.getSpeciesIds().isEmpty());
    }

    @Override
    public List<Long> getSpeciesIds() {
        return species;
    }

    @Override
    public List<String> getProteinsTsv(Long speciesId) {
        try {
            List<String> records = new ArrayList<String>();
            final ResultSet proteins = db
                    .executeQuery("SELECT protein_id, protein_external_id, preferred_name, annotation"
                            + " FROM paxdb5_0.proteins WHERE species_id = " + speciesId);
            while (proteins.next()) {
                records.add(proteins.getString(1) + "\t" + proteins.getString(2) + "\t" + proteins
                        .getString(3) + "\t" + proteins.getString(4));
            }
            return records;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Long, Set<String>> getProteinNames(long speciesId) {
        log.info("getProteinNames for " + speciesId);
        try {
            final ResultSet names;
            names = db.executeQuery("SELECT protein_id, protein_name "
                    + " FROM paxdb5_0.proteins_names WHERE species_id = " + speciesId);
            Map<Long, Set<String>> proteinNames = new HashMap<Long, Set<String>>();
            while (names.next()) {
                final long id = names.getLong(1);
                if (!proteinNames.containsKey(id)) {
                    proteinNames.put(id, new HashSet<String>());
                }
                proteinNames.get(id).add(names.getString(2));
            }
            return proteinNames;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("can't read protein names", e);
        }
    }
}
