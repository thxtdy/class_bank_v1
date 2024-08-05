package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice // IoC 대상 (싱글톤 패턴)
public class GlobalControllerAdvice {
	
	// ConfigDataResourceNotFoundException
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public ResponseEntity<Object> handleResourceNotDFoundException(Exception e) {
		
		System.out.println("GlobalControllerAdvice : 오류 확인 : ");

		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
}
