package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

	@Value("${forum.jwt.expiration}")
	private String expiration; //injetar propriedade que esta no application.properties
	
	@Value("${forum.jwt.secret}")
	private String secret;
	
	public String gerarToken(Authentication authentication) {
		Usuario logado = (Usuario) authentication.getPrincipal();
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));
		
		return Jwts.builder() //biblioteca jjwt
				.setIssuer("API do Fórum da Alura") //quem está gerando o token
				.setSubject(logado.getId().toString()) //usuário que está logado
				.setIssuedAt(hoje) //data da geração do token
				.setExpiration(dataExpiracao)//quando haverá expiração do token
				.signWith(SignatureAlgorithm.HS256, secret)//criptografia (algoritmo, senha da aplicação gerada aleatoriamente)
				.compact(); //compactar e transformar tudo em string
	}

	public boolean isTokenValido(String token) {
		
		try {
			Jwts.parser() //faz o parse do token
			.setSigningKey(this.secret) //chave da aplicação para crptografar e descriptografar
			.parseClaimsJws(token); //passa o token
			
			return true;
		}
		catch(Exception ex) {
			return false; //se o token está inválido entra no catch
		}
		
	}

	public Long getIdUsuario(String token) {
		Claims claims = Jwts.parser() //faz o parse do token
		.setSigningKey(this.secret) //chave da aplicação para crptografar e descriptografar
		.parseClaimsJws(token).getBody(); //passa o token e retorna o body
		
		return Long.parseLong(claims.getSubject());
	}

}
