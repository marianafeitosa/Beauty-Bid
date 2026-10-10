package com.leilao.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.core.Ordered;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter
        implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path =
                exchange
                        .getRequest()
                        .getURI()
                        .getPath();

        HttpMethod method =
                exchange
                        .getRequest()
                        .getMethod();


        /*
         * Requisições OPTIONS são usadas pelo navegador
         * para CORS.
         */
        if (
                HttpMethod.OPTIONS.equals(method)
        ) {

            return chain.filter(
                    exchange
            );
        }


        /*
         * Rotas públicas de autenticação.
         */
        if (
                path.equals(
                        "/api/auth/login"
                )
                ||
                path.equals(
                        "/api/auth/register"
                )
        ) {

            return chain.filter(
                    limparHeadersInternos(
                            exchange
                    )
            );
        }


        /*
         * Consulta de leilões pode ser pública.
         *
         * GET /api/leiloes
         * GET /api/leiloes/{id}
         *
         * Mas GET dos lances não será público.
         */
        if (
                HttpMethod.GET.equals(method)
                &&
                rotaPublicaDeLeilao(path)
        ) {

            return chain.filter(
                    limparHeadersInternos(
                            exchange
                    )
            );
        }


        /*
         * Daqui para baixo precisa estar logado.
         */
        HttpCookie cookie =
                exchange
                        .getRequest()
                        .getCookies()
                        .getFirst(
                                "AUTH_TOKEN"
                        );

        if (cookie == null) {

            return negar(
                    exchange,
                    HttpStatus.UNAUTHORIZED
            );
        }


        try {

            Claims claims =
                    validarToken(
                            cookie.getValue()
                    );

            String usuarioId =
                    claims.getSubject();

            String role =
                    claims.get(
                            "role",
                            String.class
                    );


            /*
             * Regras de ADM.
             */
            if (
                    precisaSerAdm(
                            path,
                            method
                    )
                    &&
                    !"ADM".equals(role)
            ) {

                return negar(
                        exchange,
                        HttpStatus.FORBIDDEN
                );
            }


            /*
             * Nunca confiamos em headers enviados
             * diretamente pelo cliente.
             *
             * Primeiro removemos.
             * Depois criamos usando o JWT válido.
             */
            ServerWebExchange novoExchange =
                    exchange
                            .mutate()
                            .request(
                                    exchange
                                            .getRequest()
                                            .mutate()

                                            .headers(
                                                    headers -> {

                                                        headers.remove(
                                                                "X-User-Id"
                                                        );

                                                        headers.remove(
                                                                "X-User-Role"
                                                        );

                                                        headers.add(
                                                                "X-User-Id",
                                                                usuarioId
                                                        );

                                                        headers.add(
                                                                "X-User-Role",
                                                                role
                                                        );
                                                    }
                                            )

                                            .build()
                            )

                            .build();


            return chain.filter(
                    novoExchange
            );

        } catch (Exception e) {

            /*
             * JWT inválido, expirado ou adulterado.
             */
            return negar(
                    exchange,
                    HttpStatus.UNAUTHORIZED
            );
        }
    }


    /*
     * Valida assinatura e validade do JWT.
     */
    private Claims validarToken(
            String token
    ) {

        SecretKey key =
                Keys.hmacShaKeyFor(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return Jwts
                .parser()

                .verifyWith(key)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }


    /*
     * GET de leilão público.
     *
     * Bloqueamos especificamente URLs contendo
     * /lances.
     */
    private boolean rotaPublicaDeLeilao(
            String path
    ) {

        if (
                !path.startsWith(
                        "/api/leiloes"
                )
        ) {
            return false;
        }

        return !path.contains(
                "/lances"
        );
    }


    /*
     * Define quais operações precisam de ADM.
     */
    private boolean precisaSerAdm(
            String path,
            HttpMethod method
    ) {

        /*
         * Relatórios são exclusivos do ADM.
         */
        if (
                path.startsWith(
                        "/api/relatorios"
                )
        ) {

            return true;
        }


        /*
         * CRUD administrativo de usuários.
         */
        if (
                path.startsWith(
                        "/api/auth/usuarios"
                )
        ) {

            return true;
        }


        /*
         * Criar, alterar ou excluir leilão
         * é função do ADM.
         *
         * POST de /lances NÃO entra aqui.
         */
        if (
                path.startsWith(
                        "/api/leiloes"
                )
                &&
                !path.contains(
                        "/lances"
                )
        ) {

            return HttpMethod.POST.equals(method)
                    ||
                    HttpMethod.PUT.equals(method)
                    ||
                    HttpMethod.DELETE.equals(method);
        }


        return false;
    }


    /*
     * Remove tentativa de falsificação de usuário.
     */
    private ServerWebExchange limparHeadersInternos(
            ServerWebExchange exchange
    ) {

        return exchange
                .mutate()
                .request(
                        exchange
                                .getRequest()
                                .mutate()
                                .headers(
                                        headers -> {

                                            headers.remove(
                                                    "X-User-Id"
                                            );

                                            headers.remove(
                                                    "X-User-Role"
                                            );
                                        }
                                )
                                .build()
                )
                .build();
    }


    /*
     * Retorna 401 ou 403.
     */
    private Mono<Void> negar(
            ServerWebExchange exchange,
            HttpStatus status
    ) {

        exchange
                .getResponse()
                .setStatusCode(status);

        return exchange
                .getResponse()
                .setComplete();
    }


    /*
     * Faz este filtro rodar cedo.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}