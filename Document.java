import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author zhang. Created Oct 25, 2015.
 */
public class Document implements Comparable<Document> {
	private static final int DEAULT_MAX_LENGTH = 1;
	private HTMLTree parseTree;
	private HashMap<String, WordsDatabase> databases;
	private final int length;
	private String name;

	private NLP nlp;

	/**
	 * temporally score for the query just ran.
	 */
	private double score;

	/**
	 * 
	 * construct a document given HTML file as array of lines
	 * 
	 * @param name
	 * @param htmlF
	 */
	public Document(String name, Collection<String> htmlF) {
		this.name = name;
		System.out.println(name);
		parseTree = new HTMLTree(htmlF);
		length = parseTree.textLength();
		databases = new HashMap<String, WordsDatabase>();
		addDatabase("", 3);
		addDatabase("a", Integer.MAX_VALUE);
		addDatabase("title", Integer.MAX_VALUE);
		addDatabase("p", 3);
		addDatabase("li", 3);
		addDatabase("head", 3);
		addDatabase("h", 3);
		addDatabase("h2", 3);
		addDatabase("h3", 3);

//		nlp = new NLP(nlpF);
	}

	/**
	 * 
	 * Add a database into the document of everything inside tag
	 *
	 * @param maxLength
	 */
	private void addDatabase(String tag, int maxLength) {
		databases.put(tag, new WordsDatabase(maxLength, parseTree.TextWithinTag(tag)));
	}

	/**
	 *
	 * same as {@link Document#getFullFreqency(String)} except that it only
	 * checks data within a certain tag
	 * 
	 * @param searchedString
	 * @param tag
	 * @param len
	 * @return
	 */
	public int getFreqencyWithinTag(String searchedString, String tag, int len) {
		if (!databases.containsKey(tag))
			addDatabase(tag, DEAULT_MAX_LENGTH);
		return databases.get(tag).frequency(searchedString, len);
	}

	/**
	 *
	 * @param searchedString
	 * @return the occurrence of SearchedString in this document.
	 */
	public int getFrequency(String searchedString, int len) {
		return databases.get("").frequency(searchedString, len);
	}

	/**
	 *
	 * @return
	 */
	public double getLength() {
		return length;
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param score
	 */
	public void updateScore(double score) {
		this.score = score;
	}

	/**
	 * score of the latest query
	 * 
	 * @return
	 *
	 */
	public double getScore() {
		return score;
	}

	@Override
	public int compareTo(Document arg0) {
		if (score > arg0.score)
			return -1;
		if (score == arg0.score)
			return 0;
		return 1;
	}

	@Override
	public String toString() {
		return name + " " + score;
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}

}
