package com.ozerutkualtun.gcad.service;


import com.ozerutkualtun.gcad.model.ApiResponse;

public interface EventService {

    ApiResponse<Object> createEvent();

    ApiResponse<Object> listEvents();
}
