package com.busanit501.team_project.service;

import com.busanit501.team_project.domain.APIUser;
import com.busanit501.team_project.domain.Member;
import com.busanit501.team_project.dto.ChangePasswordRequestDTO;
import com.busanit501.team_project.dto.MemberDTO;
import com.busanit501.team_project.repository.APIUserRepository;
import com.busanit501.team_project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final APIUserRepository apiUserRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화

    @Override
    public void register(Member member) {
        if(memberRepository.existsByMemberId(member.getMemberId()))
        {
            throw new RuntimeException("아이디가 이미 존재합니다.");
        }
        Member encodeMember = Member.builder()
                .memberId(member.getMemberId())
                .password(member.getPassword())
                .userName(member.getUserName())
                .email(member.getEmail())
                .build();
        memberRepository.save(encodeMember);
    }

    @Override
    public boolean isRegistered(String memberId) {
        boolean isRegistered = memberRepository.existsByMemberId(memberId);
        return isRegistered;
    }

    @Override
    public void join(MemberDTO  memberDTO) {
        log.info("회원가입 서비스 호출 : " + memberDTO);

        // 아이디 중복 체크
        if (memberRepository.existsByMemberId(memberDTO.getMemberId())) {
            throw  new IllegalArgumentException("이미 사용 중인 아이디입니다."); // 예외처리
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());

        // DTO -> 엔티티 변환
        Member member = Member.builder()
                .memberId(memberDTO.getMemberId()) // 아이디
                .password(encodedPassword) // 암호화된 비밀번호
                .userName(memberDTO.getUserName()) // 이름
                .email(memberDTO.getEmail()) // 이메일
                .build();

        // DB 저장
        memberRepository.save(member);
        APIUser apiUser = APIUser.builder()
                .memberId(member.getMemberId())
                .password(member.getPassword())
                .build();
        apiUserRepository.save(apiUser);

        log.info("회원가입 완료 : " + member.getMemberId());
    }

    // 아이디 중복 체크
    @Override
    public boolean checkId(String memberId) {
        // memberId가 존재하면 true 반환(중복), 존재하지 않으면 false반환(사용가능)
        return memberRepository.existsByMemberId(memberId);
    }

    @Override
    public MemberDTO getMember(String memberId) {
        MemberDTO dto = memberRepository.findByMemberId(memberId)
                .map(member -> new MemberDTO(
                        member.getMemberId(),
                        member.getUserName(),
                        member.getEmail(),
                        member.getPassword()))
                .orElse(null);
        log.info("memberService dto : " + dto);
        return dto;
    }

    @Transactional
    public void changePassword(String memberId, ChangePasswordRequestDTO requestDTO) {
        // 1. 현재 로그인된 사용자 정보를 DB에서 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        APIUser apiuser = apiUserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 입력된 '현재 비밀번호'가 저장된 비밀번호와 일치하는지 확인
        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. '새 비밀번호'를 암호화하여 엔티티에 설정
        member.changePw(passwordEncoder.encode(requestDTO.getNewPassword()));
        apiuser.changePw(member.getPassword());
        // 4. 변경된 정보를 DB에 저장 (@Transactional에 의해 메서드 종료 시 자동 반영)
        memberRepository.save(member);
    }
}
