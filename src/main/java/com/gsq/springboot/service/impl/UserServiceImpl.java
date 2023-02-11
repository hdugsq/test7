package com.gsq.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gsq.springboot.common.Constants;
import com.gsq.springboot.entity.dto.UserDTO;
import com.gsq.springboot.entity.User;
import com.gsq.springboot.mapper.UserMapper;
import com.gsq.springboot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gsq.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public UserDTO login(UserDTO dto){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",dto.getUsername());
        queryWrapper.eq("password",dto.getPassword());
        User one=getOne(queryWrapper);
        if (one != null) {
            BeanUtil.copyProperties(one,dto,true);
            dto.setNickname(one.getNickname());
            String token=TokenUtils.getToken(one.getId().toString(),one.getPassword());
            dto.setToken(token);
            dto.setRole(one.getRole());
            dto.setConstants(Constants.CODE_200);
        } else {
            dto.setConstants(Constants.CODE_600);
        }
        return dto;
    }
    public String register(User user){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",user.getUsername());
        User one=getOne(queryWrapper);
        try {
            if (one == null) {
                save(user);
                return Constants.CODE_200;
            } else {
                return Constants.CODE_600;
            }
        } catch (Exception e) {
            return Constants.CODE_500;
        }
    }

    @Override
    public User getByPhone(String s) {
        User byPhone = userMapper.getByPhone(s);
        System.out.println(byPhone);
        return byPhone;
    }
}
