package commons.mining.model;

import java.util.Arrays;
import java.util.Collections;

import commons.idea.Idea;

public enum KeyType {

	SRC_IPV4() {
		@Override
		public Object getKey(Idea idea) {
			return idea.getSource().get(0).getIP4().get(0);
		}
	},

	SRC_TAR_IPV4() {
		@Override
		public Object getKey(Idea idea) {
			return Collections.unmodifiableList(Arrays.asList(
					idea.getSource().get(0).getIP4().get(0),
					idea.getTarget().get(0).getIP4().get(0)));
		}
	};

	public abstract Object getKey(Idea idea);
}
