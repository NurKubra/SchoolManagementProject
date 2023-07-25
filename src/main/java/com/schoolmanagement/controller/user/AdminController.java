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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;



    //admin ile alakali isler admin ekle, admin sil, adminleri getir gibi islemleri yazacagiz
    //ama adminin yapabilcegi islemleri degil !!

    //not: save() ********************************************************
    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<AdminResponse>> saveAdmin(@RequestBody @Valid         //ResponseEntity kullanmak zorunda degiliz
                                                                    AdminRequest adminRequest){ //bana gelen requesti bu objeyle mappliyorum

    return ResponseEntity.ok(adminService.saveAdmin(adminRequest));  //bu methoda istedgim tum islemleri service katmanina gonderiyorum

    }

    //Not: getAll() *****************************************************
    @GetMapping("/getAllAdminsByPage") // best practice , donen nesnelerin DTO olmasidir
    public ResponseEntity<Page<Admin>> getAllAdminsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        Page<Admin> admins = adminService.getAllAdminsByPage(page, size, sort, type);
        return new ResponseEntity<>(admins, HttpStatus.OK);  //responseEntity objesi olustrduk

    }

    //Not: delete() ********************************************************
    @DeleteMapping("/delete/{id}") // admins/delete/1
    public ResponseEntity<String> deleteAdminById(@PathVariable Long id) {

        return ResponseEntity.ok(adminService.deleteAdminById(id));
    }
}
