package com.example.backend.address;

import com.example.backend.service.RequestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AddressConfig {
    private  final AddressRepository repository;



    public AddressConfig(AddressRepository repository) {
        this.repository = repository;
    }

    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            Address address1 = new Address("87.80.0.0/15", "87.80.0.0", "87.81.255.255","SKY", "AS1422", "AS_SKY",4.5);
            Address address2 = new Address("62.99.0.0/16", "62.99.0.0", "62.99.255.255","Euskaltel", "AS12569", "EUNET_AS",3.7);
            Address address3 = new Address("127.0.0.0/8", "127.0.0.0", "127.255.255.255", "LOCALHOST", 5.0);
            //save the address in the database
            repository.save(address1);
            repository.save(address2);
            repository.save(address3);

        };
    }
}
