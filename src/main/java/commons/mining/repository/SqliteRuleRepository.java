package commons.mining.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import commons.mining.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SqliteRuleRepository implements RuleRepository {

    private static final Logger logger = LoggerFactory.getLogger(SqliteRuleRepository.class);

    private static final int QUERY_TIMEOUT = 20;

    private String sqliteUrl;

    public SqliteRuleRepository(String sqliteUrl) {
        this.sqliteUrl = sqliteUrl;
    }

    @Override
    public List<Rule> getActiveRules() {
        try (Connection connection = DriverManager.getConnection(sqliteUrl);
                Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(QUERY_TIMEOUT);

            try (ResultSet rs = statement.executeQuery("select rule, support, confidence from rule where rule.active == 1")) {
                List<Rule> rules = new ArrayList<>();
                while(rs.next()) {
                    rules.add(Rules.fromSpmf(
                            rs.getString("rule"),
                            rs.getInt("support"),
                            rs.getDouble("confidence")));
                }
                return rules;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void saveRules(Collection<Rule> rules, KeyType keyType, int dbSize, String algorithm) {
        final String sql = "INSERT INTO rule (rule, support, number_of_sequences, confidence, database, algorithm) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        logger.info("Going to save {} rules into database '{}'", rules.size(), sqliteUrl);

        try (Connection connection = DriverManager.getConnection(sqliteUrl)) {

            for (Rule rule : rules) {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setQueryTimeout(QUERY_TIMEOUT);
                    statement.setString(1, Rules.toSpmf(rule));
                    statement.setInt(2, rule.getSupport());
                    statement.setInt(3, dbSize);
                    statement.setDouble(4, rule.getConfidence());
                    statement.setString(5, keyType.toString());
                    statement.setString(6, algorithm);
                    statement.executeUpdate();
                    logger.debug("Rule has been successfully saved into database '{}'", Rules.toSpmf(rule));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        logger.info("All rules has been successfully saved into database");

    }
}
