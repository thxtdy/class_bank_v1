package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.model.Account;

@Service
public class AccountService {
	
	private final AccountRepository accountRepository;
	
	@Autowired // 생략 가능 - DI 처리
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
	/**
	 * 계좌 생성 기능
	 * @param dto
	 * @param principal
	 */
	@Transactional
	public void createAccount(SaveDTO dto, Integer principal) {
		
		int resultValue = 0;

		try {
			resultValue = accountRepository.insert(dto.toAccount(principal));
		} catch (DataAccessException e) {
			throw new DataDeliveryException("중복된 명의의 계좌를 개설할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("이건 저도 모르는 에러입니다.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(resultValue != 1) {
			throw new DataDeliveryException("계좌 개설 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	/**
	 * 계좌 목록 보기 기능
	 * @param principal
	 * @return
	 */
	public List<Account> readAccountListByUserId(Integer userId) {
		List<Account> accountListEntity = null;
		
		try {
			accountListEntity = accountRepository.findByUserId(userId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new DataDeliveryException("나도 알 수 없는 에러입니다.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		return accountListEntity;
	}
	
}
