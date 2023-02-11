package com.gsq.springboot.controller;

//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsq.springboot.common.Constants;
import com.gsq.springboot.common.Result;
import com.gsq.springboot.entity.Menu;
import com.gsq.springboot.entity.dto.UserDTO;
import com.gsq.springboot.mapper.RoleMapper;
import com.gsq.springboot.mapper.RoleMenuMapper;
import com.gsq.springboot.service.IMenuService;
//import com.gsq.springboot.service.IRoleService;
//import com.gsq.springboot.utils.TokenUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
//import java.util.Random;
import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;

import com.gsq.springboot.service.IUserService;
import com.gsq.springboot.entity.User;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gaoshiqi
 * @since 2023-01-11
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/login/phone/{number}")
    public Result Code(@PathVariable String number){
        User user=userService.getByPhone(number);
        String s;
        if(user!=null) {
            s = RandomUtil.randomNumbers(6);
            stringRedisTemplate.opsForValue().set("phonenumber", s, 2L, TimeUnit.MINUTES);

            stringRedisTemplate.opsForValue().set("user",JSONUtil.toJsonStr(user),2L,TimeUnit.MINUTES);
            //短信接口；
            return Result.success(user);
        }else{
            return Result.error(Constants.CODE_400,"手机号未注册");
        }
    }
    @GetMapping("/login/{word}")
    public Result MLogin(@PathVariable String word) {
        if(stringRedisTemplate.opsForValue().get("phonenumber").equals(word)){
            User user = JSONUtil.toBean(stringRedisTemplate.opsForValue().get("user"), User.class);
            UserDTO dto =new UserDTO();
            dto.setUsername(user.getUsername());
            dto.setPassword(user.getPassword());
            UserDTO result=userService.login(dto);

            String role=result.getRole();
            Integer roleId = roleMapper.getByFlag(role);
            List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

            //查出系统所有菜单
            List<Menu> menus= menuService.list();
            List<Menu> userMenus= new ArrayList<>();
            //筛选菜单
            for (Menu menu : menus) {
                if(menuIds.contains(menu.getId())){
                    userMenus.add(menu);
                }
            }
            if(userMenus.size()>1||(userMenus.size()==1&&userMenus.get(0).getName()!="主页")){
                int flag=0;
                for (Menu userMenu : userMenus) {
                    if(userMenu.getName().equals("系统管理")){
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    QueryWrapper<Menu> queryWrapper=new QueryWrapper<>();
                    queryWrapper.eq("name","系统管理");
                    userMenus.add(menuService.getOne(queryWrapper));
                }
            }
            List<Menu> menus1 = menuService.findMenus(userMenus);
            result.setMenus(menus1);
            if(result.getConstants()==Constants.CODE_600){
                return Result.error(Constants.CODE_600,"用户名或密码错误");
            }else{
                return  Result.success(result);
            }
        }
        return Result.error(Constants.CODE_400,"验证码错误");
    }
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO dto) {
        String username= dto.getUsername();
        String password= dto.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"用户名或密码不能为空");
        }else{
            UserDTO result=userService.login(dto);

            String role=result.getRole();
            Integer roleId = roleMapper.getByFlag(role);
            List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

            //查出系统所有菜单
            List<Menu> menus= menuService.list();
            List<Menu> userMenus= new ArrayList<>();
            //筛选菜单
            for (Menu menu : menus) {
                if(menuIds.contains(menu.getId())){
                    userMenus.add(menu);
                }
            }
            if(userMenus.size()>1||(userMenus.size()==1&&userMenus.get(0).getName()!="主页")){
                int flag=0;
                for (Menu userMenu : userMenus) {
                    if(userMenu.getName().equals("系统管理")){
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    QueryWrapper<Menu> queryWrapper=new QueryWrapper<>();
                    queryWrapper.eq("name","系统管理");
                    userMenus.add(menuService.getOne(queryWrapper));
                }
            }
            List<Menu> menus1 = menuService.findMenus(userMenus);
            result.setMenus(menus1);
            if(result.getConstants()==Constants.CODE_600){
                return Result.error(Constants.CODE_600,"用户名或密码错误");
            }else{
                return  Result.success(result);
            }
        }
    }//@RequestBody前端jason转为后端Java对象


    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String username= user.getUsername();
        String password= user.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"用户名或密码不能为空");
        }else{
            String result=userService.register(user);
            if(result==Constants.CODE_200){
                return  Result.success(userService.register(user));
            }
            else if(result==Constants.CODE_600){
                return Result.error(Constants.CODE_600,"用户名已存在");
            }
            else{
                return Result.error(Constants.CODE_500,"系统错误");
            }

        }
    }

    @PostMapping
    public Result save(@RequestBody User user) {
        boolean a;
        stringRedisTemplate.delete("context");
         try {
             a=userService.saveOrUpdate(user);
         }
         catch (Exception e){
             return Result.error(Constants.CODE_600,"用户名已存在");
         }
         return Result.success(a);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        stringRedisTemplate.delete("context");
            return Result.success(userService.removeById(id));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
            return Result.success(userService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String username,
                               @RequestParam(defaultValue = "") String nickname) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        if(!"".equals(username)) {
            queryWrapper.like("username", username);//模糊查询
        }
        if(!"".equals(nickname)) {
            queryWrapper.like("nickname", nickname);//模糊查询
        }
        String jsonStr=stringRedisTemplate.opsForValue().get("context");
        Page<User> list;
        if(StrUtil.isBlank(jsonStr)){
            list = userService.page(new Page<>(pageNum, pageSize),queryWrapper);
            stringRedisTemplate.opsForValue().set("context", JSONUtil.toJsonStr(list));
        }else{
            list=JSONUtil.toBean(jsonStr, new TypeReference<Page<User>>() {
            },true);
        }
        return Result.success(list);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response)throws Exception{
        List<User> userList=userService.list();
        ExcelWriter writer= ExcelUtil.getWriter(true);
        //标头对应
//        writer.addHeaderAlias("username","用户名");
//        writer.addHeaderAlias("password","密码");
//        writer.addHeaderAlias("nickname","昵称");
//        writer.addHeaderAlias("email","邮件");
//        writer.addHeaderAlias("phone","电话");
//        writer.addHeaderAlias("address","地址");
//        writer.addHeaderAlias("createTime","创建时间");
//        writer.addHeaderAlias("avatarUrl","头像链接");
        //全部写入，包括标题
        writer.write(userList,true);
        stringRedisTemplate.delete("context");
        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out,true);
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public void imp(MultipartFile file)throws Exception{
        InputStream inputStream= file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.readAll(User.class);
        System.out.println(list);
        userService.saveBatch(list);
    }
}

