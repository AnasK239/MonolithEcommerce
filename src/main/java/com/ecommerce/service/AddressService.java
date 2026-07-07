package com.ecommerce.service;

import com.ecommerce.dto.AddressDTO;
import com.ecommerce.model.User;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
}
