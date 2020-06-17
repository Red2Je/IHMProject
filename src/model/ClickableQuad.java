package model;

import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class ClickableQuad{
	
	private int lat;
	private int lon;
	private MeshView meshView;
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
			this.lat = lat;
			this.lon = lon;
	}
	
	
	public int getLat() {
		return(this.lat);
	}
	
	public int getLon() {
		return(this.lon);
	}
	
	public MeshView getQuadMesh() {
		return(this.meshView);
	}


}
