package com.example.medlypharma.service;

import com.example.medlypharma.model.Order;

public interface PdfService {
    byte[] generateOrderInvoice(Order order);
}
