package com.gsq.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsq.springboot.entity.Role;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;

import com.gsq.springboot.service.IMenuService;
import com.gsq.springboot.entity.Menu;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-17
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @PostMapping
    public Boolean save(@RequestBody Menu menu) {
            return menuService.saveOrUpdate(menu);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
            return menuService.removeById(id);
    }

    @GetMapping
    public List<Menu> findAll() {
        return menuService.findMenus();
    }


    @GetMapping("/{id}")
    public Menu findOne(@PathVariable Integer id) {
            return menuService.getById(id);
    }

    @GetMapping("/page")
    public Page<Menu> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Menu> queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);//模糊查询
        }
        return menuService.page(new Page<>(pageNum, pageSize),queryWrapper);
    }
}

