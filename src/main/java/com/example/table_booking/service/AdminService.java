package com.example.table_booking.service;

import com.example.table_booking.dto.AdminBookingDTO;
import com.example.table_booking.dto.AdminRestaurantDTO;
import com.example.table_booking.dto.AdminUserDTO;

import java.util.List;

public interface AdminService {

    List<AdminRestaurantDTO> getAllRestaurantsAdmin();

    List<AdminUserDTO> getAllUsersAdmin();

    List<AdminBookingDTO> getAllBookingsAdmin();

    boolean approveRestaurant(String id);

    boolean rejectRestaurant(String id);

    boolean deleteUser(String id);

    void deleteBooking(String id);
}
