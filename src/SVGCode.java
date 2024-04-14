import com.sun.tools.javac.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SVGCode {
    public String objects;
    public String header;
    public String footer;
    public String code;

    public SVGCode(DefaultListModel<MainFrame.Shape> data, int height, int width){
        code = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "\n" +
                "<svg viewBox=\"0 0 "+ height + " " + width +"\">\n" +
                "\n" +
                "<g>\n";
        String obj = "";
        for (int i = 0; i < data.size(); i++) {
            MainFrame.Shape shape = (MainFrame.Shape) data.getElementAt(i);
            int startX = shape.startX;
            int endX = shape.endX;
            int startY = shape.startY;
            int endY = shape.endY;
            int strokeWidth = shape.stroke_width;
            Color color = shape.color;
            String colorHex = String.format("#%06X", (0xFFFFFF & color.getRGB()));
            boolean fill = shape.fill;
            String fillValue;
            if(fill == true){
                fillValue = colorHex;
            }
            else{
                fillValue = "none";
            }

            if (shape.objectType.equals("Rectangle")) {
                int widthFinal = endX - startX;
                int heightFinal = endY - startY;
                obj = "<rect x=\"" + startX + "\" y=\"" + startY + "\" width=\"" + widthFinal + "\" height=\"" + heightFinal + "\" stroke-width=\"" + strokeWidth + "\" fill=\"" + fillValue + "\" stroke=\"" + colorHex + "\"/>\n";
                code = code + obj;
            } else if (shape.objectType.equals("Ellipse")) {
                int cx = (startX + endX) / 2;
                int cy = (startY + endY) / 2;
                int rx = Math.abs((endX - startX) / 2);
                int ry = Math.abs((endY - startY) / 2);
                obj = "<ellipse cx=\"" + cx + "\" cy=\"" + cy + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" stroke-width=\"" + strokeWidth + "\" fill=\"" + fillValue + "\" stroke=\"" + colorHex + "\"/>\n";
                code = code + obj;
            } else if (shape.objectType.equals("Line")) {
                obj = "<line x1=\"" + startX + "\" y1=\"" + startY + "\" x2=\"" + endX + "\" y2=\"" + endY + "\" stroke-width=\"" + strokeWidth + "\" stroke=\"" + colorHex + "\"/>\n";
                code = code + obj;
            }
            else{

            }

        }
        code = code + "  </g>\n" +
                "\n" +
                "</svg>";
    }
}
