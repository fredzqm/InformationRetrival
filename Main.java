import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
		Scanner in, in2;
		curation = new Curation();
		for (int i = 0; i < files.length; i += 2) {
			ArrayList<String> lines = new ArrayList<>();
			ArrayList<String> lines2 = new ArrayList<String>();
			in = new Scanner(new FileInputStream(files[i]));
			in2 = new Scanner(new FileInputStream(files[i + 1]));
			while (in.hasNextLine()) {
				lines.add(in.nextLine().toLowerCase());
			}
			while (in2.hasNextLine()) {
				lines2.add(in2.nextLine().toLowerCase());
			}
			String name = files[i].toString();
			Document temp = new Document(name, lines, lines2);

			curation.add(temp);
			in.close();
			in2.close();
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
				System.out.println(r.getName() + " " + r.getScore());
			}

		}

	}

}
