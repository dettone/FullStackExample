package com.example.algamoney.token;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
//filtro com prioridade muito alta
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenPreProcessorFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		// se for tudo isso ai so ver se e tudo certo

		/*
		 * Transformar o array de cookies em um Strem, com o comando Stream.of(...)
		 * Filtrar os dados do Stream para que retorne apenas o que tenha o nome
		 * refreshToken Obter o primeiro objeto do Stream (caso exista) Transformá-lo de
		 * cookie em uma String com o seu valor. Caso não tenha encontrado um cookie com
		 * o nome refreshToken, retorna null.
		 */
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
				&& "refresh_token".equals(req.getParameter("grant_type")) && req.getCookies() != null) {

			String refreshToken = Stream.of(req.getCookies()).filter(cookie -> "refreshToken".equals(cookie.getName()))
					.findFirst().map(cookie -> cookie.getValue()).orElse(null);
			// pq nao pode ser alterado mais
			req = new MyServletRequestWrapper(req, refreshToken);
		}
		//continuar a cadeia do filtro
		chain.doFilter(req, response);
	}

	public static class MyServletRequestWrapper extends HttpServletRequestWrapper {
		private String refreshToken;

		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			// parametro usado para pegar o refresh token -> o refreshToken e o valor
			// recuperado do cookie
			map.put("refresh_token", new String[] { refreshToken });
			map.setLocked(true);
			return map;
		}
	}

}
