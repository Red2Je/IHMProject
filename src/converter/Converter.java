package converter;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class Converter {
	
	
	private static final float TEXTURE_LAT_OFFSET = -0.2f;
	private static final float TEXTURE_LON_OFFSET = 2.8f;
	
	/**
	 * A method to convert a latitude, a longitude and a radius into a 3 dimensional point
	 * @param lat the latitude to compute
	 * @param lon the longitude to compute
	 * @param radius the radius to compute
	 * @return a point3D corresponding to the translation of spherical coordinates into Cartesian coordinates
	 */
    public static Point3D geoCoordTo3dCoord(float lat, float lon,float radius) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor))*radius,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor))*radius,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor))*radius);
    }
    
    
    /**
     * A method to deduce a gradient of color from the max in the data, and whether the data is positive or not
     * @param data the data you want a color of
     * @param max the maximum data in your set
     * @return a color corresponding to a gradient computed with the max parameter
     */
    public static Color dataToColor(double data,double max,double opacity) {
    	if(data >= 0) {
    		return(new Color(data/max,0,0,opacity));
    	}else if(data<0) {
    		return(new Color(0,0,Math.abs(data/max),opacity));
    	}else {//this case represent Double.nan
    		return(null);//we divide the opacity by two because the yellow is really bright compared to the black and white model
    	}
    }

}
