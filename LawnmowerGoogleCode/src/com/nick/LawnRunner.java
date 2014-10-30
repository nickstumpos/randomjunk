package com.nick;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class LawnRunner{

public static class Lawn{
		List<List<Integer>> rows= new ArrayList<List<Integer>>();
		void addRow(List<Integer> r){
			rows.add(r);
		}
		boolean isPath(int row, int col){
			 int n = rows.get(row).get(col).intValue();
			 boolean isRowPeak = true;
			for(Integer i :rows.get(row)){
				 if(i.intValue()>n){
					 isRowPeak=false;
					 break;
				 }
			 }
			if(isRowPeak){
				return true;
			}
				boolean isColPeak = true;
				for(List<Integer> c :rows){
					 if(c.get(col).intValue()>n){
						 isColPeak=false;
						 break;
					 }
				 }
			
			return isColPeak;
		}
		
		boolean isPossible(){
			boolean ret = true;
			for(int row = 0;row<rows.size();row++){
				for(int col=0;col<rows.get(row).size();col++){
					if(!isPath(row, col)){
						ret = false;
						break;
					}
				}
				if(ret==false){
					break;
				}
			}
			return ret;
		}
	}
	static List<Lawn> read(String fileName) throws IOException {
	    
	    
	    Scanner scanner = new Scanner(new FileInputStream(fileName));
	    try {
	    	Integer numberofTests = Integer.valueOf(scanner.nextLine());
	    	List<Lawn> lawns = new ArrayList<Lawn>();
	      for(int i =0;i<numberofTests.intValue();i++){
	    	  Scanner s = new Scanner(scanner.nextLine());
	    	  int numberrows = Integer.valueOf(s.next()).intValue();
	    	  Lawn b = new Lawn();
	    	 
	    	  for(int j =0;j<numberrows;j++){
	    		  Scanner row = new Scanner(scanner.nextLine());
	    		  List<Integer> lawnRow = new ArrayList<Integer>();
	            while(row.hasNext()){                
	                lawnRow.add(Integer.valueOf(row.next()));
	                
	            }
	            row.close();
	            b.addRow(lawnRow);
	    	  }
	    	  s.close();
	    	  lawns.add(b);
	      }
	      return lawns;
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
		for(Lawn l : read("C:\\Users\\bedroomXP\\Downloads\\B-large.in")){
			if(l.isPossible()){
				answers.add("YES");
			}else{
				answers.add("NO");
			}
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
		File file = new File("C:\\Users\\bedroomXP\\Downloads\\outputLawn.txt");
		 
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