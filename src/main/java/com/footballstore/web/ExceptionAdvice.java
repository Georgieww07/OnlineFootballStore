package com.footballstore.web;

import com.footballstore.exception.DomainException;
import com.footballstore.exception.EmailAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public String handleEmailAlreadyExistException(RedirectAttributes redirectAttributes){

        redirectAttributes.addFlashAttribute("emailAlreadyExistMessage", "This email is already in use!");

        return "redirect:/register";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
            DomainException.class
    })
    public ModelAndView handleNotFoundExceptions(){

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyExceptions(Exception exception){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exceptionMessage", exception.getClass().getSimpleName());
        modelAndView.setViewName("internal-server-error");

        return modelAndView;
    }
}
