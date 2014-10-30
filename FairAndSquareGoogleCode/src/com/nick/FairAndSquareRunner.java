package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FairAndSquareRunner {
	static HashMap<Long, Boolean> h = new HashMap<>();

	public static boolean isP(String s) {
		int n = s.length();
		for (int i = 0; i < (n / 2) + 1; ++i) {
			if (s.charAt(i) != s.charAt(n - i - 1)) {
				return false;
			}
		}

		return true;
	}

	long min;
	long max;
	static Pattern regexBin;
	static Pattern regexTer;
	static Pattern regexTer2;
	static Pattern regexEvenEven;
	static Pattern regexEvenOdd;

	public static class Range {

		long min;
		long max;

		public Range(long min, long max) {
			super();
			this.min=min;
			this.max=max;
		}

		int getCount() {
			if(min==-1){
				return 0;
			}
			int count = 0;
			long sqrMin = (long) Math.sqrt(Double.valueOf(min).doubleValue());
			long sqrMax = (long) Math.sqrt(Double.valueOf(max).doubleValue());
			while (sqrMin * sqrMin < min) {
				sqrMin++;
			}
			for (Long l :h.keySet()) {
				if (l.longValue()>=sqrMin && l.longValue()<=sqrMax) {
					count++;
					
				}

			}
			return count;
		}

	}

	static List<Range> read(String fileName) throws IOException {

		Scanner scanner = new Scanner(new FileInputStream(fileName));
		try {
			Integer numberofTests = Integer.valueOf(scanner.nextLine());
			List<Range> ranges = new ArrayList<Range>();
			for (int i = 0; i < numberofTests.intValue(); i++) {
				Scanner s = new Scanner(scanner.nextLine());
				String min = s.next();
				String Max = s.next();
				long minL;
				long maxL;
				Range b = null;
				try {
					minL = Long.valueOf(min);
					try {
						maxL = Long.valueOf(Max);
					} catch (Exception e) {
						maxL = Long.MAX_VALUE;
					}
					b = new Range(-1, -1);
					
				} catch (NumberFormatException e) {
					//Not valid as we dont no paladrom this hight
				}
				

				s.close();
				if(b!=null){
					ranges.add(b);
				}
			}
			return ranges;
		} finally {
			scanner.close();
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		regexBin = Pattern.compile("^10*1?0*1?0*1?0*1?0*1?0*1?0*1$");
//		regexTer = Pattern.compile("^10*1?0*1?0*1?0*[01]?0*1?0*1?0*1?0*1$");
//		regexTer2 = Pattern.compile("^10+1?0*2?0*1?0+1$");
//		regexEvenEven = Pattern.compile("^20+2$");
//		regexEvenOdd = Pattern.compile("^20+[/d]0+2$");
//		for (long i = 0; i <= Math.pow(10, 20); i++) {
//			Long x = Long.valueOf(i);
//			int count = 0;
//			if (h.get(x) == null) {
//				String s = String.valueOf(i);
//				if (i == 0 || i == 1 || 1 == 2 || i == 3 || i == 11|| i == 22 || i == 121) {
//					count++;
//					h.put(x, Boolean.TRUE);
//					System.out.println(s);
//				}
//				if (s.length() % 2 == 0 && regexBin.matcher(s).matches()) {
//
//					if (isP(s)) {
//						System.out.println(s);
//						count++;
//						h.put(x, Boolean.TRUE);
//					}
//				} else if (s.length() % 2 != 0 && regexTer.matcher(s).matches()) {
//
//					if (isP(s)) {
//						count++;
//						System.out.println(s);
//						h.put(x, Boolean.TRUE);
//					}
//				} else if (s.length() % 2 != 0
//						&& regexTer2.matcher(s).matches()) {
//
//					if (isP(s)) {
//						count++;
//						System.out.println("h.put(Long.valueOf("+s+"),Boolean.TRUE");
//						h.put(x, Boolean.TRUE);
//					}
//				} else if (s.length() % 2 != 0
//						&& regexEvenOdd.matcher(s).matches()) {
//
//					if (isP(s)) {
//						count++;
//						System.out.println(s);
//						h.put(x, Boolean.TRUE);
//					}
//				} else if (s.length() % 2 == 0
//						&& regexEvenEven.matcher(s).matches()) {
//
//					if (isP(s)) {
//						count++;
//						System.out.println(s);
//						h.put(x, Boolean.TRUE);
//					}
//				} else {
//
//					h.put(x, Boolean.FALSE);
//				}
//			} else if (h.get(x)) {
//				count++;
//			}
//
//		}
h.put(Long.valueOf(0),Boolean.TRUE);
h.put(Long.valueOf(1),Boolean.TRUE);
h.put(Long.valueOf(2),Boolean.TRUE);
h.put(Long.valueOf(3),Boolean.TRUE);
h.put(Long.valueOf(11),Boolean.TRUE);
h.put(Long.valueOf(22),Boolean.TRUE);
h.put(Long.valueOf(121),Boolean.TRUE);
h.put(Long.valueOf(101),Boolean.TRUE);
h.put(Long.valueOf(111),Boolean.TRUE);
h.put(Long.valueOf(121),Boolean.TRUE);
h.put(Long.valueOf(1001),Boolean.TRUE);
h.put(Long.valueOf(1111),Boolean.TRUE);
h.put(Long.valueOf(2002),Boolean.TRUE);
h.put(Long.valueOf(10001),Boolean.TRUE);
h.put(Long.valueOf(10101),Boolean.TRUE);
h.put(Long.valueOf(10201),Boolean.TRUE);
h.put(Long.valueOf(11011),Boolean.TRUE);
h.put(Long.valueOf(11111),Boolean.TRUE);
h.put(Long.valueOf(100001),Boolean.TRUE);
h.put(Long.valueOf(101101),Boolean.TRUE);
h.put(Long.valueOf(110011),Boolean.TRUE);
h.put(Long.valueOf(111111),Boolean.TRUE);
h.put(Long.valueOf(200002),Boolean.TRUE);
h.put(Long.valueOf(1000001),Boolean.TRUE);
h.put(Long.valueOf(1001001),Boolean.TRUE);
h.put(Long.valueOf(1002001),Boolean.TRUE);
h.put(Long.valueOf(1010101),Boolean.TRUE);
h.put(Long.valueOf(1011101),Boolean.TRUE);
h.put(Long.valueOf(1012101),Boolean.TRUE);
h.put(Long.valueOf(1100011),Boolean.TRUE);
h.put(Long.valueOf(1101011),Boolean.TRUE);
h.put(Long.valueOf(1110111),Boolean.TRUE);
h.put(Long.valueOf(1111111),Boolean.TRUE);
h.put(Long.valueOf(10000001),Boolean.TRUE);
h.put(Long.valueOf(10011001),Boolean.TRUE);
h.put(Long.valueOf(10100101),Boolean.TRUE);
h.put(Long.valueOf(10111101),Boolean.TRUE);
h.put(Long.valueOf(11000011),Boolean.TRUE);
h.put(Long.valueOf(11011011),Boolean.TRUE);
h.put(Long.valueOf(11100111),Boolean.TRUE);
h.put(Long.valueOf(11111111),Boolean.TRUE);
		 List<String> answers = new ArrayList<String>();
		 for(Range l : read("C:\\Users\\bedroomXP\\Downloads\\C-large-2.in")){
		 answers.add(String.valueOf(l.getCount()));
		
		 }
		 output(answers);
	}

	static void output(List<String> answers) throws IOException {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < answers.size(); i++) {
			output.append("Case #");
			output.append(i + 1);
			output.append(": ");
			output.append(answers.get(i));
			output.append("\r\n");
		}
		File file = new File(
				"C:\\Users\\bedroomXP\\Downloads\\outputSQRLarge2.txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(output.toString());
		bw.close();
	}
}