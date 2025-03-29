package com.sujal.projects.airBnbApp.security;


import com.sujal.projects.airBnbApp.entity.User;

import com.sujal.projects.airBnbApp.repository.SessionRepository;
import com.sujal.projects.airBnbApp.service.UserService;
import com.sujal.projects.airBnbApp.service.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;
    @Autowired
    private SessionRepository sessionRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = requestTokenHeader.split("Bearer ")[1];
            Long userId = jwtService.getUserIdFromToken(token);
//            SessionEntity sessionInDatabase = sessionRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in session with id:" + userId));
//            String DatabaseToken = sessionInDatabase.getToken();
            //Added to check if session is valid
//            if (!token.equals(DatabaseToken)) {
//
//                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Session invalid,please login again");
//            }
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) { //set userId only if authentication user is not in authentication else we will not store it again
                User user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)  // will store some additional info like IP ADDRESS
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response); //to forward the control now to next filter in filterchain
        }
//        catch (ResponseStatusException ex) {
//            // Handle specific 401 Unauthorized cases
//            response.setStatus(ex.getStatusCode().value());
//            response.getWriter().write(ex.getReason());
//            response.getWriter().flush();
//        }
        catch(Exception e){
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }

}
