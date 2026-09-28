package br.com.brasiltravel.brasiltravel_aereo.repository;

import br.com.brasiltravel.brasiltravel_aereo.model.Usuario;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.TipoUsuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmailIgnoreCaseAndIdUsuarioNot(String email, Long idUsuario);
    boolean existsByCpfAndIdUsuarioNot(String cpf, Long idUsuario);
    List<Usuario> findByTipoUsuarioOrderByNomeAsc(TipoUsuario tipoUsuario);
}
