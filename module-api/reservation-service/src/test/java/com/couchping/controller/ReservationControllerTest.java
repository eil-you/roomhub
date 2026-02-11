package com.couchping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.couchping.entity.Reservation;
import com.couchping.model.ReservationRequest;
import com.couchping.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("?덉빟 ?앹꽦 API")
    void createReservation() throws Exception {
        // given
        ReservationRequest request = new ReservationRequest(1L, 1L, LocalDate.now(), LocalDate.now().plusDays(1),
                100000);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(reservationService).createReservation(any(ReservationRequest.class));
    }

    @Test
    @DisplayName("?ъ슜?먮퀎 ?덉빟 議고쉶 API")
    void getReservationsByUserId() throws Exception {
        // given
        Long userId = 1L;
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .roomId(1L)
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .totalPrice(100000)
                .build();

        given(reservationService.getReservationsByUserId(userId)).willReturn(List.of(reservation));

        // when & then
        mockMvc.perform(get("/api/v1/reservations/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId));

        verify(reservationService).getReservationsByUserId(userId);
    }

    @Test
    @DisplayName("?덉빟 痍⑥냼 API")
    void cancelReservation() throws Exception {
        // given
        Long reservationId = 1L;

        // when & then
        mockMvc.perform(put("/api/v1/reservations/{reservationId}/cancel", reservationId))
                .andExpect(status().isOk());

        verify(reservationService).cancelReservation(reservationId);
    }
}
