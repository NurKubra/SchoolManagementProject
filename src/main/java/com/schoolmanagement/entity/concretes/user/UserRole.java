package com.schoolmanagement.entity.concretes.user;

import com.schoolmanagement.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Data  //
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {  //enumlarla aradaki ilskiyi saglayack concret class!!!



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //RoleType enum'indan rolleri aldik
    @Enumerated(EnumType.STRING) //enumlari string yap
    @Column(length = 20)
    private RoleType roleType;



}
