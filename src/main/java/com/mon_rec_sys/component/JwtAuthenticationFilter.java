package com.mon_rec_sys.component;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mon_rec_sys.service.impl.UserDetailsServiceImpl;
import com.mon_rec_sys.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private UserDetailsServiceImpl userService;

	private JwtUtils jwtUtils;

	public JwtAuthenticationFilter(UserDetailsServiceImpl userService, JwtUtils jwtUtils) {
		this.userService = userService;
		this.jwtUtils = jwtUtils;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");

		String userName = null;
		String token = null;

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			try {
				token = requestTokenHeader.substring(7);
				try {
					userName = this.jwtUtils.extractUsername(token);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				final UserDetails userDetails = this.userService.loadUserByUsername(userName);

				if (this.jwtUtils.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				} else {
					System.out.println("Token is not valid!!");
				}
			}
		} else {
			System.out.println("Invalid Token!!!");
		}
		filterChain.doFilter(request, response);
	}
}
