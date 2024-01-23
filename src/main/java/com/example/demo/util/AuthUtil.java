package com.example.demo.util;

import com.example.demo.exception.ForbiddenException;

public class AuthUtil {
  private AuthUtil() {}

  public static void validate(String obj1, String obj2) {
    if(obj1 == null || !obj1.equals(obj2)) {
      throw new ForbiddenException();
    }
  }

}
