package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;

// Controller => String (뷰 리졸버 동작 -> JSP 파일 찾아서 렌더링 처리)
// RestController => 데이터를 반환처리
@RestController // @Controller + @ResponseBody가 합쳐짐 REST API
public class TestController {
	
	// localhost:8080/test1
	@GetMapping("/test1")
	public User test1() {
		// Message Converter => 원래라면 GSON 에서 JSON을 파싱해줘야 하지만
		// Spring boot에선 자동으로 해줌
		
		try {
			int result = 10 / 0;
		} catch (Exception e) {
			throw new UnAuthorizedException("인증이 안된 사용자입니다.", HttpStatus.UNAUTHORIZED);
		}
		return User.builder().username("둘리").password("asd123").build();
	}
	
}
