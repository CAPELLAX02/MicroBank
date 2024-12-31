package com.microbank.document.utils;

import com.microbank.document.dto.response.TransactionDetailsResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class PDFGenerator {

    public static ByteArrayInputStream generateTransactionDocument(TransactionDetailsResponse request) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                InputStream fontStream = new ClassPathResource("fonts/Arial.ttf").getInputStream();
                PDType0Font font = PDType0Font.load(document, fontStream);

                contentStream.setFont(font, 14);

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);

                contentStream.showText("Transaction Receipt");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Transaction ID: " + request.transactionId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Source Account: " + request.sourceAccountIBAN());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Source Owner: " + request.sourceAccountOwnerName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Target Account: " + request.targetAccountIBAN());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Target Owner: " + request.targetAccountOwnerName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Amount: $" + request.amount());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Timestamp: " + request.timestamp());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Thank you for using MicroBank!");

                contentStream.endText();
            }

            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error while creating PDF", e);
        }
    }

}
