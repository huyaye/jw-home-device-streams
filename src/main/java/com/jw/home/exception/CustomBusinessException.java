package com.jw.home.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomBusinessException extends RuntimeException {
   protected Integer errorCode;

   protected String errorMessage;

   protected Object resultData;
}
