import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static studentDocs.testing.fillGradesToPdf;

public class PDFDisplay extends JFrame {

    public PDFDisplay(PDDocument document) {
        setTitle("PDF Viewer");
        setSize(800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PDFPanel panel = new PDFPanel(document);
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);
    }

    public static void displayPDF(PDDocument document) {
        SwingUtilities.invokeLater(() -> {
            PDFDisplay viewer = new PDFDisplay(document);
            viewer.setVisible(true);
        });
    }

    public static void main(String[] args) {
        String studentId = "25-60008";
        PDDocument filledPdf = fillGradesToPdf(studentId);
        if (filledPdf != null) {
            displayPDF(filledPdf);
        } else {
            System.out.println("Error generating the PDF.");
        }
    }
}

class PDFPanel extends JPanel {
    private final PDDocument document;
    private final PDFRenderer renderer;
    private final double scale = 1.5;

    public PDFPanel(PDDocument document) {
        this.document = document;
        this.renderer = new PDFRenderer(document);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int yOffset = 0;

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            try {
                Graphics2D pageGraphics = (Graphics2D) g2d.create();
                pageGraphics.translate(0, yOffset);
                pageGraphics.scale(scale, scale);
                renderer.renderPageToGraphics(i, pageGraphics);
                pageGraphics.dispose();

                PDRectangle pageSize = document.getPage(i).getMediaBox();
                yOffset += (int) (pageSize.getHeight() * scale) + 20;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int totalHeight = 0;
        int maxWidth = 0;

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDRectangle page = document.getPage(i).getMediaBox();
            totalHeight += (int) (page.getHeight() * scale) + 20;
            maxWidth = Math.max(maxWidth, (int) (page.getWidth() * scale));
        }

        return new Dimension(maxWidth, totalHeight);
    }
}
