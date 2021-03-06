package com.csye6225.application.security;

import com.csye6225.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

//    @Value("${encryption.salt}")
//    private String salt;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        com.csye6225.application.objects.User user =  userRepository.findByUsername(s);
        return new User(s,user.getPassword(),new ArrayList<>());
    }

    public boolean validateUser(String username, String password){
        com.csye6225.application.objects.User user =  userRepository.findByUsername(username);
        if(user == null || !username.equals(user.getUsername()) || !passwordEncoder.matches( password,user.getPassword())){
            return false;
        }
        return true;
    }
}
