package br.com.brasiltravel.brasiltravel_aereo.service;

import br.com.brasiltravel.brasiltravel_aereo.model.Usuario;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.TipoUsuario;
import br.com.brasiltravel.brasiltravel_aereo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SessaoService {

    public static final String ID_USUARIO = "usuarioLogadoId";
    public static final String NOME_USUARIO = "usuarioLogadoNome";
    public static final String TIPO_USUARIO = "usuarioLogadoTipo";

    private final UsuarioRepository usuarioRepository;

    public SessaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void iniciarSessao(HttpSession session, Usuario usuario) {
        session.setAttribute(ID_USUARIO, usuario.getIdUsuario());
        session.setAttribute(NOME_USUARIO, usuario.getNome());
        session.setAttribute(TIPO_USUARIO, usuario.getTipoUsuario().name());
    }

    public void atualizarSessao(HttpSession session, Usuario usuario) {
        session.setAttribute(NOME_USUARIO, usuario.getNome());
        session.setAttribute(TIPO_USUARIO, usuario.getTipoUsuario().name());
    }

    public void encerrarSessao(HttpSession session) {
        session.invalidate();
    }

    public boolean estaLogado(HttpSession session) {
        return session != null && session.getAttribute(ID_USUARIO) != null;
    }

    public boolean ehAdmin(HttpSession session) {
        Object tipo = session.getAttribute(TIPO_USUARIO);
        return TipoUsuario.ADMIN.name().equals(tipo);
    }

    public Optional<Usuario> usuarioLogado(HttpSession session) {
        Object id = session.getAttribute(ID_USUARIO);
        if (!(id instanceof Long idUsuario)) {
            return Optional.empty();
        }
        return usuarioRepository.findById(idUsuario);
    }
}
