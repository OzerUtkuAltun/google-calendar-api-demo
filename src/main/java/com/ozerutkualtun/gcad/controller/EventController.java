package com.ozerutkualtun.gcad.controller;

import com.ozerutkualtun.gcad.model.ApiResponse;
import com.ozerutkualtun.gcad.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createEvent() {

        ApiResponse<Object> response = eventService.createEvent();
        return returnResponseEntityResponse(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> listEvents() {

        ApiResponse<Object> response = eventService.listEvents();
        return returnResponseEntityResponse(response);
    }

    private ResponseEntity<ApiResponse<Object>> returnResponseEntityResponse(ApiResponse<Object> apiResponse) {

        if (Boolean.TRUE.equals(apiResponse.getSuccess())) {
            return ResponseEntity.ok(apiResponse);
        } else {
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
