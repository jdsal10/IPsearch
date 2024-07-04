package com.example.backend.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    @Query("SELECT a FROM Address a WHERE a.IPCIDR =?1")
    List<Address> findByIP(String ipAddress);

    @Query("SELECT a.IPCIDR FROM  Address a")
    List<String> findallCIDRValues();
}
