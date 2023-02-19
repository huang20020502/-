package com.xin.yygh.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

//@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径,判断是否需要权限
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 如果是登录请求，不需要权限
        if (pathMatcher.match("/admin/user/**",path)){
            // 放行
            return chain.filter(exchange);
        } else {
           // 判断是否登录
            List<String> token = request.getHeaders().get("X-Token");
            if (token == null){
                // 没有登录，跳转到登录页面
                ServerHttpResponse response = exchange.getResponse();
                // 设置状态码
                response.setStatusCode(HttpStatus.SEE_OTHER);
                // 设置登录页面地址
                response.getHeaders().add(HttpHeaders.LOCATION,"http://localhost:9528");
                return response.setComplete(); // 结束请求
            } else {
                return chain.filter(exchange);
            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
