import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Put here a description of what this class does.
 *
 * @author zhang. Created Oct 28, 2015.
 */
public class HTMLTree {

	/**
	 * 
	 * G1 text before tag, "" if there isn't anything.
	 * 
	 * G2 == null if head tag, "/" if end tag
	 * 
	 * G3, the name of tag
	 * 
	 * G4, other specifications, null if there isn't anything
	 */
	private static Pattern tagPattern = Pattern.compile("(.*?)<(/)?(.*?)(\\s.*?)?>");
	private static Pattern attriExtractor = Pattern.compile("(.*)=(.*)");
	private Node root;

	/**
	 * 
	 * @param lines
	 */
	public HTMLTree(ArrayList<String> lines) {
		root = new Node(null, "RootNode");
		Node cur = root;
		String leftStr = "";
		for (String l : lines) {
			String line = leftStr + l;
			Matcher m = tagPattern.matcher(line);
			int index = 0;
			while (true) {
				if (!m.find(index))
					break;
				index = m.end();
				cur.addString(m.group(1)); // add string before this tag
				String tagName = m.group(3);
				if (isEndTag(m.group(2))) {
					cur = findHeadTag(cur, tagName);
				} else {
					cur = cur.addNode(tagName, m.group(4));
				}
			}
			leftStr = line.substring(index);
		}

		root.clearEmptyNode();
	}

	/**
	 * TODO Put here a description of what this method does.
	 * 
	 * @param cur
	 * @param cur
	 *
	 * @param tagName
	 * @return
	 * @return
	 */
	private Node findHeadTag(Node curNode, String tagName) {
		Node cur = curNode;
		try {
			while (!cur.is(tagName)) {
				Node parent = cur.getParentNode();
				parent.subNodes.addAll(cur.subNodes);
				parent.subNodes.remove(cur);
				cur = parent;
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

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @return
	 */
	public int textLength() {
		return root.textWithin().length();
	}

	/**
	 * If tag is "", it return all string in the file.
	 * 
	 * @return a collection of words within certain tag
	 */
	public Collection<String> TextWithinTag(String tag) {
		return root.textWithinTag(tag);
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param tag
	 * @param attr
	 * @return
	 */
	public Collection<String> textWithinTagCointainAttr(String tag, HashMap<String, String> attr){
		return root.textWithinTagCointainAttr(tag, attr);
	}

	@Override
	public String toString() {
		return root.toString();
	}

	private class Node {
		private Node parent;
		private ArrayList<Node> subNodes;
		private String type;
		private HashMap<String, String> attributes;

		/**
		 * TODO Put here a description of what this constructor does.
		 * 
		 * @param parent
		 *
		 */
		public Node(Node parent, String tagtype) {
			this.parent = parent;
			subNodes = new ArrayList<Node>();
			this.type = tagtype;
		}

		/**
		 * TODO Put here a description of what this constructor does.
		 *
		 * @param node
		 * @param tagName
		 * @param attr
		 */
		public Node(Node node, String tagName, String attr) {
			this(node, tagName);
			if (attr == null){
				//this.attributes = new HashMap<String, String>();
			}
			else{
				String[] list = attr.trim().split("\\w");
				if (list.length > 0) {
					this.attributes = new HashMap<String, String>();
					for (String s : list) {
						Matcher m = attriExtractor.matcher(s);
						if (m.matches()) {
							attributes.put(m.group(1), m.group(2));
						}
					}
				}
			}
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 */
		public void clearEmptyNode() {
			Iterator<Node> itr = subNodes.iterator();
			while (itr.hasNext()) {
				Node n = itr.next();
				if (n.isEmpty())
					itr.remove();
				else
					n.clearEmptyNode();
			}
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		public boolean isEmpty() {
			return subNodes.size() == 0;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param tagName
		 * @return
		 */
		public boolean is(String tagName) {
			return type.equals(tagName);
		}

		/**
		 *
		 * @param tagName
		 * @param attributes
		 * @param endTag
		 */
		protected Node addNode(String tagName, String attributes) {
			Node ret;
			if (tagName == "script")
				ret = new ScriptNode(this, tagName, 1);
			else
				ret = new Node(this, tagName, attributes);
			subNodes.add(ret);
			return ret;
		}

		/**
		 * add a string node in this tree
		 *
		 * @param str
		 */
		protected void addString(String str) {
			String s = str.trim();
			if (s.length() > 0)
				subNodes.add(new TextNode(this, s, 1));
		}

		protected Node getParentNode() {
			return parent;
		}

		@Override
		public String toString() {
			return subNodes.toString();
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param tag
		 * @return
		 */
		public LinkedList<String> textWithinTag(String tag) {
			LinkedList<String> ret = new LinkedList<String>();
			if (tag.equals(type)) {
				ret.add(textWithin().trim());
				return ret;
			}
			for (Node node : subNodes)
				ret.addAll(node.textWithinTag(tag));
			return ret;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param tag
		 * @return
		 */
		public LinkedList<String> textWithinTagCointainAttr(String tag, HashMap<String, String> attr) {
			LinkedList<String> ret = new LinkedList<String>();
			if (tag.equals(type) && ContainAttr(attr)) {
				ret.add(textWithin().trim());
				return ret;
			}
			for (Node node : subNodes)
				ret.addAll(node.textWithinTag(tag));
			return ret;
		}

		/**
		 *
		 * @param requirements
		 * @return
		 */
		private boolean ContainAttr(HashMap<String, String> requirements) {
			if (attributes == null)
				return false;
			for (String attr : requirements.keySet()) {
				if (!attributes.containsKey(attr))
					return false;
				if (!attributes.get(attr).contains(requirements.get(attr)))
					return false;
			}
			return true;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		public String textWithin() {
			if (subNodes.size() == 0)
				return "";
			if (subNodes.size() == 1)
				return subNodes.get(0).textWithin();
			StringBuilder sb = new StringBuilder();
			for (Node node : subNodes)
				sb.append(node.textWithin());
			return sb.toString();
		}
	}

	private class TextNode extends Node {
		private String text;

		/**
		 * TODO Put here a description of what this constructor does.
		 *
		 */
		TextNode(Node parent, String context, int i) {
			super(parent, "text");
			text = context;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public String toString() {
			return text;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param tag
		 * @return
		 */
		@Override
		public LinkedList<String> textWithinTag(String tag) {
			if (!tag.equals(""))
				return new LinkedList<>();
			LinkedList<String> ret = new LinkedList<String>();
			ret.add(text);
			return ret;
		}

		@Override
		public String textWithin() {
			return text + " ";
		}
	}

	private class ScriptNode extends Node {

		/**
		 * TODO Put here a description of what this constructor does.
		 *
		 */
		public ScriptNode(Node parent, String tagname, int i) {
			super(parent, tagname);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		/**
		 *
		 * @param tagName
		 * @param endTag
		 */
		@Override
		protected Node addNode(String tagName, String attributes) {
			return this;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param tag
		 * @return
		 */
		@Override
		public LinkedList<String> textWithinTag(String tag) {
			return new LinkedList<String>();
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		@Override
		public String textWithin() {
			return "";
		}

		@Override
		public String toString() {
			return "Script";
		}
	}

}
