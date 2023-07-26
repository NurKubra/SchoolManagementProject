package com.schoolmanagement.service.user;

import com.schoolmanagement.entity.concretes.user.Admin;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.mappers.AdminMapper;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.payload.response.AdminResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.user.AdminRepository;
import com.schoolmanagement.service.helper.PageableHelper;
import com.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {


    private final AdminRepository adminRepository;
    private final UniquePropertyValidator uniquePropertyValidator;           //injetion islemi yapti, bu class ve methodalrini bu katmanda kullancam
    private final AdminMapper adminMapper;                                   //dto -pojo donusumleri icin !!!
    private final UserRoleService userRoleService;
    private final PageableHelper pageableHelper;
    private final PasswordEncoder passwordEncoder;                           //password encode etmek icin
    public ResponseMessage<AdminResponse> saveAdmin(AdminRequest adminRequest) {  //dönen data typi ResponseMessage<AdminResponse> olarak degistiridk

        //AdminRequest de yani dto da username, ssn ve email unique olmali bu kural ayni zamanda tum kullanicilar icin gecerli
        //bu yuzden islem normal akisinda devam edip db ye gidip kontrol edersek exception alma ihtimalimiz var,
        //bunun yerine service katmaninda yazdigimiz method ile db ye gidip unique olma durumunu kontrol ediyoruz ve
        //db nin exception'i yerine kendi custom exception'imizi firlatiyoruz.

        //!!Girilen Username- ssn-phoneNumber unique mi kontrolu
        uniquePropertyValidator.checkDuplicate(adminRequest.getUsername(),adminRequest.getSsn(),
                adminRequest.getPhoneNumber());            //varrags oldugundan girme siram onemli !!, adminRequest deki parametrleri giriyorum kontrol icin


        //!!! dto --> POJO   --> bunun icin methodlarimi AdminMapper da yaptik

        Admin admin = adminMapper.mapAdminRequestToAdmin(adminRequest);    //pojo doncek ama bazi veriler eksik mesela built_in
        admin.setBuilt_in(false);  //diger adminler icin built_in false, default da false olsun ki her onune gelen user admin olmasin

        if(Objects.equals(adminRequest.getUsername(), "Admin")) {       //eger username "Admin" ise built_in ni true ya set et!!
            admin.setBuilt_in(true);
        }

        //bir admin yeni admin creat edebilir--> username'i Admin yazarak ilk defa varsa admin olarak atayabilir--> ama bunu istemiyoruz
        //degistirilebilir olsun istemiyorum bu yuzden runner kisminda bu durumu engelleyecegiz ve sadece bir adminin bunu degistirmesine izin vercez

        //db de roles tablosunda henuz roller yok --> bu yuzden uygulamyi calistirdgimiz anda role tablosunu doldur ve
        //built_in e admini ata.  //coreJava kisminda rollerim var ama db ksiminda atanmamis.
        // bu yuzden bu AdminRequest buyuk harfli Admin girildiginde built_in 'i true ya set et dedik.

        //!! Admin rolü veriliyor!!
        //rol tanimlamasi yapmak icin once db ye gidip kontrol etmem lazim bu rol db de var mi diye
        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));             //enumType da roller var ama UserRole clasi uzerinden atama yapmak

        //Password encode etme
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));  //adminRequest.getPassword() --> ayni sey, yani dto ya da pojo farketmez zaten mapleme islemi yappiyoruz

       Admin savedAdmin = adminRepository.save(admin);

       //ResponseMessage nesnesi olusturuluyor   --> basarili mesaji (ErrorMessages da olsuturudk) (Controller daki responseMessage  )
        return ResponseMessage.<AdminResponse>builder()
                .message(SuccessMessages.ADMIN_CREATE)
                .object(adminMapper.mapAdminToAdminResponse(savedAdmin))
                .build();

    }

    //Not: getAll() *****************************************************
    public Page<Admin> getAllAdminsByPage(int page, int size, String sort, String type) {
        Pageable pageable= pageableHelper.getPageableWithProperties(page,size,sort,type);
        return adminRepository.findAll(pageable);

    }


    //Not: delete() ********************************************************
    public String deleteAdminById(Long id) {
        //!!! id kontrol
        Optional<Admin> admin = adminRepository.findById(id); //optional yapida donmeye musaede eder, nullpoitexception alma diye

        //ama bu seferde optinal olanin ici bos mu diye bakmamiz lazim
        if(admin.isEmpty()){  //null sa exception firlat
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,id));
        } else if (admin.get().isBuilt_in()) {  //optional yapida calistigi icin normal bir nesneye ulasir gibi ulasmadik
            throw new ConflictException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }// built_in ile annnote edilen admin silinmemeli, bunu kontrol ettik

        adminRepository.deleteById(id);
        return  SuccessMessages.ADMIN_DELETE;


    }

    //not: RUNNER Tarafi icn yazdik --> aktif olan kac admin var diye sayar

    public long countAllAdmins(){
        return adminRepository.count();
    }
}
