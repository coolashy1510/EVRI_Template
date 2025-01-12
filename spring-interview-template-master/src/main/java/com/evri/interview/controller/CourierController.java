package com.evri.interview.controller;

import com.evri.interview.model.Courier;
import com.evri.interview.service.CourierService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CourierController {

    private CourierService courierService;

    /**
     * Return a list of couriers.
     * <p>
     * This will return all couriers unless the isActive parameter is provided.
     * If isActive is true, only active couriers will be returned. If isActive is false,
     * only inactive couriers will be returned.
     * </p>
     * @param isActive
     * @return
     */
    @GetMapping("/couriers")
    public ResponseEntity<List<Courier>> getAllCouriers(@RequestParam Optional<Boolean> isActive) {
        List<Courier> couriers = courierService.getAllCouriers(isActive);
        return ResponseEntity.ok(couriers);
    }

    /**
     * Updates a courier with the provided values.

     * @param courierId the ID of the courier to be updated
     * @param courier the courier object containing the updated values
     * @return a ResponseEntity containing a 200 OK status code if the update was successful,
     *         or a 404 status code if the courier was not found
     */
    @PutMapping("/courier/{courierId}")
    public ResponseEntity<String> updateCourier(@PathVariable long courierId, @RequestBody Courier courier) {
        try {
            // Check that the courier ID exists
            if (!courierService.courierExists(courierId)) {
                // Return a 404 Not Found response if the courier ID does not exist
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Courier not found");
            }
            // Update the courier
            courierService.updateCourier(courierId, courier);
            // Return a 200 OK response
            return ResponseEntity.ok("Courier updated successfully");
        } catch (RuntimeException ex) {
            // Catch application-specific exceptions
            // Return a 400 Bad Request response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating courier: " + ex.getMessage());
        } catch (Exception ex) {
            // Catch unexpected exceptions
            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/courier")
    public ResponseEntity<String> createCourier(@RequestBody Courier courier) {
        try {
            // Create the courier
            courierService.createCourier(courier);
            // Return a 201 Created response
            return ResponseEntity.status(HttpStatus.CREATED).body("Courier created successfully");
        } catch (RuntimeException ex) {
            // Catch application-specific exceptions
            // Return a 400 Bad Request response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating courier: " + ex.getMessage());
        } catch (Exception ex) {
            // Catch unexpected exceptions
            // Return a 500 Internal Server Error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
