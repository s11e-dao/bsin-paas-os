package org.apache.shenyu.exception;

import com.alibaba.dubbo.rpc.service.GenericException;
import com.google.common.collect.Sets;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import org.apache.shenyu.common.exception.ShenyuException;
import org.apache.shenyu.handler.WebFluxHttpServletRequestBodyHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * 网关统一异常处理
 *
 * @author HLW
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    private static final String BUSINESS_EXCEPTION_CLASS = BusinessException.class.getName();

    private static final Set<String> VALIDATION_EXCEPTION_CLASSES = Sets.newHashSet(
            ValidationException.class.getName(),
            ConstraintViolationException.class.getName()
    );


    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NotNull Throwable ex) {
        LOG.error("handle error: {} formatError:{} throwable:{}", exchange.getLogPrefix(), formatError(ex, exchange.getRequest()), ex);
        ServerHttpResponse response = exchange.getResponse();
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        int code = Integer.parseInt(ResponseCode.FAIL.getCode());
        String msg = ex.getMessage();
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            msg = responseStatusException.getMessage();
        } else if (ex instanceof ShenyuException && ex.getCause() instanceof GenericException) {
            GenericException genericException = (GenericException) ex.getCause();
            if (genericException.getExceptionMessage().contains(BUSINESS_EXCEPTION_CLASS) || genericException.getExceptionClass().contains(BUSINESS_EXCEPTION_CLASS)) {
                //业务异常
                BusinessException businessException = convertToBusinessException(ex);
                msg = businessException.getMessage();
                code = Integer.parseInt(businessException.getCode());
            } else if (VALIDATION_EXCEPTION_CLASSES.contains(genericException.getExceptionClass())) {
                //校验异常
                msg = convertToValidatorMessage(ex);
            } else {
                msg = genericException.getExceptionMessage();
            }
        }
        return WebFluxHttpServletRequestBodyHandler.webFluxResponseWriter(response, msg, code);
    }


    /**
     * 转换成业务异常对象
     *
     * @param ex 异常
     * @return 业务异常
     */
    private BusinessException convertToBusinessException(Throwable ex) {

        int index = ex.getMessage().indexOf("BusinessException(");
        // 提取括号内的内容
        String content = ex.getMessage().substring(index, ex.getMessage().indexOf(")", index) + 1);
        String[] parts = content.split(",");
        String codePart = parts[0].trim();
        String messagePart = content.substring(content.indexOf("message="), content.indexOf(")"));
        // 提取code和message的值
        int code = Integer.parseInt(codePart.split("=")[1]);
        String msg = messagePart.split("=")[1].replaceAll("\\)+$", "");
        return new BusinessException(String.valueOf(code), msg);
    }


    private String convertToValidatorMessage(Throwable ex) {
        String exceptionMessage = ((GenericException) ex.getCause()).getExceptionMessage();
        String[] pairs = exceptionMessage.split(",");
        StringBuilder errMsg = new StringBuilder();
        for (int i = 0; i < pairs.length; i++) {
            String[] parts = pairs[i].trim().split(":");
            String value = parts.length > 1 ? parts[1].trim() : parts[0].trim();
            errMsg.append(value).append(i < pairs.length - 1 ? "，" : "");
        }
        return errMsg.toString();
    }

    /**
     * log error info.
     *
     * @param throwable the throwable
     * @param request   the request
     */
    private String formatError(final Throwable throwable, final ServerHttpRequest request) {
        String reason = throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
        return "Resolved [" + reason + "] for HTTP " + request.getMethod() + " " + request.getPath();
    }
}
