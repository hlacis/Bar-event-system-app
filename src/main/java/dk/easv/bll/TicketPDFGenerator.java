package dk.easv.bll;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.io.image.ImageDataFactory;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import dk.easv.be.Ticket;

public class TicketPDFGenerator {

    public void generatePDF(List<Ticket> tickets, String eventName, String location, String time) throws Exception {

        // Ensure folder exists
        java.io.File folder = new java.io.File("tickets");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Create unique file name
        String fileName = "tickets/tickets_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Loop through tickets
        for (int i = 0; i < tickets.size(); i++) {

            Ticket ticket = tickets.get(i);

            // Create ticket container (card style)
            Table table = new Table(1)
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(2))
                    .setPadding(15);

            // Title
            table.addCell(new Cell()
                    .add(new Paragraph("EASV EVENT TICKET")
                            .setBold()
                            .setFontSize(20)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER));

            // Customer info
            table.addCell(new Cell()
                    .add(new Paragraph("Event: " + eventName))
                    .add(new Paragraph("Location: " + location))
                    .add(new Paragraph("Time: " + time))
                    .add(new Paragraph("Name: " + ticket.getCustomerName()))
                    .add(new Paragraph("Email: " + ticket.getCustomerEmail()))
                    .setBorder(Border.NO_BORDER));

            // Divider
            table.addCell(new Cell()
                    .add(new Paragraph("-----------------------------"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));

            // Ticket ID
            table.addCell(new Cell()
                    .add(new Paragraph("Ticket ID: " + ticket.getId()))
                    .setBorder(Border.NO_BORDER));

            // QR section
            String qrPath = generateQRCode(ticket.getId());
            Image qrImage = new Image(ImageDataFactory.create(qrPath));
            qrImage.scaleToFit(120, 120);
            qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);

            table.addCell(new Cell()
                    .add(new Paragraph("Scan for entry")
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(qrImage)
                    .setBorder(Border.NO_BORDER));

            // Add ticket to document
            document.add(table);

            // New page for next ticket
            if (i < tickets.size() - 1) {
                document.add(new AreaBreak());
            }
        }
        document.close();

        // Open PDF (Windows)
        java.io.File file = new java.io.File(fileName);

        try {
            Runtime.getRuntime().exec(
                    "rundll32 url.dll,FileProtocolHandler \"" + file.getAbsolutePath() + "\""
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate QR code image
    private String generateQRCode(String text) throws Exception {

        int width = 200;
        int height = 200;

        BitMatrix matrix = new MultiFormatWriter()
                .encode(text, BarcodeFormat.QR_CODE, width, height);

        String filePath = "tickets/qr_" + text + ".png";
        Path path = Paths.get(filePath);

        MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        return filePath;
    }
}