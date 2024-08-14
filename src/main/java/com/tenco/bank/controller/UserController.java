package com.tenco.bank.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller // IoC의 대상(Singleton)
@RequestMapping("/user") // 대문처리
public class UserController {
	
	private UserService userService;
	private final HttpSession session;
	
	// 주석 처리해주는 것은 매너이다. 난 스윗한 사람이 되고 싶어.
	
	@Autowired // DI 처리
	public UserController(UserService userService, HttpSession session) {
		this.userService = userService;
		this.session = session;
	}
	
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
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_FULLNAME, HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 객체로 전달
		userService.createuser(dto);
		
		return "redirect:/user/sign-in";
	}
	
	@GetMapping("/kakao")
	public ResponseEntity<?> codeTest(@RequestParam("code") String code) {

		URI uri = UriComponentsBuilder
				.fromUriString("https://kauth.kakao.com/oauth/token")
				.build()
				.toUri();
		System.out.println("CODE : CODE  : " + code);
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "7a81e9291d6290d4715bd26635e4af2b");
		params.add("redirect_uri", "http://localhost:8080/user/kakao");
		params.add("code", code);
		
		HttpEntity<MultiValueMap<String, String>> requestEntity
			= new HttpEntity<>(params, headers);
		
		ResponseEntity<String> response
		= restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
		System.out.println("헤더 : " + response.getHeaders());
		System.out.println("바디 : " + response.getBody());
		
		return response;
	}
	
	
	
	/**
	 * 로그인 화면 요청
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {

		return "user/signIn";
	}
	
	/**
	 * 로그인 요청 처리
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@PostMapping("/sign-in")
	public String signProc(SignInDTO dto) {
		
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		// 서비스 호출
		User principal = userService.readUser(dto);
		
		// 세션 메모리에 등록 처리
		session.setAttribute(Define.PRINCIPAL, principal);
		
		// 새로운 페이지로 이동 처리
		// TODO 계좌 목록 페이지 이동 처리 예정
		return "redirect:/account/list";
	}
	
	@GetMapping("/logout")
	public String logout() {
		session.invalidate(); // Log Out !
		return "redirect:/user/sign-in";
	}
	
}
