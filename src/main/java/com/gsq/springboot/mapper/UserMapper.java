package com.gsq.springboot.mapper;

import com.gsq.springboot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-11
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from sys_user where phone=#{s}")
    User getByPhone(String s);
}
