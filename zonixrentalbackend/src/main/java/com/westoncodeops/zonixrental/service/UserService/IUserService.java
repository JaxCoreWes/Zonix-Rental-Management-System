package com.westoncodeops.zonixrental.service.UserService;

import com.westoncodeops.zonixrental.DTOs.Requests.UserRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.UserResponse;
import com.westoncodeops.zonixrental.entities.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    User getEntityByPhone(String phonenumber);
    UserResponse getById(UUID id);
    UserResponse saveUser(UserRequest user);
    UserResponse getUserByPhone(String phonenumber);
    List<UserResponse> getCaretakers();
    List<UserResponse> getTenants();

}
