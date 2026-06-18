package com.example.medlypharma.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("timestamp", LocalDateTime.now());
		errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
		errorResponse.put("error", "Validation Failed");

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		errorResponse.put("validationErrors", errors);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("error", "Page Not Found");
		mav.addObject("message", "The page you are looking for does not exist.");
		mav.addObject("status", 404);
		return mav;
	}

	@ExceptionHandler(Exception.class)
	public Object handleGlobalException(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception", ex);
		String requestUri = request.getRequestURI();
		if (requestUri != null && requestUri.startsWith("/api/")) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("timestamp", LocalDateTime.now());
			errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.put("error", "Internal Server Error");
			errorResponse.put("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("error", "Oops! Something went wrong");
			mav.addObject("message", "We're sorry, but something unexpected happened. Please try again later or contact support if the problem persists.");
			mav.addObject("status", 500);
			return mav;
		}
	}
}
