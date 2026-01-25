package com.roomhub.controller;

import com.roomhub.model.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @GetMapping("/health")
    public CommonResponse<String> healthCheck() {
        return new CommonResponse<>(200, "Reservation Service is up and running", "OK");
    }
}
