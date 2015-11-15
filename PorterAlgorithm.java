import java.util.ArrayList;


public class PorterAlgorithm {
	static ArrayList<String> vowel = new ArrayList<String>() ; 
	
	public PorterAlgorithm(){
		vowel.add("a");
		vowel.add("e");
		vowel.add("i");
		vowel.add("o");
		vowel.add("u");
	}
	
	
	
	public static String porterAlgorithm(String input){
		for (int i = 0; i < input.length(); i++){
			if (",./<>?';:[]{}|=-_+)(*&^%$#@!~`1234567890".contains(input.substring(i, i+1)) || !"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".contains(input.substring(i, i+1))){
				return input;
			}
		}
		String result = input;
		result = step5b(step5a(step4(step3(step2(step1c(step1b2(step1b(step1a(result)))))))));
		
		return result;
		
	}
	
	public static int getm(String input, String suffix){
		String stem = input.substring(0, input.length() - suffix.length());
		String startC = getStartConstant(stem);
		String endV = getEndVowel(stem);
		String middleVCString = stem.substring(startC.length(), stem.length() - endV.length());
		int m = 0;
		
		Boolean vowelDetected = false;
		for (int indexOfVCString = 0; indexOfVCString < middleVCString.length(); indexOfVCString++){
			
			String currentChar = middleVCString.substring(indexOfVCString, indexOfVCString + 1).toLowerCase();
			if (vowel.contains(currentChar)){
				vowelDetected = true;
			}
			else{
				if (vowelDetected){
					m = m + 1;
					vowelDetected = false;
				}
			}
		}
		//System.out.println("m is " + m);
		return m;
	}
	

	
	public static String step1a(String input){
		String result = input;
		if (isEndWithString(result, "sses")){
			//System.out.print("Detect string ends with 'sses' ");
			result = result.substring(0, result.length() - 2);
			return result;
		}
		if (isEndWithString(result, "ies")){
			//System.out.print("Detect string ends with 'ies' ");
			result = result.substring(0, result.length() - 2);
			return result;
		}
		if (isEndWithChar (result, "s")){
			//System.out.print("Detect string ends with 's' ");
			result = result.substring(0, result.length() - 1);
			return result;
		}
		return result;
	}
	
	public static String step1b(String input){
		String result = input;
		if (isEndWithString(result, "eed")){
			//System.out.println("Detect string ends with 'eed' ");
			int m = getm(result, "eed");	
			if (m > 0){
				result = result.substring(0, result.length() - 1);
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ed")){
			//System.out.println("Detect string ends with 'ed' ");
			if (containsVowel(result.substring(0, result.length() - 2))){
				//System.out.println("Detect string with vowel in stem");
				result = result.substring(0, result.length() - 2);
				return result;
			}
		}
		if (isEndWithString(result, "ing")){
			//System.out.println("Detect string ends with 'ing' ");
			if (containsVowel(result.substring(0, result.length() - 3))){
				//System.out.println("Detect string with vowel in stem");
				result = result.substring(0, result.length() - 3);
				return result;
			}
		}
		
		
		
		return result;
	}
	
	public static String step1b2(String input){
		String result = input;
		if (isEndWithString(result, "at")){
			//System.out.println("Detect string ends with 'at' ");
			result = result.substring(0, result.length() - 2) + "ate";
			return result;

		}
		if (isEndWithString(result, "bl")){
			//System.out.println("Detect string ends with 'bl' ");
			result = result.substring(0, result.length() - 2) + "ble";
			return result;
		}
		if (isEndWithString(result, "iz")){
			//System.out.println("Detect string ends with 'is' ");
			result = result.substring(0, result.length() - 2) + "ize";
			return result;
		}
		
		if (endsWithDoubleConsonant (result)){
			//System.out.println("Detect duplicate letter in the end ");
			return result.substring(0, result.length() - 1);
		}
		
		if (endWithcvc (result)){
			//System.out.println("Detect string ends in cvc form");
			int m = getm(result, "");
			if (m == 1){
				//System.out.println("Detect string needs to add an E in the tail");
				return result + "e";
			}
		}
		
		return result;
	}
	
	public static String step1c(String input){
		String result = input;
		
		if (isEndWithChar(result, "y")){
			//System.out.println("Detect string ends with 'y' ");
			if (containsVowel(result.substring(0, result.length() - 1))){
				//System.out.println("Detect string needs to add an 'i' in the end");
				result = result.substring(0, result.length() - 1) + "i";
				return result;
			}
		}
		return result;
	}
	
	public static String step2(String input){
		String result = input;
		if (isEndWithString(result, "ational")){
			//System.out.println("Detect string ends with 'ational' ");
			int m = getm(result, "ational");	
			if (m > 0){
				result = result.substring(0, result.length() - 7) + "ate";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "tional")){
			//System.out.println("Detect string ends with 'tional' ");
			int m = getm(result, "tional");	
			if (m > 0){
				result = result.substring(0, result.length() - 6) + "tion";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "enci")){
			//System.out.println("Detect string ends with 'enci' ");
			int m = getm(result, "enci");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "ence";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "anci")){
			//System.out.println("Detect string ends with 'anci' ");
			int m = getm(result, "anci");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "ance";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "izer")){
			//System.out.println("Detect string ends with 'izer' ");
			int m = getm(result, "izer");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "ize";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "abli")){
			//System.out.println("Detect string ends with 'abli' ");
			int m = getm(result, "abli");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "able";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "alli")){
			//System.out.println("Detect string ends with 'alli' ");
			int m = getm(result, "alli");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "al";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "entli")){
			//System.out.println("Detect string ends with 'entli' ");
			int m = getm(result, "entli");	
			if (m > 0){
				result = result.substring(0, result.length() - 7) + "ent";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "eli")){
			//System.out.println("Detect string ends with 'eli' ");
			int m = getm(result, "eli");	
			if (m > 0){
				result = result.substring(0, result.length() - 3) + "e";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ousli")){
			//System.out.println("Detect string ends with 'ousli' ");
			int m = getm(result, "ousli");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "ous";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ization")){
			//System.out.println("Detect string ends with 'ization' ");
			int m = getm(result, "ization");	
			if (m > 0){
				result = result.substring(0, result.length() - 7) + "ize";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ation")){
			//System.out.println("Detect string ends with 'ation' ");
			int m = getm(result, "ation");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "ate";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ator")){
			//System.out.println("Detect string ends with 'ator' ");
			int m = getm(result, "ator");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "ate";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "alism")){
			//System.out.println("Detect string ends with 'alism' ");
			int m = getm(result, "alism");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "al";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "iveness")){
			//System.out.println("Detect string ends with 'iveness' ");
			int m = getm(result, "iveness");	
			if (m > 0){
				result = result.substring(0, result.length() - 7) + "ive";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "fulness")){
			//System.out.println("Detect string ends with 'fulness' ");
			int m = getm(result, "fulness");	
			if (m > 0){
				result = result.substring(0, result.length() - 7) + "ful";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ousness")){
			//System.out.println("Detect string ends with 'ousness' ");
			int m = getm(result, "ousness");	
			if (m > 0){
				result = result.substring(0, result.length() - 7) + "ous";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "aliti")){
			//System.out.println("Detect string ends with 'aliti' ");
			int m = getm(result, "aliti");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "al";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "iviti")){
			//System.out.println("Detect string ends with 'iviti' ");
			int m = getm(result, "iviti");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "ive";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "biliti")){
			//System.out.println("Detect string ends with 'biliti' ");
			int m = getm(result, "biliti");	
			if (m > 0){
				result = result.substring(0, result.length() - 6) + "ble";
				return result;
			}else{
				return result;
			}

		}
		return result;
	}
	
	public static String step3(String input){
		String result = input;
		if (isEndWithString(result, "icate")){
			//System.out.println("Detect string ends with 'icate' ");
			int m = getm(result, "icate");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "ic";
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ative")){
			//System.out.println("Detect string ends with 'ative' ");
			int m = getm(result, "ative");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "alize")){
			//System.out.println("Detect string ends with 'alize' ");
			int m = getm(result, "alize");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "al" ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "iciti")){
			//System.out.println("Detect string ends with 'iciti' ");
			int m = getm(result, "iciti");	
			if (m > 0){
				result = result.substring(0, result.length() - 5) + "ic" ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ical")){
			//System.out.println("Detect string ends with 'ical' ");
			int m = getm(result, "ical");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) + "ic" ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ful")){
			//System.out.println("Detect string ends with 'ful' ");
			int m = getm(result, "ful");	
			if (m > 0){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ness")){
			//System.out.println("Detect string ends with 'ness' ");
			int m = getm(result, "ness");	
			if (m > 0){
				result = result.substring(0, result.length() - 4) ;
				return result;
			}else{
				return result;
			}

		}
		return result;
	}
	
	public static String step4 (String input){
		String result = input;
		if (isEndWithString(result, "al")){
			//System.out.println("Detect string ends with 'al' ");
			int m = getm(result, "al");	
			if (m > 1){
				result = result.substring(0, result.length() - 2) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ance")){
			//System.out.println("Detect string ends with 'ance' ");
			int m = getm(result, "ance");	
			if (m > 1){
				result = result.substring(0, result.length() - 4) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ence")){
			//System.out.println("Detect string ends with 'ence' ");
			int m = getm(result, "ence");	
			if (m > 1){
				result = result.substring(0, result.length() - 4) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "er")){
			//System.out.println("Detect string ends with 'er' ");
			int m = getm(result, "er");	
			if (m > 1){
				result = result.substring(0, result.length() - 2) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ic")){
			//System.out.println("Detect string ends with 'ic' ");
			int m = getm(result, "ic");	
			if (m > 1){
				result = result.substring(0, result.length() - 2) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "able")){
			//System.out.println("Detect string ends with 'able' ");
			int m = getm(result, "able");	
			if (m > 1){
				result = result.substring(0, result.length() - 4) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ible")){
			//System.out.println("Detect string ends with 'ible' ");
			int m = getm(result, "ible");	
			if (m > 1){
				result = result.substring(0, result.length() - 4) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ant")){
			//System.out.println("Detect string ends with 'ant' ");
			int m = getm(result, "ant");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ement")){
			//System.out.println("Detect string ends with 'ement' ");
			int m = getm(result, "ement");	
			if (m > 1){
				result = result.substring(0, result.length() - 5) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ment")){
			//System.out.println("Detect string ends with 'ment' ");
			int m = getm(result, "ment");	
			if (m > 1){
				result = result.substring(0, result.length() - 4) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ent")){
			//System.out.println("Detect string ends with 'ent' ");
			int m = getm(result, "ent");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ou")){
			//System.out.println("Detect string ends with 'ou' ");
			int m = getm(result, "ou");	
			if (m > 1){
				result = result.substring(0, result.length() - 2) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ism")){
			//System.out.println("Detect string ends with 'ism' ");
			int m = getm(result, "ism");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ate")){
			//System.out.println("Detect string ends with 'ate' ");
			int m = getm(result, "ate");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "iti")){
			//System.out.println("Detect string ends with 'iti' ");
			int m = getm(result, "iti");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ous")){
			//System.out.println("Detect string ends with 'ous' ");
			int m = getm(result, "ous");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ive")){
			//System.out.println("Detect string ends with 'ive' ");
			int m = getm(result, "ive");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ize")){
			//System.out.println("Detect string ends with 'ize' ");
			int m = getm(result, "ize");	
			if (m > 1){
				result = result.substring(0, result.length() - 3) ;
				return result;
			}else{
				return result;
			}

		}
		if (isEndWithString(result, "ion")){
			//System.out.println("Detect string ends with 'ion' ");
			int m = getm(result, "ion");	
			if (m > 1){
				if (isEndWithChar(result.substring(0, result.length() - 3).toLowerCase(), "T") || isEndWithChar(result.substring(0, result.length() - 3).toLowerCase(), "S")){
					//System.out.println("Detect string ends with 'S' or 'T' ");
					result = result.substring(0, result.length() - 3) ;
					return result;
				}
				return result;
			}else{
				return result;
			}

		}
		return result;
	}
	
	
	public static String step5a(String input){
		String result = input;
		if (isEndWithChar(result, "e")){
			int m = getm(result, "e");	
			
			if (m > 1){
				result = result.substring(0, result.length() - 1);
				return result;
			}
			if (m == 1){
				if (! endWithcvc (result)){
					result = result.substring(0, result.length() - 1);
					return result;
				}
			}
			return result;
		}
		return result;
	}
	
	
	public static String step5b(String input){
		String result = input;
		int m = getm(result, "e");	
		
		if (m > 1){
			if (isEndWithChar(result, "l")){
				if (endsWithDoubleConsonant (result)){
					result = result.substring(0, result.length() - 1);
				}
			}
		}
		return result;
	}
	public static String getStartConstant(String input){
		String result = "";
		for (int index = 0; index < input.length(); index++){
			if (vowel.contains(input.substring(index, index + 1).toLowerCase())){
				//System.out.println("For String "+ input + ", the start consonant is " + result);
				return result;
			}
			else{
				result = result + input.substring(index, index + 1).toLowerCase();
			}
		}
		//System.out.println("For String "+ input + ", the start consonant is " + result);
		return result;
	}
	
	public static boolean isEndWithChar (String input, String endchar){
		if (input.length() == 1){
			return endchar.toLowerCase().equals(input.substring(input.length() - 1, input.length()).toLowerCase());
		}
		else{
			return !endchar.toLowerCase().equals(input.substring(input.length() - 2, input.length() - 1).toLowerCase()) && endchar.toLowerCase().equals(input.substring(input.length() - 1, input.length()).toLowerCase());
		}
		
	}
	
	public static boolean isEndWithString (String input, String endstring){
		int temp = 0;
		for (int index = endstring.length() - 1; index >= 0; index--){
			String currentChar = endstring.substring(index, index + 1);
			String correspondChar = input.substring(input.length() - 1 - temp, input.length() - temp);
			temp = temp + 1;
			if (!currentChar.toLowerCase().equals(correspondChar.toLowerCase())){
				return false;
			}
		}
		return true;
	}
	
	public static boolean containsVowel(String input){
		for (int index = 0; index < input.length(); index++){
			if (vowel.contains(input.toLowerCase().substring(index, index + 1))){
				return true;
			}
		}
		return false;
	}
	
	public static boolean endsWithDoubleConsonant(String input){
		if (input.length() <= 1){
			return false;
		}
		return (input.substring(input.length() - 1, input.length()).toLowerCase().equals(input.substring(input.length() - 2, input.length() - 1).toLowerCase()));
	}
	
	public static boolean endWithcvc(String input){
		if (input.length() <= 2){
			return false;
		}
		if (!vowel.contains(input.substring(input.length() - 1, input.length()).toLowerCase())){
			if (vowel.contains(input.substring(input.length() - 2, input.length() - 1).toLowerCase())){
				if (!vowel.contains(input.substring(input.length() - 3, input.length() - 2).toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getEndVowel(String input){
		String result = "";
		for (int index = input.length(); index >= 0 ; index--){
			if (vowel.contains(input.substring(index - 1, index).toLowerCase())){
				result = input.substring(index - 1, index).toLowerCase() + result;
			}
			else{
				//System.out.println("For String "+ input + ", the end vowel is " + result);
				return result;
			}
		}
		//System.out.println("For String "+ input + ", the end vowel is " + result);
		return result;		
	}
}
