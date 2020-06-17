package model;

import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class ClickableQuad{
	
	private int lat;
	private int lon;
	private MeshView meshView;//The meshView for the quadrilateral
	/**
	 * The constructor for the clickableQuad object, constructing a quadrilateral
	 * @param topRight the top right corner
	 * @param bottomRight the bottom right corner
	 * @param bottomLeft the bottom left corner 
	 * @param topLeft the top left corner
	 * @param material the material to set the quadrilateral with
	 * @param lat the latitude of the quadrilateral
	 * @param lon the longitude of the quadrilateral
	 */
	public ClickableQuad(Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material ,int lat, int lon) {
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
			this.meshView = meshView;
			//The method is the same as in ExplicitShape, the only differences are the two next lines :
			this.lat = lat;
			this.lon = lon;
	}
	
	/**
	 * A method to get the latitude of the clickable quadrilateral
	 * @return the latitude
	 */
	public int getLat() {
		return(this.lat);
	}
	
	/**
	 * A method to get the longitude of the quadrilateral
	 * @return the longitude
	 */
	public int getLon() {
		return(this.lon);
	}
	/**
	 * A method to get the meshView of the quadrilateral
	 * @return the meshView
	 */
	public MeshView getQuadMesh() {
		return(this.meshView);
	}


}
