package com.swiftwheelshubreactive.lib.aspect;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.service.AuditLogProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "audit", name = "enabled")
@Slf4j
public class AuditAspect {

    private static final String X_USERNAME = "X-USERNAME";
    private final AuditLogProducerService auditLogProducerService;

    @Around("@annotation(LogActivity)")
    public Mono<?> logActivity(ProceedingJoinPoint joinPoint) {
        return getJoinPointProceed(joinPoint)
                .delayUntil(_ -> sendAuditLogInfoRequest(joinPoint));
    }

    private Mono<?> getJoinPointProceed(ProceedingJoinPoint joinPoint) {
        try {
            return (Mono<?>) joinPoint.proceed();
        } catch (Throwable e) {
            throw ExceptionUtil.handleException(e);
        }
    }

    private Mono<AuditLogInfoRequest> sendAuditLogInfoRequest(ProceedingJoinPoint joinPoint) {
        return extractUsernameHeaderFromWebFluxContext()
                .flatMap(username -> sendAuditLogInfoRequest(joinPoint, username))
                .onErrorMap(ExceptionUtil::handleException);
    }

    private Mono<String> extractUsernameHeaderFromWebFluxContext() {
        return Mono.deferContextual(contextView -> {
            ServerWebExchange exchange = contextView.get(ServerWebExchange.class);

            return extractUsernameHeaderFromRequest(exchange);
        });
    }

    private Mono<String> extractUsernameHeaderFromRequest(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest())
                .map(this::getUsernameHeader);
    }

    private String getUsernameHeader(ServerHttpRequest serverHttpRequest) {
        return Optional.ofNullable(serverHttpRequest.getHeaders().getFirst(X_USERNAME))
                .orElse(StringUtils.EMPTY);
    }

    private Mono<AuditLogInfoRequest> sendAuditLogInfoRequest(ProceedingJoinPoint joinPoint, String username) {
        AuditLogInfoRequest auditLogInfoRequest = getAuditLogInfoRequest(joinPoint, username);

        return auditLogProducerService.sendAuditLog(auditLogInfoRequest)
                .thenReturn(auditLogInfoRequest);
    }

    private AuditLogInfoRequest getAuditLogInfoRequest(ProceedingJoinPoint joinPoint, String username) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogActivity logActivity = method.getAnnotation(LogActivity.class);

        log.info("Method called: {}", signature);

        List<String> parametersValues = getParametersValues(joinPoint, logActivity, signature);

        return new AuditLogInfoRequest(method.getName(), username, LocalDateTime.now(), parametersValues);
    }

    private List<String> getParametersValues(ProceedingJoinPoint joinPoint, LogActivity logActivity,
                                             MethodSignature signature) {
        return Arrays.stream(logActivity.sentParameters())
                .map(parameter -> {
                    List<String> parameters = Arrays.asList(signature.getParameterNames());
                    int indexOfElement = parameters.indexOf(parameter);

                    if (indexOfElement < 0) {
                        return StringUtils.EMPTY;
                    }

                    return joinPoint.getArgs()[indexOfElement].toString();
                })
                .toList();
    }

}
