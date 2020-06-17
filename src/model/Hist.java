package model;

import javafx.geometry.Point3D;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Hist extends Cylinder {
	private Point3D origin;
	private Point3D target;
	private int lat;
	private int lon;
	public Hist(Point3D origin, Point3D target) {
		super(0.01f,0.1f);
		this.origin = origin;
		this.target = target;
	    Point3D yAxis = new Point3D(0, 1, 0);
	    Point3D diff = this.target.subtract(this.origin);
	    double height = diff.magnitude();
	    Point3D mid = this.target.midpoint(this.origin);
	    Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
	    Point3D axisOfRotation = diff.crossProduct(yAxis);
	    double angle = Math.acos(diff.normalize().dotProduct(yAxis));
	    Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

	    this.setHeight(height);
	    this.getTransforms().addAll(moveToMidpoint,rotateAroundCenter);
	    this.lat = 0;
	    this.lon = 0;
	}

	
	public void setOrigin(Point3D newVal) {
		this.origin = newVal;
	    Point3D diff = this.target.subtract(this.origin);
	    double height = diff.magnitude();
	    this.setHeight(height);
	}
	public void setTarget(Point3D newVal) {
		this.target = newVal;
	    Point3D diff = this.target.subtract(this.origin);
	    double height = diff.magnitude();
	    this.setHeight(height);
	}
	
	public void setLon(int lon) {
		this.lon = lon;
	}
	
	public void setLat(int lat) {
		this.lat = lat;
	}
	
	public int getLat() {
		return(this.lat);
	}
	
	public int getLon() {
		return(this.lon);
	}
}
