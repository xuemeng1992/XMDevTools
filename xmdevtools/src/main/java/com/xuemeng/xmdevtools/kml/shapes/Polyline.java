package com.xuemeng.xmdevtools.kml.shapes;


import com.xuemeng.xmdevtools.kml.C;
import com.xuemeng.xmdevtools.kml.EkmlException;
import com.xuemeng.xmdevtools.kml.KmlShape;
import com.xuemeng.xmdevtools.kml.Utils;
import com.xuemeng.xmdevtools.kml.models.Point;

import java.util.List;

/**
 * Created by Edvinas on 2016-02-09.
 */
public class Polyline extends Shape implements KmlShape {

    public Polyline() {
        super();
        setType(C.Type.POLYLINE);
    }

    @Override
    public String toKml() throws EkmlException {
        if (mCoordinates == null) {
            throw new EkmlException("Coordinates can't be null");
        } else if (mCoordinates.size() < 2) {
            throw new EkmlException("Coordinates must contain at least 2 points");
        }
        return "<Placemark>\n" +
                (!super.addName().isEmpty() ? "\t" + super.addName() : "") +
                (!super.addDescription().isEmpty() ? "\t" + super.addDescription() : "") +
                "\t<Style>\n" +
                "\t\t<LineStyle>\n" +
                "\t\t\t<width>" + mLineWidth + "</width>\n" +
                "\t\t\t<color>" + Utils.RGBtoBGR(mLineColor) + "</color>\n" +
                "\t\t</LineStyle>\n" +
                "\t</Style>\n" +
                "\t<LineString>\n" +
                "\t\t<coordinates>\n" +
                "\t\t\t" + Utils.createCoordinatesString(mCoordinates, C.Type.POLYLINE) + "\n" +
                "\t\t</coordinates>\n" +
                "\t</LineString>\n" +
                "</Placemark>\n";
    }

    public static final class Builder extends Shape.Builder<Polyline, Builder> {
        public Builder(List<Point> coordinates) {
            super(coordinates);
        }

        protected Polyline createObject() {
            return new Polyline();
        }

        protected Builder thisObject() {
            return this;
        }
    }


}
