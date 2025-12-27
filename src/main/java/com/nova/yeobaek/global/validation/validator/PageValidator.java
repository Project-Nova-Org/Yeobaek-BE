package com.nova.yeobaek.global.validation.validator;

import com.nova.yeobaek.global.validation.annotation.ValidPage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageValidator implements ConstraintValidator<ValidPage, Integer> {

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
		return value != null && value >= 0;
	}
}
