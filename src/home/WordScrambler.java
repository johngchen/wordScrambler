package home;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import java.util.Iterator;

public class WordScrambler {

	// use HashSet to keep only the unique results
	private static HashSet<String> resultSet;
	// use \n to break out the line when run in main
	private static String lineBreak = "<br>";

	private static Scanner scanner = null;
	private static Set<String> dictionary = null;
	private static long counter = 0;  // just curious how many permutations generated

	public WordScrambler() throws FileNotFoundException {
		super();
		// This must be re-initiated to be run properly on a web server -- ensure to start from an empty set
		resultSet = new HashSet<String>();
		counter = 0;
	
		// only need to initiate the dictionary one for the life time of the program
		if (scanner == null) {
			URL url = this.getClass().getResource("dictionary.txt");
			scanner = new Scanner(new File(url.getPath()));
			
			dictionary = new HashSet<String>();
			// For each word in the input
			while (scanner.hasNext()) {
			    // convert the word to lower case, trim it and insert into the set
				dictionary.add(scanner.next().trim().toLowerCase());
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		// main is to be run locally, so use "\n" instead of "<br>" for pretty
		// print.
		lineBreak = "\n";
		String inputString = "petBa";
		// maybe I only want to print strings 3 or 4 letters long to reduce
		// cluster, e.g. {3,4}
		Integer[] desiredLen = { 3 };
		// unscramble(inputString, desiredLen);

		WordScrambler ws = new WordScrambler();
		ws.unscrambleAll(inputString);
	}

	/**
	 * Assistant function to prepare for the main entry point unscramble(String inputString, Integer[] desiredLen)
	 * @param inputString
	 * @return
	 * @throws FileNotFoundException
	 */
	public String unscrambleAll(String inputString)
			throws FileNotFoundException {
		int fullLength = inputString.length();
		Integer desiredLen[] = new Integer[fullLength];
		for (int i = 0; i < fullLength; i++) {
			desiredLen[i] = i + 1;  //array index starts from 0 but length starts from 1
		}
		return unscramble(inputString, desiredLen);
	}

	/**
	 * Give the option to list only permutations of given length
	 * 
	 * @param inputString
	 * @param desiredLen
	 *            e.g. {3,4}
	 * @return
	 * @throws FileNotFoundException
	 */
	public String unscramble(String inputString, Integer[] desiredLen)
			throws FileNotFoundException {
		String resultString = "";

		inputString = inputString.toLowerCase();
		int strLen = inputString.length();

		WordScrambler ws = new WordScrambler();
		ws.permuteThenRmALetter(inputString);

		// HashSet cannot be sorted, so use an array of arrayLists to sort
		// Each ArrayList in the array Keeps the strings with the same length together
		ArrayList<ArrayList<String>> sortedArray = new ArrayList<ArrayList<String>>();
		for (int i = 1; i <= strLen; i++) {
			sortedArray.add(new ArrayList<String>());
		}
		// Put the results into different lists according to the string length
		Iterator<String> iterator = resultSet.iterator();
		while (iterator.hasNext()) {
			String strTemp = iterator.next();
			int i = strTemp.length() - 1; // decide where to put it in the array
			
			// before put it in the final sortedArray, do a spell check and mark the correct words
			// also, skip checking words with 1 or 2 letters to improve performance
			if (i > 1) {
				strTemp = ws.pickTheCorrectWords(strTemp);
			}
			// note: i is the length of the string before the spell check and reformat
			sortedArray.get(i).add(strTemp);
		}
		
		// sort and print
		boolean AIspoken = false; // print the message only once
		ArrayList<String> arrayListWithWordsOfCertainLength;
		for (int i = 1; i <= sortedArray.size(); ++i) {
			// the for loop starts from 1, not 0
			arrayListWithWordsOfCertainLength = sortedArray.get(i - 1);
			
			// curious to know how many permutations generated in total
			counter += arrayListWithWordsOfCertainLength.size();
			// System.out.println(i + " : " + arrayListWithWordsOfCertainLength.size());
		
			
			// no need to print 1 or 2 letters, too easy. Use it to print header.
			if (i <= 2) { 
				if (!AIspoken) {
					resultString += (lineBreak
							+ "Lord A.I. trusts you can figure out words with 1 or even 2 letters." + lineBreak);
				}
				AIspoken = true;
				continue;
			}
			if (Arrays.asList(desiredLen).contains(i)) {			
				resultString += (lineBreak + "Permutations with " + i
						+ " letters in it:" + lineBreak);
				
				Collections.sort(arrayListWithWordsOfCertainLength); 
				resultString += arrayListWithWordsOfCertainLength;
				resultString += lineBreak;
			} else {
				resultString += (lineBreak
						+ "Strings with the following length are skipped: " + i + lineBreak);
				resultString += lineBreak;
			}
		}
		
		resultString = "Total number of permutations generated: " + counter + lineBreak + resultString; 
		System.out.println(resultString);
		return resultString;
	}

	/**
	 * Check the word against the dictionary.txt from
	 * https://github.com/dwyl/english-words with 350k+ English words
	 * 
	 * @param strTemp
	 * @return a word, surrounded by HTML tags to let it stand out on webpage
	 * @throws FileNotFoundException
	 */
	private String pickTheCorrectWords(String strTemp) {
		String fmtedString = strTemp;
		/*
		 * System.out.println(System.getProperty("user.dir"));
		 * System.out.println(strTemp);
		 */

		if (dictionary.contains(strTemp)) {
			// taggedWord is a custom tag to be used in ccs in main.jsp
			fmtedString = "<a target=\"_blank\" href=\"https://www.google.com/search?q="
					+ strTemp
					+ "&amp;ie=utf-8&amp;oe=utf-8\"><strong class=\"taggedWord\">" + strTemp + "</strong></a>";
			// System.out.println(fmtedString);
		}

		return fmtedString;
	}

	/**
	 * Because the permute method will produce all permutations using all
	 * letters in the input string, it is necessary to feed the permute method
	 * all possible (n-1) letters by going through the whole input string and
	 * removing each letter one at a time.
	 */
	private void permuteThenRmALetter(String strInput) {
		int n = strInput.length();
		String nextString;

		if (n == 0) {
			// stop
		} else {
			permute(strInput);
			for (int i = 0; i < n; i++) {
				nextString = strInput.substring(0, i)
						+ strInput.substring(i + 1);
				permuteThenRmALetter(nextString);
			}
		}
	}

	private void permute(String str) {
		permute("", str);
	}

	private void permute(String prefix, String str) {
		int n = str.length();
		if (n == 0) {
			// System.out.println(prefix);
			resultSet.add(prefix);
		} else {
			for (int i = 0; i < n; i++)
				permute(prefix + str.charAt(i),
						str.substring(0, i) + str.substring(i + 1, n));
		}
	}
}
