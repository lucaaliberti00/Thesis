package services.mining.spmf;

import commons.idea.Idea;
import commons.idea.aida.AidaUtils;
import commons.mining.model.KeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static commons.idea.Idea.readIdeasFromRawFile;

public class SequenceDatabases {

	private static final Logger logger = LoggerFactory.getLogger(SequenceDatabases.class);

	private SequenceDatabases() {
		throw new IllegalStateException("Cannot initialize utility class");
	}

	public static IdeaSequenceDatabase fromFile(String filePath, KeyType keyType) {

		long timeStart = System.currentTimeMillis();

		//List<Idea> ideas = readIdeasFromFormattedFile(new File(filePath));
		List<Idea> ideas = readIdeasFromRawFile(new File(filePath));

		SequenceDatabaseBuilder databaseBuilder = new SequenceDatabaseBuilder(keyType);

		for (Idea idea : ideas) {
			try {
				if (AidaUtils.isDuplicate(idea) || AidaUtils.isContinuing(idea)) {
					continue;
				}
				databaseBuilder.addEvent(idea);
			} catch (Exception e) {
				logger.error("Event cannot be added into database", e);
			}
		}

		IdeaSequenceDatabase db = databaseBuilder.build();
		logger.info("Metrics: total time building db from file {} s", (System.currentTimeMillis() - timeStart) / 1000d);
		return db;
	}


}
