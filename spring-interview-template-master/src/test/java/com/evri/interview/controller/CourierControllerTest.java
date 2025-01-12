package com.evri.interview.controller;

import com.evri.interview.model.Courier;
import com.evri.interview.service.CourierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CourierController.class)
@ExtendWith(SpringExtension.class)
public class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierService courierService;

    /**
     * Verifies that {@link CourierController#getAllCouriers(Optional)} returns
     * a list of all couriers when no filter is provided.
     * <p>
     * The test mocks a list of two couriers, which are returned by a call to
     * {@link CourierService#getAllCouriers(Optional)} with an empty Optional.
     * The test then verifies that a GET request to the "/api/couriers" endpoint
     * returns a JSON response with the same list of couriers.
     * </p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetAllCouriers_ReturnsAllCouriers() throws Exception {
        List<Courier> couriers = Arrays.asList(
                Courier.builder().id(1).name("First").active(true).build(),
                Courier.builder().id(2).name("Last").active(false).build()
        );

        Mockito.when(courierService.getAllCouriers(Optional.empty())).thenReturn(couriers);

        mockMvc.perform(get("/api/couriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("First"))
                .andExpect(jsonPath("$[1].name").value("Last"));
    }

/**
 * Verifies that {@link CourierController#getAllCouriers(Optional)} returns
 * only active couriers when the isActive filter is set to true.
 * <p>
 * The test mocks a list containing a single active courier, which is returned
 * by a call to {@link CourierService#getAllCouriers(Optional)} with the isActive
 * parameter set to true. The test then verifies that a GET request to
 * the "/api/couriers?isActive=true" endpoint returns a JSON response containing
 * only the active courier.
 * </p>
 *
 * @throws Exception if an unexpected error occurs
 */
    @Test
    public void testGetAllCouriers_WithIsActiveTrue_ReturnsActiveCouriers() throws Exception {
        List<Courier> couriers = Arrays.asList(
                Courier.builder().id(1L).name("First").active(true).build()
        );

        Mockito.when(courierService.getAllCouriers(Optional.of(true))).thenReturn(couriers);

        mockMvc.perform(get("/api/couriers?isActive=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("First"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    /**
     * Verifies that {@link CourierController#updateCourier(long, Courier)} returns a
     * successful response when the courier update is successful.
     * <p>
     * The test mocks a courier with ID 1 and verifies that a PUT request to the
     * "/api/courier/1" endpoint with the courier details in the request body
     * returns a JSON response with a success message.
     * </p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateCourier_Success() throws Exception {
        Courier courier = Courier.builder().id(1L).name("First").active(true).build();

        Mockito.when(courierService.courierExists(1L)).thenReturn(true);

        mockMvc.perform(put("/api/courier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(courier)))
                .andExpect(status().isOk())
                .andExpect(content().string("Courier updated successfully"));

        Mockito.verify(courierService).updateCourier(1L, courier);
    }

    /**
     * Verifies that {@link CourierController#updateCourier(long, Courier)} returns a 404
     * response when the courier to be updated does not exist.
     * <p>
     * The test mocks a courier with ID 1 and verifies that a PUT request to the
     * "/api/courier/1" endpoint with the courier details in the request body returns
     * a JSON response with a 404 status code and a "Courier not found" message.
     * </p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateCourier_NotFound() throws Exception {
        Courier courier = Courier.builder().id(1L).name("First").active(true).build();

        Mockito.when(courierService.courierExists(1L)).thenReturn(false);

        mockMvc.perform(put("/api/courier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(courier)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Courier not found"));
    }

    /**
     * Verifies that {@link CourierController#updateCourier(long, Courier)} returns a 400
     * response when the courier to be updated has invalid data.
     * <p>
     * The test mocks a courier with ID 1 and verifies that a PUT request to the
     * "/api/courier/1" endpoint with invalid data (e.g., no name) returns a JSON
     * response with a 400 status code and an "Error updating courier: Invalid
     * courier data provided" message.
     * </p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateCourier_BadRequest() throws Exception {
        Courier courier = Courier.builder().id(1L).name(null).active(true).build();

        Mockito.when(courierService.courierExists(1L)).thenReturn(true);
        Mockito.doThrow(new RuntimeException("Could not prepare statement"))
                .when(courierService).updateCourier(1L, courier);

        mockMvc.perform(put("/api/courier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(courier)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error updating courier: Could not prepare statement"));
    }

    /**
     * Verifies that {@link CourierController#updateCourier(long, Courier)} returns a
     * 400 response when an unexpected error occurs while updating a courier.
     * <p>
     * The test mocks a courier with ID 1 and verifies that a PUT request to the
     * "/api/courier/1" endpoint with the courier details in the request body
     * returns a JSON response with a 400 status code and an "Error updating
     * courier: Unexpected error" message.
     * </p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdateCourier_InternalServerError() throws Exception {
        Courier courier = Courier.builder().id(1L).name("First Name").active(true).build();

        Mockito.when(courierService.courierExists(1L)).thenReturn(true);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(courierService).updateCourier(1L, courier);

        mockMvc.perform(put("/api/courier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(courier)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error updating courier: Unexpected error"));
    }
}
