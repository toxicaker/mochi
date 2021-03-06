package cn.toxicaker.api;

import cn.toxicaker.common.JsonResp;
import cn.toxicaker.common.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    final static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResp handleException(Exception e) {
        doLog(e);
        if (e instanceof MissingServletRequestParameterException || e instanceof MethodArgumentTypeMismatchException || e instanceof HttpRequestMethodNotSupportedException) {
            return new JsonResp(JsonResp.FAILURE, "parameter error: " + e.getMessage(), null);
        }
        return new JsonResp(JsonResp.FAILURE, e.getMessage(), null);
    }

    private void doLog(Exception ex) {
        if (ex instanceof ServiceException) {
            ServiceException e = (ServiceException) ex;
            logger.error("service exception: " + e.getMessage(), ex);
        } else if (ex instanceof MissingServletRequestParameterException ||
                ex instanceof MethodArgumentTypeMismatchException ||
                ex instanceof HttpRequestMethodNotSupportedException) {
            logger.warn("parameter error", ex);
        } else if (ex != null) {
            logger.error("unknown error", ex);
        }
    }
}
