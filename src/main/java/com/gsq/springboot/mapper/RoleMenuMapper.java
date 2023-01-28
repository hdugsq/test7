package com.gsq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gsq.springboot.entity.RoleMenu;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    @Select("select menu_id from role_menu where role_id=#{roleId}")
    List<Integer> selectByRoleId(Integer roleId);
}
