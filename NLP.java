import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Put here a description of what this class does.
 *
 * @author zhang. Created Nov 14, 2015.
 */
public class NLP {
	private static final Pattern tagPattern = Pattern.compile("(.*?)<(/)?(.*?)(\\s.*?)?>");
	private static final Pattern attriExtractor = Pattern.compile("(.*?)=\"(.*?)\"");
	public static final int CLOSEDIS = 10;
	private static final double A = 5000;
	private static final int B = 100;
	private static final double C = 0.1;
	ArrayList<Sentence> sentences;
	HashMap<String, ArrayList<Sentence.Token>> occurance;

	// Node root;

	/**
	 * TODO Put here a description of what this constructor does.
	 * 
	 * @param nlpF
	 */
	public NLP(Collection<String> nlpF) {
		sentences = new ArrayList<Sentence>();
		occurance = new HashMap<>();

		Node root = new Node(null, "root", null);
		Node cur = root;
		String leftString = "";
		for (String s : nlpF) {
			String line = leftString + " " + s;
			Matcher m = tagPattern.matcher(line);
			int index = 0;
			if (line.contains("id=\"1\""))
				index = 0;
			while (true) {
				if (!m.find(index))
					break;
				cur.addString(m.group(1)); // add string before this tag
				String tagName = m.group(3);
				index = m.end();
				if (tagName.startsWith("?")) {
					break;
				}
				if (isEndTag(m.group(2))) {
					cur = findHeadTag(cur, tagName);
				} else {
					cur = cur.addNode(tagName, m.group(4));
				}
			}
			leftString = line.substring(index);
		}

		Iterator<Node> itr = root.subNodes.getFirst().subNodes.getFirst().subNodes.iterator();
		int sentenceIndex = 0;
		for (Node n : itr.next().subNodes) {
			if (!n.type.equals("sentence"))
				throw new RuntimeException();
			sentences.add(new Sentence(n, sentenceIndex));
			sentenceIndex++;
		}
	}

	/**
	 * TODO Put here a description of what this method does.
	 * 
	 * @param cur
	 * @param cur
	 *
	 * @param tagName
	 * @return
	 */
	private Node findHeadTag(Node curNode, String tagName) {
		int a;
		if (tagName.contains("tokens"))
			a = 1;
		Node cur = curNode;
		try {
			while (!cur.type.equals(tagName)) {
				cur = cur.getParentNode();
			}
		} catch (NullPointerException e) {
			throw new RuntimeException("end tag can't find head tag");
		}
		return cur.getParentNode();
	}

	/**
	 * 
	 * Identify whether this tag is an end tag, but whether there is an extra
	 * "/" inside it.
	 * 
	 * @param ident
	 * @return true if ident is "/", false if ident is null
	 */
	private boolean isEndTag(String ident) {
		if ("/".equals(ident))
			return true;
		if (ident == null)
			return false;
		throw new RuntimeException("Weired thing before tag name: " + ident);
	}

	@Override
	public String toString() {
		return sentences.toString();
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param queries
	 * @retu rn
	 */
	public double query(Collection<String> queries) {
		int count = 0;
		double close = 0;
		ArrayList<Sentence.Token> occur = new ArrayList<>();
		HashMap<String, Integer> track = new HashMap<>();
		for (String q : queries) {
			if (!occurance.containsKey(q))
				return 0;
			occur.addAll(occurance.get(q));
			track.put(q, 0);
		}
		Collections.sort(occur);
		int s = Math.max(occur.get(0).getSentenceID() - CLOSEDIS, 0);
		int i = 0, j = -1;
		nextBox: while (s + CLOSEDIS < sentences.size()) {
			s++;
			while (i < j && occur.get(i + 1).getSentenceID() > s) {
				String q = occur.get(i).word;
				track.put(q, track.get(q) - 1);
				i++;
			}
			int pj = j;
			while (j + 1 < occur.size() && occur.get(j + 1).getSentenceID() < s + CLOSEDIS) {
				j++;
				String q = occur.get(j).word;
				track.put(q, track.get(q) + 1);
			}
			if (j - pj > 1) {
				int dept = commonAncestorLevel(occur, j, pj + 1);
				close += Math.pow(0.5 , dept);
			}

			for (String q : track.keySet()) {
				if (track.get(q) == 0) {
					continue nextBox;
				} else if (track.get(q) < 0)
					throw new RuntimeException();
			}
			count++;
		}
		return close * A  + count * B / (sentences.size() - CLOSEDIS) ; // TODO: parameter
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param occur
	 * @param i
	 * @param pj
	 * @return
	 */
	private int commonAncestorLevel(ArrayList<NLP.Sentence.Token> occur, int x, int y) {
		Sentence sen = occur.get(y).getSentence();
		ArrayList<ArrayList<Sentence.WNode>> ancestors = new ArrayList<ArrayList<Sentence.WNode>>();
		ArrayList<Sentence.WNode> anc = new ArrayList();
		for (int i = x; i <= y; i++) {
			Sentence.Token t = occur.get(i);
			if (sen != t.getSentence())
				throw new RuntimeException();
			anc = new ArrayList<Sentence.WNode>();
			Sentence.WNode cur = t.getLinkedNode();
			while (cur != null) {
				anc.add(cur);
				cur = cur.getParent();
			}
			ancestors.add(anc);
		}
		Sentence.WNode parentNode = null;
		int minDis = 0;
		nextWNode: for (int i = 0; i < anc.size(); i++) {
			parentNode = anc.get(i);
			for (ArrayList<Sentence.WNode> ancestorlist : ancestors) {
				if (!ancestorlist.contains(parentNode))
					continue nextWNode;
			}
			minDis = i;
			break;
		}
		for (ArrayList<Sentence.WNode> ancestorlist : ancestors) {
			if (ancestorlist.indexOf(parentNode) < minDis)
				minDis = ancestorlist.indexOf(parentNode);
		}
		return minDis;
	}

	/**
	 * TODO Put here a description of what this class does.
	 *
	 * @author zhang. Created Nov 14, 2015.
	 */
	private class Sentence {
		private int sentenceID;
		private ArrayList<Token> tokens;
		private WNode root;

		/**
		 * TODO Put here a description of what this constructor does.
		 * 
		 * @param sentence
		 * @param sentenceIndex
		 *
		 */
		public Sentence(NLP.Node sentence, int sentenceIndex) {
			sentenceID = sentenceIndex;
			tokens = new ArrayList<Token>();
			Iterator<Node> itr = sentence.subNodes.iterator();
			// create a list of all the tokens in this sentence.
			int tokenIndex = 0;
			for (NLP.Node n : itr.next().subNodes) {
				tokens.add(new Token(n, tokenIndex));
				tokenIndex++;
			}
			int a;
			// parse the tree and make link to token list.
			if (!itr.hasNext())
				a = 1;
			String parsedString = itr.next().text;
			int index = 0;
			tokenIndex = 0;
			root = new WNode(null);
			WNode cur = root;
			for (int i = 0; i < parsedString.length(); i++) {
				if (parsedString.charAt(i) == '(') {
					cur = cur.addWNode();
					index = i + 1;
				} else if (parsedString.charAt(i) == ')') {
					String t = parsedString.substring(index, i).trim();
					if (t.length() > 0) {
						// cur.linkedToken = tokens.get(tokenIndex);
						if (!t.equals(tokens.get(tokenIndex).word))
							throw new RuntimeException();
						tokens.get(tokenIndex).linkedWNode = cur;
						// cur.text = t;
						tokenIndex++;
					}
					cur = cur.getParent();
					index = i + 1;
				} else if (parsedString.charAt(i) == ' ') {
					index = i + 1;
				}
			}
			root = root.subNodes.getFirst();
			// System.out.println(s);
			// throw new RuntimeException();
		}

		@Override
		public String toString() {
			return "Sentence " + sentenceID + "\n" + tokens.toString() + "\n";
		}

		/**
		 * TODO Put here a description of what this class does.
		 *
		 * @author zhang. Created Nov 14, 2015.
		 */
		private class WNode {
			private WNode parent;
			private LinkedList<WNode> subNodes;
			private String text;
			// private Token linkedToken;

			/**
			 * TODO Put here a description of what this constructor does.
			 *
			 */
			public WNode(WNode parent) {
				this.parent = parent;
				subNodes = new LinkedList<WNode>();
			}

			/**
			 * TODO Put here a description of what this method does.
			 *
			 * @return
			 */
			public WNode getParent() {
				return parent;
			}

			/**
			 * TODO Put here a description of what this method does.
			 *
			 * @return
			 */
			public WNode addWNode() {
				WNode n = new WNode(this);
				subNodes.add(n);
				return n;
			}

			@Override
			public String toString() {
				if (text != null)
					return text;
				return subNodes.toString();
			}
		}

		private class Token implements Comparable<Token> {
			int pos;
			private String word;
			private WNode linkedWNode;

			/**
			 * TODO Put here a description of what this constructor does.
			 * 
			 * @param token
			 * @param tokenIndex
			 *
			 */
			public Token(Node token, int tokenIndex) {
				pos = tokenIndex;
				Iterator<Node> itr = token.subNodes.iterator();
				// word = PorterAlgorithm.porterAlgorithm(itr.next().text);
				word = itr.next().text;
				if (!occurance.containsKey(word))
					occurance.put(word, new ArrayList<Token>());
				occurance.get(word).add(this);
			}

			/**
			 * TODO Put here a description of what this method does.
			 *
			 * @return
			 */
			public WNode getLinkedNode() {
				return linkedWNode;
			}

			/**
			 * TODO Put here a description of what this method does.
			 *
			 * @return
			 */
			public Sentence getSentence() {
				return Sentence.this;
			}

			public int getSentenceID() {
				return Sentence.this.sentenceID;
			}

			@Override
			public String toString() {
				return "" + getSentenceID() + " " + word;
			}

			@Override
			public int compareTo(Token arg0) {
				int a = getSentenceID() - arg0.getSentenceID();
				if (a != 0)
					return a;
				return pos - arg0.pos;
			}

		}
	}

	private class Node {
		LinkedList<Node> subNodes;
		Node parent;
		String type;
		HashMap<String, String> attributes;
		private String text;
		String d;

		/**
		 * Construct a html node known its parent node, tag name and attributes
		 * attached. Its content, subNodes within the node will be added in the
		 * future.
		 * 
		 * @param parent
		 * @param tagName
		 * @param attr
		 */
		public Node(Node parent, String tagName, String attr) {
			this.parent = parent;
			subNodes = new LinkedList<Node>();
			this.type = tagName.trim();
			if (attr != null) {
				attributes = new HashMap<String, String>();
				Matcher m = attriExtractor.matcher(attr);
				int index = 0;
				if (m.find(index)) {
					attributes.put(m.group(1).trim(), m.group(2).trim());
					index = m.end();
				}
			}

		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param group
		 */
		public void addString(String str) {
			text = str.trim();
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		public Node getParentNode() {
			return parent;
		}

		/**
		 *
		 * @param tagName
		 * @param attributes
		 * @param endTag
		 * @return
		 */
		protected Node addNode(String tagName, String attributes) {
			Node ret;
			ret = new Node(this, tagName, attributes);
			subNodes.add(ret);
			return ret;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("\n");
			sb.append(d + "<" + type);
			if (attributes != null)
				sb.append("  " + attributes);
			sb.append(">");
			String nl = d + " ";
			for (Node n : subNodes) {
				n.d = nl;
				sb.append(n);
			}
			if (text != null)
				sb.append(text);
			sb.append("<\\" + type + ">");
			return sb.toString();
		}
	}

}
