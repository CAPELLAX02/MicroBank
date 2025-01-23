package com.microbank.document.utils;

import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.exception.CustomException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    public static ByteArrayInputStream generateTransactionDocument(TransactionEvent event) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                var fontStream = new ClassPathResource("fonts/Arial.ttf").getInputStream();
                var font = PDType0Font.load(document, fontStream);

                contentStream.setFont(font, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(200, 750);
                contentStream.showText("MicroBank - Transaction Receipt");
                contentStream.endText();

                contentStream.setLineWidth(1.0f);
                contentStream.moveTo(50, 740);
                contentStream.lineTo(550, 740);
                contentStream.stroke();

                contentStream.setFont(font, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Transaction Details:");
                contentStream.endText();

                float startY = 680;
                float startX = 50;
                float tableWidth = 500;
                float rowHeight = 25;

                String formattedTimestamp = event.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                String[] headers = {"Field", "Value"};
                String[][] data = {
                        {"Source Account", event.senderAccountIban()},
                        {"Source Owner", event.senderAccountOwnerName()},
                        {"Target Account", event.receiverAccountIban()},
                        {"Target Owner", event.receiverAccountOwnerName()},
                        {"Amount", "$" + event.amount()},
                        {"Description", event.description()},
                        {"Timestamp", formattedTimestamp}
                };

                contentStream.setFont(font, 12);
                drawTable(contentStream, startX, startY, tableWidth, rowHeight, headers, data);

                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 100);
                contentStream.showText("Disclaimer: This receipt is electronically generated and does not require a signature.");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("For inquiries, please contact support@microbank.com.");
                contentStream.endText();
            }

            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new CustomException("Error while creating PDF: " + e.getMessage());
        }
    }

    private static void drawTable(PDPageContentStream contentStream, float x, float y, float width, float rowHeight, String[] headers, String[][] data) throws Exception {
        float colWidth = width / headers.length;

        contentStream.setLineWidth(0.5f);
        contentStream.addRect(x, y - rowHeight, colWidth, rowHeight);
        contentStream.addRect(x + colWidth, y - rowHeight, colWidth, rowHeight);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.newLineAtOffset(x + 5, y - 18);
        contentStream.showText(headers[0]);
        contentStream.newLineAtOffset(colWidth, 0);
        contentStream.showText(headers[1]);
        contentStream.endText();

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                contentStream.addRect(x + (col * colWidth), y - (row + 1) * rowHeight, colWidth, rowHeight);
            }
            contentStream.stroke();

            contentStream.beginText();
            contentStream.newLineAtOffset(x + 5, y - (row + 1) * rowHeight - 18);
            contentStream.showText(data[row][0]);
            contentStream.newLineAtOffset(colWidth, 0);
            contentStream.showText(data[row][1]);
            contentStream.endText();
        }
    }
}
