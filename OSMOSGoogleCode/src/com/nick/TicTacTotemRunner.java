package com.nick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacTotemRunner {

	public static class Board{
		int[] rows ={0,0,0,0};
		int[] cols ={0,0,0,0};
		int diag1;
		int diag2;
		int count;
		boolean isXWinner(){
			return (rows[0]==4)
					|| (rows[1]==4)
					|| (rows[2]==4)
					|| (rows[3]==4)
					|| (cols[0]==4)
					|| (cols[1]==4)
					|| (cols[2]==4)
					|| (cols[3]==4)
					|| (diag1==4)
					|| (diag2==4);
					
		}
		boolean isOWinner(){
			return (rows[0]==-4)
			|| (rows[1]==-4)
			|| (rows[2]==-4)
			|| (rows[3]==-4)
			|| (cols[0]==-4)
			|| (cols[1]==-4)
			|| (cols[2]==-4)
			|| (cols[3]==-4)
			|| (diag1==-4)
			|| (diag2==-4);
		}
		boolean isDone(){
			return count ==16;
		}
		void addMove(String m, int row, int col){
			if(m.equalsIgnoreCase("X")){
				if(rows[row]==-100){
					rows[row]=1;
				}else{
					rows[row]++;
				}
				if(cols[col]==-100){
					cols[col]=1;
				}else{
					cols[col]++;
				}
				if(row==col){
					if(diag1==-100){
						diag1=1;
					}else{
						diag1++;
					}
				
				}else if( row==3 && col==0
						|| row==2 && col==1
						|| row==1 && col==2
						|| row==0 && col==3){
					if(diag2==-100){
						diag2=1;
					}else{
						diag2++;
					}
				}
				count++;
			}else if(m.equalsIgnoreCase("O")){
				if(rows[row]==-100){
					rows[row]=-1;
				}else{
					rows[row]--;
				}
				if(cols[col]==-100){
					cols[col]=-1;
				}else{
					cols[col]--;
				}
				if(row==col){
					if(diag1==-100){
						diag1=-1;
					}else{
						diag1--;
					}
				
				}else if( row==3 && col==0
						|| row==2 && col==1
						|| row==1 && col==2
						|| row==0 && col==3){
					if(diag2==-100){
						diag2=-1;
					}else{
						diag2--;
					}
				}
				count++;
			}else if(m.equalsIgnoreCase("T")){
				if(rows[row]<0){
					rows[row]--;
				}else if(rows[row]>0){
					rows[row]++;
				}else{
					rows[row]=-100;
				}
				if(cols[col]<0){
					cols[col]--;
				}else if(rows[row]>0){
					cols[col]++;
				}else{
					cols[col]=-100;
				}
				
				if(row==col){
					if(diag1<0){
						diag1--;
					}else if(diag1>0){
						diag1++;
					}else{
						diag1=-100;
					}
				}else if( row==3 && col==0
						|| row==2 && col==1
						|| row==1 && col==2
						|| row==0 && col==3){
					if(diag2<0){
						diag2--;
					}else if(diag1>0){
						diag2++;
					}else{
						diag2=-100;
					}
				}
				count++;
			}
			
		}
	}
	static List<Board> read(String fileName) throws IOException {
	    
	    
	    Scanner scanner = new Scanner(new FileInputStream(fileName));
	    try {
	    	Integer numberofTests = Integer.valueOf(scanner.nextLine());
	    	List<Board> boards = new ArrayList<Board>();
	      for(int i =0;i<numberofTests.intValue();i++){
	    	  
	    	  Board b = new Board();
	    	  for(int j =0;j<4;j++){
	    		  String s = scanner.nextLine();
	            for(int k =0;k<4;k++){	                
	                b.addMove(String.valueOf(s.charAt(k)), j, k);
	            }
	            
	    	  }
	    	  boards.add(b);
	    	  if(scanner.hasNextLine()){
	    	  scanner.nextLine();
	    	  }
	      }
	      return boards;
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
		for(Board b : read("C:\\Users\\bedroomXP\\Downloads\\A-large.in")){
			if(b.isXWinner()){
				answers.add("X won");
				
			}else if(b.isOWinner()){
				answers.add("O won");
				
			}else if( b.isDone()){
				answers.add("Draw");
			}else {
				answers.add("Game has not completed");
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
		File file = new File("C:\\Users\\bedroomXP\\output.txt");
		 
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
