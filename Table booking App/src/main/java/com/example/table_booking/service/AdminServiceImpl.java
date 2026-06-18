package com.example.table_booking.service;

import com.example.table_booking.dto.AdminUserDTO;
import com.example.table_booking.dto.AdminBookingDTO;
import com.example.table_booking.dto.AdminRestaurantDTO;
import com.example.table_booking.model.Booking;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.repository.BookingRepository;
import com.example.table_booking.repository.RestaurantRepository;
import com.example.table_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Override
    public List<AdminUserDTO> getAllUsersAdmin() {
        return userRepository.findAll().stream()
                .map(user -> new AdminUserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole(), user.isAccountVerified()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean approveRestaurant(String id) {
        return restaurantService.verifyRestaurant(id);
    }

    @Override
    public boolean rejectRestaurant(String id) {
        try {
            restaurantService.deleteRestaurant(id);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public List<AdminRestaurantDTO> getAllRestaurantsAdmin() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return restaurants.stream()
                .map(restaurant -> new AdminRestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getAddress(), restaurant.getOwnerEmail(), restaurant.isVerified(), restaurant.isActive()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteUser(String id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<AdminBookingDTO> getAllBookingsAdmin() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> {
                    // Get user info
                    Optional<User> userOpt = userRepository.findById(booking.getUserId());
                    String userName = userOpt.map(User::getUsername).orElse("");
                    String userEmail = userOpt.map(User::getEmail).orElse("");
                    
                    return new AdminBookingDTO(
                            booking.getId(),
                            userName,
                            userEmail,
                            booking.getRestaurantName(),
                            booking.getTableNumber(),
                            booking.getNumberOfGuests(),
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(String id) {
        if(bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
        }
    }
}
