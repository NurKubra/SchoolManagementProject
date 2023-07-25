package com.schoolmanagement.contactmessage;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Objects;
@Service
@RequiredArgsConstructor
public class ContactMessageService {
    private final ContactMessageRepository contactMessageRepository;
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {
        // 1 kullanici 1 gunde sadece 1 mesaj atabilsin
        boolean isSameMessageWithSameEmailForToday =
                contactMessageRepository.existsByEmailEqualsAndDateEquals(contactMessageRequest.getEmail(), LocalDate.now());
        //bu mail ve tarih daha onceden db den var mi diye kontrol ediyoruz varsa kullanicinin yeniden mesaj yazmasina izin verme exception firlat
        if(isSameMessageWithSameEmailForToday){
            throw new ConflictException(ErrorMessages.ALREADY_SEND_A_MESSAGE_TODAY);
        }

        // !!! DTO --> POJO
        ContactMessage contactMessage = createContactMessage(contactMessageRequest);  //contactMessage tipinde bir obje elde ettik
        ContactMessage savedData = contactMessageRepository.save(contactMessage);    //sisteme kaydettigimiz gonderdigimiz pojo da id olmaidigi icin, kaydedilen pojoyu aliyorum db den. db ye gonderdigim pojoyu degil !!

        return ResponseMessage.<ContactMessageResponse>builder()
                .message("Contact Message Created Successfully")
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(savedData))
                .build();
    }

    // !!! mapContactMessageRequestToContactMessage (dto request --> pojo map'leme)
    private ContactMessage createContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder() //@Builder --> sayesinde builder() ile new'leme yapmadan set islemi yapabildik
                .name(contactMessageRequest.getName())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .date(LocalDate.now())
                .build();
    }
    // !!! mapContactMessageToContactMessageRequest  (pojo --> dto response map'leme)
    private ContactMessageResponse createResponse(ContactMessage contactMessage){
        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .date(LocalDate.now())
                .build();
    }


    // not: getAll() ***********************************************************
    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {
        // Pageable myPageable = PageRequest.of(page,size,Sort.by(type,sort) ;
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending()); //min 2,max 3 paramtre alir, default da date oldgu icin date'e gore siralar.
        if(Objects.equals(type,"desc")){                                       //type objemi de dahil etmek icin kontrolü burda yapiyorum
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return contactMessageRepository.findAll(pageable).map(this::createResponse);
    }
     //Page bir collection ise pageable da bu yapinin icini dolduran yardimci siniftir. -Atomik yapida calismak icin farkli siniflara gorev paylasimi yaptik
     //findAll() bana bir collection donuyor --> lambda da collection larla calisir
     //bizim createResponse() methodumuz tek bir obje icin ama bizim burda birden fazla objemiz oldugu icin methodu ard arda calistirir.Tum objeleri tek tek
     //dto ya cevirdikten sonra page yapida döner.




    // not: searchByEmail() ****************************************************
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return contactMessageRepository.findByEmailEquals(email, pageable).map(this::createResponse);
    }




    // not: searchBySubject() **************************************************
    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return contactMessageRepository.findBySubjectEquals(subject, pageable).map(this::createResponse);
    }
    // Odev : pageable yapilari helper mothod ile cagirilacak ( createPageableObject )
}











/*
package com.schoolmanagement.contactmessage;

import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessage;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor  //final olan fieldalrdan constructor olusturcak
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
     */
/* @Autowired  --> final ve @RequiredArgsConstructor ile paramtreli constructor yazmamiza gerek kalmadi
       public ContactMessageService(ContactMessageRepository contactMessageRepository) {
           this.contactMessageRepository = contactMessageRepository;
       }*//*




    //not:save() ***********************************************************************

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



    //not: getAll()***********************************************************************
    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {

        //Pageable myPageable= PageRequest.of(page,size,Sort.by(type,sort)); --alternatif pratik yol

       Pageable pageable= PageRequest.of(page,size, Sort.by(sort).ascending());  //Pageable bir obje olusturmam gerekiyor
        if(Objects.equals(type,"desc")){  //parametrede gelen String ifade type desc degilse ascendig olarak olustur.
            pageable= PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.findAll(pageable).map(this::createResponse);  //burda db den gelen pojo classlari methodum dto istedigi icin donusturmeliyiz
                //Pageable clas araciligiyla gelen veriler alindi ve page yapida bir data dondu
                //this::createResponse--> her ContactMessage nesnesini ContactMessageResponse nesnesine dönüştürmek için kullandik
    }



    // not: searchByEmail() **************************************************************
    //bu email adresiyle db deki tum verileri getir, ayni kisi birden fazla mesaj atmis olabilir--> bu yuzden sayfalrca mesaji varsa bu yuzden sayfa yapisi seklinde donduk
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {

        Pageable pageable= PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable= PageRequest.of(page,size,Sort.by(sort).descending());
        }
        return contactMessageRepository.findByEmailEquals(email,pageable).map(this::createResponse);
    }


    // not: searchBySubject() ************************************************************
    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return contactMessageRepository.findBySubjectEquals(subject, pageable).map(this::createResponse);
    }
        //ODEV --> createPageableObject --> pageable yapilari helper method ile cagrilcak
}


*/
