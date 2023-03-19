package com.qfedu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qfedu.reggie.common.R;
import com.qfedu.reggie.entity.User;
import com.qfedu.reggie.service.UserService;
import com.qfedu.reggie.utils.SMSUtils;
import com.qfedu.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注入redis缓存
     */
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送手机验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        //判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);


            //调用阿里云提供的短信服务API完成发送短信
            //第一个参数短信的签名  第二个参数的模板  第三个参数是手机号   第四个参数是传进的动态验证码
            SMSUtils.sendMessage("瑞吉外卖", "SMS_269420531", phone, code);

            //需要将生成的验证码保存到Session
            //session.setAttribute(phone, code);

            //将生成的验证码缓存到Redis中 。设置有效期5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机验证发送成功");

        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    //User返回用户信息
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码   通过手机号的ke 来取出验证码
     //   Object attribute = session.getAttribute(phone);

        //从redis中获取缓存验证码
        Object attribute =redisTemplate.opsForValue().get(phone);


        if (attribute !=null &&attribute.equals(code)){
            //如果能够比对成功，说明登录成功
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            //手机号是唯一的 去数据库比对
            User user = userService.getOne(queryWrapper);
            if (user==null){
                //说明是型用户 需要自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);

            }
            session.setAttribute("user",user.getId());
            //登录成功删除redis中缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);



        }

        return R.error("登录失败");
    }


    //用户登出
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        //清理Session中保存的当前用户登录的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


}
