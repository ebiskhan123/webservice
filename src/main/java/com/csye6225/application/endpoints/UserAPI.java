package com.csye6225.application.endpoints;

import com.csye6225.application.objects.ErrorResponse;
import com.csye6225.application.objects.User;
import com.csye6225.application.objects.UserDTO;
import com.csye6225.application.repository.UserRepository;
import com.csye6225.application.security.MyUserDetailsService;
import com.csye6225.application.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@Service
@RestController
@RequestMapping("/v1")
public class UserAPI {

    @Autowired
    UserRepository userRepository;

//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    MyUserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtils tokenUtils;

//    @Value("${encryption.salt}")
//    private String salt;

    @PostMapping(value = "/user",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity createUser(@RequestBody User user){
        try {
            user.setPassword(passwordEncoder.encode( user.getPassword()));
            User temp = userRepository.findByUsername(user.getUsername());
            if (userRepository.findByUsername(user.getUsername()) == null) {
                userRepository.save(user);
                User userLatest = userRepository.findByUsername(user.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(userLatest);
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("username already exists, try a different one"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to create user, check if input data is correct"));
        }
    }

    @PutMapping(value = "/user/self",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateUser(@RequestBody User user , @RequestHeader("Authorization") String basicToken){
        try {
            if(!tokenUtils.extractUserName(basicToken).equals(user.getUsername())){
//                System.out.println(tokenUtils.extractUserName(basicToken.split(" ")[1]));
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials do not match the user being updated");
            }
            User presentUser = userRepository.findByUsername(user.getUsername());
            if (presentUser != null) {
                user.setId(presentUser.getId());
                user.setPassword(passwordEncoder.encode( user.getPassword()));
//                System.out.println(user);
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to update data, check if data entered is of the correct "));
        }
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

//    @PostMapping(value = "/authenticate",
//            consumes = {MediaType.APPLICATION_JSON_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
//        System.out.println(authenticationRequest);
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
//                            authenticationRequest.getPassword()));
//        }catch (BadCredentialsException bce){
//            ResponseEntity.status(404).body(null);
//        }
//        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//        String jwt = tokenUtils.generateToken(userDetails);
//        return ResponseEntity.status(HttpStatus.ACCEPTED).body( new AuthenticationResponse(jwt));
//    }

    @GetMapping(value = "/user/self",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String basicToken){

        System.out.println(basicToken);
        String username = tokenUtils.extractUserName(basicToken);
        User user = userRepository.findByUsername(username);
        if(user ==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ErrorResponse("User not found"));
        UserDTO userDTO = UserDTO.builder().firstName(user.getFirstName()).lastName(user.getLastName()).
                account_created(user.getAccount_created()).account_updated(user.getAccount_updated()).id(user.getId())
                .username(user.getUsername()).build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body( userDTO);
    }

}
