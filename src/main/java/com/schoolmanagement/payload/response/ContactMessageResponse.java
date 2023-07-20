package com.schoolmanagement.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageResponse implements Serializable {  //bu class serilestirilebilir --> yani agda bir uctan baska uca paketlenmis sekilde tasinabilir

    //@Entity yoksa dto dur. Bu class DTO clasim.
    //db den gelen response client a gider bu class ile
    //db ye giderken coktan validation lardan gectigi icin burda gerek yok
    //burda id yazmadik ama response olarak id lazimsa Objenin kendisinden id'yi alip kullaniciya gosterir.

    private String name;  //contactMessageName
    private String email;
    private String subject;
    private String message;
    private LocalDate date;
}
