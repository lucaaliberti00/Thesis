package services.mining.spmf;


import commons.idea.Idea;

interface IdeaDatabaseBuilder<T> {

	void addEvent(Idea idea);

	T build();
}
