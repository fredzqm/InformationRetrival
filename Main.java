import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * TODO Put here a description of what this class does.
 *
 * @author zhang. Created Oct 25, 2015.
 */
public class Main {
	static Curation curation;

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File[] files = (new File("Presidents")).listFiles();
		Scanner in;
		curation = new Curation();
		for (File presidentFile : files) {
			in = new Scanner(presidentFile);
			LinkedList<String> lines = new LinkedList<String>();
			while (in.hasNext()) {
				lines.add(in.nextLine().toLowerCase());
			}
			int a = 0;
			if (presidentFile.getName().contains("Reagan"))
				a = 0;
			Document temp = new Document(presidentFile.toString(), lines);
			
			curation.add(temp);
			in.close();
		}

		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();
			
			ArrayList<Document> re = curation.query(s);
			if (re.size() == 0) {
				System.out.println("No match!");
				continue;
			}

			System.out.println("Result:");
			for (Document r : re) {
				System.out.println( r.getName() + " " + r.getScore() );
			}
			
		}

	}

}
