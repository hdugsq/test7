package com.gsq.springboot.service.impl;

import com.gsq.springboot.entity.Menu;
import com.gsq.springboot.mapper.MenuMapper;
import com.gsq.springboot.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-17
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    private void digui(List<Menu> list,List<Menu> parentNode){
        if(parentNode==null){return;}
        else{
            for (Menu menu : parentNode) {
                List<Menu>children=list.stream().filter(m-> menu.getId().equals(m.getPid())).collect(Collectors.toList());
                menu.setChildren(children);
                digui(list,children);
            }
        }
    }
    @Override
    public List<Menu> findMenus() {
        List<Menu> list= list();
        List<Menu> parentNode=list.stream().filter(menu-> menu.getPid()==null).collect(Collectors.toList());
        digui(list,parentNode);
        return parentNode;
    }

    @Override
    public List<Menu> findMenus(List<Menu> list) {
        List<Menu> parentNode=list.stream().filter(menu-> menu.getPid()==null).collect(Collectors.toList());
        digui(list,parentNode);
        return parentNode;
    }


}
