package com.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.server.models.StockModel;
import com.server.repos.StockRepo;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private StockRepo stockRepo;

    @Override
    public void run(String... args) throws Exception {
        // Check if stocks already exist
        if (stockRepo.count() == 0) {
            // Add sample stocks
            StockModel stock1 = new StockModel();
            stock1.setUser("sample-user-1");
            stock1.setSymbol("AAPL");
            stock1.setName("Apple Inc.");
            stock1.setPrice(175.50);
            stock1.setCount(10);
            stock1.setTotalPrice(1755.00);
            stock1.setStockExchange("NASDAQ");
            stockRepo.save(stock1);

            StockModel stock2 = new StockModel();
            stock2.setUser("sample-user-1");
            stock2.setSymbol("GOOGL");
            stock2.setName("Alphabet Inc.");
            stock2.setPrice(142.30);
            stock2.setCount(5);
            stock2.setTotalPrice(711.50);
            stock2.setStockExchange("NASDAQ");
            stockRepo.save(stock2);

            StockModel stock3 = new StockModel();
            stock3.setUser("sample-user-2");
            stock3.setSymbol("MSFT");
            stock3.setName("Microsoft Corporation");
            stock3.setPrice(378.85);
            stock3.setCount(8);
            stock3.setTotalPrice(3030.80);
            stock3.setStockExchange("NASDAQ");
            stockRepo.save(stock3);

            StockModel stock4 = new StockModel();
            stock4.setUser("sample-user-1");
            stock4.setSymbol("TSLA");
            stock4.setName("Tesla, Inc.");
            stock4.setPrice(248.42);
            stock4.setCount(3);
            stock4.setTotalPrice(745.26);
            stock4.setStockExchange("NASDAQ");
            stockRepo.save(stock4);

            StockModel stock5 = new StockModel();
            stock5.setUser("sample-user-2");
            stock5.setSymbol("AMZN");
            stock5.setName("Amazon.com, Inc.");
            stock5.setPrice(155.18);
            stock5.setCount(12);
            stock5.setTotalPrice(1862.16);
            stock5.setStockExchange("NASDAQ");
            stockRepo.save(stock5);

            System.out.println("Sample stocks data initialized successfully!");
        }
    }
}
