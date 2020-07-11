package br.com.alura.forum;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.alura.forum.model.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSpringDataWebSupport
//@EnableSpringDataWebSupport habilta paginação e ordenacação automáticas
@EnableCaching
@EnableSwagger2
public class ForumApplication {
	
	@Autowired 
	private UsuarioRepository usuarioRepository;
	
	@PostConstruct
	public void init(){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = "123456";
		String encodedPassword = passwordEncoder.encode(password);

		System.out.println();
		System.out.println("Password is         : " + password);
		System.out.println("Encoded Password is : " + encodedPassword);
		System.out.println();
		
		//boolean isPasswordMatch = passwordEncoder.matches(password, encodedPassword);
		List<Usuario> usuarios = usuarioRepository.findAllByEmail("aluno@email.com");
		
		usuarios.forEach(usuario -> {
			usuarioRepository.delete(usuario);
		});
		
		
		
		Usuario usuario = new Usuario();
		usuario.setNome("Aluno");
		usuario.setEmail("aluno@email.com");
		usuario.setSenha(encodedPassword);
		usuarioRepository.save(usuario);
		
	}

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}
	

}
