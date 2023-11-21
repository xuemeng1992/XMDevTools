package com.xuemeng.xmdevtools.kml.shapes;


import com.xuemeng.xmdevtools.kml.C;
import com.xuemeng.xmdevtools.kml.models.Point;

import java.util.List;

/**
 * Created by Edvinas on 2016-02-10.
 */
public abstract class Shape extends BasicShape {

    protected String mLineColor = C.LINE_COLOR;
    protected double mLineWidth = C.LINE_WIDTH;
    public List<Point> mCoordinates;

    @Override
    public String toString() {
        return "Name: " + mName + "\n"
                + "Description: " + mDescription + "\n"
                + "Coordinates: " + mCoordinates.toString() + "\n";
    }

    protected static abstract class Builder<T extends Shape, B extends Builder<T, B>> extends BasicShape.Builder<T, B> {

        public Builder(List<Point> coordinates) {
            object.mCoordinates = coordinates;
        }

        public B lineColor(String color) {
            object.mLineColor = color;
            return thisObject;
        }

        public B lineWidth(double width) {
            object.mLineWidth = width;
            return thisObject;
        }

    }
}
