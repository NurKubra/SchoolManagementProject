package com.schoolmanagement.security.jwt;

import com.schoolmanagement.security.service.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class JwtUtils { //jwt ile ilgili islemlerin yapilcagi class

   private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class); //JwtUtils de kullanilmak icin bir Log objesi olustur
                                                                                 //loglama islemi icin bu objeyi kullncam

   //applicationin genelinde kullanilcaksa -->  application.propertiesde yaziyorum
    @Value("${backendapi.app.jwtExpirationMs}") //suraya git bana burdan bu degeri getir
    private long jwtExpirationMs;

    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret;

    //ilk login islemimde bir kere Jwt Token methodum calisir, expresiion suresine kadar calizmaz,
    // sure dolunca yeni bir jwt token olusturmak icin yeniden calisir

    //Not: Generate JWT **************************************************
    //JwtTokeni generate etmek icin methodum, jwt Token i username ile olustucagiz
    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userDetails.getUsername());

    }

    //yardimci method (sadece burda kullanilcak)
    public String generateTokenFromUsername(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    //Not: Validate JWT **************************************************

    //Not: getUserNameFromJWT ********************************************
    //securty contex den getirebilirim ya da


}
