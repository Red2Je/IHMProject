/**
 * A class that holds the model of the application (the matrix of values of temperature anomalies)
 * @author Nicolas Sylvestre
 */
package model;

import java.util.ArrayList;


public class Model {

	private ArrayList<ArrayList<Double>> data;
	/**
	 * The constructor only calls the filereader to set its data
	 */
	public Model() {
		this.data = FileRead.Read("..\\ProjetIHM\\src\\model\\tempanomaly_4x4grid.csv");
	}
	
	/**
	 * A getter for the matrix of data
	 * @return the matrix of data
	 */
	public ArrayList<ArrayList<Double>> get(){
		return(this.data);
	}
	
	/**
	 * A method to get the minimum temperature difference in the data
	 * @return the minimum temperature
	 */
	public double getMin() {
		double min = Double.MAX_VALUE;
		for(ArrayList<Double> line : data) {
			for(Double values : line) {
				if(values < min) {
					min = values;
				}
			}
		}
		return(min);
	}
	
	
	/**
	 * A method to get the maximum temperature difference in the data
	 * @return the maximum temperature
	 */
	public double getMax() {
		double max = Double.MIN_VALUE;
		for(ArrayList<Double> line : data) {
			for(double values : line) {
				if(values > max) {
					max = values;
				}
			}
		}
		return(max);
	}
	
	public double getData(int year, int lat, int lon) {
		int yCoord = Model.sphereToCoord(lat, lon);
		return(this.data.get(yCoord).get(year-1880));//we add an offset to transform the year into coordinates
		
	}

	/**
	 * A method to translate the latitude and longitude in the data matrix coordinates
	 * @param lat the latitude
	 * @param lon the longitude
	 * @return the number of the line in the data matrix corresponding to this latitude and longitude.
	 */
	public static int sphereToCoord(int lat, int lon) {
		return((90*(lat+88)+lon+178)/4);
		
	}
	
	/**
	 * A method to translate the y coordinate in the data matrix  into longitude and latitude
	 * @param y the y parameter in the matrix
	 * @return an array containing first the latitude coordinate and then the longitude
	 */
	public static Integer[] coordToSphere(int y) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		out.add((int)Math.floor(y/90)*4-88);
		out.add((y%90)*4-178);
		Integer[] cast = new Integer[2];
		return(out.toArray(cast));
	}
	
	
	/**
	 * A method to return an arrayList of all the temperatures anomalies for a certain year, sorted by zone (in the order of the read files)
	 * @param year the year you ant to display the anomalies of
	 * @return an arraylist with the anomalies, sorted in the file's latitude and longitude order
	 */
	public ArrayList<Double> getYearData(int year){
		int xCoords = year-1880;//we translate to the actual coordinate of the year, in the data
		ArrayList<Double> output = new ArrayList<Double>();
		for(int temp = 0; temp<this.data.size();temp++) {//we go through each line and add the value for the year to the output
			output.add(this.data.get(temp).get(xCoords));
		}
		return(output);
	}
	
	
	/**
	 * A method to return the anomalies for an area, with the years sorted upward
	 * @param lat the latitude of the area
	 * @param lon the longitude of the area
	 * @return an arrayList of the temperature anomalies at the coordinates, between 1880 and 2020
	 */
	public ArrayList<Double> getDataArea(int lat, int lon) {
		int xCoord = Model.sphereToCoord(lat, lon);
		return(this.data.get(xCoord));
	}
}
