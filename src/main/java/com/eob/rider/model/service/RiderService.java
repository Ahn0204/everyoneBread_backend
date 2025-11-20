package com.eob.rider.model.service;

import org.springframework.stereotype.Service;

import com.eob.rider.model.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    
}
