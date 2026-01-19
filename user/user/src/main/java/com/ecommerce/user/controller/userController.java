package com.ecommerce.user.controller;


import com.ecommerce.user.service.UserService;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class userController {

    @Autowired
    private UserService userService;

   // private static Logger logger = LoggerFactory.getLogger(userController.class);

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return new ResponseEntity<>(userService.fetchAllUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest) {
       userService.addUser(userRequest);
        return ResponseEntity.ok("User Details Added Successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        log.info("Request received for user: {}", id);
            return userService.fetchUser(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id,
                                             @RequestBody UserRequest updatedUserRequest){
            boolean updated = userService.updateUser(id, updatedUserRequest);
            if (updated) return ResponseEntity.ok("User Updated Successfully!");
            return ResponseEntity.notFound().build();
    }


}
