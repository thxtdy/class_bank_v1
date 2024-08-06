package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.service.UserService;

@Controller // IoC의 대상(Singleton)
@RequestMapping("/user") // 대문처리
public class UserController {
	
	@Autowired // DI 처리
	private UserService userService;
	
	// 주석 처리해주는 것은 매너이다. 난 스윗한 사람이 되고 싶어.
	
	/**
	 * 회원 가입 페이지 요청
	 * 주소 설계 : http://localhost:8080/user/sign-up
	 * @return signUp.jsp
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		
		// prefix: /WEB-INF/view/ 
	    // suffix: .jsp 
		return "user/signUp";
	}
	
	/**
	 * Sign Up Process (회원 가입 로직 처리 요청)
	 * 주소 설계 : http://localhost:8080/user/sign-up
	 * @param dto
	 * @return index(TODO - 수정 예정)
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {
		
		// Controller 에서 일반적인 코드 작업
		// 1. 인증검사 (여기서는 인증검사 불필요)
		// 2. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("유저 이름을 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException("모든 이름을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 객체로 전달
		userService.createuser(dto);
		
		return "redirect:/index";
	}
	
}
