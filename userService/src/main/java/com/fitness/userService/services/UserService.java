package com.fitness.userService.services;

import com.fitness.userService.dto.RegisterRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.models.User;
import com.fitness.userService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse register(RegisterRequest request){
       if(userRepository.existsByEmail(request.getEmail())){
           User existingUser = userRepository.findByEmail(request.getEmail());
           UserResponse userResponse = new UserResponse();
           userResponse.setId(existingUser.getId());
           userResponse.setKeyCloakId(existingUser.getKeyCloakId());
           userResponse.setPassword(existingUser.getPassword());
           userResponse.setEmail(existingUser.getEmail());
           userResponse.setFirstName(existingUser.getFirstName());
           userResponse.setLastName(existingUser.getLastName());
           userResponse.setCreatedAt(existingUser.getCreatedAt());
           userResponse.setUpdatedAt(existingUser.getUpdatedAt());
           return userResponse;
       }
       User user = new User();
       user.setEmail(request.getEmail());
       user.setKeyCloakId(request.getKeyCloakId());
       user.setFirstName(request.getFirstName());
       user.setLastName(request.getLastName());
       user.setPassword(request.getPassword());

       User savedUser = userRepository.save(user);
       UserResponse userResponse = new UserResponse();
       userResponse.setId(savedUser.getId());
       userResponse.setKeyCloakId(savedUser.getKeyCloakId());
       userResponse.setPassword(savedUser.getPassword());
       userResponse.setEmail(savedUser.getEmail());
       userResponse.setFirstName(savedUser.getFirstName());
       userResponse.setLastName(savedUser.getLastName());
       userResponse.setCreatedAt(savedUser.getCreatedAt());
       userResponse.setUpdatedAt(savedUser.getUpdatedAt());
       return userResponse;
    }
    public UserResponse getUser(String id){
       User user = userRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("User Not Found"));
       UserResponse userResponse = new UserResponse();
       userResponse.setId(user.getId());
       userResponse.setPassword(user.getPassword());
       userResponse.setEmail(user.getEmail());
       userResponse.setFirstName(user.getFirstName());
       userResponse.setLastName(user.getLastName());
       userResponse.setCreatedAt(user.getCreatedAt());
       userResponse.setUpdatedAt(user.getUpdatedAt());
       return userResponse;
    }

    public Boolean existByKeyCloakId(String userId) {
        return userRepository.existsByKeyCloakId(userId);
    }
}
