package main.camunda.response;

import lombok.*;

import java.io.Serializable;


@Data
public class Response{
    int statusCodeValue;
    String statusCode;
    String body;

    public Response(int statusCodeValue, String statusCode, String body){
        this.statusCode = statusCode;
        this.body = body;
        this.statusCodeValue = statusCodeValue;
    }
}
