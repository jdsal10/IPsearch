package com.example.backend.service;

import com.example.backend.address.Address;
import inet.ipaddr.AddressStringException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RequestService {
    String getClientIPAddress(HttpServletRequest request);
    List<Address> checkIPIsInGivenRange(String inputIP) throws AddressStringException;

    Boolean Validate_Input(String IP);
}
