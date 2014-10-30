package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class MineSweeperSolverRunner {

	public static class Game {
		long numberOfBombs;
		List<Stack<String>> board = new ArrayList<>();
		int rows;
		int columns;

		private void fillBlanksAndNumbers() {
			for (int i = board.size() - 1; i >= 0; i--) {
				Stack<String> s = board.get(i);
				if (i > 0) {
					while (!board.get(i - 1).isEmpty()
							&& s.size() < board.get(i - 1).size() + 1) {
						if (s.size() < columns) {
							s.push("#");
						} else {
							break;
						}
					}
				}
				while (!s.isEmpty() && s.size() < columns) {
					if ("*".equals(s.peek())) {
						s.push("#");
					} else {
						s.push(".");
					}
				}

			}
		}

		private int fillMinesFromTopLeft() {
			int placed = 0;
				if(columns==1){
					for(Stack<String> s : board){
						if (numberOfBombs>placed) {
							s.push("*");
							placed++;
							if(placed==numberOfBombs){
								break;
							}
						}
					}
				}else if(rows==1){
					for(Stack<String> s : board){
						if (numberOfBombs>placed) {
							while(s.size()<columns){
								s.push("*");
								placed++;
								break;
							}
						}
					}
				}else if(columns==2){
					return placed;
				}
				int counter=0;
				for(Stack<String> s : board){
					counter++;
					if (numberOfBombs>placed) {
						while (s.size() < columns-2) {
							s.push("*");
							placed++;
							if (placed >= numberOfBombs) {
								break;
							}
						}						
					}
				}
				if(counter >1 && counter<rows && rows-counter==1){
					if(board.get(counter-1).size()-board.get(counter).size()==1){
						board.get(counter).push(board.get(counter-1).pop());
					}
				}
			return placed;
		}

		String answer() {
			String ret = "\r\n";
			for (int i = 0; i < rows; i++) {
				board.add(new Stack<String>());
			}
			int placed = fillMinesFromTopLeft();
			if (placed != numberOfBombs) {
				ret += "Impossible"+"\r\n";

			} else {
				fillBlanksAndNumbers();

				if ((!(numberOfBombs+1==rows*columns))&&!board.get(board.size() - 1).isEmpty()
						&& ("#".equals(board.get(board.size() - 1).peek()) || "*"
								.equals(board.get(board.size() - 1).peek()))) {
					ret = "\r\nImpossible\r\n";
				} else {
					for (int i = 1; i <= board.size(); i++) {

						while (!board.get(board.size() - i).isEmpty()
								&& !("#".equals(board.get(board.size() - i)
										.peek()) || "*".equals(board.get(
										board.size() - i).peek()))) {
							board.get(board.size() - i).pop();
						}

						while (!board.get(board.size() - i).isEmpty()
								&& ("#".equals(board.get(board.size() - i)
										.peek()))) {
							board.get(board.size() - i).pop();
						}
						while (board.get(board.size() - i).size() < columns) {
							board.get(board.size() - i).push(".");
						}
					}
					board.get(board.size() - 1).pop();
					board.get(board.size() - 1).push("c");
					for (Stack<String> s : board) {
						while (s.size() < columns) {
							s.push(".");
						}
					}
					// print reversed as im lazy
					
				}
			}
			for (Stack<String> s : board) {
				while (!s.isEmpty()) {
					ret += s.pop();
				}
				ret += "\r\n";
			}
			return ret;
		}
	}

	static List<Game> read(String fileName) throws IOException {
		List<Game> games = new ArrayList<>();
		Scanner scanner = new Scanner(new FileInputStream(fileName));
		try {
			Integer numberofTests = Integer.valueOf(scanner.nextLine());
			while (scanner.hasNextLine()) {
				Game b = new Game();
				Scanner gameLine = new Scanner(scanner.nextLine());
				b.rows = gameLine.nextInt();
				b.columns = gameLine.nextInt();
				b.numberOfBombs = gameLine.nextLong();
				games.add(b);
				gameLine.close();

			}
			return games;
		} finally {
			scanner.close();

		}

	}

	private static Map<Integer, Boolean> readRow(Scanner board1Row1) {
		Map<Integer, Boolean> board1r = new HashMap<Integer, Boolean>();
		while (board1Row1.hasNext()) {
			board1r.put(Integer.valueOf(board1Row1.next()), Boolean.TRUE);
		}
		return board1r;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		List<String> answers = new ArrayList<String>();
		for (Game b : read("C:\\Users\\bedroomXP\\Downloads\\C-small-attempt5.in")) {

			answers.add(String.valueOf(b.answer()));

		}
		output(answers);
	}

	static void output(List<String> answers) throws IOException {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < answers.size(); i++) {
			output.append("Case #");
			output.append(i + 1);
			output.append(":");
			output.append(answers.get(i));
			// output.append("\r\n");
		}
		File file = new File("C:\\Users\\bedroomXP\\mine.out");

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
