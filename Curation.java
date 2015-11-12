import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO Put here a description of what this class does.
 *
 * @author zhang. Created Oct 26, 2015.
 */
public class Curation implements Cloneable {
	public static HashMap<String, Integer> weight;
	ArrayList<Document> documents;
	private double avgLength;
	private int totalLength;

	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 */
	public Curation() {
		documents = new ArrayList<Document>();
		totalLength = 0;
		weight = new HashMap<String, Integer>();
		weight.put("", 10);
		weight.put("a", 20);
		weight.put("head", 10);
		weight.put("h1", 60);
		weight.put("h2", 40);
		weight.put("h3", 30);
		weight.put("title", 100);
		weight.put("li", 8);
	}

	/**
	 * 
	 * @param document
	 */
	public void add(Document document) {
		documents.add(document);
		totalLength += document.getLength();
	}

	/**
	 * the number of documents with searchedString over total number of
	 * documents
	 * 
	 * @param searchedString
	 * @return
	 */
	public int popularity(String searchedString) {
		int count = 0;
		int len = numOfwords(searchedString);
		for (Document doc : documents)
			if (doc.getFrequency(searchedString, len) > 0)
				count++;
		return count;
	}

	private int numOfwords(String searchWord) {
		int w = 0;
		boolean space = true;
		for (int i = 0; i < searchWord.length(); i++) {
			if (searchWord.charAt(i) != ' ') {
				if (space) {
					w++;
					space = false;
				}
			} else {
				space = true;
			}
		}
		return w;
	}

	/**
	 * an sorted arrayList of document as result.
	 * 
	 * Call getScore()
	 * 
	 * @param input
	 * @return an sorted arrayList of result
	 */
	public ArrayList<Document> query(ArrayList<String> query) {
		avgLength = totalLength / documents.size();
		for (Document doc : documents){
			doc.updateScore(score(query, doc));
		}
		Collections.sort(documents); // sort descending, highest score at 0.

		ArrayList<Document> finalResult = new ArrayList<Document>();
		double max = documents.get(0).getScore();
		for (int i = 0; i < documents.size(); i++) {
			Document d = documents.get(i);
			if (d.getScore() <= max * 0.75)
				return finalResult;
			finalResult.add(d);
		}
		return finalResult;
	}

	/**
	 *
	 * @param query
	 * @param doc
	 * @return
	 */
	public double score(ArrayList<String> query, Document doc) {
		double scoreForKeyinD = 0;
		for (String tag : weight.keySet()) {
			scoreForKeyinD = scoreForKeyinD + weight.get(tag) * BM25(doc, query, tag);
		}
		return Math.abs( scoreForKeyinD );
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Document r : documents) {
			sb.append(r.getName() + " " + r.getScore() );
		}
		return sb.toString();
	}

	static double k1 = 1.2;
	static double b = 0.75;

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param D
	 * @param Q
	 * @param tag
	 * @return
	 */
	private double BM25(Document D, ArrayList<String> Q, String tag) {
		double score = 0;
		for (int i = 0; i < Q.size(); i++) {
			int len = i + 1;
			for (int j = 0; j < Q.size() - len + 1; j++) {
				String newString = "";
				for (int k = 0; k < len; k++) {
					if (newString == "") {
						newString = Q.get(j);
					} else {
						newString = newString + " " + Q.get(j + k);
					}
				}
				int fqiD = D.getFreqencyWithinTag(newString, tag, len);
				double lastPart = (fqiD * (k1 + 1)) / (fqiD + k1 * (1 - b + b * (D.getLength() / avgLength)));
				int numOccur = popularity(newString);
				score += idf(numOccur, documents.size()) * lastPart;
			}
			score *= len;
		}
		return score;

	}

	private double idf(double nqi, double numOfDocuments) {
		double result = Math.log((numOfDocuments - nqi + 0.5) / (nqi + 0.5));
		return result;
	}

}
