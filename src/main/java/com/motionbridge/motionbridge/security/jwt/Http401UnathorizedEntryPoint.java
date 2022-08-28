package com.motionbridge.motionbridge.security.jwt;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Component
@Slf4j
public class Http401UnathorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = "Access Denied";
        LocalDateTime now = LocalDateTime.now();

        UnathorizedResponse unathorizedResponse = new UnathorizedResponse(now.toString(), message, HttpStatus.UNAUTHORIZED);
        String newResponse = new Gson().toJson(unathorizedResponse);

        PrintWriter out = response.getWriter();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.write(newResponse);
        out.flush();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class UnathorizedResponse {
        private String dateTime;
        private String message;
        private HttpStatus status;
    }
}
