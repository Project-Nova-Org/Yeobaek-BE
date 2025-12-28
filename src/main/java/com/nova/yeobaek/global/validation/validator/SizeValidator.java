package com.nova.yeobaek.global.validation.validator;

import com.nova.yeobaek.global.validation.annotation.ValidSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SizeValidator implements ConstraintValidator<ValidSize, Integer> {

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
		return value != null && value > 0;
	}
}
