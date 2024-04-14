import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class JSONCode {
    public String code;

    public JSONCode(DefaultListModel<MainFrame.Shape> data, int height, int width) {
        StringBuilder jsonBuilder = new StringBuilder("[");

        // Adding height and width as a separate JSON object
        String mainWindowSize = "{\"type\":\"MainWindow\",\"height\":" + height + ",\"width\":" + width + "}";
        jsonBuilder.append(mainWindowSize);

        for (int i = 0; i < data.size(); i++) {
            MainFrame.Shape shape = data.getElementAt(i);
            int startX = shape.startX;
            int endX = shape.endX;
            int startY = shape.startY;
            int endY = shape.endY;
            int strokeWidth = shape.stroke_width;
            Color color = shape.color;
            String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            boolean fill = shape.fill;
            String fillValue = fill ? colorHex : "none";

            if (shape.objectType.equals("Rectangle")) {
                int widthFinal = endX - startX;
                int heightFinal = endY - startY;
                String rectangleJson = "{\"type\":\"Rectangle\",\"x\":" + startX + ",\"y\":" + startY + ",\"width\":" + widthFinal + ",\"height\":" + heightFinal + ",\"strokeWidth\":" + strokeWidth + ",\"fill\":\"" + fillValue + "\",\"stroke\":\"" + colorHex + "\"}";
                jsonBuilder.append(",");
                jsonBuilder.append(rectangleJson);
            } else if (shape.objectType.equals("Ellipse")) {
                int cx = (startX + endX) / 2;
                int cy = (startY + endY) / 2;
                int rx = Math.abs((endX - startX) / 2);
                int ry = Math.abs((endY - startY) / 2);
                String ellipseJson = "{\"type\":\"Ellipse\",\"cx\":" + cx + ",\"cy\":" + cy + ",\"rx\":" + rx + ",\"ry\":" + ry + ",\"strokeWidth\":" + strokeWidth + ",\"fill\":\"" + fillValue + "\",\"stroke\":\"" + colorHex + "\"}";
                jsonBuilder.append(",");
                jsonBuilder.append(ellipseJson);
            } else if (shape.objectType.equals("Line")) {
                String lineJson = "{\"type\":\"Line\",\"x1\":" + startX + ",\"y1\":" + startY + ",\"x2\":" + endX + ",\"y2\":" + endY + ",\"strokeWidth\":" + strokeWidth + ",\"stroke\":\"" + colorHex + "\"}";
                jsonBuilder.append(",");
                jsonBuilder.append(lineJson);
            }
        }
        jsonBuilder.append("]");
        code = jsonBuilder.toString();
    }
}
