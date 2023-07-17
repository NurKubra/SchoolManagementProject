package com.schoolmanagement.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)  //class seviyede null olmayanlari JSON icine alma, null olanlari alma
public class ResponseMessage<E> {  //generic data tipinde
    //custom bir ResponseEntity clasi olusturudm -->
    private E object;  //BAK !!
    private String message;
    private HttpStatus httpStatus;
}
