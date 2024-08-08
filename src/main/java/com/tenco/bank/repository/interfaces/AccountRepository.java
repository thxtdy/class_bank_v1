package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.Account;

@Mapper
public interface AccountRepository {
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id);
	
	// 고민! - 계좌 조회
	// -> 한 사람의 유저는 여러개의 계좌 번호를 가질 수 있다.
	// 파라미터 설계를 할때 2개 이상의 파라미터를 설계하고자 한다면 @Param 안에 기입해줘야 함.

	// @Param 쓰는 이유.
	// interface 파라미터명과 xml 에 사용할 변수명을 다르게 사용해야 할때
	public List<Account> findByUserId(@Param("userId") Integer principalId);
	// -> account id 값으로 계좌 정보 조회
	public Account findByNumber(@Param("number") String id);
}
