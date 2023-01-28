package com.gsq.springboot.service;

import com.gsq.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-17
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus();

    List<Menu> findMenus(List<Menu> list);
}
