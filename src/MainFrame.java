import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

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

        // Add buttons to DrawingSubPanel
        JButton button1 = new JButton("Rectangle");
        JButton button2 = new JButton("Ellipse");
        JButton button3 = new JButton("Line");
        JButton button4 = new JButton("Freehand");
        JButton button5 = new JButton("Pick a color");
        subPanelDrawing1.add(button1);
        subPanelDrawing1.add(button2);
        subPanelDrawing1.add(button3);
        subPanelDrawing1.add(button4);
        subPanelDrawing1.add(button5);

        JList<Shape> shapeList = new JList<>(shapes);
        shapeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add shape list to subPanel1
        subPanel1.setLayout(new BorderLayout()); // Set layout manager to BorderLayout
        subPanel1.add(new JScrollPane(shapeList), BorderLayout.NORTH);

        // Add mouse click listener to select shape
        shapeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = shapeList.getSelectedIndex();
                if (index != -1) {
                    selectedShape = shapes.getElementAt(index);

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



        subPanelDrawing1.setMaximumSize(new Dimension(Integer.MAX_VALUE, subPanelDrawing1.getPreferredSize().height));

        subPanelDrawing1.setBorder(BorderFactory.createEtchedBorder());
        subPanelDrawing2.setBorder(BorderFactory.createEtchedBorder());

        controlPanel.add(subPanel1);
        controlPanel.add(subPanel2);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, drawingPanel, controlPanel);
        splitPane.setResizeWeight(0.6);
        getContentPane().add(splitPane);

        subPanelDrawing2.addMouseMotionListener(new MyMouseMotionListener());
        subPanelDrawing2.addMouseListener(new MyMouseListener());

    }



    public void drawPoint(int x, int y) {
        Graphics2D g = (Graphics2D) subPanelDrawing2.getGraphics();
        g.setColor(color);
        g.fillOval(x, y, 10, 10);
    }

    public void drawRect(int x1, int y1, int x2, int y2) {
        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);

        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        shapes.addElement(new Shape(startX, startY, endX, endY, "Rectangle", color, false, 10));
        repaint();
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        shapes.addElement(new Shape(x1, y1, x2, y2, "Line", color, false, 10));
        repaint();
    }

    public void drawEllipse(int x1, int y1, int x2, int y2) {
        shapes.addElement(new Shape(x1, y1, x2, y2, "Ellipse", color, false, 10));
        repaint();
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) subPanelDrawing2.getGraphics();
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.elementAt(i);
            g2d.setColor(shape.color);
            switch(shape.objectType){
                case("Line"):
                    g2d.drawLine(shape.startX, shape.startY, shape.endX, shape.endY);
                    break;

                case("Ellipse"):
                    g2d.drawOval(shape.startX, shape.startY, shape.endX - shape.startX, shape.endY - shape.startY);
                    break;

                case("Rectangle"):
                    int x = shape.startX;
                    int y = shape.startY;
                    int width = shape.endX - shape.startX;
                    int height = shape.endY - shape.startY;
                    g2d.drawRect(x,y,width,height);
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
                        repaint();
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
            startX = e.getX();
            startY = e.getY();
            freeHandX = new ArrayList<Integer>();
            freeHandY = new ArrayList<Integer>();


        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!currentDrawingMode.equals("Freehand")) {
                endX = e.getX();
                endY = e.getY();

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
            else{
                Shape freehand = new Shape(freeHandX, freeHandY,"Freehand", color, 10);
                shapes.addElement(freehand);
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
