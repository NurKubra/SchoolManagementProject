package com.schoolmanagement;

import com.schoolmanagement.entity.enums.Gender;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.service.user.AdminService;
import com.schoolmanagement.service.user.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class SchoolManagementApplication implements CommandLineRunner  {  //run methodu override ediliyor, application calismadan bu method calissin
	private final UserRoleService userRoleService;
	private final AdminService adminService;

	//@RequiredArgsConstructor yerine Paramtreli constructor kullandik !! bu sekilde injektion yaptik
	public SchoolManagementApplication(UserRoleService userRoleService, AdminService adminService) {
		this.userRoleService = userRoleService;
		this.adminService = adminService;
	}


	public static void main(String[] args) {
		SpringApplication.run(SchoolManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//!!! Role tablosunu dolduralim
		//UserRole clasina ihtiyacim var ve UserRoleService clasina ihtiyacim var
		//Role tablom bosmu diye kontrol etmem lazim
		if(userRoleService.getAllUserRole().isEmpty()) {
			userRoleService.save(RoleType.ADMIN);  //admin varmi diye db de bakti yoksa kaydetti
			userRoleService.save(RoleType.MANAGER);
			userRoleService.save(RoleType.ASSISTANT_MANAGER);
			userRoleService.save(RoleType.TEACHER);
			userRoleService.save(RoleType.ADVISORY_TEACHER);
			userRoleService.save(RoleType.STUDENT);
			userRoleService.save(RoleType.GUEST_USER);

		}
		 //!! BuiltIn Admin olusturuyoruz
		if(adminService.countAllAdmins()==0) {  //aktif olan kac admin var diye sayar once
			AdminRequest adminRequest = new AdminRequest();
			adminRequest.setUsername("Admin");
			adminRequest.setSsn("111-11-1111");
			adminRequest.setPassword("12345678");  //burda encode etmeye gerek yok asaidaki saveAdmin() methodu icinde encode edilcek
			adminRequest.setName("Ahmet");
			adminRequest.setSurname("soyad");
			adminRequest.setPhoneNumber("111-111-1111");
			adminRequest.setGender(Gender.MALE);
			adminRequest.setBirthDay(LocalDate.of(1980,2,2));
			adminRequest.setBirthPlace("Texas");
			adminService.saveAdmin(adminRequest);
		}
		}


	}




	//@componentScan -->bu package'in altindaki tum classlardaki Bean'leri tarar

