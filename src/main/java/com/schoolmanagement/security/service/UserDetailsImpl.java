package com.schoolmanagement.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
//security'nin anlayacagi UserDetails clasi oldugunu belirttik
//bu methodlari true ya cevirecegiz
//GrantedAuthority --> kullanici rollerinin Security dilindeki hali


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {  //Security nin User classi, kendi user'imizi burda tanimliyoruz

    //bu USER db ye gitmiyor ama db den gelen User'i buna cevircegimiz icin tum field'leri yazmamiz lazim , db ye gitmedigi icin
    //Validation yapmaya gerek yok
    private Long id;

    private String username;

    private String name;

    private Boolean isAdvisor;  //

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;  //role icin

    //Bu classin const parametre olarak kendi User'imizin fieldlerini verecegiz--> bu sekilde dönüsüm saglanamis olcak
    public UserDetailsImpl(Long id, String username, String name, Boolean isAdvisor, String password, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.isAdvisor = isAdvisor;
        this.password = password;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();  //ici bos objem olustu--> icine GrantedAuthorty Lerimi ekleyecgim
        grantedAuthorities.add(new SimpleGrantedAuthority(role));  //GrantedAuthority turunde rollerim var artik (burasi aslinda paramtreli cont.)
        this.authorities= grantedAuthorities;
    }

    //not GrantedAuthority interface oldugu icin ondan tureyen SimpleGrantedAuthority kullandik.
    //GrantedAuthority kullanmamizin nedeni --> soyutlama(designPattern)
    //SpringBoot kullanimin arka planinda bircok designPattern kullanilmis ve kodlar en efektif sekilde dizayn edilmis

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {  //db deki password u nasil yazdiysak onu tanimlamamiz lazim
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {  //bir kullanicini hesabinin suresi dolmus mu dolmamis mi kontrol eder
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {  //herhangi bir kullanici kitlenmis mi, db de bir unlock diye tanimli bir tablo var mi
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { //validation sureleri ile ilgili
        return true;
    }

    @Override
    public boolean isEnabled() {      //kullanici aktif mi
        return true;
    }

    //elimde iki kullanici var onlar birbirine esit mi dgil mi diye kontrol etmek istersem
    public boolean equals(Object o){  //herhangi bir obje gelcek yani kullanici bu gelen nesneyi paramtredeki nesneyle karsilastir
        if(this==o){                 //this dedigim bi methodu cagiran nesneyi temsil eder
            return true;
        }
        //sinif turu ile karsilastirma
        if(o==null || getClass()!=o.getClass()){  //iki class ayni mi ? diye baakrim --> getClass() clasin turunu doner(object clasini extends eden classlarin sahip oldugu classlar bu methidu cagirabilir)
            return false;
        }
        //id ile karsilastirma
        UserDetailsImpl user = (UserDetailsImpl) o;  //iki clasin id sine de baakriz,
        return Objects.equals(id, user.getId());     //birinci id, ile user.getId() bu methodu cagiran clasin id si karsilastirilcak

    }

}
