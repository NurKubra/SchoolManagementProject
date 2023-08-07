package com.schoolmanagement.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageRequest implements Serializable {
    //bu bilgiler client dan aldigim icin Validation yapmaliyim!!
    //Client dan gelen requestlerin java formatina mapplendigi class

    @NotNull(message = "Please enter name")
    @Size(min=4,max=16, message = "Your name schould be at least 4 characters")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String name;  //contactMessageName


    @Email(message = "Please enter valid email")
    @Size(min=5,max=20, message = "Your email schould be at least 5 characters")
    @NotNull(message = "Please enter your email")
    private String email;


    @Size(min=4,max = 50, message = "Your subject should be at least 4 characters")
    @NotNull(message = "Please enter subject")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your subject must consist of the character .")
    private String subject;


    @Size(min=4,max = 50, message = "Your message should be at least 4 characters")
    @NotNull(message = "Please enter message")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String message;

}
/*
 "Serializable" arayüzünü bir sınıfa eklemek, nesnelerin durumlarını farklı veri biçimlerine dönüştürüp geri
 yükleyebilmenizi sağlayarak uygulama geliştirmeyi daha esnek ve verimli hale getirir. Ancak,
 sınıfların serileştirilebilir olması, uygun bir şekilde dikkate alınması
ve güvenliğin göz önünde bulundurulması gereken bazı önemli hususları beraberinde getirir.

regexp = "\\A(?!\\s*\\Z).+"-->
Bu ifade, metnin başlangıcında boşluk veya yeni satırların olmadığı ve en az bir karakter içerdiği durumları kabul eder.
 */