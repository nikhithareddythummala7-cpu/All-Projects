package com.example.medlypharma.service;

import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.OrderItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateOrderInvoice(Order order) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Medly-Pharma Order Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Order Details
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            document.add(new Paragraph("Order Number: " + order.getOrderNumber(), headerFont));
            document.add(new Paragraph("Order Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(new Paragraph("Customer: " + order.getUser().getFullName(), normalFont));
            document.add(new Paragraph("Email: " + order.getUser().getEmail(), normalFont));
            document.add(new Paragraph("Shipping Address: " + order.getShippingAddress(), normalFont));
            document.add(new Paragraph("Payment Method: " + order.getPaymentMethod(), normalFont));
            document.add(new Paragraph("Status: " + order.getStatus(), normalFont));
            document.add(Chunk.NEWLINE);

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 2, 2});

            // Table Header
            PdfPCell cell = new PdfPCell(new Phrase("Medicine", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Qty", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Unit Price", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Total", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            // Table Data
            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getMedicine().getName(), normalFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                table.addCell(new Phrase("$" + item.getUnitPrice(), normalFont));
                table.addCell(new Phrase("$" + item.getTotalPrice(), normalFont));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Total Amount
            Paragraph total = new Paragraph("Total Amount: $" + order.getTotalAmount(), headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out.toByteArray();
    }
}
