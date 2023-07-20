package com.schoolmanagement.entity.concretes.user;

import com.schoolmanagement.entity.abstracts.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder  //her fielddan bir constructor uretir
//karsilastirma yapilmayacaksa iki bean objesi birbirine esit mi diye bakcaksak @Data kullaniriz
public class Dean extends User {
//extra bir ozellik atamdik



}
