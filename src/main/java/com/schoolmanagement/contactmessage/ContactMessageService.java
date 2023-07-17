package com.schoolmanagement.contactmessage;

import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessage;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor  //final olan fieldalrdan constructor olusturcak
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
     /* @Autowired  --> final ve @RequiredArgsConstructor ile paramtreli constructor yazmamiza gerek kalmadi
       public ContactMessageService(ContactMessageRepository contactMessageRepository) {
           this.contactMessageRepository = contactMessageRepository;
       }*/



    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {

        //!!! 1 kullanici 1 gunde sadece 1 mesaj atabilsin email+date kontrol edecegiz

        //hazir methodlar benim sorunumu cozmuyor --> bu yuzden kendimiz yazacagiz sorgularimizi
        //contactMessageRequest bugünün tarihini ve db deki tarihi almali ve karsilastirmali

       boolean isSameMessageWithSameEmailForToday=
               contactMessageRepository.existByEmailEqualsAndDateEquals(contactMessageRequest.getEmail(), LocalDate.now());
                //existByEmailEqualsAndDateEquals --> JPA da bulunan method keyword'lerini birlestirip yazdigimizda otomatik bir method elde ettik

        if(isSameMessageWithSameEmailForToday){
            throw new ConflictException(ErrorMessage.ALREADY_SEND_A_MESSAGE_TODAY);
            // throw new ConflictException("1 gunde max bir adet mesaj gonderebilirisniz");  //bu kod clean degil !!
            // String yazma tercih edilmemeli --> cunku bu exception kullanilan her yerden bu kodla ilgili degisiklilgi manuel olarak degistirmek zorundayim
            //degistirme ihtimali olan kodlar HARDCODE olarak yazilamamali
        }
        //!!! DTO --> POJO
        //elimde bir dto var onu db ye kaydedebilmek icin pojo ya ceviriyorum
        ContactMessage contactMessage= createContactMessage(contactMessageRequest); //data unique degil cunku id si yok --db ye giden pojo
        ContactMessage savedData= contactMessageRepository.save(contactMessage);    //icinde id olan pojom               --db den gelen pojo

        return ResponseMessage.<ContactMessageResponse>builder()
                .message("Contact Message Created Succesfully")
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(savedData))
                .build();
    }

    // mapContactMessageRequestToContactMessage  (DTO yu POJO ya cevirir)
    private ContactMessage createContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder()
                .name(contactMessageRequest.getName())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .date(LocalDate.now())
                .build();
            //syntax'i builder() ile baslayip build() ile bitiriyoruz !!
    }
    //POJO yu DTO ya cevirir
    private ContactMessageResponse createResponse(ContactMessage contactMessage){

        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .date(LocalDate.now())
                .build();

    }


    }


