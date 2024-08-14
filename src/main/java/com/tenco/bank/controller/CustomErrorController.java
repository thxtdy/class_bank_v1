package com.tenco.bank.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.tenco.bank.handler.exception.RedirectException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 존재하지 않은 경로 요청 시 예외 처리 (404)
 */
@Controller // IoC (Singleton)
public class CustomErrorController implements ErrorController {

	@GetMapping("/error")
	public void handleError(HttpServletRequest request) {
		
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		if (status != null) {
			// parseInt int 타입으로 변환 || valueOf Integer 객체로 변환
			Integer statusCode = Integer.valueOf(status.toString());
			
			if(statusCode == HttpStatus.NOT_FOUND.value()) {
				throw new RedirectException("잘못된 요청입니다." , HttpStatus.NOT_FOUND);
			}
			
			// 상세 설정 가능함
			// else if (statusCode == ....)
			
		}
		
	}

}
