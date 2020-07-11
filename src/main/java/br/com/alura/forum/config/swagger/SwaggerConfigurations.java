package br.com.alura.forum.config.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.alura.forum.model.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurations {

	@Bean
	public Docket forumApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.alura.forum")) //a partir de que pacore pode ler
				.paths(PathSelectors.ant("/**")) //quais paths podem ser lidos
				.build()
				//ignora nos endpoints os parâmetros relacinados à classe usuario pra não expor login, senha e perfis de acesso,
				.ignoredParameterTypes(Usuario.class)
				.globalOperationParameters(Arrays.asList( 
						//adiciona parâmetro global (todos endpoits)
						//com isso adicionamos o campo para passar senha no Swagger
						new ParameterBuilder()
						.name("Authorization")
						.description("Header para token JWT")
						.modelRef(new ModelRef("string"))
						.parameterType("header")
						.required(false)
						.build()));
	}

}
