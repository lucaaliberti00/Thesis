package commons.mining.repository;

import java.util.Collection;
import java.util.List;

import commons.mining.model.*;;

public interface RuleRepository {

    List<Rule> getActiveRules();

    void saveRules(Collection<Rule> rules, KeyType keyType, int dbSize, String algorithm);
}
