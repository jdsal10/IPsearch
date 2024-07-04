package com.example.backend.address;

import jakarta.persistence.*;

//This basically is what the table looks like
@Entity   //This is for hibernate
@Table    //This is for the table in the database
public class Address {
    @Id
    @SequenceGenerator(
            name = "address_sequence",
            sequenceName = "address_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "address_sequence"
    )
    private  Long id;
    private String IPCIDR;
    private String IPSTART;
    private String IPEND;
    private String NAME;
    private String ASNUMBER;
    private String ASNAME;
    private Double Rating;


    public Address(String IPCIDR, String IPSTART, String IPEND, String NAME, String ASNUMBER, String ASNAME, Double rating) {
        this.IPCIDR = IPCIDR;
        this.IPSTART = IPSTART;
        this.IPEND = IPEND;
        this.NAME = NAME;
        this.ASNUMBER = ASNUMBER;
        this.ASNAME = ASNAME;
        Rating = rating;
    }

    public Address(String IPCIDR, String IPSTART, String IPEND, String NAME, Double rating) {
        this.IPCIDR = IPCIDR;
        this.IPSTART = IPSTART;
        this.IPEND = IPEND;
        this.NAME = NAME;
        Rating = rating;
    }

    public Address(Long id, String IPCIDR, Double rating) {
        this.id = id;
        this.IPCIDR = IPCIDR;
        Rating = rating;
    }
    public Address(String IPCIDR, Double rating) {
        this.IPCIDR = IPCIDR;
        Rating = rating;
    }

    public Address() {

    }

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIPCIDR() {
        return IPCIDR;
    }

    public void setIPCIDR(String IPCIDR) {
        this.IPCIDR = IPCIDR;
    }

    public String getNAME() {
        return NAME;
    }
    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getASNUMBER() {
        return ASNUMBER;
    }

    public void setASNUMBER(String ASNUMBER) {
        this.ASNUMBER = ASNUMBER;
    }

    public String getASNAME() {
        return ASNAME;
    }

    public void setASNAME(String ASNAME) {
        this.ASNAME = ASNAME;
    }
    public Double getRating() {
        return Rating;
    }

    public void setRating(Double rating) {
        Rating = rating;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", IPCIDR='" + IPCIDR + '\'' +
                ", Name='" + NAME + '\'' +
                ", ASNUMBER='" + ASNUMBER + '\'' +
                ", ASNAME='" + ASNAME + '\'' +
                ", Rating=" + Rating +
                '}';
    }
}
