package dk.easv.bll;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.io.image.ImageDataFactory;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import dk.easv.be.Ticket;

public class TicketPDFGenerator {

    public void generatePDF(List<Ticket> tickets, String eventName, String location, String time) throws Exception {

        File folder = new File("tickets");
        if (!folder.exists()) {
            folder.mkdir();
        }

        String fileName = "tickets/tickets_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);

            Table table = new Table(1)
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(2))
                    .setPadding(15);

            table.addCell(new Cell()
                    .add(new Paragraph("EASV EVENT TICKET")
                            .setBold()
                            .setFontSize(20)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell()
                    .add(new Paragraph("Event: " + eventName))
                    .add(new Paragraph("Location: " + location))
                    .add(new Paragraph("Time: " + time))
                    .add(new Paragraph("Name: " + ticket.getCustomerName()))
                    .add(new Paragraph("Email: " + ticket.getCustomerEmail()))
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell()
                    .add(new Paragraph("-----------------------------"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));

            table.addCell(new Cell()
                    .add(new Paragraph("Ticket ID: " + ticket.getId()))
                    .setBorder(Border.NO_BORDER));

            String qrPath = generateQRCode(ticket.getId());
            Image qrImage = new Image(ImageDataFactory.create(qrPath));
            qrImage.scaleToFit(120, 120);
            qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);

            table.addCell(new Cell()
                    .add(new Paragraph("Scan for entry")
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(qrImage)
                    .setBorder(Border.NO_BORDER));

            document.add(table);

            if (i < tickets.size() - 1) {
                document.add(new AreaBreak());
            }
        }

        document.close();

        File file = new File(fileName);
        openPdf(file);
    }

    private void openPdf(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                return;
            }

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("mac")) {
                new ProcessBuilder("open", file.getAbsolutePath()).start();
            } else if (os.contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()).start();
            } else if (os.contains("nix") || os.contains("nux")) {
                new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
            }
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