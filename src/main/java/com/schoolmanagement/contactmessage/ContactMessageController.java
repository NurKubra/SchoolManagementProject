package com.schoolmanagement.contactmessage;

import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequestMapping("/contactMessages")   //gelen requesti alltaki class ile mapple endpointin cevabi bu classta  //dispatcher servlet, handler mapping sayesinde bu annotaion'i görüyor ve gelen requestin responce'nun bu classta oldg biliyor
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;





    // not: save()************************************************************
    @PostMapping("/save")  // http://localhost:8080/contactMessages/save    + POST  //anoim bir kullanici da mesaj gonderebilsin diye sadece save yazdik
    public ResponseMessage<ContactMessageResponse> save(@RequestBody
                                                        @Valid ContactMessageRequest contactMessageRequest) {
        return contactMessageService.save(contactMessageRequest);
    }

    //iki ayri proje ayni endpoint ayni port ve ayni mapping methodulya calismaz!! Mutlaka biri farkli olmali
    //http status kodunu setleyebilmek icin ResponceEntity clasini kullaniyoruz!
    //Burda ResponseEntity<String> kullanmayacagiz --> payload adinda bir package actik ve icine Responce package icinde sadece
    //responce lar ile ilgili classlar var, ayni islemi request dto icin de yaptik.
    //ResponseEntity clasi yerine manuel olarak ResponseMessage() olusturduk.(Generic bir class)





    // not: getAll() ***********************************************************
    @GetMapping("/getAll") // http://localhost:8080/contactMessages/getAll + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")   //manager --> dean, assistant_manager --> vicedean
    public Page<ContactMessageResponse> getAll( // getAll(int page, int size, Direction type)
                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @RequestParam(value = "sort", defaultValue = "date") String sort,
                                                @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return contactMessageService.getAll(page,size,sort,type);
    }
    //default deger icin 0. yani ilk sayfayi getirsin(endpointde deger girmzsek default gelir)
    //String olarak yazdigimiz kisim "size" gibi endpointde yazilma seklidir
    //bu degerler @RequestParam ile gelcek, gelmezse default degerler gelir







    // not: searchByEmail() ****************************************************
    @GetMapping("/searchByEmail")  // http://localhost:8080/contactMessages/searchByEmail?email=xxx@yyy.com  +  GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')") //method calismadan once yetkilendirmesine bakiyor, rolüne (bu rollerden biriyse bu methodu calsitir)
    public Page<ContactMessageResponse> searchByEmail(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return contactMessageService.searchByEmail(email,page,size,sort,type);
    }






    // not: searchBySubject() **************************************************
    @GetMapping("/searchBySubject") // http://localhost:8080/contactMessages/searchBySubject?subject=deneme  + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<ContactMessageResponse> searchBySubject(
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return contactMessageService.searchBySubject(subject,page,size,sort,type);
    }

}
