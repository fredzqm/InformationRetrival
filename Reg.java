import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TODO Put here a description of what this class does.
 *
 * @author zhang. Created Oct 25, 2015.
 */
public class Reg {

	/**
	 * G1 text before tag, "" if there isn't anything. G2 == null if head tag,
	 * "/" if end tag G3, the name of tag G4, other specifications, null if
	 * there isn't anything
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		int length = 4;
		Scanner in = new Scanner(new File("input.txt"));
		String s = in.nextLine();
		System.out.println("input:   " + s);
	}

	private static ArrayList<String> getWord(String s, int length) {
		Pattern word = Pattern.compile("([a-zA-Z[-]])+(\\s([a-zA-Z[-]])+){" + (length - 1) + "}");
		ArrayList<String> ret = new ArrayList<String>();
		Matcher m = word.matcher(s);
		int index = 0;
		while (m.find(index)) {
			ret.add(m.group(0));
			index = m.end(1);
		}
		return ret;
	}
}
