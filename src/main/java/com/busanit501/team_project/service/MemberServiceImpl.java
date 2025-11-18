package com.busanit501.team_project.service;

import com.busanit501.team_project.domain.APIUser;
import com.busanit501.team_project.dto.APIUserRegisterDTO;
import com.busanit501.team_project.dto.ChangePasswordRequestDTO;
import com.busanit501.team_project.dto.APIUserDTO;
import com.busanit501.team_project.repository.APIUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{

    private final APIUserRepository apiUserRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화

    @Override
    public void register(APIUserDTO apiUserDTO) {
        if(apiUserRepository.existsByMemberId(apiUserDTO.getMemberId()))
        {
            throw new RuntimeException("아이디가 이미 존재합니다.");
        }
        APIUser apiUser = APIUser.builder()
                .memberId(apiUserDTO.getMemberId())
                .password(passwordEncoder.encode(apiUserDTO.getPassword()))
                .userName(apiUserDTO.getUserName())
                .email(apiUserDTO.getEmail())
                .role(apiUserDTO.getRole())
                .social(apiUserDTO.isSocial())
                .build();
        apiUserRepository.save(apiUser);
    }

    @Override
    public boolean isRegistered(String memberId) {
        boolean isRegistered = apiUserRepository.existsByMemberId(memberId);
        return isRegistered;
    }

    @Override
    public void join(APIUserRegisterDTO apiUserRegisterDTO) {
        log.info("회원가입 서비스 호출 : " + apiUserRegisterDTO);

        // 아이디 중복 체크
        if (apiUserRepository.existsByMemberId(apiUserRegisterDTO.getMemberId())) {
            throw  new IllegalArgumentException("이미 사용 중인 아이디입니다."); // 예외처리
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(apiUserRegisterDTO.getPassword());

        // DTO -> 엔티티 변환
        APIUser apiUser = APIUser.builder()
                .memberId(apiUserRegisterDTO.getMemberId()) // 아이디
                .password(encodedPassword) // 암호화된 비밀번호
                .userName(apiUserRegisterDTO.getUserName()) // 이름
                .email(apiUserRegisterDTO.getEmail()) // 이메일
                .social(false) // 일반 회원가입은 social이 false
                .role("USER") // 기본 역할
                .build();

        // DB 저장
        apiUserRepository.save(apiUser);

        log.info("회원가입 완료 : " + apiUser.getMemberId());
    }

    @Override
    public boolean checkId(String memberId) {
        // memberId가 존재하면 true 반환(중복), 존재하지 않으면 false반환(사용가능)
        return apiUserRepository.existsByMemberId(memberId);
    }

    @Override
    public APIUserDTO getMember(String memberId) {
        APIUserDTO dto = apiUserRepository.findByMemberId(memberId)
                .map(apiUser -> new APIUserDTO(
                        apiUser.getMemberId(),
                        apiUser.getPassword(),
                        apiUser.getUserName(),
                        apiUser.getEmail(),
                        apiUser.getRole(),
                        apiUser.isSocial(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + apiUser.getRole())) // 권한 추가
                ))
                .orElse(null);
        log.info("memberService dto : " + dto);
        return dto;
    }

    @Transactional
    public void changePassword(String memberId, ChangePasswordRequestDTO requestDTO) {
        // 1. 현재 로그인된 사용자 정보를 DB에서 조회
        APIUser apiUser = apiUserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 입력된 '현재 비밀번호'가 저장된 비밀번호와 일치하는지 확인
        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), apiUser.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. '새 비밀번호'를 암호화하여 엔티티에 설정
        apiUser.changePw(passwordEncoder.encode(requestDTO.getNewPassword()));
        // 4. 변경된 정보를 DB에 저장 (@Transactional에 의해 메서드 종료 시 자동 반영)
        apiUserRepository.save(apiUser);
    }

    @Transactional
    @Override
    public APIUserDTO processSocialLogin(String registrationId, String socialId, String email, String nickname) {
        String memberId = registrationId + "_" + socialId; // 소셜 ID와 서비스 제공자를 결합하여 우리 앱의 memberId 생성

        // 이미 존재하는 회원인지 확인
        Optional<APIUser> result = apiUserRepository.findByMemberId(memberId);
        APIUser apiUser;

        if (result.isPresent()) {
            // 기존 회원인 경우
            apiUser = result.get();
            log.info("Existing social user: {}", memberId);
            // 필요시 회원 정보 업데이트 로직 추가
            // 이름과 이메일이 변경되었을 수 있으므로 업데이트
            if (nickname != null && !nickname.isEmpty() && !apiUser.getUserName().equals(nickname)) {
                apiUser.changeUserName(nickname);
            }
            if (email != null && !email.isEmpty() && !apiUser.getEmail().equals(email)) {
                apiUser.changeEmail(email);
            }
            apiUserRepository.save(apiUser); // 변경된 정보 저장
        } else {
            // 신규 회원인 경우
            log.info("New social user: {}", memberId);
            String tempPassword = passwordEncoder.encode("socialpassword"); // 소셜 로그인은 비밀번호가 필요 없지만, 엔티티 제약조건 때문에 필요

            apiUser = APIUser.builder()
                    .memberId(memberId)
                    .password(tempPassword)
                    .userName(nickname != null ? nickname : memberId)
                    .email(email)
                    .role("USER") // 기본 역할
                    .social(true)
                    .build();
            apiUserRepository.save(apiUser);
        }

        // APIUserDTO 반환 시 비밀번호는 제외
        return new APIUserDTO(
                apiUser.getMemberId(),
                apiUser.getPassword(), // 비밀번호는 DTO에 포함하지 않음 (실제 비밀번호 필드 사용)
                apiUser.getUserName(),
                apiUser.getEmail(),
                apiUser.getRole(),
                apiUser.isSocial(),
                List.of(new SimpleGrantedAuthority("ROLE_" + (apiUser.getRole() != null && !apiUser.getRole().isEmpty() ? apiUser.getRole() : "USER")))
        );
    }
}
