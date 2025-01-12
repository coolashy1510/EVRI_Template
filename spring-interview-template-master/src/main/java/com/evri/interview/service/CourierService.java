package com.evri.interview.service;

import com.evri.interview.model.Courier;
import com.evri.interview.repository.CourierEntity;
import com.evri.interview.repository.CourierRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);
    private CourierTransformer courierTransformer;
    private CourierRepository repository;


    /**
     * Retrieves a list of couriers, optionally filtered by their active status.
     *
     * @param isActive an Optional<Boolean> that specifies the filter condition:
     *                 - If present and true, only active couriers will be returned.
     *                 - If present and false, only inactive couriers will be returned.
     *                 - If not present, all couriers will be returned without filtering.
     * @return a List of Courier objects, filtered based on the isActive parameter if provided.
     */
    public List<Courier> getAllCouriers(@RequestParam Optional<Boolean> isActive) {
        Stream<Courier> courierStream = repository.findAll()
                .stream()
                .map(courierTransformer::toCourier);

        if (isActive != null && isActive.isPresent()) {
            Boolean filterIsActive = isActive.orElse(false);
            logger.info("Filtering couriers by isActive = {}", filterIsActive);
            return courierStream
                    .filter(courier -> courier.isActive() == filterIsActive)
                    .collect(Collectors.toList());
        }
        logger.info("No isActive filter applied. Returning all couriers.");
        return courierStream.collect(Collectors.toList());
    }

    /**
     * Updates a courier with the provided values.
     * <p>
     * Courier ID is used to identify the courier to be updated. If the courier
     * does not exist, a RuntimeException is thrown.
     * <p>
     * The provided name is split into first name and last name, and these
     * values are updated in the courier entity.
     * <p>
     *
     * @param courierId the ID of the courier to be updated
     * @param courier   the courier object containing the updated values
     */
    public void updateCourier(long courierId, Courier courier) {
        try {
            // Check that the courier ID exists
            CourierEntity entity = repository.findById(courierId).orElseThrow(() -> new RuntimeException("Courier not found"));

            // Get the first name and last name from the name
            String[] nameParts = getNameParts(courier.getName());
            entity.setFirstName(nameParts[0]);
            entity.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            entity.setActive(courier.isActive());

            // Update the courier
            repository.save(entity);
        } catch (DataAccessException ex) {
            // Handle database-related exceptions
            throw new RuntimeException("Database error occurred while updating courier", ex);
        } catch (IllegalArgumentException ex) {
            // Handle invalid input
            throw new RuntimeException("Invalid courier data provided", ex);
        }
    }

    /**
     * Returns an array of the first name and last name split from the
     * provided name.
     * <p>
     * The name is split at the first space character. The first part of the
     * split result is the first name, and the remaining parts are the last
     * name.
     * <p>
     * If the name is null or empty, an IllegalArgumentException is thrown.
     *
     * @param name the name to be split
     * @return an array of the first name and last name
     */
    private String[] getNameParts(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.info("Name cannot be null or empty");
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        // Split the name at the first space character
        return name.trim().split(" ", 2);
    }

    /**
     * Returns true if the courier with the specified ID exists in the database, false otherwise.
     * <p>
     * This method is used by the CourierController to check that the courier ID exists
     * before performing an update operation.
     *
     * @param courierId the ID of the courier to check
     *                  * @return true if a courier with the given ID exists, false otherwise
     */
    public boolean courierExists(Long courierId) {
        return repository.existsById(courierId);
    }

    public void createCourier(Courier courier) {
        if (courier.getName() == null || courier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Courier name cannot be null or empty");
        }

        String[] nameParts = getNameParts(courier.getName());
        CourierEntity entity = CourierEntity.builder()
                .firstName(nameParts[0])
                .lastName(nameParts.length > 1 ? nameParts[1] : "")
                .active(courier.isActive())
                .build();

        repository.save(entity);
    }
}
