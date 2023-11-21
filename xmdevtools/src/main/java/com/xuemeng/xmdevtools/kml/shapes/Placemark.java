package com.xuemeng.xmdevtools.kml.shapes;


import com.xuemeng.xmdevtools.kml.C;
import com.xuemeng.xmdevtools.kml.EkmlException;
import com.xuemeng.xmdevtools.kml.KmlShape;
import com.xuemeng.xmdevtools.kml.models.Point;

/**
 * Created by Edvinas on 2016-02-09.
 */
public class Placemark extends BasicShape implements KmlShape {

    public Point mCoordinate;

    public Placemark() {
        super();
        setType(C.Type.PLACEMARK);
    }

    @Override
    public String toString() {
        return "Name: "+super.mName + "\n"
                + "Description: "+super.mDescription + " \n"
                + "Coordinate: "+ mCoordinate.toString() + " \n";
    }


    @Override
    public String toKml() throws EkmlException {
        if(mCoordinate == null) {
            throw new EkmlException("Coordinate can't be null");
        }
        return "<Placemark> \n" +
                (!super.addName().isEmpty() ? "\t"+super.addName() : "")  +
                (!super.addDescription().isEmpty() ? "\t"+super.addDescription() : "")  +
                "\t<Point>\n" +
                "\t\t<coordinates>\n" +
                "\t\t\t"+mCoordinate.toString()+"\n" +
                "\t\t</coordinates>\n" +
                "\t</Point> \n" +
                "</Placemark>\n";
    }


    public static final class Builder extends BasicShape.Builder<Placemark, Builder> {

        public Builder(Point coordinate) {
            object.mCoordinate = coordinate;
        }

        public Builder(long longitude, long latitude) {
            object.mCoordinate = new Point(longitude, latitude);
        }

        public Builder(long longitude, long latitude, long altitude) {
            object.mCoordinate = new Point(longitude, latitude, altitude);
        }

        protected Placemark createObject() {
            return new Placemark();
        }

        protected Builder thisObject() {
            return this;
        }
    }
}
