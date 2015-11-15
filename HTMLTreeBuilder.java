import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Put here a description of what this class does.
 *
 * @author zhang. Created Oct 28, 2015.
 */
public class HTMLTreeBuilder {

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File intput = new File("input.txt");
		Scanner in = new Scanner(intput);
		ArrayList<String> lines = new ArrayList<>();
		while (in.hasNextLine()) {
			lines.add(in.nextLine());
		}
		in.close();
		PrintWriter fr = new PrintWriter("output.txt");
		NLP nlp = new NLP(lines);
		ArrayList<String> a = new ArrayList();
		a.add("this");
		a.add("is");
		nlp.query(a);
		System.out.println(nlp.query(a));
		// fr.write(nlp.sentences.toString());
		// System.out.println(nlp.sentences);
	}

}
