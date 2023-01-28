package com.gsq.springboot.entity.dto;

import com.gsq.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

//登录请求的类
@Data
public class UserDTO {
    private String username;
    private String password;
    private String nickname;
    private String token;
    private String constants;
    private String role;
    private List<Menu> menus;
}
