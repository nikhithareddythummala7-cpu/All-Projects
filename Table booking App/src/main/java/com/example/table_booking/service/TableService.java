package com.example.table_booking.service;

import com.example.table_booking.model.Table;
import com.example.table_booking.repository.TableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableService {

    private static final Logger logger = LoggerFactory.getLogger(TableService.class);

    @Autowired
    private TableRepository tableRepository;

    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    public Optional<Table> getTableById(String id) {
        return tableRepository.findById(id);
    }

    public List<Table> getTablesByRestaurantId(String restaurantId) {
        return tableRepository.findByRestaurantId(restaurantId);
    }

    public List<Table> getAvailableTablesByRestaurantId(String restaurantId, int minCapacity) {
        return tableRepository.findByRestaurantIdAndCapacityGreaterThanEqual(restaurantId, minCapacity);
    }

    public Table createTable(Table table) {
        return tableRepository.save(table);
    }

    public Table updateTable(String id, Table table) {
        return tableRepository.findById(id)
                .map(existingTable -> {
                    existingTable.setTableNumber(table.getTableNumber());
                    existingTable.setCapacity(table.getCapacity());
                    return tableRepository.save(existingTable);
                })
                .orElse(null);
    }

    public boolean deleteTable(String id) {
        if (tableRepository.existsById(id)) {
            tableRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
