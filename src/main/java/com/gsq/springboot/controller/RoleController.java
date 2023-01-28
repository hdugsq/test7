package com.gsq.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsq.springboot.entity.User;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Controller;

import com.gsq.springboot.service.IRoleService;
import com.gsq.springboot.entity.Role;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-17
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;

    @PostMapping
    public Boolean save(@RequestBody Role role) {
            return roleService.saveOrUpdate(role);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
            return roleService.removeById(id);
    }

    @GetMapping
    public List<Role> findAll() {
            return roleService.list();
            }

    @GetMapping("/{id}")
    public Role findOne(@PathVariable Integer id) {
            return roleService.getById(id);
    }

    @GetMapping("/page")
    public Page<Role> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);//模糊查询
        }
            return roleService.page(new Page<>(pageNum, pageSize),queryWrapper);
    }

    /**
     * 绑定角色菜单关系
     */

    @PostMapping("/roleMenu/{roleId}")
    public Boolean roleMenu(@PathVariable Integer roleId,@RequestBody List<Integer> menuIds) {
        return roleService.setRoleMenu(roleId,menuIds);
    }

    @GetMapping("/roleMenu/{roleId}")
    public List<Integer> getMenuIds(@PathVariable Integer roleId) {
        return roleService.getRoleMenu(roleId);
    }
}

