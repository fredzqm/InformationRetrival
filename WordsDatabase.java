import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Database stores words. We can search it for certain word or consecutively
 * words. The maximum number of consecutively word is can searched is
 * {@link WordsDatabase#maxLength}.
 * 
 * @author zhang. Created Oct 26, 2015.
 */
public class WordsDatabase {
	private ArrayList<HashMap<String, Integer>> data;
	private int maxLength;

	/**
	 * 
	 * create a database with given data, array list of string within each tag
	 * ususally.
	 * 
	 * @param i
	 * @param input
	 * @param maxLength
	 */
	public WordsDatabase(int maxlength, Collection<String> input) {
		data = new ArrayList<>();
		this.maxLength = maxlength;
		add(input);
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param strings
	 */
	public void add(Collection<String> strings) {
		int length = 1;
		while (length <= maxLength) {
			Pattern word = Pattern.compile("(\\w)+(\\s(\\w)+){" + (length - 1) + "}");
			ArrayList<String> matches = new ArrayList();
			for (String str : strings) {
				matches.addAll(getWord(str, word));
			}
			if (matches.size() == 0)
				break;
			addToList(length, matches);
			length++;
		}
		int a = 0;

	}

	/**
	 * 
	 * The first spot in arrayList is the number of matches of one-length word.
	 * The second spot in arrayList is the number of matches of two-length word.
	 * etc.
	 * 
	 * For example, if pass a multiple word string like "Barack Obama" into this
	 * function, and ret is the arrayList returned, then ret.get(0) counts the
	 * number of occurrence of "Barack" or "Obama", while ret.get(1) counts the
	 * number of occurrence of "Barack Obama" consecutively.
	 * 
	 * If there are more than {@link WordsDatabase#maxLength} words in
	 * searchWord, it ignores matches of words longer than
	 * {@link WordsDatabase#maxLength};
	 * 
	 * @param searchWord
	 * @param len 
	 * @return an arrayList that represent the number of matches
	 */
	public int frequency(String searchWord, int len) {
		return matchInLenght(searchWord, len);
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param searchWord
	 * @return
	 */
	

	private int matchInLenght(String s, int length) {
		if (length == 0)
			return 0;
		if (data.size() < length)
			return 0;
		HashMap<String, Integer> map = data.get(length - 1);
		if (!map.containsKey(s))
			return 0;
		return map.get(s);
	}

	/**
	 * add additional data into the database
	 * 
	 * @param length
	 * @param matches
	 */
	private void addToList(int length, Collection<String> matches) {
		while (data.size() < length) {
			data.add(new HashMap<String, Integer>());
		}
		HashMap<String, Integer> map = data.get(length - 1);
		for (String s : matches) {
			if (!map.containsKey(s))
				map.put(s, 0);
			map.put(s, map.get(s) + 1);
		}
	}

	/**
	 * 
	 *
	 * @param str
	 *            string to search for
	 * @param length
	 *            length of string retrived
	 * @return array list of consecutive sub words with certain length
	 */
	public static ArrayList<String> getWord(String str, Pattern word) {
		ArrayList<String> ret = new ArrayList<String>();
		Matcher m = word.matcher(str);
		int index = 0;
		while (m.find(index)) {
			ret.add(m.group(0));
			index = m.end(1);
		}
		return ret;
	}

}
