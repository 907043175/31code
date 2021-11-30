package com.code31.common.baseservice.utils;

import com.code31.common.baseservice.common.response.MsgResponse;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BeanValidators {
    private static Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

    public static <T> List<String> validate(T t) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            messageList.add(constraintViolation.getMessage());
        }
        return messageList;
    }


    public static  <T> boolean  validate(T t, MsgResponse msg) {
        List<String>errorList = validate(t);
        if (errorList == null || errorList.size() < 1)
            return true;

        if (msg != null){
            msg.setCode(-1l);
            msg.setMsg(errorList.get(0));
        }

        return false;
    }


}
