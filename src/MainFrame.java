import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainFrame extends JFrame {
    private DrawingPanel drawingPanel;
    private JPanel subPanelDrawing2;
    private String currentDrawingMode = "Rectangle";
    private int startX, startY, endX, endY;
    public DefaultListModel<Shape> shapes = new DefaultListModel<>();
    public Color color = Color.BLACK;
    public Shape selectedShape;
    public ArrayList<Integer> freeHandX;
    public ArrayList<Integer> freeHandY;
    private JTextField startXField, startYField, endXField, endYField, objectTypeField, objectColor, width, fill;
    private boolean isListenerActive = true;
    private int stroke_width = 5;
    private boolean inCooldown = false;
    private JFrame displayFrame;
    public long lastTimeClicked = System.currentTimeMillis();
    private void updateForm(Shape shape) {
        if(shape != null){
            isListenerActive = false;
            startXField.setText(String.valueOf(shape.startX));
            startYField.setText(String.valueOf(shape.startY));
            endXField.setText(String.valueOf(shape.endX));
            endYField.setText(String.valueOf(shape.endY));
            objectTypeField.setText(shape.objectType);
            objectColor.setText(shape.color.toString().format("#%06X", (0xFFFFFF & shape.color.getRGB())));
            fill.setText(String.valueOf(shape.fill));
            width.setText(String.valueOf(shape.stroke_width));
            isListenerActive = true;
        }

    }

    DocumentListener changeListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            textChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            textChanged();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            textChanged();
        }
    };

    private void textChanged(){
        try {
            if (selectedShape != null && isListenerActive == true) {
                int newStartX = Integer.parseInt(startXField.getText());
                int newStartY = Integer.parseInt(startYField.getText());
                int newEndX = Integer.parseInt(endXField.getText());
                int newEndY = Integer.parseInt(endYField.getText());
                String newObjectType = objectTypeField.getText();
                Color newColor = Color.decode(objectColor.getText());
                boolean newFill = Boolean.parseBoolean(fill.getText());
                int newWidth = Integer.parseInt(width.getText());

                Shape newShape = new Shape(newStartX, newStartY, newEndX, newEndY, newObjectType, newColor, newFill, newWidth);

                shapes.removeElement(selectedShape);
                shapes.addElement(newShape);
                selectedShape = newShape;
                repaint();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format");
        }
    }
    public class Shape{
        int startX;
        int startY;
        int endX;
        int endY;
        String objectType;
        Color color;
        boolean fill;
        int stroke_width;
        ArrayList<Integer> xCoordinates;
        ArrayList<Integer> yCoordinates;


        public Shape(int startX, int startY, int endX, int endY, String objectType, Color color, boolean fill, int stroke_width){
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.objectType = objectType;
            this.color = color;
            this.fill = fill;
            this.stroke_width = stroke_width;
        }
        public Shape(ArrayList<Integer> x, ArrayList<Integer> y,String objectType, Color color, int stroke_width){
            this.xCoordinates = x;
            this.yCoordinates = y;
            this.color = color;
            this.stroke_width = stroke_width;
            this.objectType = objectType;
        }

        @Override
        public String toString() {
            return String.format("%s - (%d, %d) to (%d, %d)",
                    objectType, startX, startY, endX, endY);
        }
    }
    public MainFrame() {
        setTitle("Málkování");
        setSize(1200, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        drawingPanel = new DrawingPanel();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        drawingPanel.setLayout(new BoxLayout(drawingPanel, BoxLayout.Y_AXIS));

        JPanel subPanelDrawing1 = new JPanel();
        subPanelDrawing2 = new JPanel();

        drawingPanel.add(subPanelDrawing1);
        drawingPanel.add(subPanelDrawing2);

        JPanel subPanel1 = new JPanel();
        JPanel subPanel2 = new JPanel();
        subPanel1.setBorder(BorderFactory.createEtchedBorder());
        subPanel2.setBorder(BorderFactory.createEtchedBorder());

        subPanel1.setPreferredSize(null);
        subPanel2.setPreferredSize(null);
        controlPanel.add(subPanel1, BorderLayout.WEST);
        controlPanel.add(subPanel2, BorderLayout.EAST);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, subPanel1, subPanel2);
        splitPane2.setResizeWeight(0.8);
        controlPanel.add(splitPane2);

        // Add buttons to DrawingSubPanel
        JButton button1 = new JButton("Rectangle");
        JButton button2 = new JButton("Ellipse");
        JButton button3 = new JButton("Line");
        JButton button4 = new JButton("Freehand");
        JButton button5 = new JButton("Pick a color");
        JButton button6 = new JButton("Set stroke width");
        JButton button7 = new JButton("download JSON");
        JButton button8 = new JButton("Current SVG");
        subPanelDrawing1.add(button1);
        subPanelDrawing1.add(button2);
        subPanelDrawing1.add(button3);
        subPanelDrawing1.add(button4);
        subPanelDrawing1.add(button5);
        subPanelDrawing1.add(button6);
        subPanelDrawing1.add(button7);
        subPanelDrawing1.add(button8);

        //Control panel
        // Create form components
        startXField = new JTextField(5);
        startYField = new JTextField(5);
        endXField = new JTextField(5);
        endYField = new JTextField(5);
        objectTypeField = new JTextField(10);
        objectColor = new JTextField(5);
        fill = new JTextField(5);
        width = new JTextField(5);
        JButton saveBtn = new JButton("Save");
        JButton openBtn = new JButton("Open SVG");

        // Add the document listener to all text fields
        startXField.getDocument().addDocumentListener(changeListener);
        startYField.getDocument().addDocumentListener(changeListener);
        endXField.getDocument().addDocumentListener(changeListener);
        endYField.getDocument().addDocumentListener(changeListener);
        objectTypeField.getDocument().addDocumentListener(changeListener);
        objectColor.getDocument().addDocumentListener(changeListener);
        fill.getDocument().addDocumentListener(changeListener);
        width.getDocument().addDocumentListener(changeListener);

        // Create form layout
        JPanel formPanel = new JPanel(new GridLayout(9, 2));
        formPanel.add(new JLabel("Start X:"));
        formPanel.add(startXField);
        formPanel.add(new JLabel("Start Y:"));
        formPanel.add(startYField);
        formPanel.add(new JLabel("End X:"));
        formPanel.add(endXField);
        formPanel.add(new JLabel("End Y:"));
        formPanel.add(endYField);
        formPanel.add(new JLabel("Object Type:"));
        formPanel.add(objectTypeField);
        formPanel.add(new JLabel("Color:"));
        formPanel.add(objectColor);
        formPanel.add(new JLabel("Fill: "));
        formPanel.add(fill);
        formPanel.add(new JLabel("Width"));
        formPanel.add(width);
        formPanel.add(saveBtn);
        formPanel.add(openBtn);

        subPanel2.add(formPanel);

        JList shapeList = new JList<>(shapes);
        shapeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(shapeList);
        subPanel1.setLayout(new BorderLayout());
        subPanel1.add(scrollPane, BorderLayout.CENTER);

        // Add mouse click listener to select shape
        shapeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = shapeList.getSelectedIndex();
                if (index >= 0) {
                    selectedShape = shapes.getElementAt(index);
                    updateForm(selectedShape);
                }
            }
        });

        button1.addActionListener(e -> currentDrawingMode = "Rectangle");
        button2.addActionListener(e -> currentDrawingMode = "Ellipse");
        button3.addActionListener(e -> currentDrawingMode = "Line");
        button4.addActionListener(e -> currentDrawingMode = "Freehand");

        button5.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(null, "Pick a Color", color);
            if (selectedColor != null) {
                color = selectedColor;

            }
        });
        button6.addActionListener(e -> {
            displayNumberInNewWindow(stroke_width);
        });

        button7.addActionListener(e -> saveJSON());
        button8.addActionListener(e -> {
            displaySVGInNewWindow(shapes);
        });

        saveBtn.addActionListener(e -> saveSVG());
        openBtn.addActionListener(e -> openSVG());

        subPanelDrawing1.setMaximumSize(new Dimension(Integer.MAX_VALUE, subPanelDrawing1.getPreferredSize().height));

        subPanelDrawing1.setBorder(BorderFactory.createEtchedBorder());
        subPanelDrawing2.setBorder(BorderFactory.createEtchedBorder());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, drawingPanel, controlPanel);
        splitPane.setResizeWeight(0.6);
        getContentPane().add(splitPane);

        subPanelDrawing2.addMouseMotionListener(new MyMouseMotionListener());
        subPanelDrawing2.addMouseListener(new MyMouseListener());

    }

    private void displaySVGInNewWindow(DefaultListModel<Shape> shapes) {
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = new JFrame("SVG Code");
        displayFrame.setSize(700, 600);
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        SVGCode code = new SVGCode(shapes, subPanelDrawing2.getHeight(), subPanelDrawing2.getWidth());

        JTextArea textArea = new JTextArea(code.code);
        textArea.setEditable(true);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SVGChanged(textArea.getText());
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SVGChanged(textArea.getText());
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SVGChanged(textArea.getText());
                repaint();
            }
        });
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 600));

        displayFrame.add(scrollPane);

        displayFrame.pack();
        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);
    }
    private void SVGChanged(String svgCode){
        try {
            DefaultListModel<Shape> shapesFromSVG = new DefaultListModel<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(svgCode.getBytes());
            org.w3c.dom.Document document = builder.parse(is);

            NodeList nodeList = document.getElementsByTagName("g").item(0).getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element shapeElement = (Element) nodeList.item(i);
                    String tagName = shapeElement.getTagName();

                    switch (tagName) {
                        case "rect":
                            int x = Integer.parseInt(shapeElement.getAttribute("x"));
                            int y = Integer.parseInt(shapeElement.getAttribute("y"));
                            int width = Integer.parseInt(shapeElement.getAttribute("width"));
                            int height = Integer.parseInt(shapeElement.getAttribute("height"));
                            int strokeWidthRect = Integer.parseInt(shapeElement.getAttribute("stroke-width"));
                            String strokeColorRect = shapeElement.getAttribute("stroke");
                            boolean fillRect = !shapeElement.getAttribute("fill").equals("none");
                            Color colorRect = Color.decode(strokeColorRect);
                            shapesFromSVG.addElement(new Shape(x, y, x + width, y + height, "Rectangle", colorRect, fillRect, strokeWidthRect));
                            break;

                        case "line":
                            int x1 = Integer.parseInt(shapeElement.getAttribute("x1"));
                            int y1 = Integer.parseInt(shapeElement.getAttribute("y1"));
                            int x2 = Integer.parseInt(shapeElement.getAttribute("x2"));
                            int y2 = Integer.parseInt(shapeElement.getAttribute("y2"));
                            int strokeWidthLine = Integer.parseInt(shapeElement.getAttribute("stroke-width"));
                            String strokeColorLine = shapeElement.getAttribute("stroke");
                            Color colorLine = Color.decode(strokeColorLine);
                            shapesFromSVG.addElement(new Shape(x1, y1, x2, y2, "Line", colorLine, false, strokeWidthLine));
                            break;

                        case "ellipse":
                            int cx = Integer.parseInt(shapeElement.getAttribute("cx"));
                            int cy = Integer.parseInt(shapeElement.getAttribute("cy"));
                            int rx = Integer.parseInt(shapeElement.getAttribute("rx"));
                            int ry = Integer.parseInt(shapeElement.getAttribute("ry"));
                            int strokeWidthEllipse = Integer.parseInt(shapeElement.getAttribute("stroke-width"));
                            String strokeColorEllipse = shapeElement.getAttribute("stroke");
                            boolean fillEllipse = !shapeElement.getAttribute("fill").equals("none");
                            Color colorEllipse = Color.decode(strokeColorEllipse);
                            shapesFromSVG.addElement(new Shape(cx - rx, cy - ry, cx + rx, cy + ry, "Ellipse", colorEllipse, fillEllipse, strokeWidthEllipse));
                            break;

                        default:
                            break;
                    }

                }
            }
            shapes.clear();
            for (int i = 0; i <shapesFromSVG.size() ; i++) {
                shapes.addElement(shapesFromSVG.getElementAt(i));
            }

            repaint();


        } catch (ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void displayNumberInNewWindow(int number) {
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = new JFrame("Set new stroke width");
        displayFrame.setSize(400, 200);
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a text field and set its text to the specified number
        JTextField textField = new JTextField(String.valueOf(number));
        textField.setPreferredSize(new Dimension(400, 100));
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                strokeChanged(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                strokeChanged(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                strokeChanged(textField.getText());
            }
        });
        displayFrame.add(textField);

        displayFrame.pack();
        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);
    }
    private void strokeChanged(String text){
        try{stroke_width = Integer.parseInt(text);}
        catch (NumberFormatException e){
            System.out.println(e);
        }
    }

    private void saveJSON(){
        JSONCode code = new JSONCode(shapes, subPanelDrawing2.getHeight(), subPanelDrawing2.getWidth());

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

                writer.write(code.code);

                writer.close();

                System.out.println("JSON code saved to " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSVG() {
        SVGCode code = new SVGCode(shapes, subPanelDrawing2.getHeight(), subPanelDrawing2.getWidth());
        String finalCode = code.code;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SVG Files", "svg");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

                writer.write(finalCode);

                writer.close();

                System.out.println("SVG code saved to " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openSVG() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("SVG files", "svg");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(selectedFile);

                // Extract shapes from the SVG document
                NodeList shapesRect = document.getElementsByTagName("rect");
                for (int i = 0; i < shapesRect.getLength(); i++) {
                    Element shapeElement = (Element) shapesRect.item(i);
                    int startX = Integer.parseInt(shapeElement.getAttribute("x"));
                    int startY = Integer.parseInt(shapeElement.getAttribute("y"));
                    int width = Integer.parseInt(shapeElement.getAttribute("width"));
                    int height = Integer.parseInt(shapeElement.getAttribute("height"));
                    int strokeWidth = Integer.parseInt(shapeElement.getAttribute("stroke-width"));
                    String fillValue = shapeElement.getAttribute("fill");
                    Color color = Color.decode(shapeElement.getAttribute("stroke"));

                    MainFrame.Shape shape = new MainFrame.Shape(startX, startY, startX + width, startY + height, "Rectangle", color, !fillValue.equals("none"), strokeWidth);
                    shapes.addElement(shape);
                }
                NodeList shapesEllipse = document.getElementsByTagName("ellipse");
                for (int i = 0; i < shapesEllipse.getLength(); i++) {
                    Element shapeElement = (Element) shapesEllipse.item(i);
                    int cx = Integer.parseInt(shapeElement.getAttribute("cx"));
                    int cy = Integer.parseInt(shapeElement.getAttribute("cy"));
                    int rx = Integer.parseInt(shapeElement.getAttribute("rx"));
                    int ry = Integer.parseInt(shapeElement.getAttribute("ry"));
                    int strokeWidth = Integer.parseInt(shapeElement.getAttribute("stroke-width"));
                    String fillValue = shapeElement.getAttribute("fill");
                    Color color = Color.decode(shapeElement.getAttribute("stroke"));
                    int startX = cx - rx;
                    int endX = cx + rx;
                    int startY = cy - ry;
                    int endY = cy + ry;

                    MainFrame.Shape shape = new MainFrame.Shape(startX,startY,endX, endY,"Ellipse", color, !fillValue.equals("none"), strokeWidth);
                    shapes.addElement(shape);
                }

                NodeList shapesLine = document.getElementsByTagName("line");
                for (int i = 0; i < shapesLine.getLength(); i++) {
                    Element shapeElement = (Element) shapesLine.item(i);
                    int startX = Integer.parseInt(shapeElement.getAttribute("x1"));
                    int startY = Integer.parseInt(shapeElement.getAttribute("y1"));
                    int endX = Integer.parseInt(shapeElement.getAttribute("x2"));
                    int endY = Integer.parseInt(shapeElement.getAttribute("y2"));
                    int strokeWidth = Integer.parseInt(shapeElement.getAttribute("stroke-width"));
                    Color color = Color.decode(shapeElement.getAttribute("stroke"));

                    MainFrame.Shape shape = new MainFrame.Shape(startX, startY, endX, endY, "Line", color, false, strokeWidth);
                    shapes.addElement(shape);
                }

                repaint();

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error reading SVG file", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException | org.xml.sax.SAXException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error parsing SVG file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    public void drawPoint(int x, int y) {
        Graphics2D g = (Graphics2D) subPanelDrawing2.getGraphics();
        g.setColor(color);
        g.fillOval(x, y, stroke_width, stroke_width);

    }

    public void drawRect(int x1, int y1, int x2, int y2) {
        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);

        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        shapes.addElement(new Shape(startX, startY, endX, endY, "Rectangle", color, false, stroke_width));
        repaint();
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        shapes.addElement(new Shape(x1, y1, x2, y2, "Line", color, false, stroke_width));
        repaint();
    }

    public void drawEllipse(int x1, int y1, int x2, int y2) {
        shapes.addElement(new Shape(x1, y1, x2, y2, "Ellipse", color, false, stroke_width));
        repaint();
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) subPanelDrawing2.getGraphics();
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.elementAt(i);
            g2d.setColor(shape.color);
            g2d.setStroke(new BasicStroke(shape.stroke_width));
            switch(shape.objectType){
                case("Line"):
                    g2d.drawLine(shape.startX, shape.startY, shape.endX, shape.endY);
                    break;

                case("Ellipse"):
                    g2d.drawOval(shape.startX, shape.startY, shape.endX - shape.startX, shape.endY - shape.startY);
                    if(shape.fill){
                        g2d.fillOval(shape.startX, shape.startY, shape.endX - shape.startX, shape.endY - shape.startY);
                    }

                    break;

                case("Rectangle"):
                    int x = shape.startX;
                    int y = shape.startY;
                    int width = shape.endX - shape.startX;
                    int height = shape.endY - shape.startY;
                    g2d.drawRect(x,y,width,height);
                    if(shape.fill){
                        g2d.fillRect(x,y,width,height);
                    }
                    break;
                case("Freehand"):
                    if(shape.xCoordinates != null && shape.yCoordinates != null){
                        for(int j = 0; j < shape.xCoordinates.size(); j++) {
                            int yCoordinate = shape.yCoordinates.get(j);
                            int xCoordinate = shape.xCoordinates.get(j);
                            g2d.fillOval(xCoordinate, yCoordinate, shape.stroke_width, shape.stroke_width);
                        }
                    }
                    break;

            }

        }

    }

    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

        }
    }

    class MyMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            Graphics2D g = (Graphics2D) subPanelDrawing2.getGraphics();
            g.setStroke(new BasicStroke(stroke_width));
            if (currentDrawingMode.equals("Freehand")) {
                drawPoint(e.getX(), e.getY());
                freeHandX.add(e.getX());
                freeHandY.add(e.getY());

            } else {
                repaint();
                endX = e.getX();
                endY = e.getY();

                switch (currentDrawingMode) {
                    case "Rectangle":
                        g.drawRect(startX, startY, endX - startX, endY - startY);
                        break;
                    case "Ellipse":
                        g.drawOval(startX, startY, endX - startX, endY - startY);
                        break;
                    case "Line":
                        g.drawLine(startX, startY, endX, endY);
                        break;

                }

            }

        }

    }
    class MyMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            long curTime = System.currentTimeMillis();
            if(curTime - lastTimeClicked < 200){

                inCooldown = true;
                Graphics2D g = (Graphics2D) subPanelDrawing2.getGraphics();
                g.setStroke(new BasicStroke(stroke_width));
                g.drawLine(e.getX(), 0, e.getX(), subPanelDrawing2.getHeight());
                drawLine(e.getX(), 0, e.getX(), subPanelDrawing2.getHeight());
                subPanelDrawing2.repaint();
            }
            else{
                lastTimeClicked = curTime;
                inCooldown = false;
            }
            startX = e.getX();
            startY = e.getY();
            freeHandX = new ArrayList<Integer>();
            freeHandY = new ArrayList<Integer>();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(!inCooldown){
                if (!currentDrawingMode.equals("Freehand")) {
                    endX = e.getX();
                    endY = e.getY();

                    if(startX != endX && startY != endY){
                        switch (currentDrawingMode) {
                            case "Rectangle":
                                drawRect(startX, startY, endX, endY);
                                break;
                            case "Ellipse":
                                drawEllipse(startX, startY, endX, endY);
                                break;
                            case "Line":
                                drawLine(startX, startY, endX, endY);
                                break;
                            default:
                                break;
                        }
                    }
                }
                else{
                    Shape freehand = new Shape(freeHandX, freeHandY,"Freehand", color, stroke_width);
                    shapes.addElement(freehand);
                }
            }

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
