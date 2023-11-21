package com.xuemeng.xmdevtools.kml.creator;




import com.xuemeng.xmdevtools.kml.EkmlException;
import com.xuemeng.xmdevtools.kml.KmlShape;
import com.xuemeng.xmdevtools.kml.reader.KmlReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edvinas on 2016-02-10.
 */
public class KmlCreator {

    /**
     * Makes KML from referred shape.
     *
     * @param shape KmlShape
     * @return KML as string
     * @throws EkmlException
     */
    public static String makeKML(final KmlShape shape) throws EkmlException {
        String KML = "";
        KML += header();
        KML += shape.toKml();
        KML += footer();
        return KML;
    }

    /**
     * Makes KML from referred shapes.
     *
     * @param shapes KML shapes array
     * @return KML as string
     * @throws EkmlException
     */
    public static String makeKML(final List<KmlShape> shapes) throws EkmlException {
        String KML = "";
        KML += header();
        for (KmlShape shape : shapes) {
            KML += shape.toKml();
        }
        KML += footer();
        return KML;
    }

    /**
     * Writes KML data to referred file.
     *
     * @param kml        KML string
     * @param targetFile target file name
     * @return True  - if write was successful.
     */
    public static boolean toFile(String kml, String targetFile) {
        BufferedWriter output = null;
        try {
            File file = new File(targetFile);
            output = new BufferedWriter(new FileWriter(file));
            output.write(kml);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * Contains KML file header.
     * Header is required in all KML files
     *
     * @return Header string.
     */
    private static String header() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "<Document>\n";
    }

    /**
     * Contains KML file footer.
     * Footer is required in all KML files
     *
     * @return Footer string.
     */
    private static String footer() {
        return "</Document>\n" +
                "</kml>\n";
    }


    /**
     * Appends referred file with new shape.
     * Shape is placed to the end of all existing placemarks.
     *
     * @param kmlShape Kml Shape
     * @param filePath KML file path
     * @return True - if append was successful
     */
    public static boolean append(KmlShape kmlShape, String filePath) {
        List<KmlShape> list = new ArrayList<KmlShape>();
        list.add(kmlShape);
        return append(list, filePath);
    }

    /**
     * Appends referred file with new shapes.
     * Shapes is placed to the end of all existing placemarks.
     *
     * @param kmlShapes Kml Shapes list
     * @param filePath  KML file path
     * @return True - if append was successful
     */
    public static boolean append(List<KmlShape> kmlShapes, String filePath) {
        String kml = "";

        try {
            String fileText = KmlReader.readFileContent(filePath);
            if (fileText.trim().isEmpty()) { //if file is empty or not exists - makes new KML
                kml = makeKML(kmlShapes);
            } else {
                kml = fileText.replace(footer(), "");
                if (fileText.length() == kml.length()) {
                    return false; //means that scanned file is not kml, because it doesn't have KML footer.
                }
                for (KmlShape shape : kmlShapes) {
                    kml += shape.toKml();
                }
                kml += footer();
            }
            toFile(kml, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}