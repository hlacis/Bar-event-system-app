package dk.easv.bll;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import dk.easv.be.Ticket;

public class TicketPDFGenerator {

    public void generatePDF(Ticket ticket) throws Exception {

        // ensure folder exists
        java.io.File folder = new java.io.File("tickets");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String fileName = "tickets/ticket_" + ticket.getId() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("EASV EVENT TICKET")
                .setBold()
                .setFontSize(20));

        document.add(new Paragraph(" "));

        // Ticket info
        document.add(new Paragraph("Name: " + ticket.getCustomerName()));
        document.add(new Paragraph("Email: " + ticket.getCustomerEmail()));
        document.add(new Paragraph("Event ID: " + ticket.getEventId()));

        document.add(new Paragraph(" "));

        // Divider
        document.add(new Paragraph("-----------------------------"));

        document.add(new Paragraph("Ticket ID: " + ticket.getId()));

        document.add(new Paragraph(" "));

        // QR code
        String qrPath = generateQRCode(ticket.getId());
        Image qrImage = new Image(ImageDataFactory.create(qrPath));
        qrImage.scaleToFit(120, 120);

        document.add(new Paragraph("Scan for entry"));
        document.add(qrImage);

        document.close();

        java.io.File file = new java.io.File(fileName);

        try {
            Runtime.getRuntime().exec(
                    "rundll32 url.dll,FileProtocolHandler \"" + file.getAbsolutePath() + "\""
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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