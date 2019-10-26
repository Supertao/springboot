package com.springboot.filter;


import javax.servlet.*;
import java.io.IOException;

@javax.servlet.annotation.WebFilter(filterName = "WebFilter",urlPatterns = "/**")
public class WebFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("filter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println(servletRequest.getServerName());
        System.out.println("do filter");
        filterChain.doFilter(servletRequest,servletResponse);

    }

    @Override
    public void destroy() {
        System.out.println("filter destroy");
    }
}
