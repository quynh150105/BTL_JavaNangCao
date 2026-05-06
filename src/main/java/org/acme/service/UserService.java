package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.acme.Exception.NotFoundException;
import org.acme.dto.request.UpdateUserRequest;
import org.acme.dto.response.UserResponse;
import org.acme.entity.User;
import org.acme.mapper.UserMapper;
import org.acme.repository.UserRepository;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserMapper userMapper;

    public List<UserResponse> getAll(){
        return userRepository.findAll()
                .project(UserResponse.class)
                .list();
    }

    public UserResponse getById(Long id){

        return userRepository.find("id", id)
                .project(UserResponse.class)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public UserResponse deleteById(Long id){
        User user = userRepository.findById(id);
        if(user == null){
            throw new NotFoundException("User not found");
        }
        userRepository.delete(user);
        return userMapper.toUseResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request){
        User user = userRepository.findById(id);
        if(user== null){
            throw new NotFoundException("user not found");
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());
        return userMapper.toUseResponse(user);
    }

}
