/**
 * A class to represent both quadrilateral arrays and histograms arrays, with their associated methods
 * @author Nicolas Sylvestre
 */
package model;
import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;

import converter.Converter;

public class ExplicitShape {

	ArrayList<MeshView> quadArray = new ArrayList<MeshView>();
	ArrayList<Hist> histArray = new ArrayList<Hist>();
	/**
	 * The constructor setup both quadArray and histArray with all the meshView inside
	 */
	public ExplicitShape() {
		System.out.println("[Shaping] Building shapes");
		for(int lat = -88; lat <89; lat+=4) {
			for(int lon = -178; lon<179 ; lon+=4) {
				PhongMaterial material = new PhongMaterial();
        		Point3D topRight = Converter.geoCoordTo3dCoord(lat+2, lon+2,1.004f);
        		Point3D bottomRight = Converter.geoCoordTo3dCoord(lat-2, lon+2,1.004f);
        		Point3D bottomLeft = Converter.geoCoordTo3dCoord(lat-2, lon-2,1.004f);
        		Point3D topLeft = Converter.geoCoordTo3dCoord(lat+2, lon-2,1.004f);
        		MeshView Quad = ExplicitShape.addQuadrilateral(topRight, bottomRight, bottomLeft, topLeft, material);
        		this.quadArray.add(Quad);
				Point3D origin = Converter.geoCoordTo3dCoord(lat, lon, 1.004f);
				Point3D target = Converter.geoCoordTo3dCoord(lat, lon, 1.1f);
				Hist line = new Hist(origin,target);
				this.histArray.add(line);
			}
		}
		System.out.println("[Shaping] end of the shape building");
		
	}
	
	
	/**
	 * A method to add a quadrilateral thanks to its 4 points
	 * @param topRight the top right point of the quadrilateral
	 * @param bottomRight the bottom right point of the quadrilateral
	 * @param bottomLeft the bottom left point of the quadrilateral
	 * @param topLeft the top left point of the quadrilateral
	 * @param material the material of the quadrilateral
	 * @return a meshview corresponding to a quadrilateral
	 */
	public static MeshView addQuadrilateral(Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material) {
		final TriangleMesh triangleMesh = new TriangleMesh();
		final float[] points = {
				(float)topRight.getX(),(float)topRight.getY(),(float)topRight.getZ(),
				(float)topLeft.getX(),(float)topLeft.getY(),(float)topLeft.getZ(),
				(float)bottomLeft.getX(),(float)bottomLeft.getY(),(float)bottomLeft.getZ(),
				(float)bottomRight.getX(),(float)bottomRight.getY(),(float)bottomRight.getZ()
		};
		
		final float[] texCoords = {
			1,1,
			1,0,
			0,1,
			0,0
		};
		
		final int[] faces = {
			0,1,1,0,2,2,
			0,1,2,2,3,3
		};
		
		triangleMesh.getPoints().setAll(points);
		triangleMesh.getTexCoords().setAll(texCoords);
		triangleMesh.getFaces().setAll(faces);
		final MeshView meshView = new MeshView(triangleMesh);
		meshView.setMaterial(material);
		return(meshView);
	}
	
	/**
	 * A method to update the color and the height (for the histograms) of both quadArray and histArray
	 * @param yearData the year you want to visualize the data at
	 * @param max the maximum of the data
	 * @param min the minimum of the data
	 */
	public void setColorAndData(ArrayList<Double> yearData, double max, double min) {
		for(int i = 0;i<this.quadArray.size();i++) {//We set the max for the int to quadArray.size because all the array quadArray, histArray and yearData has the same length
			PhongMaterial material = new PhongMaterial();
			material.setDiffuseColor(Converter.dataToColor(yearData.get(i), max, min, 0.4));//We compute the new color with the converter method
			this.quadArray.get(i).setMaterial(material);
			Hist current = this.histArray.get(i);
			current.setOrigin(Converter.geoCoordTo3dCoord((float)current.getLat(), (float)current.getLon(), 1.05f));
			float newValue;//We make a disjunction to determine if the value is NaN or not
			if(yearData.get(i).floatValue() == Float.NaN) {
				newValue = 0.01f;
			}else {
				newValue = (float) (Math.floor(yearData.get(i).doubleValue()*10)/10);//Here we have something special: we round the data up to 0.1 because else creating a cylinder is way too laggy, for some reason
			}
			current.setTarget(Converter.geoCoordTo3dCoord((float)current.getLat(), (float)current.getLon(), newValue));
			current.setMaterial(material);
		}
	}
	
	/**
	 * A getter for the quadArray
	 * @return the quadArray
	 */
	public ArrayList<MeshView> getQuad(){
		return(this.quadArray);
	}
	/**
	 * A getter for the histArray
	 * @return the histArray
	 */
	public ArrayList<Hist> getHist(){
		return(this.histArray);
	}

}
