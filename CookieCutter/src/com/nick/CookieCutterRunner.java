package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CookieCutterRunner {

	public static class Game{
		double farmCost;
		double cookieGoal;
		double farmBonus;
		double currentRate=2;
		double timeSpent=0;
		double timeToReachGoal(){
			return (cookieGoal/currentRate);
		}
		double timeToReachGoalWithNewBonus(){
			return cookieGoal/(currentRate+farmBonus);
		}
		double timeToReachFarm(){
			return farmCost/currentRate;
		}
		String answer(){
			
			while(true){
				double timeToReachGoal=timeToReachGoal();
				double timeToReachFarm=timeToReachFarm();	
				double timeToReachGoalWithBonus=timeToReachGoalWithNewBonus();
				if(timeToReachGoal<=timeToReachFarm){
					timeSpent+=timeToReachGoal;
					break;
				}else if(timeToReachGoal<=(timeToReachFarm+timeToReachGoalWithBonus)){
					timeSpent+=timeToReachGoal;
					break;
				}else{
					timeSpent+=timeToReachFarm;
					currentRate+=farmBonus;
				}
			}
			return String.valueOf(timeSpent);
		}
	}
	

	static List<Game> read(String fileName) throws IOException {
		List<Game> games = new ArrayList<>();
	    Scanner scanner = new Scanner(new FileInputStream(fileName));
	    try {
	    	Integer numberofTests = Integer.valueOf(scanner.nextLine());
	      while(scanner.hasNextLine()){	    	  
	    	  Game b = new Game();  
	    	  Scanner gameLine = new Scanner(scanner.nextLine());
	    	  b.farmCost=Double.valueOf(gameLine.next());
	    	  b.farmBonus=Double.valueOf(gameLine.next());
	    	  b.cookieGoal=Double.valueOf(gameLine.next());
	    	  gameLine.close();
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
		for(Game b : read("C:\\Users\\bedroomXP\\Downloads\\B-large.in")){
		
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
		File file = new File("C:\\Users\\bedroomXP\\cookie-large.out");
		 
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
