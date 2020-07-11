package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalheTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	TopicoRepository topicoRepository;
	
	@Autowired
	CursoRepository cursoRepository;
	
	@GetMapping
	@Cacheable("listaDeTopicos") //listaDeTopicos é o id do cache
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso,
			//@RequestParam int pagina, @RequestParam int qtd, @RequestParam String ordenacao
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao
			){ //@PageableDefault deixa ordenação e paginação padrão caso nenhuma seja enviada como parâmetro
		
		//RequestParam não obrigatóri colocar. Mas deixa explícito que o parâmetro vem da URL. Por padrão
		//o parâmetro se torna obrigatório. Se for opcional usar required = false
		//Pageable paginacao = PageRequest.of(pagina, qtd, Direction.DESC, ordenacao);
		//Linhas substituídas pela paginação e ordenação automáticas Pageable
		
		Page<Topico> topicos;
		if(nomeCurso == null) {
			topicos = topicoRepository.findAll(paginacao);
		}
		else {
			topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
		}
		return TopicoDto.converter(topicos);
	}
	
	@PostMapping
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //limpa o cache ao finalizar o método
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		//@valid para fazer a validação dos valores recebidos
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);
		
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheTopicoDto> detalhar(@PathVariable Long id) { //@PathVariable para ler parâmetro do corpo e não na URL
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			return ResponseEntity.ok(new DetalheTopicoDto(optional.get())); //get para pegar o Topico que está dentro de Optional
		}
		return ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{id}")
	@Transactional //para que o spring comit no final do método
	@CacheEvict(value = "listaDeTopicos", allEntries = true) 
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form){
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true) 
	public ResponseEntity<?> remover (@PathVariable Long id){
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build(); //corpo vazio
		}
		
		return ResponseEntity.notFound().build();
	}
}

