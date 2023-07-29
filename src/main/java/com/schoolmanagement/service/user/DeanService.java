package com.schoolmanagement.service.user;

import com.schoolmanagement.entity.concretes.user.Dean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.mappers.DeanMapper;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.user.DeanRepository;
import com.schoolmanagement.service.helper.PageableHelper;
import com.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanService {


    private final DeanRepository deanRepository;
    private final UniquePropertyValidator uniquePropertyValidator;  //unique mi diye kontrol etmemi sagliyor
    private final DeanMapper deanMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;


    // not:save() *********************************************************************
    public ResponseMessage<DeanResponse> save(DeanRequest deanRequest) {
        //!!Dublicate Kontrolü  --username, ssn, phonenumber
        uniquePropertyValidator.checkDuplicate(deanRequest.getUsername(),deanRequest.getSsn(),deanRequest.getPhoneNumber());

        //!!DTO --> POJO (pojoya donusturunce role ü extra atamam lazim, ayni zamanda password encode edilmesi lazim)
        Dean dean = deanMapper.mapDeanRequestToDean(deanRequest);
        dean.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        dean.setPassword(passwordEncoder.encode(dean.getPassword()));

        Dean savedDean = deanRepository.save(dean);

        return  ResponseMessage.<DeanResponse>builder()
                .message(SuccessMessages.DEAN_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .object(deanMapper.mapDeanToDeanResponse(savedDean))
                .build();


    }
    // not: updateById() ****************************************************************
    public ResponseMessage<DeanResponse> update(DeanRequest deanRequest, Long deanId) {
        //!! id kontrolu (ya yoksa)
        Dean dean = isDeanExist(deanId);

        //!!dublicate kontrolü --> burda checkDuplicate gereksiz kontrollere gircegi icin gelistirilmesi lazim(bu degerlerin diger katmanlarda olup olmaidgini kontrol ediyorum )
               //uniquePropertyValidator.checkDuplicate(deanRequest.getUsername(),deanRequest.getPhoneNumber(),deanRequest.getSsn()); yerine aktuel methodumu yazdim
               //uzerinde degisiklik yapmadigim fieldlerin dublicate kontrolü performans kaybina neden olur, bu yuzden yeni bir method yazdik
        uniquePropertyValidator.checkUniqueProperties(dean,deanRequest);

        //!! DTO --> POJO guncellenen yeni bilgileri dean objesine kaydetme  (requestle gelen verilerin olup olmaidgini kontrol ediyorum)
              //bizim var olan donuusm methodumuzda id yok, ama bizim requestimizde id var bu yuzden yeni bir method yaziyoruz
        Dean updatedDean = deanMapper.mapDeanRequestToUpdatedDean(deanRequest,deanId);

        //!! password encode
        updatedDean.setPassword(passwordEncoder.encode(deanRequest.getPassword()));

        //!! degisiklik yapilmis hali kaydetme (encode edilmis)
        Dean savedDean = deanRepository.save(updatedDean);

        return ResponseMessage.<DeanResponse>builder()  //genric yapida oldugu icin bu sekilde yapiyoruz
                .message(SuccessMessages.DEAN_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(deanMapper.mapDeanToDeanResponse(savedDean))  //updatedDean gonderirsek encode olmamis password gider + pojo--> dto cevirdik!!
                .build();
    }
    //id yi kontrol ettigim method !!
    private Dean isDeanExist(Long deanId){
        return deanRepository.findById(deanId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,deanId)));
        //eger aradigimiz id li kullanici yoksa ResourceNotFoundException firlattir!!

    }
    //Optional yapilar ya ici dolu mu diye kontrol edilir ya da orElseThrow ile kontrol edilir (Dean döndürür)
    //orElseThrow -_> yoksa exception firlat varsa gonder
    //basina throw yazmadan new yazip exeption'imizi yaziyoruz





    //not : Delete() ****************************************************************
    public ResponseMessage<?> deleteDeanById(Long deanId) {
       //!! id var mi kontrol
        isDeanExist(deanId);

        deanRepository.deleteById(deanId);

        return ResponseMessage.builder()
                .message(SuccessMessages.DEAN_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();     //objeyi vermedim --> Json icinde null yazmayacak null olmasina ragmen ama ResponseMessage da NON_NULL Kullanamk fayda sagladi

    }

    // Not :  getById() **************************************************************
    public ResponseMessage<DeanResponse> getDeanById(Long deanId) {
        // Dean dean = isDeanExist(deanId);    //varsa idli dean getir yoksa exception
        return ResponseMessage.<DeanResponse>builder()
                .message(SuccessMessages.DEAN_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(deanMapper.mapDeanToDeanResponse(isDeanExist(deanId)))  //pojo yu alip response cevircek "isDeanExist(deanId)" kalibi direk verdik geriye pojo dean objesini dondugunu bildigim icin
                .build();

    }

    // Not :  getAll() ***************************************************************
    public List<DeanResponse> getAllDeans() {
        return deanRepository.findAll() // List<Dean> ;db de tum deanler geliyor collection yapida
                .stream()  // Stream<Dean> ; akis degisti collection lardan Stream e dondu
                .map(deanMapper::mapDeanToDeanResponse) // Stream<DeanResponse>  ; pojo --> dto yukardan gelen her dean akis olara burda yazdigim methodun parametresien yaziliyor
                .collect(Collectors.toList());          //yapiyi liste cevirdik tekrardan
    }


    // Not :  getAllDeansByPage() ****************************************************
    public Page<DeanResponse> getAllDeansByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type); //pagebaleHepler injekte ettim

        return deanRepository.findAll(pageable).map(deanMapper::mapDeanToDeanResponse); //findAll() pageable bir obje aliyor ve map ile pojo --> dto donusturduk --> burdaki map() streamin degil

    }
}
