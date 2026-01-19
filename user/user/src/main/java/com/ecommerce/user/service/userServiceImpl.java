package com.ecommerce.user.service;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
;

@Service
public class userServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
   // private final List<User> userList = new ArrayList<>();
  //  private Long nexId = 1L;
   @Override
   public List<UserResponse> fetchAllUser() {
       return userRepository.findAll().stream()
               .map(user -> modelMapper.map(user,UserResponse.class))
               .toList();
   }

    @Override
    public void addUser(@RequestBody UserRequest userRequest) {
        User user = new User();
        modelMapper.map(userRequest,user);
        if (userRequest.getAddress() != null && user.getAddress() == null){
            user.setAddress(new Address());
            modelMapper.map(userRequest.getAddress(),user.getAddress());
        }
        userRepository.save(user);

    }


    @Override
    public Optional<UserResponse> fetchUser(String id) {
      return userRepository.findById(id)
              .map(user -> modelMapper.map(user,UserResponse.class));
    }

    @Override
    public boolean updateUser(String id, UserRequest updatedUserRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    modelMapper.map(updatedUserRequest, existingUser);
                    if (updatedUserRequest.getAddress() != null) {
                        if (existingUser.getAddress() == null) {
                            existingUser.setAddress(new Address());
                        }
                        modelMapper.map(updatedUserRequest.getAddress(), existingUser.getAddress());
                    }

                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);

    }
}
