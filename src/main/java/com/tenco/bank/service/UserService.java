package com.tenco.bank.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

import lombok.RequiredArgsConstructor;

@Service // Ioc 대상 (Singleton)
@RequiredArgsConstructor
public class UserService {
	
	// DI - 의존 주입
	// @Autowired // -> new 를 하지 않아도 이걸 쓰면 자동으로 heap 메모리 영역으로 올라감(객체 생성)
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final PasswordEncoder passwordEncoder;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	/**
	 * 회원 등록 서비스 기능
	 * 트랜잭션 처리 (기본이다)
	 * http://localhost:8080/user/sign-up 
	 * @param dto
	 */
	@Transactional // 트랜잭션 처리는 반드시 습관화
	public void createuser(SignUpDTO dto) {
		System.out.println(dto.getMFile().getOriginalFilename());
		if(!dto.getMFile().isEmpty()) {
			String[] fileNames = uploadFile(dto.getMFile());
			dto.setOriginFileName(fileNames[0]);
			dto.setUploadFileName(fileNames[1]);
			
		}
		int result = 0;
		try {
			// 코드 추가 부분
			// 회원 가입 요청 시 사용자가 던진 비밀번호 값을 암호화 처리 해야 함
			String hashPwd = passwordEncoder.encode(dto.getPassword());
			System.out.println("Hashed : " + hashPwd);
			dto.setPassword(hashPwd);
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
		
		// 기능 수정
		// username 으로만 -> SELECT 처리 할 것임
		// 2가지의 경우의 수 -> 1. 객체가 존재 || 2.null
		
		// 객체 안에 사용자의 password 가 존재한다.(암호화 되어있는 값)
		
		// passwordEncoder 안에 match 해주는 메소드가 있다
		
		try {
			userEntity = userRepository.findByUsername(dto.getUsername());
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(userEntity == null) {
			throw new DataDeliveryException("존재하지 않는 아이디입니다.", HttpStatus.BAD_REQUEST);
		}
		
		// userEntity.getPassword = encoding 이 완료된 Password
		// dto.getPassword = Raw(생) 값의 Password
		
		// 													 RAW				 Hashed
		boolean isPwdMatched = passwordEncoder.matches(dto.getPassword(), userEntity.getPassword());
		
		if(isPwdMatched == false) {
			throw new DataDeliveryException("비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return userEntity;
		
	}
	/**
	 * 서버 운영체제의 파일 업로드 기능
	 * Multipart: 사용자가 작성한 파일명
	 * @param mFile
	 * @return
	 */
	private String[] uploadFile(MultipartFile mFile) {
		
		if(mFile.getSize() > Define.MAX_FILE_SIZE) {
			throw new DataDeliveryException("파일 크기는 최대 20MB입니다.", HttpStatus.BAD_REQUEST);
		}
		
		// TODO - 초급 개발자들이 맨날 빼 먹는 코드 기억하긩
		// 서버 컴퓨터에 파일을 넣을 디렉토리가 있는지 검사!!!
//		String saveDirectory = Define.UPLOAD_FILE_DIRECTORY;
//		System.out.println("uploadDir : " + uploadDir);
//		File directory = new File(saveDirectory);
//		if(!directory.exists()) {
//			directory.mkdirs();
//		}
		
		// 코드 수정 8/14
		// File - getAbsolutePath() : 파일 시스템의 절대 경로를 나타낸다
		// Linux 또는 MacOS에 맞춰서 절대 경로를 생성할 수 있다.
//		String saveDirectory = new File(uploadDir).getAbsolutePath();
		String saveDirectory = uploadDir;
		System.out.println("SaveDirectory : " + saveDirectory);
		
		// 파일 이름 생성(중복 이름 예방) 
		String uploadFileName = UUID.randomUUID() + "_" + mFile.getOriginalFilename();
		// 파일 전체경로 + 새로생성한 파일명 
		String uploadPath = saveDirectory + File.separator + uploadFileName;
		System.out.println("----------------------------");
		System.out.println(uploadPath);
		System.out.println("----------------------------");
		File destination = new File(uploadPath);
		
		// 반드시 수행
		try {
			mFile.transferTo(destination);
		} catch (IllegalStateException | IOException  e) {
			e.printStackTrace();
			throw new DataDeliveryException("파일 업로드중에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
		return new String[] {mFile.getOriginalFilename(), uploadFileName}; 
	}
}
