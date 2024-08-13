package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired // DI
	private final AuthInterceptor authInterceptor;
	
	// @RequiredArgsConstructor <- 생성자 대신 사용할 수 없다
	
	// 우리가 만든 AuthInterciptor를 등록해야 함.
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(authInterceptor).addPathPatterns("/account/**")
												.addPathPatterns("/auth/**");
	}
	
}
