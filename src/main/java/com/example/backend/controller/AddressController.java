package com.example.backend.controller;

import com.example.backend.address.Address;
import com.example.backend.address.AddressRepository;
import com.example.backend.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@Controller
public class AddressController {
    private static final Logger logger = LogManager.getLogger("trying");
    private static final Logger logg = LogManager.getLogger("second");
    private final AddressRepository addressRepository;
    private final RequestService requestService;
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.url}")
    private String recaptchaServerURL;


    private final RestTemplate restTemplate;
    @Autowired
    public AddressController(AddressRepository addressRepository, RequestService requestService, RestTemplateBuilder restTemplateBuilder) {
        this.addressRepository = addressRepository;
        this.requestService = requestService;
        this.restTemplate = restTemplateBuilder.build();
    }

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model){
//        String clientIPAddress = request.getRemoteAddr();
        String clientIPAddress = requestService.getClientIPAddress(request);
//        String clientIPAddress = "hello";
        //Server side validation. This is to validate the users ip address when accessing the website
        if (requestService.Validate_Input(clientIPAddress) == false){
            model.addAttribute("errorMessage1", "Invalid Ip address format.");
            return "searchIP";
        }

        Boolean Found = null;
        List<Address> InRange = null;
        String errorMessage = null;
        try{
            InRange = requestService.checkIPIsInGivenRange(clientIPAddress);
            if (InRange.isEmpty()){
                Found = false;
            }else{
                Found = true;
            }
        } catch (AddressStringException e){
            errorMessage = "An error occurred while checking the IP range.";
        }

        List<String> CIDRValues = addressRepository.findallCIDRValues();
        model.addAttribute("cidr", CIDRValues);

        model.addAttribute("clientIPAddress", clientIPAddress);
        model.addAttribute("InRange",InRange);
        model.addAttribute("Found", Found);

//        logger.info(InRange);
        return "index";
    }


    @PostMapping("/searchingIP")
    public @ResponseBody String searchingIP(@RequestParam("inputIP3") String inputIP3, @RequestParam("g-recaptcha-response") String gRecaptchaResponse, Model model, HttpServletRequest request) throws AddressStringException{
//        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        Boolean CapthcaValidated = verifyReCAPTCHA(gRecaptchaResponse);
        if (CapthcaValidated) {
            try {
                Boolean Valid = requestService.Validate_Input(inputIP3);
                if (Valid) {
                    List<Address> InRange3 = requestService.checkIPIsInGivenRange(inputIP3);

//                ObjectMapper objectMapper = new ObjectMapper();
//                String jsonResponse = objectMapper.writeValueAsString(InRange3);

                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("SearchedIP", inputIP3);
                    jsonResponse.put("ASNUMBER", InRange3.get(0).getASNUMBER());
                    jsonResponse.put("ASNAME", InRange3.get(0).getASNAME());
                    jsonResponse.put("NAME", InRange3.get(0).getNAME());
                    jsonResponse.put("rating", InRange3.get(0).getRating());

//                logger.info(jsonResponse.toString());
                    return jsonResponse.toString();
                }
                return "{\"error\": \"Invalid IP address format\"}";

            } catch (Exception e) {
                logger.error("An error occurred: {}", e.getMessage(), e);
                return "{\"error\": \"An error occurred while processing the request\"}";
            }
        }
        return "searchIP";

    }

    private Boolean verifyReCAPTCHA(String gRecaptchaResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", recaptchaSecret);
        map.add("response", gRecaptchaResponse);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(recaptchaServerURL, request, String.class);
        logg.info(response);

//        if (response.getStatusCode().value() != 200){
//            return false;
//        }
//        return true;
        String body = response.getBody();
        JSONObject jsonObject = new JSONObject(body);

        boolean success = jsonObject.getBoolean("success");
//        logg.info(success);
        if (success != true){
            return false;
        }
        return true;

    }
}
