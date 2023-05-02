package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@BeforeAll /* executa primeiro esse teste antes de todos os outros */
	void start() {

		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", "-"));
	}

	@Test
	@DisplayName("😎 Deve Cadastrar um novo Usuário")
	public void deveCriarUmUsuario() {

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Paulo Antunes", "paulo_antunes@email.com.br", "12345678", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("😎 Não Deve permitir a duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria", "maria@email.com.br", "12345678", "-"));

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Maria", "maria@email.com.br", "12345678", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("😎 Deve atualizar os dados do Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Juliana", "juliana@email.com.br", "12345678", "-"));

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(usuarioCadastrado.get().getId(),
				"Juliana Andrews", "juliana_andrews@email.com.br", "12345678", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😎 Deve listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Guilherme", "guilherme@email.com.br", "12345678", "-"));

		usuarioService.cadastrarUsuario(new Usuario(0L, "Jeniffer", "jeniffer@email.com.br", "12345678", "-"));

		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	/*desafio logar */

	@Test
	@DisplayName("😎 Deve iniciar sessão do usuário")
	public void deveAutenticarUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Ramona", "ramona@email.com.br", "12345678", "-"));

		HttpEntity<UsuarioLogin> autenticarUsuario = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L,
				"", "ramona@email.com.br", "12345678", "-", ""));

		ResponseEntity<UsuarioLogin> resposta = testRestTemplate
				.exchange("/usuarios/logar", HttpMethod.POST,
				autenticarUsuario, UsuarioLogin.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	
	/*desafio listar por ID */
	
	@Test
	@DisplayName("😎 Deve listar Usuário por ID")
	public void deveMostrarUsuarioID() {

		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
				"Ramona", "ramona@email.com.br", "12345678", "-"));
		
		Long usuario = usuarioCadastrado.get().getId();

		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/" + usuario, HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
}
}