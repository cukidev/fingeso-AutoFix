package com.tingeso.autoFix.controllers;

import com.tingeso.autoFix.entities.VehicleEntity;
import com.tingeso.autoFix.services.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    // Prueba para mostrar Vehículos
    @Test
    public void listVehicle_ShouldReturnVehicles() throws Exception {
        VehicleEntity vehicle1 = new VehicleEntity();
        vehicle1.setId(1L);
        vehicle1.setLicensePlate("AAA123");
        vehicle1.setBrand("Toyota");
        vehicle1.setModel("Corolla");
        vehicle1.setV_type("Car");
        vehicle1.setYear_of_manufacture(2020);
        vehicle1.setEngine_type("Gasoline");
        vehicle1.setSeats(5);
        vehicle1.setMileage(10000);

        VehicleEntity vehicle2 = new VehicleEntity();
        vehicle2.setId(2L);
        vehicle2.setLicensePlate("BBB456");
        vehicle2.setBrand("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setV_type("Car");
        vehicle2.setYear_of_manufacture(2019);
        vehicle2.setEngine_type("Diesel");
        vehicle2.setSeats(5);
        vehicle2.setMileage(15000);

        List<VehicleEntity> vehicleList = Arrays.asList(vehicle1, vehicle2);

        given(vehicleService.getVehicles()).willReturn(vehicleList);

        mockMvc.perform(get("/api/v1/vehicle/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand", is("Toyota")))
                .andExpect(jsonPath("$[1].brand", is("Honda")));
    }

    /// Prueba para crear Vehículo
    @Test
    public void createVehicle_ShouldReturnSavedVehicle() throws Exception {
        VehicleEntity newVehicle = new VehicleEntity();
        newVehicle.setLicensePlate("ZZTT99");
        newVehicle.setBrand("Nissan");
        newVehicle.setModel("Sentra");
        newVehicle.setV_type("Car");
        newVehicle.setYear_of_manufacture(2021);
        newVehicle.setEngine_type("Gasoline");
        newVehicle.setSeats(5);
        newVehicle.setMileage(5000);

        given(vehicleService.createVehicle(any(VehicleEntity.class))).willReturn(newVehicle);

        String vehicleJson = """
        {
            "licensePlate": "ZZTT99",
            "brand": "Nissan",
            "model": "Sentra",
            "v_type": "Car",
            "year_of_manufacture": 2021,
            "engine_type": "Gasoline",
            "seats": 5,
            "mileage": 5000
        }
        """;

        mockMvc.perform(post("/api/v1/vehicle/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate", is("ZZTT99")))
                .andExpect(jsonPath("$.brand", is("Nissan")));
    }

    // Prueba para actualizar Vehículo
    @Test
    public void updateVehicle_ShouldReturnUpdatedVehicle() throws Exception {
        VehicleEntity vehicleToUpdate = new VehicleEntity();
        vehicleToUpdate.setLicensePlate("ZZTT99");
        vehicleToUpdate.setBrand("Nissan");
        vehicleToUpdate.setModel("Sentra");
        vehicleToUpdate.setV_type("Car");
        vehicleToUpdate.setYear_of_manufacture(2021);
        vehicleToUpdate.setEngine_type("Gasoline");
        vehicleToUpdate.setSeats(5);
        vehicleToUpdate.setMileage(12000);

        given(vehicleService.updateVehicle(eq(1L), any(VehicleEntity.class))).willReturn(vehicleToUpdate);

        String vehicleJson = """
        {
            "licensePlate": "ZZTT99",
            "brand": "Nissan",
            "model": "Sentra",
            "v_type": "Car",
            "year_of_manufacture": 2021,
            "engine_type": "Gasoline",
            "seats": 5,
            "mileage": 12000
        }
        """;

        mockMvc.perform(put("/api/v1/vehicle/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate", is("ZZZ999")))
                .andExpect(jsonPath("$.mileage", is(12000)));
    }

    @Test
    public void deleteVehicleByID_ShouldReturn204()throws Exception{
        when(vehicleService.deleteVehicle(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/vehicle/{id}",1L))
                .andExpect(status().isNoContent());
    }

    // Prueba para obtener un vehículo por ID que existe
    @Test
    public void getVehicleById_ShouldReturnVehicle() throws Exception {
        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setId(1L);
        vehicle.setLicensePlate("CCAA77");
        vehicle.setBrand("Ford");
        vehicle.setModel("Focus");
        vehicle.setV_type("Car");
        vehicle.setYear_of_manufacture(2018);
        vehicle.setEngine_type("Hybrid");
        vehicle.setSeats(5);
        vehicle.setMileage(30000);

        // Usando doReturn en lugar de willReturn
        doReturn(Optional.of(vehicle)).when(vehicleService).getVehicleByIdVin(1L);

        mockMvc.perform(get("/api/v1/vehicle/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate", is("CCAA77")))
                .andExpect(jsonPath("$.brand", is("Ford")));
    }


    // Prueba para verificar el manejo de datos incorrectos en la creación de vehículos
    @Test
    public void createVehicle_WhenDataIsInvalid_ShouldReturnBadRequest() throws Exception {
        String vehicleJson = """
        {
            "licensePlate": "INVALID",
            "brand": "",
            "model": "Accord",
            "v_type": "Car",
            "year_of_manufacture": 2022,
            "engine_type": "Gasoline",
            "seats": 5,
            "mileage": 100
        }
        """;

        mockMvc.perform(post("/api/v1/vehicle/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isBadRequest());

        verify(vehicleService, never()).createVehicle(any(VehicleEntity.class));
    }

    // Prueba para actualizar un vehículo que no existe
    @Test
    public void updateVehicle_WhenVehicleDoesNotExist_ShouldReturnNotFound() throws Exception {
        VehicleEntity vehicleToUpdate = new VehicleEntity();
        vehicleToUpdate.setId(1L);
        vehicleToUpdate.setLicensePlate("CCGG77");
        vehicleToUpdate.setBrand("Ford");
        vehicleToUpdate.setModel("Focus");
        vehicleToUpdate.setV_type("Car");
        vehicleToUpdate.setYear_of_manufacture(2018);
        vehicleToUpdate.setEngine_type("Hybrid");
        vehicleToUpdate.setSeats(5);
        vehicleToUpdate.setMileage(32000);

        // Usando doReturn para manejar el Optional.empty
        doReturn(Optional.empty()).when(vehicleService).updateVehicle(eq(1L), any(VehicleEntity.class));

        String vehicleJson = """
    {
        "licensePlate": "CCGG77",
        "brand": "Ford",
        "model": "Focus",
        "v_type": "Car",
        "year_of_manufacture": 2018,
        "engine_type": "Hybrid",
        "seats": 5,
        "mileage": 32000
    }
    """;

        mockMvc.perform(put("/api/v1/vehicle/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isNotFound());
    }

    // Prueba para borrar un vehículo que no existe
    @Test
    public void deleteVehicle_WhenVehicleDoesNotExist_ShouldReturnNotFound() throws Exception {
        given(vehicleService.deleteVehicle(1L)).willReturn(false);

        mockMvc.perform(delete("/api/v1/vehicle/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}

