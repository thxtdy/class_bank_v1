package com.tenco.bank.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

@Service
public class AccountService {
	
	private final AccountRepository accountRepository;
	private final HistoryRepository historyRepository;
	
	@Autowired // 생략 가능 - DI 처리
	public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
		this.accountRepository = accountRepository;
		this.historyRepository = historyRepository;
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
			throw new DataDeliveryException(Define.EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(resultValue != 1) {
			throw new DataDeliveryException(Define.FAIL_TO_CREATE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
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
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new DataDeliveryException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		return accountListEntity;
	}
	/**
	 * 출금 기능
	 * @param dto
	 * @param principalId
	 */
	// 1. 계좌의 존재 여부부터 판단 -- SELECT
	// 2. 본인 계좌 여부도 확인 -- 객체 상태값에서 비교
	// 3. 계좌 비밀번호 확인 -- 객체 상태값에서 일치 여부 확인
	// 4. 잔액 여부 확인 -- 객체 상태값에서 확인
	// 5. 출금 처리 -- UPDATE
	// 6. history_tb 에 등록 -- INSERT
	// 7. 트랜잭션 처리
	@Transactional
	public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {
		// 1.
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		// 2.
		accountEntity.checkOwner(principalId);
		// 3.
		accountEntity.checkPassword(dto.getWAccountPassword());
		// 4.
		accountEntity.checkBalance(dto.getAmount());
		// 5.
		accountEntity.withdraw(dto.getAmount());
		//update
		accountRepository.updateById(accountEntity);
		
		// 6.
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
		int rowResultCount = historyRepository.insert(history);
		if(rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	// 1. 계좌 조회
	// 2. 계좌 명의 확인
	// 3. 잔액 확인
	// 4. 입금 처리
	// 5. 거래 내역 남기기
	@Transactional
	public void updateAccountDeposit(DepositDTO dto, Integer id) {
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		accountEntity.checkOwner(id);
		accountEntity.deposit(dto.getAmount());
		accountRepository.updateById(accountEntity);
		
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(null);
		history.setDBalance(accountEntity.getBalance());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());
		
		int result = historyRepository.insert(history);
		if(result != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	/**
	 * 이체 페이지 요청
	 * @param dto
	 * @param principalId
	 * 1. 출금 계좌 존재 여부 확인 -- SELECT
	 * 2. 입금 계좌 존재 여부 확인 -- SELECT
	 * 3. 출금 계좌 본인 소유 확인 -- 객체의 상태값과 세션 ID 비교
	 * 4. 출금 계좌 비밀번호 확인 -- 객체의 상태값과 DTO 값 비교
	 * 5. 출금 계좌의 잔액 확인 -- 객체의 상태값과 DTO 값 비교
	 * 6. 입금 계좌 객체 상태값 변경 처리 (잔액 + 거래금액)
	 * 7. 입금 계좌 -- UPDATE
	 * 8. 출금 계좌 객체 상태값 변경 처리 (잔액 - 거래금액)
	 * 9. 출금 계좌 -- UPDATE
	 * 10. 거래 내역 등록 처리
	 * 11. 트랜잭션 처리
	 */
	@Transactional
	public void updateAccountTransfer(TransferDTO dto, Integer principalId) {
		Account dAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		Account wAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if (dAccountEntity == null || wAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		wAccountEntity.checkOwner(principalId);
		wAccountEntity.checkPassword(dto.getPassword());
		wAccountEntity.checkBalance(dto.getAmount());
		wAccountEntity.withdraw(dto.getAmount());
		accountRepository.updateById(wAccountEntity);
		
		dAccountEntity.deposit(dto.getAmount());
		accountRepository.updateById(dAccountEntity);
		
		History history = History.builder()
				.amount(dto.getAmount())
				.wBalance(wAccountEntity.getBalance())
				.dBalance(dAccountEntity.getBalance())
				.wAccountId(principalId)
				.dAccountId(dAccountEntity.getId())
				.build();
		int result = historyRepository.insert(history);
		if(result != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 단일 계좌 조회 기능
	 * @param accountId (px)
	 * @return
	 */
	public Account readAccountById(Integer accountId) {
		Account accountEntity = accountRepository.findByAccountId(accountId);
		
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return accountEntity;
	}
	/**
	 * 단일 계좌 거래 내역 조회
	 * @param type = [all, deposit, withdrawal]
	 * @param accountId (Primary Key)
	 * @return 전체, 입금, 출금 거래 내역 3가지 타입 반환 (HistoryAccount list)
	 */
	@Transactional
	public List<HistoryAccount> readHistoryByAccountId(@Param("type")String type, @Param("accountId") Integer accountId) {
		List<HistoryAccount> list = new ArrayList<>();
		list = historyRepository.findByAccountIdAndTypeOfHistory(type, accountId);
		return list;
	}
}
