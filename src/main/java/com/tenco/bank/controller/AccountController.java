package com.tenco.bank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;

import jakarta.servlet.http.HttpSession;

@Controller // IoC 대상(Singleton)
@RequestMapping("/account")
public class AccountController {
	
	// 계좌 생성 화면 요청
	private final AccountService accountService;
	private final HttpSession session;
	
	public AccountController(AccountService accountService, HttpSession session) {
		this.accountService = accountService;
		this.session = session;
	}
	/**
	 * 계좌 생성 페이지 요청
	 * 주소 설계 : http://localhost:8080/account/save
	 * @return save.jsp
	 */
	@GetMapping("/save")
	public String savePage() {
		
		// 1. 인증 검사 필요(account 전체에 필요)
		User principal = (User)session.getAttribute("principal");
		
		if(principal == null) {
			throw new UnAuthorizedException("로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED);
		}
		
		return "account/save";
	}
	/**
	 * 계좌 생성 기능
	 * @param dto
	 * @return index
	 */
	@PostMapping("/save")
	public String saveProc(SaveDTO dto) {
		// 1. form 데이터 추출 (파싱 전략)
		// 2. 인증 검사
		// 3. 유효성 검사
		// 4. 서비스 호출
		System.out.println(dto.toString());
		User principal = (User)session.getAttribute("principal");
		
		if(principal == null) {
			throw new UnAuthorizedException("로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED);
		}
		
		if(dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new DataDeliveryException("계좌 번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		} 
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		} 
		if (dto.getBalance() <= 0 || dto.getBalance() == null) {
			throw new DataDeliveryException("금액을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		if (principal.getId() == 0) {
			throw new UnAuthorizedException("로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED);
		}
		
		accountService.createAccount(dto, principal.getId());
		
		return "redirect:/index";
		
	}
	/**
	 * 계좌 목록 화면 요청
	 * 주소 설계 : http://localhost:8080/account/list
	 * @return list.jsp
	 */
	@GetMapping("/list")
	public String listPage(Model model) {
		
		// 1. 인증 검사
		User principal = (User)session.getAttribute("principal");
		if(principal == null) {
			throw new UnAuthorizedException("로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED);
		}
		// 2. 유효성 검사
		// 3. 서비스 호출
		
		// JSP 데이터를 넣어주는 방법
		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());
		if(accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}
		return "account/list";
	}
	
}
