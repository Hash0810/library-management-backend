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

        // Table
        PdfPTable table = new PdfPTable(3); // book name, borrow date, return date
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 3, 3});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        table.addCell(new PdfPCell(new Phrase("Book Name", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Borrow Date", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Return Date", headerFont)));

        StringBuilder qrInfoBuilder = new StringBuilder();

        for (BookTransaction tx : transactions) {
            table.addCell(tx.getBook().getBookName());
            table.addCell(tx.getBorrowDate().toString());
            table.addCell(tx.getReturnDate() != null ? tx.getReturnDate().toString() : "Not Returned");

            // Append to QR info string
            qrInfoBuilder.append(tx.getBorrowDate()).append(" - ").append(tx.getBook().getBookName()).append("\n");
        }

        document.add(table);

        // QR Code
        byte[] qrBytes = QRCodeGenerator.generateQRCode(qrInfoBuilder.toString(), 150, 150);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.setAlignment(Element.ALIGN_RIGHT);
        document.add(new Paragraph("\n"));
        document.add(qrImage);

        document.close();
        writer.close();

        return out.toByteArray();
    }
}
