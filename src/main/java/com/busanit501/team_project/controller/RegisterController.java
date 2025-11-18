package com.busanit501.team_project.controller;

import com.busanit501.team_project.dto.APIUserRegisterDTO; // MemberDTO 대신 APIUserRegisterDTO 사용
import com.busanit501.team_project.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class RegisterController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> join(@Valid @RequestBody APIUserRegisterDTO apiUserRegisterDTO) {
        log.info("회원가입 요청 수신 : " + apiUserRegisterDTO);
        try {
            memberService.join(apiUserRegisterDTO);
            return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.OK);
        }  catch (IllegalArgumentException e) {
            log.info("회원가입 실패 (비즈니스 로직) : " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
        } catch (Exception e) {
            log.info("회원가입 실패 : " + e.getMessage());
            return new ResponseEntity<>("회원가입에 실패하였습니다. : " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }

    @GetMapping("/checkId")
    public ResponseEntity<String> checkId(@RequestParam("memberId") String memberId) {
        log.info("아이디 중복 확인 요청 : " + memberId);
        boolean check = memberService.checkId(memberId);
        if (check) {
            return new ResponseEntity<>("이미 사용 중인 아이디입니다.",
                    HttpStatus.CONFLICT); // 409 Conflict
        } else {
            return new ResponseEntity<>("사용 가능한 아이디입니다.",
                    HttpStatus.OK); // 200 ok
        }
    }
}
