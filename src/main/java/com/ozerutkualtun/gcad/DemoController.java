package com.ozerutkualtun.gcad;

import com.ozerutkualtun.gcad.model.ApiResponse;
import com.ozerutkualtun.gcad.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/calendar/event")
@Slf4j
@RequiredArgsConstructor
public class DemoController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse> createEvent() {

        ApiResponse response = eventService.createEvent();

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
