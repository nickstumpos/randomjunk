package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class OsmosRunner {

	public static class Mote{
		int size;
		
	}
	
	public static class Game{
		PriorityQueue<Integer> motes;
		Mote mainMote;
		
		public boolean simplify(){
			while(!motes.isEmpty() && motes.peek()<mainMote.size){
				mainMote.size = mainMote.size+motes.poll().intValue();
			}
			return motes.isEmpty();
		}
		public int playGame(){
				boolean done = simplify();
				int moves = 0;
				if(!done){
				while(!simplify()){
					Mote testMote = new Mote();
					testMote.size= mainMote.size;
					int numberToEat = 0;
					if(testMote.size<=1){
						moves= motes.size();
						break;
					}
					while(true){
						numberToEat++;
						testMote.size=testMote.size+testMote.size-1;
						if(testMote.size+testMote.size-1>motes.peek().intValue()){
							break;
						}
					}
					mainMote.size=testMote.size;
					if(simplify()){
						moves+=numberToEat;
						break;
					}
					if(numberToEat>motes.size()){
						moves+=motes.size();
						break;
					}else{
						moves+=numberToEat;
					}
				}
				
		
				}
				return moves;
		
	}
	}
	static List<Game> read(String fileName) throws IOException {
		List<Game> games = new ArrayList<>();
	    Scanner scanner = new Scanner(new FileInputStream(fileName));
	    try {
	    	Integer numberofTests = Integer.valueOf(scanner.nextLine());
	      while(scanner.hasNextLine()){	    	  
	    	  Game b = new Game();  
	    	  Scanner line1 = new Scanner(scanner.nextLine());
	    	  Scanner line2 = new Scanner(scanner.nextLine());
	    	  Mote mainMote = new Mote();
	    	  mainMote.size=Integer.valueOf(line1.next()).intValue();
	          PriorityQueue<Integer> motes = new PriorityQueue<>(Integer.valueOf(line1.next()).intValue());
	          while(line2.hasNext()){
	        	  motes.add(Integer.valueOf(line2.next()));
	          }
	    	  b.mainMote=mainMote;
	    	  b.motes = motes;
	    	  games.add(b);
	      }
	      return games;
	    }
	    finally{
	      scanner.close();
	    }
	   
	  }
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		List<String> answers = new ArrayList<String>();
		for(Game b : read("C:\\Users\\bedroomXP\\Downloads\\A-small-attempt1.in")){
		
			answers.add(String.valueOf(b.playGame()));
			
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
		File file = new File("C:\\Users\\bedroomXP\\A-small.out");
		 
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
