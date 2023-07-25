package com.schoolmanagement.service.helper;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PageableHelper {
    public Pageable getPageableWithProperties(int page, int size, String sort, String type){
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return pageable;
    }

    public Pageable getPageableWithProperties(int page, int size) {
        return PageRequest.of(page, size, Sort.by("id").descending());
    }
    //pageRequest clasinin of methodunu cagriyor bize pageable bir
    //client dan ya da frontend ksimindan sort ve sirlaam kismini secme sansi vermeden direk girdik
    //frontend tarfin siralama tercihini sunuyoruz , asagida direk veriyoruz. Iki method da ayni isi yapiyor
}
