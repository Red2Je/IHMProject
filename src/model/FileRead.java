package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileRead {


	/**
	 * A method to read the content of the csv file passed in parameter
	 * @param path the path of the csv files to read
	 * @return an ArrayList of ArrayList of doubles, containing the datas in a matrix form
	 */
	public static ArrayList<ArrayList<Double>> Read(String path) {
		ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
		System.out.println("[FileReader] Reading the file...");
		try {
			FileReader file = new FileReader(path);
			BufferedReader bufRead = new BufferedReader(file);
			bufRead.readLine();//we avoid the line telling what is what
			String line = bufRead.readLine();
			
			while ( line != null) {
			   	String[] array = line.split(",");
			   	//we wont be reading the latitude and longitude as they are constant, we will get these data from the coordinate of the matrix that is output
			   	
			   	//Then we read the values, replacing the N/A by zeros
			   	
		   			ArrayList<Double> values = new ArrayList<>();//We create an arraylist for each lines
				   	for(int column = 2 ; column < array.length ; column++) {
				   		try {
				   			values.add(Double.parseDouble(array[column]));//If the parsing is valid, we add the value parsed to the Arraylist
				   		}catch(NumberFormatException nfe) {
				   			values.add(Double.NaN);//if the value in the case isn't a double or can be parsed as a double, we set the value to 0
				   		}

				   	}
			   		output.add(values);
			    line = bufRead.readLine();
			}

			bufRead.close();
			file.close();
			
			
		} catch (IOException e) {
			System.out.println("[FileReader] The reading has terminated : ");
			e.printStackTrace();
		}
		System.out.println("[FileReader] End of the reading");
		return(output);
	}
	
	
	
	/**
	 * A method to return the list of areas available
	 * @return an arraylist of arraylist, the last one having the two latitude and longitude coordinates in them, in that order
	 */
	public ArrayList<ArrayList<Integer>> getAreaList(){
		ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
		for(int lat = -88; lat<=88 ; lat+=2) {
			for(int lon = -178 ; lon <=178 ; lon +=2) {
				ArrayList<Integer> coords = new ArrayList<Integer>();
				coords.add(lat);
				coords.add(lon);
				output.add(coords);
			}
		}
		return(output);
	}
}
