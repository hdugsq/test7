package com.gsq.springboot.mapper;

import com.gsq.springboot.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-17
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from sys_role where flag= #{flag}")
    Integer getByFlag(@Param("flag") String role);
}
