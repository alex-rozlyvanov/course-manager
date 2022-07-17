package com.goals.course.manager.service.validation;

//@Getter
//@AllArgsConstructor
public record ValidationResult(boolean isValid, String message) {
    private static final ValidationResult VALID = new ValidationResult(true, null);

    public static ValidationResult valid() {
        return VALID;
    }

    public static ValidationResult invalid(final String message) {
        return new ValidationResult(false, message);
    }

    public boolean isNotValid() {
        return !isValid;
    }
}
