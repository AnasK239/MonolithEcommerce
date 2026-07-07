package com.ecommerce.service.impl;

import com.ecommerce.dto.AddressDTO;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {

        var Address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();

        return null;
    }
}
