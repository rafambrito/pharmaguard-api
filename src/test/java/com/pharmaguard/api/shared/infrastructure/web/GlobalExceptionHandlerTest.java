package com.pharmaguard.api.shared.infrastructure.web;

import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ProblemDetail;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource());

    private static ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    static class ValidationTarget {
        @SuppressWarnings("unused")
        void handle(String body) {
        }
    }

    @Test
    void shouldHandleBusinessExceptionAsConflict() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test/business");

        ProblemDetail problem = handler.handleBusinessException(
                new BusinessException("Conflito de regra de negócio"),
                request
        );

        assertThat(problem.getStatus()).isEqualTo(409);
        assertThat(problem.getTitle()).isEqualTo("Conflict");
        assertThat(problem.getDetail()).isEqualTo("Conflito de regra de negócio");
        assertThat(problem.getProperties()).containsKey("timestamp");
        assertThat(problem.getProperties()).containsEntry("path", "/api/test/business");
    }

    @Test
    void shouldHandleResourceNotFoundAsNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test/not-found");

        ProblemDetail problem = handler.handleNotFoundException(
                new ResourceNotFoundException("Recurso não encontrado"),
                request
        );

        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getTitle()).isEqualTo("Not Found");
        assertThat(problem.getDetail()).isEqualTo("Recurso não encontrado");
        assertThat(problem.getProperties()).containsEntry("path", "/api/test/not-found");
    }

    @Test
    void shouldHandleValidationErrorsAsBadRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test/validation");

        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "obj");
        bindingResult.addError(new FieldError("obj", "nome", "não deve estar em branco"));

        Method method = ValidationTarget.class.getDeclaredMethod("handle", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ProblemDetail problem = handler.handleMethodArgumentNotValidException(exception, request);

        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getDetail()).isEqualTo("Requisicao invalida. Verifique os campos informados.");
        assertThat(problem.getProperties()).containsEntry("path", "/api/test/validation");

        @SuppressWarnings("unchecked")
        List<Map<String, String>> errors = (List<Map<String, String>>) problem.getProperties().get("errors");
        assertThat(errors).containsExactly(Map.of(
            "field", "nome",
            "code", "não deve estar em branco",
            "message", "não deve estar em branco"));
    }

    @Test
    void shouldHandleConstraintViolationAsBadRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test/constraint");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("filtro.dataInicial");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("deve ser maior que a data final");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ProblemDetail problem = handler.handleConstraintViolationException(exception, request);

        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getTitle()).isEqualTo("Bad Request");
        assertThat(problem.getDetail()).isEqualTo("Requisicao invalida. Verifique os parametros informados.");

        @SuppressWarnings("unchecked")
        List<Map<String, String>> errors = (List<Map<String, String>>) problem.getProperties().get("errors");
        assertThat(errors).containsExactly(Map.of(
            "field", "filtro.dataInicial",
            "code", "deve ser maior que a data final",
            "message", "deve ser maior que a data final"));
    }

    @Test
    void shouldHandleUnexpectedExceptionAsInternalServerErrorWithoutInternalDetails() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test/unexpected");

        ProblemDetail problem = handler.handleUnexpectedException(
                new RuntimeException("stack trace interno não deve ser exposto"),
                request
        );

        assertThat(problem.getStatus()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getDetail()).isEqualTo("Ocorreu um erro inesperado.");
        assertThat(problem.getDetail()).doesNotContain("stack trace interno");
        assertThat(problem.getProperties()).containsEntry("path", "/api/test/unexpected");
    }
}
