package services.mining.spmf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import commons.idea.Idea;
import commons.mining.model.Item;
import commons.mining.model.KeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;


class SequenceDatabaseBuilder implements IdeaDatabaseBuilder<IdeaSequenceDatabase> {

	private static final Logger logger = LoggerFactory.getLogger(SequenceDatabaseBuilder.class);

	private Map<Object, Sequence> sequences = new HashMap<>();
	private KeyType keyType;
	private Map<Item, Integer> itemMapping = new HashMap<>();
	private int eventsSum = 0;

	SequenceDatabaseBuilder(KeyType keyType) {
		this.keyType = keyType;
	}

	@Override
	public void addEvent(Idea idea) {
		Integer item = itemMapping.computeIfAbsent(new Item(idea), k -> itemMapping.size());
		Sequence sequence = sequences.computeIfAbsent(keyType.getKey(idea), k -> new Sequence());
		sequence.addItemset(new Integer[] { item });
		eventsSum++;
	}

	@Override
	public IdeaSequenceDatabase build() {
		logger.info("Building sequence database with KeyType {}, {} sequences, {} items total and {} of unique items",
				keyType, sequences.size(), eventsSum, itemMapping.size());
		SequenceDatabase database = new SequenceDatabase();
		sequences.values().forEach(database::addSequence);

		if (!itemMapping.isEmpty()) {
			database.minItem = Collections.min(itemMapping.values());
			database.maxItem = Collections.max(itemMapping.values());
		}

		Map<Integer, Item> reverseItemMapping = itemMapping.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		return new IdeaSequenceDatabase(database, reverseItemMapping, keyType);
	}
}
