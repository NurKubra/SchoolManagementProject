package com.schoolmanagement.security.service;

import com.schoolmanagement.entity.concretes.user.*;
import com.schoolmanagement.repository.user.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service //SpringSecurty Service katmanim !
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {  //kendi userimin username'i vercem bana UserDeatils e cevirdigimiz Service clasimiz !!! donusumler burda yapiliyor

    //tum kullnicilarimin repositroy si uzerinden db de kontrol edicem
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final DeanRepository deanRepository;
    private final ViceDeanRepository viceDeanRepository;
    private final StudentRepository studentRepository;


    @Override
    @Transactional   //rollback mekanizmasi icin yani herhangi bir problem cikarsa yani exception --> o zamn guvenlik noktasian donmek icin
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  //bu methodu override ettik

        //ortak bir repository olmadigindan tek tek gitmtmiz lazim
        Admin admin = adminRepository.findByUsernameEquals(username);
        Teacher teacher = teacherRepository.findByUsernameEquals(username);
        Dean dean = deanRepository.findByUsernameEquals(username);
        ViceDean viceDean = viceDeanRepository.findByUsernameEquals(username);
        Student student = studentRepository.findByUsernameEquals(username);

        if(student!=null){  //student ise securtynin anlacagci dilde verileri atadik,
            return new UserDetailsImpl(   //userDetails de olusturudgumuz parametreli const cagriyoruz
                    student.getId(),
                    student.getUsername(),
                    student.getName(),
                    false,
                    student.getPassword(),
                    student.getUserRole().getRoleType().name()
            );
        }else if(teacher!= null){
            return new UserDetailsImpl(
                    teacher.getId(),
                    teacher.getUsername(),
                    teacher.getName(),
                    false,
                    teacher.getPassword(),
                    teacher.getUserRole().getRoleType().name()
            );
        } else if (admin!=null) {
            return new UserDetailsImpl(
                    admin.getId(),
                    admin.getUsername(),
                    admin.getName(),
                    false,
                    admin.getPassword(),
                    admin.getUserRole().getRoleType().name()
            );
        }else if ( dean != null) {
            return new UserDetailsImpl(
                    dean.getId(),
                    dean.getUsername(),
                    dean.getName(),
                    false,
                    dean.getPassword(),
                    dean.getUserRole().getRoleType().name()
            );
        } else if ( viceDean != null) {
            return new UserDetailsImpl(
                    viceDean.getId(),
                    viceDean.getUsername(),
                    viceDean.getName(),
                    false,
                    viceDean.getPassword(),
                    viceDean.getUserRole().getRoleType().name()
            );

        }

        throw new UsernameNotFoundException("User " + username + " 'n't found");

    }
}
