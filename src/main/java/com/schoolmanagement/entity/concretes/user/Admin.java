package com.schoolmanagement.entity.concretes.user;

import com.schoolmanagement.entity.abstracts.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Admin extends User {  //User abstract class dan extends ediyorum


    //bir objenin ya da field in built_in olmasi demek silinmez, degistirilemez olmasi demektir.

    private boolean built_in;
}
