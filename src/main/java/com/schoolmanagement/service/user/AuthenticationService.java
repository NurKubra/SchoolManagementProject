package com.schoolmanagement.service.user;

import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.payload.request.LoginRequest;
import com.schoolmanagement.payload.response.AuthResponse;
import com.schoolmanagement.security.jwt.JwtUtils;
import com.schoolmanagement.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    //login isleminden sonra tetiklencek tum methodlar burda caliscak
    //login icin ayri dto response da ayri dto(AuthResponse tum filedler String) kullaniyorum
    //

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {

        //!!! request icinden username ve password aliniyor (String olarak aldik )
        String username =loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // !!! AuthenticationManager uzerinden kullaniciyi valide ediyoruz (username ve passwordu valide ediyor, direk bakamiyor, cunku authentication turunde bir nesneye cevirmemiz lazim )
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        //authentication icinde UserDetails var
        // !!! Valide edilen user context e gonderiliyor (valide edilen kullanici objesi gecici olarak tutulmak icin)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // !!! JWT token olusturuluyor (valide edildikten sonra client tarafina gondermek icin jwt token create ediyorum)
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        // !!! response icindeki fieldlar dolduruluyor
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();  //UserDetails i aliyorum

        Set<String> roles  = userDetails.getAuthorities() //birden fazla rol icerme ihtimali old. icn ama benim projemde tek bir rol icerdigini biliyorum onetoone dan bu yuzden ilk rolu alyiorum
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();

        // !!! Response nesnesi olusturuluyor (UserDetails den cogu fielda ulasabilrim ama bazi )
        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.username(userDetails.getUsername());
        authResponse.token(token.substring(7));
        authResponse.name(userDetails.getName());
        // !!! role bilgisi setleniyor
        if(role.isPresent()){
            authResponse.role(role.get());
            if(role.get().equalsIgnoreCase(RoleType.TEACHER.name())) {
                authResponse.isAdvisory(userDetails.getIsAdvisor().toString());
            }
        }

        return ResponseEntity.ok(authResponse.build());


    }
}