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


    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {

        //!!! request icinden username ve password aliniyor (String olarak aldik )
        String username =loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // !!! AuthenticationManager uzerinden kullaniciyi valide ediyoruz (username ve passwordu valide ediyor, direk bakamiyor, cunku authentication sinifi turunde bir nesneye cevirmemiz lazim )
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        //UsernamePasswordAuthenticationToken icine username ve password girilerek authentication nesnesi döndürülür,
        // yani authentication olan nesneye ulasmami saglar --> cunku icinde UserDetails var
        //böyle kullanici yoksa exception firlatir


        // !!! Valide edilen kullanici user context e gonderiliyor (valide edilen kullanici objesi gecici olarak tutulmak icin)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // !!! JWT token olusturuluyor (valide edildikten sonra client tarafina gondermek icin jwt token create ediyorum)
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);
        //jwtUtils injekte ettik, generateJwtToken() methoduyla jwtToken olusturuyoruz

        // !!! response icindeki fieldlar dolduruluyor
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //authentication clasinin getPrincipal() methoduyal userDetail den aliyorum ama GrandtedAutherity olarak aldigimiz iicn userDetails e cevirdik
        //GrantedAutherity tipinde olan rolleri yani colletioni Set yapisi icine atiyorum
        Set<String> roles  = userDetails.getAuthorities() //birden fazla rol icerme ihtimali old. icn ama benim projemde tek bir rol icerdigini biliyorum onetoone dan bu yuzden ilk rolu alyiorum
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();
        //yukardaki cogul olan yapiyi tek bir yapiya cevirdik

        // !!! Response nesnesi olusturuluyor (UserDetails den cogu fielda ulasabilrim ama bazi )
        //  builder'i farkli yoldan kullanma
        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();  //ara bir class olusturup icine atyioruz
        authResponse.username(userDetails.getUsername());
        authResponse.token(token.substring(7));
        authResponse.name(userDetails.getName());

        // !!! role bilgisi setleniyor
        if(role.isPresent()){
            authResponse.role(role.get());  //optional bir yapi oldugu icin get ile aliriz
            if(role.get().equalsIgnoreCase(RoleType.TEACHER.name())) {            //isAdvisor kontrolü
                authResponse.isAdvisory(userDetails.getIsAdvisor().toString());  //authResponse da String seklinde yazdigimiz iicn
            }
        }
        return ResponseEntity.ok(authResponse.build());

        //not : AuthResponse nesnemin icindeki fieldlari dolduruken -->
        // username'e requesten ulasabilirz, ssn contexe attigimiz UserDetails uzerinden, role e da UserDetails uzerinden
        // ulasabilirz ama rollerimiz grantedAutorty turunde cevirmemiz lazim, tokeni yeni elde ettik, name 'e UserDetails uzerinden
        // ulasailirz ikisinde de string, isAdvisory da UserDetails de var ama boolen halde --> onu da Stringe cveirmeiz lazim
    }
}