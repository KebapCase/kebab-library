package com.kebab.core.controller.advices

import com.kebab.core.exception.ApplicationInitializationException
import com.kebab.core.exception.IncorrectlyFormattedRequestException
import com.kebab.core.exception.MalformedRequestDataException
import com.kebab.core.exception.ModelValidationException
import com.kebab.core.exception.UnknownUrlException
import com.kebab.core.helper.ExceptionHandlingHelper.toResponse
import com.kebab.core.util.lazyLogger
import com.kebab.core.util.toJson
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity.unprocessableEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class ExceptionPostprocessor {

    private val log by lazyLogger()

    @ExceptionHandler(ModelValidationException::class)
    fun validationException(exception: ModelValidationException) = with(exception.body().toJson()) {
        log.error(this, exception)

        unprocessableEntity().contentType(MediaType.APPLICATION_JSON_UTF8).body(this)!!
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun entityNotFoundException(exception: Exception) =
            toResponse(exception, NOT_FOUND)

    @ExceptionHandler(UnknownUrlException::class)
    fun unknownUrlException(exception: Exception) =
            toResponse(exception, NOT_FOUND)

    @ExceptionHandler(MalformedRequestDataException::class)
    fun malformedRequestDataException(exception: Exception) =
            toResponse(exception, BAD_REQUEST)

    @ExceptionHandler(HttpMessageNotReadableException::class, MethodArgumentTypeMismatchException::class)
    fun httpMessageNotReadableException(exception: Exception) =
            toResponse(com.kebab.core.exception.MalformedRequestDataException(), BAD_REQUEST, com.kebab.core.exception.MalformedRequestDataException.REASON, exception.message)

    @ExceptionHandler(MultipartException::class, MissingServletRequestPartException::class, ServletRequestBindingException::class)
    fun handleMalformedRequestParametersException(exception: Exception) =
            toResponse(com.kebab.core.exception.IncorrectlyFormattedRequestException(), BAD_REQUEST, com.kebab.core.exception.IncorrectlyFormattedRequestException.REASON, exception.message)

    @ExceptionHandler(ApplicationInitializationException::class)
    fun applicationPartiallyInitializedException(exception: Exception) =
            toResponse(exception, INTERNAL_SERVER_ERROR)

    @ExceptionHandler(IncorrectlyFormattedRequestException::class)
    fun applicationIncorrectlyFormattedRequestException(exception: Exception) =
            toResponse(exception, BAD_REQUEST)

}
