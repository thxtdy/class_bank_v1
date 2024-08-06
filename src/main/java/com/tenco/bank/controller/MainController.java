package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.handler.exception.UnAuthorizedException;

@Controller // IoC 대상(싱글톤 패턴 관리가 된다) -- 제어의 역전

public class MainController {
	
	// REST API 기반으로 주소 설계 가능
	
	// 주소 설계
	// http://localhost:8080/main-page
	@GetMapping({"/main-page", "/index"})
//	@ResponseBody // 데이터를 반환한다 like RestController ~
	public String mainPage() {
		System.out.println("mainpage() 호출 확인");
		// [JSP 파일 찾기 (yml 설정)] - 뷰 리졸버
		// prefix: /WEB-INF/view
		// 		   /main
		// suffix: .js
		
		return "main";
	}
	
	// TODO - 삭제 예정
	// 주소 설계
	// http://localhost:8080/error-test1/true
	@GetMapping("/error-test1")
	public String errorPage() {
		if(true) {
			throw new RedirectException("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}
		return "main";
	}
	
	@GetMapping("/error-test2")
	public String errorData() {
		if(true) {
			throw new DataDeliveryException("잘못된 데이터입니다.", HttpStatus.BAD_REQUEST);
		}
		return "main";
	}
	@GetMapping("/error-test3")
	public String errorUnAuthorized() {
		if(true) {
			throw new UnAuthorizedException("인증이 되지 않은 사용자입니다.", HttpStatus.BAD_REQUEST);
		}
		return "main";
	}
	
	
}
