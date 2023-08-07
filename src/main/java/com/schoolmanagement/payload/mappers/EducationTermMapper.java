package com.schoolmanagement.payload.mappers;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.payload.response.EducationTermResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EducationTermMapper {

// not : bu class da @Builder(toBuilder=true ) yu burda kullancagiz --> yani burdaki ilk methodu klonlayip asagida kullandik
// Builder ile constructor'a sonradan parametre eklemeyi saglar ve new'lemeye gerek kalmaz!! -- toBuilder= true ile atanan class static gibi davranir
// !!! DTO --> POJO
public EducationTerm mapEducationTermRequestToEducationTerm(EducationTermRequest educationTermRequest) {
    return EducationTerm.builder()
            .term(educationTermRequest.getTerm())
            .startDate(educationTermRequest.getStartDate())
            .endDate(educationTermRequest.getEndDate())
            .lastRegistrationDate(educationTermRequest.getLastRegistrationDate())
            .build();
}

    // !!! POJO --> DTO
    public EducationTermResponse mapEducationTermToEducationTermResponse(EducationTerm educationTerm){
        return EducationTermResponse.builder()
                .id(educationTerm.getId())
                .term(educationTerm.getTerm())
                .startDate(educationTerm.getStartDate())
                .endDate(educationTerm.getEndDate())
                .lastRegistrationDate(educationTerm.getLastRegistrationDate())
                .build();
    }

    // !!! Update icin DTO --> POJO
    public EducationTerm mapEducationTermRequestToUpdatedEducationTerm(Long id, EducationTermRequest educationTermRequest){
        return mapEducationTermRequestToEducationTerm(educationTermRequest)
                .toBuilder()  //yukardaki objeyi degil onun fieldleriyla ayni olan objeyi klonlayacgim
                .id(id)       //bu kullanidigm objeye id yi ekledim !
                .build();
    }

}
