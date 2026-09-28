package br.com.brasiltravel.brasiltravel_aereo.controller;

import br.com.brasiltravel.brasiltravel_aereo.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.CompanhiaAereaRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.UsuarioRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.VooRepository;
import br.com.brasiltravel.brasiltravel_aereo.service.SessaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final SessaoService sessaoService;
    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final AeroportoRepository aeroportoRepository;
    private final CompanhiaAereaRepository companhiaAereaRepository;
    private final VooRepository vooRepository;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;

    public AdminController(SessaoService sessaoService,
                           UsuarioRepository usuarioRepository,
                           DestinoRepository destinoRepository,
                           AeroportoRepository aeroportoRepository,
                           CompanhiaAereaRepository companhiaAereaRepository,
                           VooRepository vooRepository,
                           SolicitacaoViagemRepository solicitacaoViagemRepository) {
        this.sessaoService = sessaoService;
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.companhiaAereaRepository = companhiaAereaRepository;
        this.vooRepository = vooRepository;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
    }

    @GetMapping("/admin")
    public String painel(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessaoService.estaLogado(session)) {
            redirectAttributes.addFlashAttribute("erro", "Faça login como administrador.");
            return "redirect:/login";
        }

        if (!sessaoService.ehAdmin(session)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso permitido apenas para administradores.");
            return "redirect:/cliente/perfil";
        }

        model.addAttribute("qtdUsuarios", usuarioRepository.count());
        model.addAttribute("qtdDestinos", destinoRepository.count());
        model.addAttribute("qtdAeroportos", aeroportoRepository.count());
        model.addAttribute("qtdCompanhias", companhiaAereaRepository.count());
        model.addAttribute("qtdVoos", vooRepository.count());
        model.addAttribute("qtdSolicitacoes", solicitacaoViagemRepository.count());

        return "admin/painel";
    }
}
