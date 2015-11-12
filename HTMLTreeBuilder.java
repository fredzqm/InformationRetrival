import java.io.File;
import java.io.FileNotFoundException;
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
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		File intput = new File("input.txt");
		Scanner in = new Scanner(intput);
		ArrayList<String> lines = new ArrayList<>();
		while (in.hasNextLine()) {
			lines.add(in.nextLine());
		}
		in.close();

		System.out.println( new HTMLTree(lines) );
	}


}
