package com.example.backend.service.impl;

import com.example.backend.address.Address;
import com.example.backend.address.AddressRepository;
import com.example.backend.service.RequestService;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.format.IPAddressRange;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class RequestServiceImpl implements RequestService {

    private static final Logger logger = LogManager.getLogger("try");


    private  final String LOCALHOST_IPV4 = "127.0.0.1";
    private final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private  final  String UNKNOWN = "unknown";

    private final AddressRepository addressRepository;

    @Autowired
    public RequestServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public String getClientIPAddress(HttpServletRequest request) {
        logger.info("Reading try");
        String errorMessage = null;


        String clientIPAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(clientIPAddress) || LOCALHOST_IPV6.equals(clientIPAddress)){
                clientIPAddress = "127.0.0.1";
//                logger.info(clientIPAddress);
            }

        logger.info("Finished Reading try");
        return clientIPAddress;
    }

    @Override
    public List<Address> checkIPIsInGivenRange(String inputIP) throws AddressStringException {
        //If cidr is found we want to store the rest of the data
        List<Address> matchingData = new ArrayList<>();
        List<String> CIDRValues = addressRepository.findallCIDRValues();

        //Create an object for each cidr value and store it in a hash map
        Map<String, SubnetUtils> subnetUtilsMap = new HashMap<>();
        for (String subnetObjects : CIDRValues) {
            subnetUtilsMap.put(subnetObjects, new SubnetUtils(subnetObjects));
        }

        //Iterate over all cidr values to get their network range. If clients Ip matches return all the data relating to that cidr value
        for (int i = 0; i < CIDRValues.size(); i++) {
            String currentCIDR = CIDRValues.get(i);
            SubnetUtils subnetUtils = subnetUtilsMap.get(currentCIDR);
            String networkAddress = subnetUtils.getInfo().getNetworkAddress();
            String broadcastAddress = subnetUtils.getInfo().getBroadcastAddress();
            IPAddress startIPAddress = new IPAddressString(networkAddress).getAddress();
            IPAddress endIPAddress = new IPAddressString(broadcastAddress).getAddress();
            IPAddressSeqRange ipRange = startIPAddress.toSequentialRange(endIPAddress);
            IPAddress inputIPAddress = new IPAddressString(inputIP).toAddress();
            Boolean found = ipRange.contains(inputIPAddress);
            if (found) {
                List<Address> matchingAddress = addressRepository.findByIP(currentCIDR);
                matchingData.addAll(matchingAddress);
            }
        }
        logger.info(matchingData);
        return matchingData;
    }


    @Override
    public Boolean Validate_Input(String IP){
        // Regex expression for validating IPv4
        String regex="(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";

        // Regex expression for validating IPv6
//        String regex1="((([0-9a-fA-F]){1,4})\\:){7}([0-9a-fA-F]){1,4}";

        Pattern p = Pattern.compile(regex);
//        Pattern p1 = Pattern.compile(regex1);

        if (p.matcher(IP).matches()){
            return true;
        }
//        else if (p1.matcher(IP).matches()) {
//            return true;
//        }
        return false;
    }


}
