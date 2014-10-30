package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MagicTrickRunner {

	public static class Trick{
		static final String volCheated="Volunteer cheated!";
		static final String magSucks="Bad magician!";
		Map<Integer,Map<Integer,Boolean>> boardMap1 = new HashMap<Integer, Map<Integer,Boolean>>();
		Map<Integer,Map<Integer,Boolean>> boardMap2 = new HashMap<Integer, Map<Integer,Boolean>>();
		Integer pick1;
		Integer pick2;
		void addBoard1Row(Map<Integer,Boolean> row, Integer rownum){
			
			boardMap1.put(rownum, row);
		}
		void addBoard2Row(Map<Integer,Boolean> row, Integer rownum){
			boardMap2.put(rownum, row);
		}
		void setPick1(Integer row){
			pick1=row;
		}
		void setPick2(Integer row){
			pick2=row;
		}
		String answer(){
			List<Integer> result = new ArrayList<>();
			Map<Integer,Boolean> r1 = boardMap1.get(pick1);
			Map<Integer,Boolean> r2 = boardMap2.get(pick2);
			if(r1!=null&&r2!=null){
				for(Integer i :r1.keySet()){
					if(r2.containsKey(i)){
						result.add(i);
						if(result.size()>1){
							break;
						}
					}
				}
			}
			String ret = null;
			if(result.size()==1){
				ret = String.valueOf(result.get(0));
			}else if(result.size()>1){
				ret = magSucks;
			}else{
				ret = volCheated;
			}
			return ret;
		}
	}
	

	static List<Trick> read(String fileName) throws IOException {
		List<Trick> tricks = new ArrayList<>();
	    Scanner scanner = new Scanner(new FileInputStream(fileName));
	    try {
	    	Integer numberofTests = Integer.valueOf(scanner.nextLine());
	      while(scanner.hasNextLine()){	    	  
	    	  Trick b = new Trick();  
	    	  Scanner pick1Line = new Scanner(scanner.nextLine());
	    	  Scanner board1Row1 = new Scanner(scanner.nextLine());
	    	  Scanner board1Row2 = new Scanner(scanner.nextLine());
	    	  Scanner board1Row3 = new Scanner(scanner.nextLine());
	    	  Scanner board1Row4 = new Scanner(scanner.nextLine());
	    	  Scanner pick2Line = new Scanner(scanner.nextLine());
	    	  Scanner board2Row1 = new Scanner(scanner.nextLine());
	    	  Scanner board2Row2 = new Scanner(scanner.nextLine());
	    	  Scanner board2Row3 = new Scanner(scanner.nextLine());
	    	  Scanner board2Row4 = new Scanner(scanner.nextLine());
	    	  
	    	  b.setPick1(Integer.valueOf(pick1Line.next()));
        	  
	           b.addBoard1Row(readRow(board1Row1), Integer.valueOf(1));
	           b.addBoard1Row(readRow(board1Row2), Integer.valueOf(2));
	           b.addBoard1Row(readRow(board1Row3), Integer.valueOf(3));
	           b.addBoard1Row(readRow(board1Row4), Integer.valueOf(4));
	    	  b.setPick2(Integer.valueOf(pick2Line.next()));     	  
	           b.addBoard2Row(readRow(board2Row1), Integer.valueOf(1));
	           b.addBoard2Row(readRow(board2Row2), Integer.valueOf(2));
	           b.addBoard2Row(readRow(board2Row3), Integer.valueOf(3));
	           b.addBoard2Row(readRow(board2Row4), Integer.valueOf(4));
	    	  tricks.add(b);
	    	  pick1Line.close();
	    	  board1Row1.close();
	    	  board1Row2 .close();
	    	  board1Row3 .close();
	    	  board1Row4 .close();
	    	  pick2Line.close();
	    	  board2Row1.close();
	    	  board2Row2.close();  
	    	  board2Row3.close();
	    	  board2Row4.close();
	      }
	      return tricks;
	    }
	    finally{
	      scanner.close();
	      
	    }
	   
	  }
	private static Map<Integer, Boolean> readRow(Scanner board1Row1) {
		Map<Integer,Boolean> board1r = new HashMap<Integer,Boolean>();
		   while(board1Row1.hasNext()){
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
		for(Trick b : read("C:\\Users\\bedroomXP\\Downloads\\A-small-attempt0.in")){
		
			answers.add(String.valueOf(b.answer()));
			
		}
		output(answers);
	}
	static void output(List<String> answers) throws IOException{
		StringBuilder output = new StringBuilder();
		for(int i=0;i<answers.size();i++){
			output.append("Case #");
			output.append(i+1);
			output.append(": ");
			output.append(answers.get(i));
			output.append("\r\n");
		}
		File file = new File("C:\\Users\\bedroomXP\\magicTest.out");
		 
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
