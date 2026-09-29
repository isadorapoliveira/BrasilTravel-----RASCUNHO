package br.com.brasiltravel.brasiltravel.controller;

import br.com.brasiltravel.brasiltravel.model.SolicitacaoViagem;
import br.com.brasiltravel.brasiltravel.model.enums.StatusSolicitacao;
import br.com.brasiltravel.brasiltravel.model.enums.TipoUsuario;
import br.com.brasiltravel.brasiltravel.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel.repository.UsuarioRepository;
import br.com.brasiltravel.brasiltravel.service.SessaoService;
import br.com.brasiltravel.brasiltravel.service.SolicitacaoViagemService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminSolicitacaoController {

    private final SessaoService sessaoService;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitacaoViagemService solicitacaoViagemService;

    public AdminSolicitacaoController(SessaoService sessaoService,
                                      SolicitacaoViagemRepository solicitacaoViagemRepository,
                                      UsuarioRepository usuarioRepository,
                                      SolicitacaoViagemService solicitacaoViagemService) {
        this.sessaoService = sessaoService;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.solicitacaoViagemService = solicitacaoViagemService;
    }

    @GetMapping("/admin/solicitacoes")
    public String listar(@RequestParam(required = false) StatusSolicitacao status,
                         @RequestParam(required = false) Long idCliente,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        List<SolicitacaoViagem> solicitacoes = solicitacaoViagemRepository.filtrarAdmin(status, idCliente);

        model.addAttribute("solicitacoes", solicitacoes);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("clienteSelecionado", idCliente);
        model.addAttribute("statusDisponiveis", StatusSolicitacao.values());
        model.addAttribute("clientes", usuarioRepository.findByTipoUsuarioOrderByNomeAsc(TipoUsuario.CLIENTE));
        return "admin/solicitacoes/lista";
    }

    @GetMapping("/admin/solicitacoes/{id}")
    public String detalhe(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        SolicitacaoViagem solicitacao = solicitacaoViagemRepository.findByIdSolicitacao(id).orElse(null);
        if (solicitacao == null) {
            redirectAttributes.addFlashAttribute("erro", "Solicitação não encontrada.");
            return "redirect:/admin/solicitacoes";
        }

        model.addAttribute("solicitacao", solicitacao);
        model.addAttribute("statusDisponiveis", StatusSolicitacao.values());
        return "admin/solicitacoes/detalhe";
    }

    @PostMapping("/admin/solicitacoes/status/{id}")
    public String alterarStatus(@PathVariable Long id,
                                @RequestParam StatusSolicitacao statusSolicitacao,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        try {
            solicitacaoViagemService.alterarStatusAdmin(id, statusSolicitacao);
            redirectAttributes.addFlashAttribute("sucesso", "Status da solicitação atualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/solicitacoes";
    }

    private String validarAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessaoService.estaLogado(session)) {
            redirectAttributes.addFlashAttribute("erro", "Faça login como administrador.");
            return "redirect:/login";
        }
        if (!sessaoService.ehAdmin(session)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso permitido apenas para administradores.");
            return "redirect:/cliente/perfil";
        }
        return null;
    }
}
