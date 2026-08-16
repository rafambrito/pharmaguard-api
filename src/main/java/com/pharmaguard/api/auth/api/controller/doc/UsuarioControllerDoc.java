package com.pharmaguard.api.auth.api.controller.doc;

import com.pharmaguard.api.auth.api.dto.request.AtualizarUsuarioRequest;
import com.pharmaguard.api.auth.api.dto.request.CriarUsuarioRequest;
import com.pharmaguard.api.auth.api.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Usuarios", description = "API para operacoes CRUD de usuarios")
public interface UsuarioControllerDoc {

    @Operation(summary = "Criar usuario", description = "Cria um novo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class), examples = @ExampleObject(value = "{\"id\":1,\"nome\":\"Joao da Silva\",\"email\":\"joao@pharmaguard.com\",\"login\":\"joao\",\"tipo\":\"COLABORADOR\",\"status\":\"ATIVO\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    ResponseEntity<UsuarioResponse> criar(
            @Valid @RequestBody(description = "Dados do usuario", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriarUsuarioRequest.class), examples = @ExampleObject(value = "{\"nome\":\"Joao da Silva\",\"email\":\"joao@pharmaguard.com\",\"login\":\"joao\",\"tipo\":\"COLABORADOR\",\"senha\":\"Senha@123\",\"status\":\"ATIVO\"}"))) CriarUsuarioRequest request);

    @Operation(summary = "Listar usuarios", description = "Lista todos os usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class)))
    ResponseEntity<List<UsuarioResponse>> listar();

    @Operation(summary = "Buscar usuario por id", description = "Retorna um usuario pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    })
    ResponseEntity<UsuarioResponse> buscarPorId(@Parameter(description = "Id do usuario", required = true) Long id);

    @Operation(summary = "Atualizar usuario", description = "Atualiza os dados de um usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    })
    ResponseEntity<UsuarioResponse> atualizar(
            @Parameter(description = "Id do usuario", required = true) Long id,
            @Valid @RequestBody(description = "Dados para atualizacao", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarUsuarioRequest.class), examples = @ExampleObject(value = "{\"nome\":\"Joao Atualizado\",\"email\":\"joao.atualizado@pharmaguard.com\",\"login\":\"joao\",\"status\":\"ATIVO\"}"))) AtualizarUsuarioRequest request);

    @Operation(summary = "Remover usuario", description = "Remove um usuario por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    })
    ResponseEntity<Void> remover(@Parameter(description = "Id do usuario", required = true) Long id);
}
