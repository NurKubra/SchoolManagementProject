package com.schoolmanagement.payload.request;

import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import com.schoolmanagement.payload.response.abstracts.BaseUserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AdminRequest extends BaseUserRequest {


    private boolean builtIn; //Built_in Admini runner kisminda atayacagiz, service katinda kodunu yazacgiz true ya da false gelme

}
