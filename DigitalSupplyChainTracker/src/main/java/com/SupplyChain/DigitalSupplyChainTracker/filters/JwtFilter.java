package com.SupplyChain.DigitalSupplyChainTracker.filters;

import com.SupplyChain.DigitalSupplyChainTracker.Utils.JwtUtils;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

private final JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwtToken;
        final String userEmail;

        //1. Look for the "Authorization" header
        String authHeader = request.getHeader("Authorization");

        //2. Validate the header(Invalid)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //3. Valid = extract details
        jwtToken = authHeader.substring(7);


        userEmail = jwtUtils.extractUsername(jwtToken);
        //4. Found Email & User not already logged in
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            //Validate JWT token
            if(!jwtUtils.isValidToken(jwtToken, userDetails)) {
                filterChain.doFilter(request, response);
                return;
            }

            //Create the authentication token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(userDetails);

            // 8. CRUCIAL STEP: Place the token into the SecurityContextHolder.
            // This marks the user as "Logged In" / "Active" for the rest of this specific request execution.
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);

    }
}
