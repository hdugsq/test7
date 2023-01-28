package com.gsq.springboot.service;

import com.gsq.springboot.common.Result;
import com.gsq.springboot.entity.dto.UserDTO;
import com.gsq.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-11
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO dto);

    String register(User user);
}
