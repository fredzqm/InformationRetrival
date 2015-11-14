import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
	private static final Pattern tagPattern = Pattern.compile("(.*?)<(/)?(.*?)(\\s.*?)?>");
	private static final Pattern attriExtractor = Pattern.compile("(.*?)=\"(.*?)\"");
	private static final Set<String> USELESSNODES = new HashSet<String>() {
		{
			add("script");
			add("style");
			add("noscript");
		}
	};
	private static final Set<String> INLINENODES = new HashSet<String>() {
		{
			add("b");
			add("span");
			add("small");
			add("strong");
			add("i");
			add("em");
			add("mark");
			add("del");
			add("ins");
			add("sub");
			add("sup");
		}
	};

	private static final Collection<NodeTarget> IGNORENODES = new ArrayList<NodeTarget>() {
		{
			add(new NodeTarget("img"));
			add(new NodeTarget("div", "class=\"appendix\""));
			add(new NodeTarget("div", "class=\"suggestions\""));
			add(new NodeTarget("div", "class=\"nav\""));
			add(new NodeTarget("div", "id=\"nav\""));
			add(new NodeTarget("div", "id=\"footer\""));
			add(new NodeTarget("div", "id=\"refbegin\""));
			add(new NodeTarget("div", "id=\"reflist\""));
			add(new NodeTarget("table", "class=\"nav\""));
			add(new NodeTarget("table", "class=\"noprint\""));
			add(new NodeTarget("link"));
		}
	};

	private Node root;

	/**
	 * 
	 * @param htmlF
	 */
	public HTMLTree(Collection<String> htmlF) {
		root = new Node(null, "RootNode", null);
		Node cur = root;
		String leftStr = "";
		boolean comment = false; // if currently within an HTML comment
		for (String l : htmlF) {
			String line = leftStr + l;
			if (comment) {
				int i = line.indexOf("-->");
				if (i > 0) {
					comment = false;
					leftStr = line.substring(i + 3);
				} else {
					leftStr = "";
				}
			} else {
				Matcher m = tagPattern.matcher(line);
				int index = 0;
				while (true) {
					if (!m.find(index))
						break;
					cur.addString(m.group(1)); // add string before this tag
					String tagName = m.group(3);
					if (tagName.startsWith("!--")) {
						index = m.start(3) + 3;
						comment = true;
						break;
					} else {
						index = m.end();
						if (isEndTag(m.group(2))) {
							cur = findHeadTag(cur, tagName);
						} else {
							cur = cur.addNode(tagName, m.group(4));
						}
					}
				}
				leftStr = line.substring(index);
			}
		}
		root.clearNode();
		root.combineTextNode();
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
		Node cur = curNode;
		try {
			while (!cur.type.equals(tagName)) {
				Node parent = cur.getParentNode();
				parent.subNodes.addAll(cur.subNodes);
				cur.subNodes = new LinkedList<>();
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
		NodeTarget target = new NodeTarget(tag);
		return root.textWithin(target);
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param tag
	 * @param attr
	 * @return
	 */
	public Collection<String> textWithinTagCointainAttr(String tag, String attr) {
		NodeTarget target = new NodeTarget(tag, attr);
		return root.textWithin(target);
	}

	@Override
	public String toString() {
		root.d = "";
		return root.toString();
	}

	private static class Node {
		protected final Node parent;
		protected LinkedList<Node> subNodes;
		protected final String type;
		protected Map<String, String> attributes;
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
			this.type = tagName;
			if (attr != null) {
				this.attributes = new HashMap<String, String>();
				Matcher m = attriExtractor.matcher(attr);
				int index = 0;
				if (m.find(index)) {
					attributes.put(m.group(1), m.group(2));
					index = m.end();
				}
				if (attributes.size() == 0)
					attributes = null;
			}

		}

		/**
		 * 
		 * clean out useless and empty nodes in this tree to speed up future
		 * process.
		 * 
		 */
		public void clearNode() {
			Iterator<Node> itr = subNodes.iterator();
			while (itr.hasNext()) {
				Node n = itr.next();
				if (n.useless()) {
					itr.remove();
				} else {
					n.clearNode();
				}
			}
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 */
		public void combineTextNode() {
			ListIterator<Node> itr = subNodes.listIterator();
			Node cur;
			Node pre;
			boolean t = false;
			// while (itr.hasNext()) {
			// pre = cur;
			// cur = itr.next();
			// if (t) {
			// if (cur.isText()) {
			// t
			// }
			// } else {
			// if (cur.isText()) {
			// t = true;
			// }
			// }
			// }
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		protected boolean isText() {
			return INLINENODES.contains(type);
		}

		/**
		 * tell whether this node is useless for future process, remove needless
		 * nodes
		 * 
		 * @return
		 */
		protected boolean useless() {
			if (subNodes.size() == 0 && attributes == null)
				return true;
			for (NodeTarget tar : IGNORENODES)
				if (match(tar))
					return true;
			return false;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param n
		 * @return
		 */
		protected boolean match(NodeTarget n) {
			if (!type.equals(n.type))
				return false;
			if (n.attributes == null)
				return true;
			if (attributes == null)
				return false;
			for (String attr : n.attributes.keySet()) {
				if (!attributes.containsKey(attr))
					return false;
				if (!attributes.get(attr).contains(n.attributes.get(attr)))
					return false;
			}
			return true;
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
			if (USELESSNODES.contains(tagName))
				ret = new UselessNode(this, tagName);
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
				subNodes.add(new TextNode(this, s));
		}

		protected Node getParentNode() {
			return parent;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(d + "<" + type + attributes + ">\n");
			String nl = d + " ";
			for (Node n : subNodes) {
				n.d = nl;
				sb.append(n);
			}
			sb.append(d + "<\\" + type + ">\n");
			return sb.toString();
		}

		/**
		 *
		 * @param tag
		 * @return
		 */
		public LinkedList<String> textWithin(NodeTarget target) {
			LinkedList<String> ret = new LinkedList<String>();
			if (match(target)) {
				ret.add(textWithin().trim());
				return ret;
			}
			for (Node node : subNodes)
				ret.addAll(node.textWithin(target));
			return ret;
		}

		/**
		 *
		 * @return
		 */
		public String textWithin() {
			if (subNodes.size() == 0)
				return "";
			if (subNodes.size() == 1)
				return subNodes.getFirst().textWithin();
			StringBuilder sb = new StringBuilder();
			for (Node node : subNodes)
				sb.append(node.textWithin());
			return sb.toString();
		}
	}

	private static class TextNode extends Node {
		private String text;

		/**
		 * 
		 * @param parent
		 * @param context
		 * @param i
		 *
		 */
		TextNode(Node parent, String context) {
			super(parent, "text", null);
			text = context;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		@Override
		protected boolean isText() {
			return true;
		}

		@Override
		public boolean useless() {
			return false;
		}

		@Override
		public String toString() {
			return d + text;
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @param tag
		 * @return
		 */
		@Override
		public LinkedList<String> textWithin(NodeTarget target) {
			LinkedList<String> ret = new LinkedList<String>();
			if (!target.type.equals(""))
				ret.add(text);
			return ret;
		}

		@Override
		public String textWithin() {
			return text + " ";
		}
	}

	private static class UselessNode extends Node {

		/**
		 * TODO Put here a description of what this constructor does.
		 *
		 */
		public UselessNode(Node parent, String tagname) {
			super(parent, tagname, null);
		}

		@Override
		public boolean useless() {
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
		public LinkedList<String> textWithin(NodeTarget target) {
			throw new RuntimeException();
		}

		/**
		 * TODO Put here a description of what this method does.
		 *
		 * @return
		 */
		@Override
		public String textWithin() {
			throw new RuntimeException();
		}

		@Override
		public String toString() {
			return d + "uselessNode " + type;
		}
	}

	private static class NodeTarget extends Node {

		/**
		 * TODO Put here a description of what this constructor does.
		 *
		 * @param node
		 * @param tagName
		 * @param attr
		 */
		public NodeTarget(String tagName, String attr) {
			super(null, tagName, attr);
		}

		public NodeTarget(String tagName) {
			super(null, tagName, null);
		}

	}

}