package com.schoolmanagement.security.jwt;


import com.schoolmanagement.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component //bu classi enjekte edebilmek icin
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {//jwt token ile bir filtre olusturmak istersek bu classi kullaniriz, filtre classi olabilmesi icin extends etmemiz lazim

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils; //class icindeki methodlara ihtiyacim var
    @Autowired
    private UserDetailsServiceImpl userDetailsService;  //useri userdetails e cevirmek icin

    //tokeni filtreleyecgiz, requestten tokeni alacagiz
    //token su sekilde olusturuluyor -->Bearer fsdadfhfgjg seklinde

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {  //gelen request ve response u fitlreye(FilterChain) dahil et demek !!

        try {
            // !!! requestin icinden JWT token cekiliyor(method asagida)
            String jwt = parseJwt(request);
            //!!! Validate JWT Token
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {  //gelen token null mi hem de ben mi urettim diye kontrol ediyorum (validateJwtToken methodu kontrol ediyor)
                // !!! username bilgisini JWT tokenden cekiyoruz
                String username = jwtUtils.getUserNameFromJwtToken(jwt); //gelen token bize ait olan bir kullaniciya ait oldugunu anlayinca gelen tokenin username Ini gecici olaral securty contex e obje olraka koyuyuorum (login olan userin oobje hali)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                request.setAttribute("username", username);
                // Security Context e authenticate edilen kullanici gonderiliyor
                //login olan user i securty contexte koymak icin authentication objesi olusturma
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,  //user
                        null,         //password userDetails icinde oldug iicn yazmadik
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //login olan useri securty contexte koyma
                SecurityContextHolder.getContext().setAuthentication(authentication);  //securtyContex in login olan objeyi tutmasini saglar, ama paarmtre olarak bir authenticate objesi girebiirz


            }
        } catch (UsernameNotFoundException e) {
            LOGGER.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);   //parametre ile gelen request ve response umu filtreye dahil et !!!!!
    }


    //!!! bana gelen requestin icinden jwt token cekiliyor

    private String parseJwt(HttpServletRequest request) {  //gelenRequest icinden aliyoruz

        String headerAuth = request.getHeader("Authorization");   //requestin headerin de bu baslik varsa
        //dogru jwtTokeni mi aldik kontrol etmek icin
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) { //headerAuth string bir ifade var mi ve bu sekilde "Bearer "basliyor mu
            return headerAuth.substring(7);
        }

        return null;
    } //token elde ettigim bu methodu artik yukarda kullanabilirm
}



     /* @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //filterChain hazir 16 tane filtrenin disinda yeni filtre eklemek istersek

        //!!! bana gelen requestin icinden jwt token cekiliyor
        String jwt = parseJwt(request);

        //!!! Validate JWT Token
        if(jwt != null && jwtUtils.validateJwtToken(jwt)){
            //!!! username bilgisini JWT tokenden cekiyoruz --> securityContex te tutmak icin
            String username = jwtUtils.getUserNameFromJwtToken(jwt); //securtyContexte username adinda olustudugum veride tutuyorum jwt tokenden aldigim username i
            UserDetails userDetails= userDetailsService.loadUserByUsername(username);  //username mimi UserDetails e ceviren method
                                                                                       //artik elimde valide edilmis username
            request.setAttribute("username",username); //requestin attributune username i ekledim
            //Security contexe authenticate edilen kullanici gönderiliyor
            UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );  //artik authentication turunde bir nesnem olustu
        }
    }*/