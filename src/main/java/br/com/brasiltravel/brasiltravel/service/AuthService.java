package br.com.brasiltravel.brasiltravel.service;

import br.com.brasiltravel.brasiltravel.dto.CadastroUsuarioForm;
import br.com.brasiltravel.brasiltravel.dto.PerfilUsuarioForm;
import br.com.brasiltravel.brasiltravel.model.Usuario;
import br.com.brasiltravel.brasiltravel.model.enums.TipoUsuario;
import br.com.brasiltravel.brasiltravel.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;

    public AuthService(UsuarioRepository usuarioRepository, SenhaService senhaService) {
        this.usuarioRepository = usuarioRepository;
        this.senhaService = senhaService;
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return Optional.empty();
        }

        return usuarioRepository.findByEmailIgnoreCase(email.trim())
                .filter(usuario -> Boolean.TRUE.equals(usuario.getAtivo()))
                .filter(usuario -> senhaService.conferirSenha(senha, usuario.getSenhaHash()));
    }

    @Transactional
    public Usuario cadastrarCliente(CadastroUsuarioForm form) {
        validarCadastro(form);

        Usuario usuario = new Usuario(
                limpar(form.getNome()),
                limpar(form.getEmail()).toLowerCase(),
                senhaService.gerarHash(form.getSenha()),
                limpar(form.getCpf()),
                limpar(form.getTelefone()),
                TipoUsuario.CLIENTE
        );

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarPerfil(Long idUsuario, PerfilUsuarioForm form) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot(limpar(form.getEmail()), idUsuario)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado para outro usuário.");
        }

        if (usuarioRepository.existsByCpfAndIdUsuarioNot(limpar(form.getCpf()), idUsuario)) {
            throw new IllegalArgumentException("Este CPF já está cadastrado para outro usuário.");
        }

        usuario.setNome(limpar(form.getNome()));
        usuario.setEmail(limpar(form.getEmail()).toLowerCase());
        usuario.setCpf(limpar(form.getCpf()));
        usuario.setTelefone(limpar(form.getTelefone()));

        return usuarioRepository.save(usuario);
    }

    public PerfilUsuarioForm criarFormPerfil(Usuario usuario) {
        return new PerfilUsuarioForm(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone()
        );
    }

    private void validarCadastro(CadastroUsuarioForm form) {
        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("A senha e a confirmação de senha não conferem.");
        }

        String email = limpar(form.getEmail());
        String cpf = limpar(form.getCpf());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Este CPF já está cadastrado.");
        }
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
