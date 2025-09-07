package org.teya.ledgerservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
    private Set<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnum annotation) {
        acceptedValues = new HashSet<>();
        for (Enum<?> e : annotation.enumClass().getEnumConstants()) {
            acceptedValues.add(e.name());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // @NotBlank handles
        return acceptedValues.contains(value.toUpperCase(Locale.ROOT));
    }
}

