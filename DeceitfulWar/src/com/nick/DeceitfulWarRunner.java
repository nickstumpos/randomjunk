package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class DeceitfulWarRunner {

	public static class Game{
		List<Double> myWeights= new ArrayList<>();
		List<Double> enemyWeight= new ArrayList<>();
		
		String answer(){
		String ret="";
		PriorityQueue<Double> myWeightsQ = new PriorityQueue<Double>(myWeights);
		PriorityQueue<Double> enemyWeightQ = new PriorityQueue<>(enemyWeight.size(), new Comparator<Double>() {

			@Override
			public int compare(Double arg0, Double arg1) {
				return -1*arg0.compareTo(arg1);
			}
		});
		enemyWeightQ.addAll(enemyWeight);
		int myScore=0;
		while(!myWeightsQ.isEmpty() && !enemyWeightQ.isEmpty()){
			Double myCoice=myWeightsQ.poll();
			Double enemyChoice=enemyWeightQ.poll();
			if(myCoice>enemyChoice){
				myScore++;
			}
		}
		 myWeightsQ = new PriorityQueue<Double>(myWeights.size(), new Comparator<Double>() {

				@Override
				public int compare(Double arg0, Double arg1) {
					return -1*arg0.compareTo(arg1);
				}
			});
		 myWeightsQ.addAll(myWeights);
		 enemyWeightQ = new PriorityQueue<Double>(enemyWeight.size(), new Comparator<Double>() {

				@Override
				public int compare(Double arg0, Double arg1) {
					return -1*arg0.compareTo(arg1);
				}
			});
		 enemyWeightQ.addAll(enemyWeight);
		 int myScore2=0;
			while(!myWeightsQ.isEmpty() && !enemyWeightQ.isEmpty()){
				if(myWeightsQ.poll()>enemyWeightQ.poll()){
					myScore2++;
				}
			}
			int deceiptScore = Math.max(myScore, myScore2);
		return String.valueOf(deceiptScore)+" "+String.valueOf(myScore2);
		}
	}
	

	static List<Game> read(String fileName) throws IOException {
		List<Game> games = new ArrayList<>();
	    Scanner scanner = new Scanner(new FileInputStream(fileName));
	    try {
	    	Integer numberofTests = Integer.valueOf(scanner.nextLine());
	      while(scanner.hasNextLine()){	   
	    	  scanner.nextLine();
	    	  Game b = new Game();  
	    	  Scanner myLine = new Scanner(scanner.nextLine());
	    	  Scanner enemyLine = new Scanner(scanner.nextLine());
	    	  while(myLine.hasNext()){
	    		  b.myWeights.add(myLine.nextDouble());
	    	  }
	    	  while(enemyLine.hasNext()){
	    		  b.enemyWeight.add(enemyLine.nextDouble());
	    	  }
	    	  enemyLine.close();
	    	  myLine.close();
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
		for(Game b : read("C:\\Users\\bedroomXP\\Downloads\\war.in")){
		
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
		File file = new File("C:\\Users\\bedroomXP\\war.out");
		 
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
