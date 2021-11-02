package com.demo.recpay.recpay.model;

import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private String username;
    private String password;
    private String roles="ROLE_USER";
}
