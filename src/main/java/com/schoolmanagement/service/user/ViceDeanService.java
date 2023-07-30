
package com.schoolmanagement.service.user;

import com.schoolmanagement.entity.concretes.user.ViceDean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.mappers.ViceDeanMapper;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.ViceDeanResponse;
import com.schoolmanagement.repository.user.ViceDeanRepository;
import com.schoolmanagement.service.helper.PageableHelper;
import com.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViceDeanService {


    private final ViceDeanRepository viceDeanRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final ViceDeanMapper viceDeanMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final PageableHelper pageableHelper;


    // Not :  Save() *************************************************************************
    public ResponseMessage<ViceDeanResponse> saveViceDean(ViceDeanRequest viceDeanRequest) {
        // !!! Dublicate  --> emailsiz
        uniquePropertyValidator.checkDuplicate(viceDeanRequest.getUsername(), viceDeanRequest.getSsn(), viceDeanRequest.getPhoneNumber());
        // !!! DTO --> POJO  --> viceDeanMapper
        ViceDean viceDean = viceDeanMapper.mapViceDeanRequestToViceDean(viceDeanRequest);
        // !!! role ve password.encode  --> gelen request de role yok
        viceDean.setPassword(passwordEncoder.encode(viceDeanRequest.getPassword()));
        viceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));

        ViceDean savedViceDean = viceDeanRepository.save(viceDean);    //artik elimde bir pojo var
        return ResponseMessage.<ViceDeanResponse>builder()
                .message(SuccessMessages.VICE_DEAN_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .object(viceDeanMapper.mapViceDeanToViceDeanResponse(savedViceDean)) //pojo --> dto
                .build();

    }


    // Not :  UpdateById() ********************************************************************
    public ResponseMessage<ViceDeanResponse> updateViceDean(ViceDeanRequest viceDeanRequest, Long viceDeanId) {
        // !!! var mi yok mu kontrolu
        Optional<ViceDean> viceDean = isViceDeanExist(viceDeanId);  //asagidaki methoddan donen pojom objem --> viceDean
        // !!! unique mi ??
        uniquePropertyValidator.checkUniqueProperties(viceDean.get(), viceDeanRequest);
        // !!! DTO --> POJO      //not role'u Mapper da ekledik !
        ViceDean updatedViceDean = viceDeanMapper.mapViceDeanRequestToUpdatedViceDean(viceDeanRequest, viceDeanId);
        // !!! Password.encode
        updatedViceDean.setPassword(passwordEncoder.encode(viceDeanRequest.getPassword()));

        ViceDean savedViceDean = viceDeanRepository.save(updatedViceDean);

        return ResponseMessage.<ViceDeanResponse>builder()
                .message(SuccessMessages.VICE_DEAN_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(viceDeanMapper.mapViceDeanToViceDeanResponse(savedViceDean))
                .build();


    }

    //id li var mi diye kontrol ??  --> farkli olarak optional yapida yazdik
    private Optional<ViceDean> isViceDeanExist(Long viceDeanId){
        Optional<ViceDean> viceDean = viceDeanRepository.findById(viceDeanId);  //optional ise --> viceDean null olma ihtimali var !!
        // null olma ihtimalini kontrol ediyorum !! bossa exception firlatcam
        if(! viceDeanRepository.findById(viceDeanId).isPresent()) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, viceDeanId));
        } else {
            return viceDean;  //Pojom optional seklinde doner !!
        }
    }

    //isPresent() ici dolu mu diye bakar


    // Not :  Delete() *************************************************************************
    public ResponseMessage deleteViceDeanByUserId(Long viceDeanId) {

        isViceDeanExist(viceDeanId);
        viceDeanRepository.deleteById(viceDeanId);

        return ResponseMessage.builder()      //generic yapiyi kullanamyacaiz o yuzden direk setleme islemi yaptik
                .message(SuccessMessages.VICE_DEAN_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }
        //not --> bu methoda isExistId yi kontrol etmeden sistemde kayitli olmayan bir idli kisiyi silmek istedgimizde 500 yani server
        // hatasi aldik --> ama bu yaniltici bir hata--> bu yuzden isViceDeanExisst ekledik !!

    // Not :  getById() ************************************************************************
    public ResponseMessage<ViceDeanResponse> getViceDeanByViceDeanId(Long viceDeanId) {

        return ResponseMessage.<ViceDeanResponse>builder()
                .message(SuccessMessages.VICE_DEAN_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(viceDeanMapper.mapViceDeanToViceDeanResponse(isViceDeanExist(viceDeanId).get()))
                .build();


        //bu methodda geriye bir dto objesi dondurmem lazim bunu direk object icinde yapmak istiyorum
        //pojo yu dto ya cevirebilmk icin elimde bulunan methodu cagriyorum
        // bu method icinde oncelikle bu id ye shaip kullanicim var mi diye kontrol etmem lazim
        // isExist ile kontrol ediyorum fakat isExist bana Opzional bir veri donurdugu icin get() methodunu cagiriyorum
        //bu sayede Optional<viceDean> icindeki pojo objesine ulasbailirm !!
    }


    // Not :  getAll() *************************************************************************
    public List<ViceDeanResponse> getAllViceDeans() {

        return viceDeanRepository.findAll() // List<ViceDean> (pojo donuyor bu method)
                .stream() // Stream<ViceDean>
                .map(viceDeanMapper::mapViceDeanToViceDeanResponse) // Stream<ViceDeanResponse> (streamden donen pojolari dto ya cevirdik ama hala stream halde )
                .collect(Collectors.toList()); // List<ViceDeanResponse>  (streami List e ceviridk)
    }


    // Not :  getAllWithPage() ******************************************************************
    public Page<ViceDeanResponse> getAllViceDeanByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);

        return viceDeanRepository.findAll(pageable).map(viceDeanMapper::mapViceDeanToViceDeanResponse);
        //pagebale yapiyi map ile pojo --> dto ya cevirdik
    }




}