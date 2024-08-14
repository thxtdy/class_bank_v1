package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
	
	// 코드 추가
	// c:\Users\KDP\Documents\Lightshot\a.png <-- 서버 컴퓨터 상의 실제 이미지 경로
	// 프로젝트 상에선 (클라이언트가 HTML 소스로 보는 경로) /images/uploads/**
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/images/uploads/**").addResourceLocations("file:\\C:\\work_springg\\uploads/");
	}
	
	
	@Bean // IoC 대상 (Singleton)
	PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
		
	}
	
}
