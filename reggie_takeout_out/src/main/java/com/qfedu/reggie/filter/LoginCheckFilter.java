package com.qfedu.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.qfedu.reggie.common.BaseContext;
import com.qfedu.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.VarHandle;

/**
 * 检查用户是否完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheekFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获得本次请求的URL
        String requestURI = request.getRequestURI();
        //不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",//登录请求
                "/employee/logout",//退出请求
                "/backend/**", //静态资源
                "/front/**",//静态资源
                "/user/sendMsg",//移动端发送验证码
                "/user/login"//移动端口登录请求
        };
        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要处理 就直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        //4、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，登录用户id为: {}",request.getSession().getAttribute("employee"));

            Long enpId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(enpId);



//            long id = Thread.currentThread().getId();
//            log.info("线程id为: {}",id);


            filterChain.doFilter(request, response);
            return;
        }
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }




        ////5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }

        }
        return false;


    }
}
