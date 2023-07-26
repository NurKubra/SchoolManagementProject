package com.schoolmanagement.security.jwt;

import com.schoolmanagement.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

//not : jwttoken --> header + payload(userla ilgili bilgiler) + signature(secret ile imza)

@Component
public class JwtUtils { //jwt ile ilgili islemlerin yapilcagi class

   private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class); //JwtUtils de kullanilmak icin bir Log objesi olustur
                                                                                  //loglama islemi icin bu objeyi kullncam

   //applicationin genelinde kullanilcaksa -->  application.propertiesde yaziyorum
    @Value("${backendapi.app.jwtExpirationMs}") //jwt token kullanim suresi, 1 gunluk milisaniye suresi girdik
    private long jwtExpirationMs;

    @Value("${backendapi.app.jwtSecret}")     //
    private String jwtSecret;
    //ilk login islemimde bir kere Jwt Token methodum calisir, expresiion suresine kadar calismaz,
    // sure dolunca yeni bir jwt token olusturmak icin yeniden calisir



    //!!! Not: Generate JWT **************************************************
    //JwtTokeni generate etmek icin methodum, jwt Token i username ile olustucagiz
    // Authnetication uzerinden login olmus yani authenticate olmus user'a ulasmayai saglar
    //object doncegi icin datatype ini UserDetailsImpl e cevirdik
    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return generateTokenFromUsername(userDetails.getUsername());

    }

    //yardimci method (sadece burda kullanilcak)  -- jwt token create eder.
    public String generateTokenFromUsername(String username){
        return Jwts.builder()           //jwt olusturucuyu saglar
                .setSubject(username)   //login olan user'in username
                .setIssuedAt(new Date()) // defaultda sytem.currentMillies() --> tokenin olusturuldugu milisaniye
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs)) //olustruldgu surenin ustune ne kadar sure gecerli olcagi
                .signWith(SignatureAlgorithm.HS512, jwtSecret)  //hashleme ile tek yonlu sifreleme,karsilastirmda kullanilir.
                .compact(); //ayarlari tamamlar, token olusturuluyor
    }


    //!!!Not: Validate JWT ************************************************** (JwtToken dogrulama islemi) --benim gonderdigim jwt token mi

    public boolean validateJwtToken(String jwtToken ){
        try {
            Jwts.parser()     //ayristir
                    .setSigningKey(jwtSecret)   //bu anahtar ile karsilastir
                    .parseClaimsJws(jwtToken);  //imzalar uyumlu ise JWT gecerli.
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("Jwt token is expired : {}", e.getMessage() );
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Jwt token is unsupported : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Jwt token is invalid : {}", e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("Jwt Signature is invalid : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Jwt is empty : {}", e.getMessage());
        }
        return false;
    }


    //secretkey jwtTokenin belli ksimlarini acmayi saglar
    //burda bir validation yapti ve bu sirada exception firlama ihtimali var
    //yanlis bir jwtToken, ya da suresi gecmis bir jwtToken oalilir bu gibi durumlarda oluscak exceptionlari engellemk icin
    //Error message larini loglarsam geri donup loglarin tutuldugu dosyadan bakabilirm.


    //!!!Not: getUserNameFromJWT ******************************************** (token icinde username'i gondercegiz, jwt token icinden username alma)
    //username jwt tokenin body kisminda, bu methodla yukarda verdigim username kismini alabilmis oluyorum
    public String getUserNameFromJwtToken(String token){   //usernam euzerinden user objeme ulasabilirim

        return Jwts.parser()  //ayristirci methodum
                .setSigningKey(jwtSecret) //Bu anahtari kullanark ayristir
                .parseClaimsJws(token) //tokenin dogrulanip icindeki claimsleri dondurur
                .getBody()         //claimsler icinden subjecti al ve geri doncur
                .getSubject();  //cunku subject icinden username i aliyorum
    }
}
