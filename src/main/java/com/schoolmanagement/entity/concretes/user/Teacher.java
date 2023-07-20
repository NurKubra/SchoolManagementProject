package com.schoolmanagement.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.schoolmanagement.entity.abstracts.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
//equals ve hashcode methodlarini otomatik olarak olusmasini sagladim
//eger bu entity clasini baska birinden uretmissem ust siniflarin da hash code'unu hesapliyor
//iki teacher'i kiyaslayacagim zaman hashcode'lari hazir oldugu icin kiyaslama daha hizli gercklesir.
//parent ve child da da null olan fieldlarin hashcode unu hesaplamaz -->onlyExplicitlyIncluded = true sayesinde
public class Teacher extends User {

    //bir ogretmen bir rehber ogretmeni olur,
    @OneToOne(mappedBy = "teacher",cascade = CascadeType.ALL,orphanRemoval = true) // ?
    @JsonIgnore  //donguyu kirmak icin kullaniriz !!
    private AdvisoryTeacher advisoryTeacher;


    private Boolean isAdvisor; // bir ogretmnein advisor olup olmaidgini kontrol etmek icin
}
