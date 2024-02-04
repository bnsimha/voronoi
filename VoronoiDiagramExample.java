import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class VoronoiDiagramExample extends JFrame {

    private final List<Coordinate> points;
    private final JPanel drawingPanel;
    private final JSlider pointsSlider;

    public VoronoiDiagramExample(List<Coordinate> points) {
        this.points = points;
        setTitle("Voronoi Diagram");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a drawing panel
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintVoronoiDiagram(g);
            }
        };
        add(drawingPanel, BorderLayout.CENTER);

        // Create a refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPoints();
                drawingPanel.repaint();
            }
        });

        // Create a slider for adjusting the number of points
        pointsSlider = new JSlider(JSlider.HORIZONTAL, 5, 50, points.size());
        pointsSlider.setMajorTickSpacing(5);
        pointsSlider.setMinorTickSpacing(1);
        pointsSlider.setPaintTicks(true);
        pointsSlider.setPaintLabels(true);
        pointsSlider.addChangeListener(e -> {
            pointsSlider.setValue((int) Math.round(pointsSlider.getValue()));
            refreshPoints();
            drawingPanel.repaint();
        });

        // Create a panel for buttons and sliders
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(refreshButton);
        controlPanel.add(new JLabel("Number of Points:"));
        controlPanel.add(pointsSlider);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void paintVoronoiDiagram(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Compute Voronoi diagram
        VoronoiDiagramBuilder voronoiBuilder = new VoronoiDiagramBuilder();
        voronoiBuilder.setSites(points);
        Geometry voronoiDiagram = voronoiBuilder.getDiagram(new GeometryFactory());

        // Draw Voronoi diagram
        g2d.setColor(Color.RED);
        for (int i = 0; i < voronoiDiagram.getNumGeometries(); i++) {
            Geometry cell = voronoiDiagram.getGeometryN(i);
            Coordinate[] coordinates = cell.getCoordinates();
            int[] xPoints = new int[coordinates.length];
            int[] yPoints = new int[coordinates.length];

            for (int j = 0; j < coordinates.length; j++) {
                xPoints[j] = (int) coordinates[j].x;
                yPoints[j] = (int) coordinates[j].y;
            }

            g2d.drawPolygon(xPoints, yPoints, coordinates.length);

            // Calculate the center of the polygon
            double centerX = 0;
            double centerY = 0;
            for (Coordinate coord : coordinates) {
                centerX += coord.x;
                centerY += coord.y;
            }
            centerX /= coordinates.length;
            centerY /= coordinates.length;

            // Draw a black dot at the center
            g2d.setColor(Color.BLACK);
            int dotSize = 5; // Adjust the size of the dot as needed
            g2d.fillOval((int) centerX - dotSize / 2, (int) centerY - dotSize / 2, dotSize, dotSize);
        }
    }


    private void refreshPoints() {
        int numPoints = pointsSlider.getValue();
        points.clear();
        for (int i = 0; i < numPoints; i++) {
            points.add(new Coordinate(Math.random() * drawingPanel.getWidth(), Math.random() * drawingPanel.getHeight()));
        }
    }

    public static void main(String[] args) {
        List<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            points.add(new Coordinate(Math.random() * 500, Math.random() * 500));
        }

        SwingUtilities.invokeLater(() -> new VoronoiDiagramExample(points));
    }
}
