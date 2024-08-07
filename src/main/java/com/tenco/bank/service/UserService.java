package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;

@Service // Ioc 대상 (Singleton)
public class UserService {
	
	// DI - 의존 주입
	// @Autowired // -> new 를 하지 않아도 이걸 쓰면 자동으로 heap 메모리 영역으로 올라감(객체 생성)
	private UserRepository userRepository;

    @Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	
	}
	
	/**
	 * 회원 등록 서비스 기능
	 * 트랜잭션 처리 (기본이다)
	 * http://localhost:8080/user/sign-up 
	 * @param dto
	 */
	@Transactional // 트랜잭션 처리는 반드시 습관화
	public void createuser(SignUpDTO dto) {
		
		int result = 0;
		
		try {
			result = userRepository.insert(dto.toUser());
		} catch (DataAccessException e) {
			throw new DataDeliveryException("중복된 이름을 사용할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류입니다.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(result != 1) {
			throw new DataDeliveryException("회원 가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	/**
	 * 로그인 기능
	 * http://localhost:8080/user/sign-in
	 * @param dto
	 */
	@Transactional
	public User readUser(SignInDTO dto) {
		User userEntity = null;
		try {
			userEntity = userRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(userEntity == null) {
			throw new DataDeliveryException("아이디 혹은 비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return userEntity;
		
		
	}
}
