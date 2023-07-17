package com.schoolmanagement.contactmessage;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity  //Annotaion'i unique bir id ister!
@Data  //@Getter, @Setter, @toString, @EqualsAndHashCode (her nesnenin benzersiz bir HashCode'unu hesapliyor,
//                                                        iki clasii kiyaslar ve icerigindeki kombinasyona gore bir hashcode atar,
//                                                        iki classda da ayniysa hersey HashCode'lari ayni olarak atar)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)  //Builder DesignPattern'i kullanir.//bir classdan obje olusturudugmuzda extra set yapmaktan kurtarir?
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //yetkiyi tamamen db ye biraktim, bir data silindiginde o id yi bir daha vermez
    private Long id; //contactMessageId

    @NotNull
    private String name; //contactMessageName

    @NotNull
    private String email;

    @NotNull
    private String subject;

    @NotNull
    private String message;

    //2025-06-05
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")  //tarih formatinin String formata donmesini saglar
    private LocalDate date;


}
