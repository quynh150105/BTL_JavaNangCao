package org.acme.service;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.acme.dto.request.CreateUserRequest;
import org.acme.dto.request.LoginRequest;
import org.acme.dto.response.LoginResponse;
import org.acme.dto.response.RegisterResponse;
import org.acme.entity.User;
import org.acme.repository.UserRepository;
import org.acme.utils.JwtUtils;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public RegisterResponse register(CreateUserRequest request){

        User user = userRepository.find("username",request.getUsername()).firstResult();
        if(user != null){
            throw new RuntimeException("Username already exists");
        }

        user = User.builder()
                .username(request.getUsername())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .email(request.getEmail())
                .build();
        userRepository.persist(user);

        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public LoginResponse login(LoginRequest request){
        User user = userRepository.find("username", request.getUsername()).firstResult();
        if(user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return LoginResponse.builder()
                .token(JwtUtils.generateToken(user.getUsername()))
                .build();
    }

}
