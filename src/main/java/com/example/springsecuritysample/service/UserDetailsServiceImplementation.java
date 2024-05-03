package com.example.springsecuritysample.service;

import com.example.springsecuritysample.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
           com.example.springsecuritysample.repository.User user = userRepository.findByUsername(username);
            return new User(username, user.getPassword(), new ArrayList<>());
        } catch (Exception exp){
            throw new UsernameNotFoundException("User Not Found : "+username);
        }
    }

}
