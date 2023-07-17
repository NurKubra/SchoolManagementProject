package com.schoolmanagement.payload.messages;

public class ErrorMessage { //Application'inin herhangi bir yerinde kullancagimiz mesajlari yazacagiz

    //parametresiz constr. parametresini private yaparsam hic bir yerden new'leyemem --> bu sayede hic bir yerden degistrilmez
    //bu classdan bir instance olusmasini engelledik
    private ErrorMessage() {
    }

    public static final String ALREADY_SEND_A_MESSAGE_TODAY= "Error: You have already send a message with the e-mail";


}
