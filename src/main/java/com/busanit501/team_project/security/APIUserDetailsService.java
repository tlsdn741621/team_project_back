package com.busanit501.team_project.security;

import com.busanit501.team_project.domain.APIUser;
import com.busanit501.team_project.dto.APIUserDTO;
import com.busanit501.team_project.repository.APIUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class APIUserDetailsService implements UserDetailsService {

    private final APIUserRepository apiUserRepository;


    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Optional<APIUser> result = apiUserRepository.findById(memberId);

        APIUser apiUser = result.orElseThrow(() -> new UsernameNotFoundException("Member Not Found :" +memberId));

        APIUserDTO dto = new APIUserDTO(
                apiUser.getMemberId(),
                apiUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        log.info("APIUserDetailsService - Created APIUserDTO : {}", dto);
        return dto;
    }
}
