package com.lib.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.lib.model.BookTransaction;
import com.lib.util.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.lib.model.User;
import com.lib.model.Fine;
import java.time.LocalDate;


@Service
public class ReceiptService {

    public byte[] generateReceiptPdf(List<BookTransaction> transactions) throws IOException, DocumentException, WriterException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Book Borrowing Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Table setup
        PdfPTable table = new PdfPTable(3); // book name, borrow date, return date
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 3, 3});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        table.addCell(new PdfPCell(new Phrase("Book Name", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Borrow Date", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Return Date", headerFont)));

        // QR info builder
        StringBuilder qrInfoBuilder = new StringBuilder("Borrowed Books:\n");

        for (BookTransaction tx : transactions) {
            table.addCell(tx.getBook().getBookName());
            table.addCell(tx.getBorrowDate().toString());
            table.addCell(tx.getReturnDate() != null ? tx.getReturnDate().toString() : "Not Returned");

            // Add unique & scannable info to QR code content
            qrInfoBuilder.append("ID: ")
                         .append(tx.getBook().getId()) // assumes Book has getId()
                         .append(", Name: ").append(tx.getBook().getBookName())
                         .append(", Borrowed: ").append(tx.getBorrowDate())
                         .append("\n");
        }

        document.add(table);

        // Generate and insert QR Code
        byte[] qrBytes = QRCodeGenerator.generateQRCode(qrInfoBuilder.toString(), 200, 200); // bump size to 200x200
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.setAlignment(Element.ALIGN_RIGHT);
        document.add(new Paragraph("\n"));
        document.add(qrImage);

        document.close();
        writer.close();

        return out.toByteArray();
    }
    public byte[] generateFinePDF(User user, Fine fine) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
    
            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Fine Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
    
            document.add(new Paragraph("\n"));
    
            // User Info
            document.add(new Paragraph("User: " + user.getUsername()));
            document.add(new Paragraph("Email: " + user.getEmail()));
            document.add(new Paragraph("Fine ID: " + fine.getId()));
            document.add(new Paragraph("Amount: ₹" + fine.getAmount()));
            document.add(new Paragraph("Issued On: " + fine.getIssuedDate()));
            document.add(new Paragraph("Due Date: " + fine.getDueDate()));
    
            document.add(new Paragraph("\n\nThank you for your cooperation."));
    
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return baos.toByteArray();
    }

}
