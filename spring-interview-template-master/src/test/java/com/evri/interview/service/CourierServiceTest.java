package com.evri.interview.service;

import com.evri.interview.model.Courier;
import com.evri.interview.repository.CourierEntity;
import com.evri.interview.repository.CourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CourierServiceTest {

    private static final Long COURIER_ID = 1L;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private CourierTransformer courierTransformer;

    @InjectMocks
    private CourierService courierService;

    private Courier courier;
    private CourierEntity courierEntity;

    @BeforeEach
    public void setup() {
        courier = Courier.builder().id(COURIER_ID).name("John Doe").active(true).build();
        courierEntity = CourierEntity.builder()
                .id(COURIER_ID)
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .build();
    }

    /**
     * Verifies that {@link CourierService#getAllCouriers(Optional)} returns all couriers when no filter is provided.
     */
    @Test
    void testGetAllCouriers_noFilter() {
        // Arrange
        List<CourierEntity> entities = Arrays.asList(
                CourierEntity.builder().id(1).firstName("First").lastName("Last").active(true).build(),
                CourierEntity.builder().id(2).firstName("Last").lastName("First").active(false).build()
        );

        List<Courier> expectedCouriers = Arrays.asList(
                Courier.builder().id(1).name("First").active(true).build(),
                Courier.builder().id(2).name("Last").active(false).build()
        );

        when(courierRepository.findAll()).thenReturn(entities);
        when(courierTransformer.toCourier(entities.get(0))).thenReturn(expectedCouriers.get(0));
        when(courierTransformer.toCourier(entities.get(1))).thenReturn(expectedCouriers.get(1));

        // Act
        List<Courier> result = courierService.getAllCouriers(Optional.empty());

        // Assert
        assertEquals(expectedCouriers, result);
        verify(courierRepository).findAll();
        verify(courierTransformer, times(2)).toCourier(any(CourierEntity.class));
    }

    /**
     * Verifies that {@link CourierService#getAllCouriers(Optional)} returns only active couriers
     * when the isActive filter is set to true.
     */
    @Test
    void testGetAllCouriers_filterActiveTrue() {
        // Arrange
        List<CourierEntity> entities = Arrays.asList(
                CourierEntity.builder().id(1).firstName("First").lastName("Last").active(true).build(),
                CourierEntity.builder().id(2).firstName("Last").lastName("First").active(false).build()
        );

        List<Courier> transformedCouriers = Arrays.asList(
                Courier.builder().id(1).name("First").active(true).build(),
                Courier.builder().id(2).name("Last").active(false).build()
        );

        List<Courier> expectedCouriers = Collections.singletonList(
                Courier.builder().id(1).name("First").active(true).build()
        );

        when(courierRepository.findAll()).thenReturn(entities);
        when(courierTransformer.toCourier(entities.get(0))).thenReturn(transformedCouriers.get(0));
        when(courierTransformer.toCourier(entities.get(1))).thenReturn(transformedCouriers.get(1));

        // Act
        List<Courier> result = courierService.getAllCouriers(Optional.of(true));

        // Assert
        assertEquals(expectedCouriers, result);
        verify(courierRepository).findAll();
        verify(courierTransformer, times(2)).toCourier(any(CourierEntity.class));
    }

    /**
     * Verifies that {@link CourierService#getAllCouriers(Optional)} returns only inactive couriers
     * when the isActive filter is set to false.
     */
    @Test
    void testGetAllCouriers_filterActiveFalse() {
        // Arrange
        List<CourierEntity> entities = Arrays.asList(
                CourierEntity.builder().id(1).firstName("First").lastName("Last").active(true).build(),
                CourierEntity.builder().id(2).firstName("Last").lastName("First").active(false).build()
        );

        List<Courier> transformedCouriers = Arrays.asList(
                Courier.builder().id(1).name("First").active(true).build(),
                Courier.builder().id(2).name("Last").active(false).build()
        );

        List<Courier> expectedCouriers = Collections.singletonList(
                Courier.builder().id(2).name("Last").active(false).build()
        );

        when(courierRepository.findAll()).thenReturn(entities);
        when(courierTransformer.toCourier(entities.get(0))).thenReturn(transformedCouriers.get(0));
        when(courierTransformer.toCourier(entities.get(1))).thenReturn(transformedCouriers.get(1));

        // Act
        List<Courier> result = courierService.getAllCouriers(Optional.of(false));

        // Assert
        assertEquals(expectedCouriers, result);
        verify(courierRepository).findAll();
        verify(courierTransformer, times(2)).toCourier(any(CourierEntity.class));
    }

    /**
     * Verifies that {@link CourierService#getAllCouriers(Optional)} returns all couriers
     * when a null value is passed as the isActive filter.
     */
    @Test
    void testGetAllCouriers_nullOptional() {
        // Arrange
        List<CourierEntity> entities = Arrays.asList(
                CourierEntity.builder().id(1).firstName("First").lastName("Last").active(true).build(),
                CourierEntity.builder().id(2).firstName("Last").lastName("First").active(false).build()
        );

        List<Courier> expectedCouriers = Arrays.asList(
                Courier.builder().id(1).name("First").active(true).build(),
                Courier.builder().id(2).name("Last").active(false).build()
        );

        when(courierRepository.findAll()).thenReturn(entities);
        when(courierTransformer.toCourier(entities.get(0))).thenReturn(expectedCouriers.get(0));
        when(courierTransformer.toCourier(entities.get(1))).thenReturn(expectedCouriers.get(1));

        // Act
        List<Courier> result = courierService.getAllCouriers(null);

        // Assert
        assertEquals(expectedCouriers, result);
        verify(courierRepository).findAll();
        verify(courierTransformer, times(2)).toCourier(any(CourierEntity.class));
    }

    /**
     * Verifies that {@link CourierService#getAllCouriers(Optional)} returns an empty list
     * when the repository is empty, and that no attempt is made to transform entities.
     */
    @Test
    void testGetAllCouriers_emptyRepository() {
        // Arrange
        when(courierRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Courier> result = courierService.getAllCouriers(Optional.empty());

        // Assert
        assertTrue(result.isEmpty());
        verify(courierRepository).findAll();
        verify(courierTransformer, never()).toCourier(any(CourierEntity.class));
    }

    /**
     * Verifies that {@link CourierService#courierExists(Long)} returns true
     * when the courier with the given ID exists in the database.
     */
    @Test
    public void testCourierExists_True() {
        Mockito.when(courierRepository.existsById(COURIER_ID)).thenReturn(true);

        boolean exists = courierService.courierExists(COURIER_ID);

        assertTrue(exists);
        Mockito.verify(courierRepository).existsById(COURIER_ID);
    }

    /**
     * Verifies that {@link CourierService#courierExists(Long)} returns false
     * when the courier with the given ID does not exist in the database.
     */
    @Test
    public void testCourierExists_False() {
        Mockito.when(courierRepository.existsById(COURIER_ID)).thenReturn(false);

        boolean exists = courierService.courierExists(COURIER_ID);

        assertFalse(exists);
        Mockito.verify(courierRepository).existsById(COURIER_ID);
    }

    /**
     * Verifies that {@link CourierService#updateCourier(long, Courier)} successfully updates
     * the courier with the given ID when the courier exists in the database.
     */
    @Test
    public void testUpdateCourier_Success() {
        Mockito.when(courierRepository.findById(COURIER_ID)).thenReturn(Optional.of(courierEntity));

        courierService.updateCourier(COURIER_ID, courier);

        // Verify repository save operation
        Mockito.verify(courierRepository).save(Mockito.argThat(entity ->
                entity.getFirstName().equals("John") &&
                        entity.getLastName().equals("Doe") &&
                        entity.isActive()));
    }

/**
 * Verifies that {@link CourierService#updateCourier(long, Courier)} throws a
 * RuntimeException with the message "Courier not found" when attempting to
 * update a courier that does not exist in the database.
 * <p>
 * The test mocks the repository to return an empty Optional for the given
 * courier ID, simulating a non-existent courier, and asserts that the
 * expected exception and message are thrown.
 */
    @Test
    public void testUpdateCourier_CourierNotFound() {
        Mockito.when(courierRepository.findById(COURIER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                courierService.updateCourier(COURIER_ID, courier));

        assertEquals("Courier not found", exception.getMessage());
    }

/**
 * Verifies that {@link CourierService#updateCourier(long, Courier)} throws a
 * RuntimeException with the message "Invalid courier data provided" when attempting
 * to update a courier with an invalid name.
 * <p>
 * The test mocks the repository to return an existing courier entity for the given
 * courier ID, and asserts that the expected exception and message are thrown
 * when the provided courier has a null name.
 */
    @Test
    public void testUpdateCourier_InvalidName() {
        Courier invalidCourier = Courier.builder().id(COURIER_ID).name(null).active(true).build();
        Mockito.when(courierRepository.findById(COURIER_ID)).thenReturn(Optional.of(courierEntity));

        Exception exception = assertThrows(RuntimeException.class, () ->
                courierService.updateCourier(COURIER_ID, invalidCourier));

        assertEquals("Invalid courier data provided", exception.getMessage());
    }

}
