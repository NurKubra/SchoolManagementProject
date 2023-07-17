package com.schoolmanagement.contactmessage;

import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/contactMessages")    //gelen requesti alltaki class ile mapple endpointin cevabi bu classta
                                       //dispatcher servlet, handler mapping sayesinde bu annotaion'i görüyor ve gelen requestin responce'nun bu classta oldg biliyor.
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    //endpoint ve Mapping dogru mu cok önemli !!
    //not:save() ***********************************************************************
    @PostMapping("/save")  //http://localhost:8080/contactMessages/save + POST
    public ResponseMessage<ContactMessageResponse> save(@RequestBody
                                                        @Valid ContactMessageRequest contactMessageRequest){  //DTO Objemi kullaniyorum

        return contactMessageService.save(contactMessageRequest);

    }
    //http status kodunu setleyebilmek icin ResponceEntity clasini kullaniyoruz!
    //Burda ResponseEntity<String> kullanmayacagiz --> payload adinda bir package actik ve icine Responce package icinde sadece
    //responce lar ile ilgili methodlar var
}
