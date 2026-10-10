package com.leilao.login.service;

import com.leilao.common.EventPublisher;

import com.leilao.login.dto.AtualizarUsuarioDTO;
import com.leilao.login.dto.CadastroDTO;
import com.leilao.login.dto.LoginDTO;
import com.leilao.login.dto.UsuarioResponse;

import com.leilao.login.model.Usuario;

import com.leilao.login.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    private final PasswordService passwordService;

    private final EventPublisher publisher;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordService passwordService,
            EventPublisher publisher
    ) {

        this.repository =
                repository;

        this.passwordService =
                passwordService;

        this.publisher =
                publisher;
    }

    public UsuarioResponse cadastrarCliente(
            CadastroDTO dto
    ) {

        if (
                repository.existsByEmail(
                        dto.email()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um usuário com esse e-mail"
            );
        }

        Usuario usuario =
                new Usuario();

        usuario.setNome(
                dto.nome()
        );

        usuario.setEmail(
                dto.email()
        );

        usuario.setSenhaHash(
                passwordService
                        .criptografar(
                                dto.senha()
                        )
        );

        /*
         * Cadastro público SEMPRE cria CLIENTE.
         */
        usuario.setRole(
                "CLIENTE"
        );

        usuario =
                repository.save(
                        usuario
                );

        publisher.publicar(
                "usuario",
                "criado",
                usuario.getId(),
                Map.of(
                        "nome",
                        usuario.getNome(),

                        "email",
                        usuario.getEmail(),

                        "role",
                        usuario.getRole()
                )
        );

        return UsuarioResponse.from(
                usuario
        );
    }

    public Usuario autenticar(
            LoginDTO dto
    ) {

        Usuario usuario =
                repository
                        .findByEmail(
                                dto.email()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "E-mail ou senha inválidos"
                                )
                        );

        boolean senhaCorreta =
                passwordService
                        .confere(
                                dto.senha(),
                                usuario.getSenhaHash()
                        );

        if (!senhaCorreta) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "E-mail ou senha inválidos"
            );
        }

        return usuario;
    }

    public UsuarioResponse buscar(
            String id
    ) {

        return UsuarioResponse.from(
                buscarEntidade(id)
        );
    }

    public List<UsuarioResponse> listar() {

        return repository
                .findAll()
                .stream()
                .map(
                        UsuarioResponse::from
                )
                .toList();
    }

    public UsuarioResponse atualizar(
            String id,
            AtualizarUsuarioDTO dto
    ) {

        Usuario usuario =
                buscarEntidade(id);

        if (
                dto.nome() != null
                &&
                !dto.nome().isBlank()
        ) {

            usuario.setNome(
                    dto.nome()
            );
        }

        if (
                dto.email() != null
                &&
                !dto.email().isBlank()
                &&
                !dto.email()
                        .equals(
                                usuario.getEmail()
                        )
        ) {

            if (
                    repository.existsByEmail(
                            dto.email()
                    )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe um usuário com esse e-mail"
                );
            }

            usuario.setEmail(
                    dto.email()
            );
        }

        if (
                dto.senha() != null
                &&
                !dto.senha().isBlank()
        ) {

            usuario.setSenhaHash(
                    passwordService
                            .criptografar(
                                    dto.senha()
                            )
            );
        }

        if (
                dto.role() != null
                &&
                !dto.role().isBlank()
        ) {

            if (
                    !"ADM".equals(
                            dto.role()
                    )
                    &&
                    !"CLIENTE".equals(
                            dto.role()
                    )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Role deve ser ADM ou CLIENTE"
                );
            }

            usuario.setRole(
                    dto.role()
            );
        }

        usuario =
                repository.save(
                        usuario
                );

        publisher.publicar(
                "usuario",
                "atualizado",
                usuario.getId(),
                Map.of(
                        "nome",
                        usuario.getNome(),

                        "email",
                        usuario.getEmail(),

                        "role",
                        usuario.getRole()
                )
        );

        return UsuarioResponse.from(
                usuario
        );
    }

    public void excluir(
            String id
    ) {

        Usuario usuario =
                buscarEntidade(id);

        repository.delete(
                usuario
        );

        publisher.publicar(
                "usuario",
                "excluido",
                id,
                Map.of(
                        "nome",
                        usuario.getNome(),

                        "email",
                        usuario.getEmail()
                )
        );
    }

    public Usuario buscarEntidade(
            String id
    ) {

        return repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        )
                );
    }
}