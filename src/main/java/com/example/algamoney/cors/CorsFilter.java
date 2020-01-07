package com.example.algamoney.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
	private String origenPermitida = "http://localhost:8000";// TODO configurar para diferentes ambientes

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		// tem que enviar em todas as requisicoes por isso e antes do if
		// cors so adiciona os headers http
		response.setHeader("Acess-Control-Allow-Origins", origenPermitida);
		response.setHeader("Acess-Control-Allow-Credentials", "true");

		if ("OPTIONS".equals(request.getMethod()) && origenPermitida.equals(request.getHeader("Origin"))) {
			response.setHeader("Acess-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			response.setHeader("Acess-Control-Allow-Headers", "Authorization,Content-Type, Accept");
			response.setHeader("Acess-Control-Allow-Max-Age", "3600");

			response.setStatus(HttpServletResponse.SC_OK);

		} else {
			chain.doFilter(req, resp);
		}

	}

}
