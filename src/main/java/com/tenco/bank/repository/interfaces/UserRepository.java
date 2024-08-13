package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.User;

// MyBatis 설정 확인
// UserRepository 인터페이스와 user.xml 파일을 매칭시킨다.
@Mapper // 반드시 선언해야 동작함
public interface UserRepository {
	public int insert(User user);
	public int updateById(User user);
	public int deleteById(Integer id);
	public User findById(Integer id);
	public List<User> findAll();
	
	// 로그인 기능 X (username, password) -> return User ..
	// 매개변수 2개 이상 시 반드시 @Param 어노테이션 사용
	public User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

	public User findByUsername(@Param("username") String username);
}
