package com.ecommerce.controller;



import com.ecommerce.model.AppRoles;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.jwt.JwtUtils;
import com.ecommerce.security.request.LoginRequest;
import com.ecommerce.security.request.SignupRequest;
import com.ecommerce.security.response.MessageResponse;
import com.ecommerce.security.response.UserInfoResponse;
import com.ecommerce.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUSer(
            @RequestBody LoginRequest loginRequest
    ) {
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            Map<String , Object> map = new HashMap<>();
            map.put("message" , "bad credentials");
            map.put("status" , false);
            return new ResponseEntity<Object>(map , HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        UserInfoResponse loginResponse
                = new UserInfoResponse(userDetails.getId(),jwtCookie.toString(),userDetails.getUsername()
                ,roles);

        return ResponseEntity.ok().header(
                HttpHeaders.SET_COOKIE , jwtCookie.toString()
        ).body(loginResponse);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupRequest signupRequest
    ){
        if(userRepository.existsByUsername((signupRequest.getUsername())) ){
            return ResponseEntity.badRequest().body(
                new MessageResponse("Username is already taken!")
            );
        }

        if(userRepository.existsByEmail((signupRequest.getEmail())) ){
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Email is already registered!")
            );
        }

        User user = new User(
                signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getEmail()
        );

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException("Role not found !"));
            roles.add(userRole);
        }

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                            .orElseThrow(()-> new RuntimeException("Role not found !"));
                    roles.add(adminRole);
                    break;
                case "seller":
                    Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                            .orElseThrow(()-> new RuntimeException("Role not found !"));
                    roles.add(sellerRole);
                    break;
                default:
                    Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                            .orElseThrow(()-> new RuntimeException("Role not found !"));
                    roles.add(userRole);
                    break;
            }
        });
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {
        if(authentication != null){
            return authentication.getName();
        }
        else return "Guest";
    }

    @GetMapping("/user")
    public ResponseEntity<?> currentUser(Authentication authentication) {

        if(authentication != null){
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .toList();

            UserInfoResponse userInfoResponse
                    = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(),roles);

            return ResponseEntity.ok().body(userInfoResponse);
        }
        else return new ResponseEntity<>("Guest", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/signout")
    public ResponseEntity<?> signOut(Authentication authentication) {
        if(authentication == null) {
            return new ResponseEntity<>("Already logged out!", HttpStatus.UNAUTHORIZED);
        }
        ResponseCookie clearedCookie = jwtUtils.getClearedCookie();
        return ResponseEntity.ok().header(
                HttpHeaders.SET_COOKIE , clearedCookie.toString()
        ).body(new MessageResponse("You have been logged out successfully!"));
    }
}
