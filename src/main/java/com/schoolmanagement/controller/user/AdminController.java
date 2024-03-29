package com.schoolmanagement.controller.user;


import com.schoolmanagement.entity.concretes.user.Admin;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.payload.response.AdminResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.user.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;



    //admin ile alakali isler admin ekle, admin sil, adminleri getir gibi islemleri yazacagiz
    //ama adminin yapabilcegi islemleri degil !!
    //admin ile ilgili islemleri sadeec admin yapabilir --> bu yuzden tek bir rol oldugu icin hasRole ile yapilabilir alternatif olarak
    //ilk admini runner da ekledik


    //not: save() ********************************************************
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<AdminResponse>> saveAdmin(@RequestBody @Valid
                                                                    AdminRequest adminRequest){
    return ResponseEntity.ok(adminService.saveAdmin(adminRequest));  //reponseEntity icinde ok methodu kullanmis oluyoruz

    }
    //ResponseEntity kullanmak zorunda degiliz
    //bana gelen requesti bu objeyle mappliyorum
    //bu methoda istedgim tum islemleri service katmanina gonderiyorum



    //Not: getAll() *****************************************************
    @GetMapping("/getAllAdminsByPage") // best practice , donen nesnelerin DTO olmasidir
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<Admin>> getAllAdminsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        Page<Admin> admins = adminService.getAllAdminsByPage(page, size, sort, type);
        return new ResponseEntity<>(admins, HttpStatus.OK);  //responseEntity objesi olusturduk,


    }


    //Not: delete() ********************************************************
    @DeleteMapping("/delete/{id}") // admins/delete/1    //admini bulabilcegim unique bir deger elimde olmasi lazimki dogru admini silebilebileyim
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteAdminById(@PathVariable Long id) { //bir tane veri alcaksam PathVariable ile alirim

        return ResponseEntity.ok(adminService.deleteAdminById(id));
    }
}
