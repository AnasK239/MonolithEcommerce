package com.ecommerce.controller;


import com.ecommerce.dto.AddressDTO;
import com.ecommerce.model.Address;
import com.ecommerce.service.AddressService;
import com.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private AuthUtil authUtil;

    @PutMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(
            @RequestBody AddressDTO addressDTO
    ){

        AddressDTO returnDTO = addressService.createAddress(addressDTO , authUtil.loggedInUser());

        return new ResponseEntity<>(returnDTO ,HttpStatus.CREATED);
    }
}
