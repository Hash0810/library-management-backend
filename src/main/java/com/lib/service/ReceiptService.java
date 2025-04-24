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
    public byte[] generateFinePDF(User user, Fine fine) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
    
        document.add(new Paragraph("Library Fine Notification")
                .setBold()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    
        document.add(new Paragraph("Dear " + user.getFullName() + ","));
        document.add(new Paragraph("You have incurred a fine for the following reason:\n"));
    
        Table table = new Table(2);
        table.addCell("Book Name");
        table.addCell(fine.getTransaction().getBook().getBookName());
    
        table.addCell("Fine Amount");
        table.addCell("$" + fine.getAmount());
    
        table.addCell("Reason");
        table.addCell(fine.getReason());
    
        table.addCell("Borrow Date");
        table.addCell(fine.getTransaction().getBorrowDate().toString());
    
        table.addCell("Due Date");
        LocalDate dueDate = fine.getTransaction().getBorrowDate().plusDays(14);
        table.addCell(dueDate.toString());
    
        table.addCell("Generated On");
        table.addCell(LocalDate.now().toString());
    
        document.add(table);
        document.add(new Paragraph("\nPlease pay this fine as soon as possible to avoid any further penalties.\nThank you."));
    
        document.close();
        return out.toByteArray();
    }

}
