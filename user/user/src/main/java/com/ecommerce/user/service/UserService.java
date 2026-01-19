package com.ecommerce.user.service;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserResponse> fetchAllUser();

    void addUser(@RequestBody UserRequest userRequest);

    Optional<UserResponse> fetchUser(String id);


    boolean updateUser(String id, UserRequest updatedUserRequest);
}
