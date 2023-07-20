package com.schoolmanagement.entity.abstracts;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.concretes.user.UserRole;
import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;


@MappedSuperclass //hibernate uzerinden takip edilir tablo olusturulmamasina ragmen--> bu user clasindan db de tablo olusturma, ama bu userclasinin extends ettigi classlardan tablo olustur
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
//admin user'in child'i ve hem parentdan hem de kendi sahip oldugu fieldlari iceren parametreli const olusturmayi saglar.
//bu yuzden hem parent hem de child classa @SuperBuilder yazilmali --> bu sayede olusturulcak parametreli const'da
//parent'in field'lari da olcak !! Child da yalnizca @Builder yazarsak sadece child'In sahip oldugu fieldlar const'a alinir

public abstract class User {  //entity olsun istemiyorum

        //entity clasi olmamasina ragmen @MappedSuperclass sayesinde tablodaki childlar icin id bu classdan alinir.
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private String username;

        @Column(unique = true)
        private String ssn;   //matematiksel islem de kullanamyacagiz bu yuzden String

        private String name;

        private String surname;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate birthDay;

        private String birthPlace;

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // hassas veri oldugu icin sadece yazarken bu veri kullanilabilsin, db den okuma islemlerinde kullanma!
        private String password;

        @Column(unique = true)
        private String phoneNumber;

        //role
        //degistirlmeyecek yapilar enum ile --> enum clasina direk ulasmak erismek yerine arada is yapan concret bir class ile yapilir
        //bir kullanici sadece bir role sahip olsun dedigimiz icin onetoone!
        //@MappedSuperclass ilk iki annotaion'i ile hibernate tarafindan okunmasi saglanir,@SuperBuilder ile de asagidaki fiedin okunmasi saglanir
        @OneToOne
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private UserRole userRole;

        // gender
        @Enumerated(EnumType.STRING)
        private Gender gender;

        //ÖNEMLI NOT:
        /*
        @ManyToOne ya da @OneToOne iliskim olmasina ragmen --> one olmaisna ragmen many atamasi yaparsak ne olur?
        Spring 3.0 a kadar bu duruma bu atamaya izin verir. Hibernate tablolar arasindaki ilsikiyi takip etmez ve izin verir. Biz bunu
        onlemek icin Uniqie gibi yani tek bir veri(one) atanabilsin diye ozellikler ekleyip kontrol altina aliyoruz. Ama Spring
        3.0 versiyondan itibaren bunu otomatik yapar ve birden fazla veri atamsina izin vermez --> Exception firlatir.
        --> bu ozellik sayesinde --> ogretmene ikinci bir ilski atayacagiz (rehber ogretmeni)(onetoone) olmaisna ragmen!!
        --> ozetle --> onetoone --> onetomany gibi davranir
         */
}
