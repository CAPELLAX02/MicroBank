package com.microbank.document.utils;

import com.microbank.document.dto.event.TransactionEvent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PDFGenerator {

    public static ByteArrayInputStream generateTransactionDocument(TransactionEvent event) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                var fontStream = new ClassPathResource("fonts/Arial.ttf").getInputStream();
                var font = PDType0Font.load(document, fontStream);

                contentStream.setFont(font, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);

                contentStream.showText("Transaction Receipt");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Transaction ID: " + event.transactionId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Source Account: " + event.senderAccountIban());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Source Owner: " + event.senderAccountOwnerName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Target Account: " + event.receiverAccountIban());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Target Owner: " + event.receiverAccountOwnerName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Amount: $" + event.amount());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Timestamp: " + event.timestamp());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Thank you for using MicroBank!");

                contentStream.endText();
            }

            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error while creating PDF", e);
        }
    }

    public static ByteArrayInputStream generateTransactionThumbnail(TransactionEvent event) {
        return null;
    }
}
