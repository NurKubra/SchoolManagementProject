package com.schoolmanagement.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)   //yani null olmayan, girilmeyen degerler null oldugu icin onlari kullaniciya donemyecek
public class AuthResponse {   //login islemi sonrasi authenticate edilmis kullanicinin dönen bilgileri

    private String username;
    private String ssn;
    private String role;
    private String token;
    private String name;
    private String isAdvisory;

}