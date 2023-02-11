package com.gsq.springboot.utils;
/**
生成token
 */


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gsq.springboot.entity.User;
import com.gsq.springboot.service.IUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
@Component
public class TokenUtils {
    private static IUserService staticService;
    @Resource
    private  IUserService userService;

    @PostConstruct//项目启动时调用
    public void setUserService(){
        staticService=userService;
    }

    public static String getToken(String userId,String sign){
        return JWT.create().withAudience(userId)   //签发对象
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))  //有效时间2小时
                .sign(Algorithm.HMAC256(sign));  //密钥
    }

    public static User getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            try {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticService.getById(Integer.valueOf(userId));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
