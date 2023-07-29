package com.schoolmanagement.service.helper;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class PageableHelper {

    //Asgidaki Iki method da ayni isi yapiyor !!!
    public Pageable getPageableWithProperties(int page, int size, String sort, String type){
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return pageable;
    }

    //ikinci methodda sadece zortunlu paramtreliri aldim, iki methodun da ismi ayni
    public Pageable getPageableWithProperties(int page, int size) {
        return PageRequest.of(page, size, Sort.by("id").descending());
    }
    //pageRequest clasinin of methodunu cagriyor bize pageable bir nesne donuyor
    //client dan ya da frontend ksimindan sort ve sirlaam kismini secme sansi vermeden direk girdik
    //frontend tarafin siralama tercihini sunuyoruz , asagida direk veriyoruz.
}
