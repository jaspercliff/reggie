package com.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * 检查用户是否已经完成登录
 * 1，获取本次请求的uri
 * 2.判断是否需要处理
 * 3.如果不需要 则直接放行
 * 4。判断登录状态，如果已经登录，直接放行
 * 5。如果未登录则返回未登录结果
 */
@Slf4j
@WebFilter( filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
//路径匹配器 支持
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =   (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        log.info("拦截的请求",requestURI);
//        定义不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        long id = Thread.currentThread().getId();
        log.info("线程id = {}",id);

        boolean check = check(urls, requestURI);
        if (check) {
            log.info("本次请求不需要处理"+requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("employee")!=null) {
            log.info("用户已经登录"+request.getSession().getAttribute("employee"));
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，判断本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
