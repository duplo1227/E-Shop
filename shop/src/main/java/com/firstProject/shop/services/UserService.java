package com.firstProject.shop.services;


import com.firstProject.shop.entities.Role;
import com.firstProject.shop.entities.User;
import com.firstProject.shop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(User user){
        String username = user.getUsername();
        if (userRepository.findByUsername(username) != null){
            log.info("This user already exists");
            return false;
        }
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        log.info("Saving new user " + username);
        userRepository.save(user);
        return true;
    }

    public List<User> userList(){
        return userRepository.findAll();
    }

    public void banUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user != null){
            if (user.isActive()){
                user.setActive(false);
                log.info("User {} was banned", user.getUsername());
            }else {
                user.setActive(true);
                log.info("User {} was unbanned", user.getUsername());
            }
        }
        userRepository.save(user);
    }
    public User getUserByPrincipal(Principal principal){
        if (principal == null){
            return new User();
        }
        return userRepository.findByUsername(principal.getName());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null){
            new UsernameNotFoundException(
                    String.format("Username '%s' not found", username)
            );
            return null;
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
