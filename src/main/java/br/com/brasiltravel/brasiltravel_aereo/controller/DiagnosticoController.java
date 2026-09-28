package br.com.brasiltravel.brasiltravel_aereo.controller;

import br.com.brasiltravel.brasiltravel_aereo.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.CompanhiaAereaRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.SolicitacaoVooRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.UsuarioRepository;
import br.com.brasiltravel.brasiltravel_aereo.repository.VooRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiagnosticoController {

    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final AeroportoRepository aeroportoRepository;
    private final CompanhiaAereaRepository companhiaAereaRepository;
    private final VooRepository vooRepository;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;
    private final SolicitacaoVooRepository solicitacaoVooRepository;

    public DiagnosticoController(UsuarioRepository usuarioRepository,
                                 DestinoRepository destinoRepository,
                                 AeroportoRepository aeroportoRepository,
                                 CompanhiaAereaRepository companhiaAereaRepository,
                                 VooRepository vooRepository,
                                 SolicitacaoViagemRepository solicitacaoViagemRepository,
                                 SolicitacaoVooRepository solicitacaoVooRepository) {
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.companhiaAereaRepository = companhiaAereaRepository;
        this.vooRepository = vooRepository;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
        this.solicitacaoVooRepository = solicitacaoVooRepository;
    }

    @GetMapping("/diagnostico")
    public String diagnostico(Model model) {
        model.addAttribute("qtdUsuarios", usuarioRepository.count());
        model.addAttribute("qtdDestinos", destinoRepository.count());
        model.addAttribute("qtdAeroportos", aeroportoRepository.count());
        model.addAttribute("qtdCompanhias", companhiaAereaRepository.count());
        model.addAttribute("qtdVoos", vooRepository.count());
        model.addAttribute("qtdSolicitacoes", solicitacaoViagemRepository.count());
        model.addAttribute("qtdSolicitacoesVoos", solicitacaoVooRepository.count());
        model.addAttribute("voos", vooRepository.findByAtivoTrueOrderByDataHoraPartidaAsc());
        return "diagnostico";
    }
}
