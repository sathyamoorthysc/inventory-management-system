package com.cams.inventorymanagement.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationUtil {
  private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private static final Validator validator = factory.getValidator();

  private BeanValidationUtil() {}

  public static Validator getValidator() {
    return validator;
  }

  public static <T> Set<ConstraintViolation<T>> validateAndGetViolations(T object) {
    return validator.validate(object);
  }
}
