package johyoju04.springsecurityjwt.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import johyoju04.springsecurityjwt.config.common.Constants;
import johyoju04.springsecurityjwt.security.AccessTokenAuthentication;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static johyoju04.springsecurityjwt.config.common.Constants.*;

//필터 제일 앞에 위치하게 한다.
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
@Component
public class ApiLogFilter extends OncePerRequestFilter {

    private final static List<Pattern> reqParamPwPatterns = Arrays.asList(
            Pattern.compile("(?<=\\\"" + PASSWORD + "\\\":\\\")[\\S]+(?=\\\"\\,)"),
            Pattern.compile("(?<=\\\"" + PASSWORD + "\\\":\\\")[\\S]+(?=\\\"\\})")
    );

    private final ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MDC.clear();

        final LocalDateTime startDatetime = ZonedDateTime.now().withZoneSameInstant(koreaZoneId).toLocalDateTime();
        ApiLog.ApiLogBuilder apiLogBuilder = ApiLog.builder()
                .time(startDatetime);

        final String threadId = UUID.randomUUID().toString();
        MDC.put(MDC_KEY_THREAD_ID, threadId);
        apiLogBuilder.threadId(threadId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth instanceof AccessTokenAuthentication token) {
            MDC.put(MDC_KEY_USER_SEQ, token.getUserId().toString());
        }

        ContentCachingRequestWrapper wrappedRequest = request instanceof ContentCachingRequestWrapper
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);

        //html 같은 파일 요청도 있을 경우 - swagger도 있다.
//        boolean cantWrap = !request.getRequestURI().startsWith("/api");

        ContentCachingResponseWrapper wrappedResponse = response instanceof ContentCachingResponseWrapper
                ? (ContentCachingResponseWrapper) response
                : new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            final LocalDateTime endDatetime = ZonedDateTime.now().withZoneSameInstant(koreaZoneId).toLocalDateTime();
            final long durationMillis = ChronoUnit.MILLIS.between(startDatetime, endDatetime);
            apiLogBuilder.duration(durationMillis);

            setRequestLog(wrappedRequest, apiLogBuilder);
            setResponseLog(wrappedResponse, apiLogBuilder);

            try {
                wrappedResponse.copyBodyToResponse();
            } catch (IOException ignored) {
            }

            try {
                log.info(objectMapper.writeValueAsString(apiLogBuilder.build()));
            } catch (Exception ignored) {
            }

        }
    }

    private void setRequestLog(ContentCachingRequestWrapper wrappedRequest, ApiLog.ApiLogBuilder apiLogBuilder) {
        String userIp = wrappedRequest.getHeader("X-Forwarded-For");
        if (userIp == null)
            userIp = wrappedRequest.getRemoteAddr();

        apiLogBuilder
                .userIp(userIp)
                .userAgent(wrappedRequest.getHeader(HttpHeaders.USER_AGENT))
                .method(wrappedRequest.getMethod())
                .path(wrappedRequest.getRequestURI())
                .patternPath((String) wrappedRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));

        HttpMethod method = HttpMethod.valueOf(wrappedRequest.getMethod());

        if (method == HttpMethod.GET) {
            apiLogBuilder.reqParam(wrappedRequest.getQueryString());
        } else if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            String reqParam = getMessagePayload(wrappedRequest);
            reqParam = hidePw(reqParam);
            apiLogBuilder.reqParam(reqParam);
        }
    }

    private void setResponseLog(ContentCachingResponseWrapper wrappedResponse, ApiLog.ApiLogBuilder apiLogBuilder) {

        apiLogBuilder.statusCode(wrappedResponse.getStatus());

        if (HttpStatus.valueOf(wrappedResponse.getStatus()).isError()) {
            apiLogBuilder.resBody(getMessagePayload(wrappedResponse));
        }

        apiLogBuilder.userSeq(
                Optional.ofNullable(MDC.get(Constants.MDC_KEY_USER_SEQ))
                        .filter(v -> StringUtils.hasLength(v))
                        .map(Long::parseLong)
                        .orElse(null));
    }

    private String getMessagePayload(ContentCachingRequestWrapper wrappedRequest) {
        byte[] buf = wrappedRequest.getContentAsByteArray();
        if (buf.length <= 0)
            return null;
        try {
            return tripJsonString(
                    new String(buf, 0, Math.min(buf.length, 1000), wrappedRequest.getCharacterEncoding())
            );
        } catch (UnsupportedEncodingException ex) {
            return "[unknown]";
        }
    }

    private String getMessagePayload(ContentCachingResponseWrapper wrappedResponse) {
        byte[] buf = wrappedResponse.getContentAsByteArray();
        if (buf.length <= 0)
            return null;
        try {
            return new String(buf, 0, Math.min(buf.length, 1000), wrappedResponse.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
            return "[unknown]";
        }
    }

    private String tripJsonString(String jsonString) {
        StringBuilder sb = new StringBuilder(jsonString);

        boolean prevBackslash = false;
        boolean openDoubleQuote = false;
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (openDoubleQuote) {
                if (!prevBackslash && ch == '"')
                    openDoubleQuote = false;
            } else if (ch == '"') {
                openDoubleQuote = true;
            } else if (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r') {
                sb.deleteCharAt(i--);
            }

            prevBackslash = ch == '\\';
        }

        return sb.toString();
    }

    private String hidePw(String reqParam) {
        if (!StringUtils.hasLength(reqParam)) {
            return reqParam;
        }
        if (!(reqParam.contains("\"" + PASSWORD + "\""))) {
            return reqParam;
        }

        for (final Pattern reqParamPwPattern : reqParamPwPatterns) {
            Matcher matcher = reqParamPwPattern.matcher(reqParam);
            if (!matcher.find()) {
                continue;
            }
            String pw = matcher.group();
            StringBuilder starPw = new StringBuilder();
            for (int i = 0; i < pw.length(); i++) {
                starPw.append('*');
            }
            return reqParam.substring(0, matcher.start()) + starPw + reqParam.substring(matcher.end());
        }

        return reqParam;
    }

    @Builder
    @Getter
    static class ApiLog {
        //빌더 패턴을 통해 인스턴스를 만들 때 특정 필드를 특정 값으로 초기화
        @Builder.Default
        private final String logType = "API";
        private final LocalDateTime time;
        private final String threadId;
        private final Long duration;
        private final String userIp;
        private final String userAgent;
        private final Long userSeq;
        private final String method;
        private final String path;
        private final String patternPath;
        private final String reqParam;
        private final Integer statusCode;
        private final String resBody;
    }
}
