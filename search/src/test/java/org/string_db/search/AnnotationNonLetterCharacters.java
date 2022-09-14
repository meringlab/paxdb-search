package org.string_db.search;

import org.string_db.jdbc.DbManager;
import org.string_db.jdbc.PostgresqlDbManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Figure out non-alphanumeric characters appear in annotations.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class AnnotationNonLetterCharacters {
    public static void main(String[] args) throws SQLException {
        Set<Character> characters = new HashSet<Character>();
        DbManager dbManager = new PostgresqlDbManager();
        for (Integer species : Arrays.asList(4932, 3702, 7227, 9606, 10090)) {
            final ResultSet resultSet = dbManager.executeQuery("select annotation from paxdb5_0.proteins where species_id = " + species);
            while (resultSet.next()) {
                final String annotation = resultSet.getString(1);
                for (int i = 0; i < annotation.length(); i++) {
                    final char c = annotation.charAt(i);
                    if (!Character.isLetterOrDigit(c)) {
                        characters.add(c);
                    }
                }
            }
        }
        dbManager.shutdown();
        System.out.println(characters.size());
        System.out.println(characters);
    }
}
