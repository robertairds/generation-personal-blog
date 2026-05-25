package com.generation.blogpessoal.controller; 

import java.util.List; // 
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

// @ são anotações, elas dão superpoderes para as linhas que vêm logo abaixo delas.

@RestController // avisa que essa classe é o recepcionista 
@RequestMapping("/postagens") // define que se alguém gritar "postagens", o recepcionista atende
@CrossOrigin(origins = "*", allowedHeaders = "*") // deixa quaquer aplicativo (tv, celular, frontend) se conectar aqui.
public class PostagemController {

    @Autowired // conecta o robô (postagemRepository) na mesa do recepcionista. 
    private PostagemRepository postagemRepository;
    
    @Autowired
    private TemaRepository temaRepository;
    
    @GetMapping // o cliente diz "Quero ver a lista"
    public ResponseEntity<List<Postagem>> getAll(){ //  
    	return ResponseEntity.ok(postagemRepository.findAll()); // o atendente manda o robô mostrar tudo
    }                      // coloca tudo num pacote com o selo "deu bom (ok - 200)" e entrega pro cliente
    
    @GetMapping ("/{id}") // cliente pede: "quero X número X"
    public ResponseEntity<Postagem> getById (@PathVariable Long id) {
    	return postagemRepository.findById(id) // o robô vai procurar na gaveta numero X
    			.map(resposta -> ResponseEntity.ok(resposta)) // se achar, bota o selo "OK"
    			.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // se estiver vazia, devolve "não encontrado (404 Not Found)
    }
    
    @GetMapping("/titulo/{titulo}") 
    public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
    	return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); // o atendente manda o robô "Busque qualquer X que tenha esse texto no nome, não importa se o cliente escreveu com letra maiúscula ou minúscula
    }
    
    @PostMapping // comando para cadastrar
    public ResponseEntity<Postagem> post (@Valid @RequestBody Postagem postagem){ // o cliente entrega uma ficha preenchida com os dados o X novo
    	
    	if (temaRepository.existsById(postagem.getTema(). getId())) {
    		
    		postagem.setId(null);
    		
    		return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
    	}
    	
    	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
    }
    
    @PutMapping // comando para modificar
    public ResponseEntity<Postagem> put (@Valid @RequestBody Postagem postagem){
    	if (postagemRepository.existsById(postagem.getId())) {
    		
    		if (temaRepository.existsById(postagem.getTema().getId()))
    			return ResponseEntity.status(HttpStatus.OK)
    					.body(postagemRepository.save(postagem));
    		
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
    	}
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}") // comando para banir o X número X do catálogo
    public void delete (@PathVariable Long id) {
    	Optional<Postagem> postagem = postagemRepository.findById(id);
    	
    	if(postagem.isEmpty()) // o atendente olhada para a prateleira. Se o X não estiver lá, ele avisa "Não posso deletar o que não existe (404)"
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND); // o sistema responde com o código 204 "Sumido com sucesso"
    	
    	postagemRepository.deleteById(id); // se o X existir, o robô joga ele no lixo
    }
}