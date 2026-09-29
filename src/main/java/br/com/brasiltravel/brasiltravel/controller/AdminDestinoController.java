package br.com.brasiltravel.brasiltravel.controller;

import br.com.brasiltravel.brasiltravel.model.Destino;
import br.com.brasiltravel.brasiltravel.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel.repository.SolicitacaoViagemRepository;
import br.com.brasiltravel.brasiltravel.service.SessaoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminDestinoController {

    private final SessaoService sessaoService;
    private final DestinoRepository destinoRepository;
    private final AeroportoRepository aeroportoRepository;
    private final SolicitacaoViagemRepository solicitacaoViagemRepository;

    public AdminDestinoController(SessaoService sessaoService,
                                  DestinoRepository destinoRepository,
                                  AeroportoRepository aeroportoRepository,
                                  SolicitacaoViagemRepository solicitacaoViagemRepository) {
        this.sessaoService = sessaoService;
        this.destinoRepository = destinoRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.solicitacaoViagemRepository = solicitacaoViagemRepository;
    }

    @GetMapping("/admin/destinos")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        model.addAttribute("destinos", destinoRepository.findAllByOrderByCidadeAscEstadoAsc());
        return "admin/destinos/lista";
    }

    @GetMapping("/admin/destinos/novo")
    public String novo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Destino destino = new Destino();
        destino.setAtivo(true);

        model.addAttribute("destino", destino);
        model.addAttribute("modoEdicao", false);
        return "admin/destinos/form";
    }

    @PostMapping("/admin/destinos/salvar")
    public String salvar(@Valid @ModelAttribute("destino") Destino destino,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        normalizar(destino);

        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", destino.getIdDestino() != null);
            return "admin/destinos/form";
        }

        destinoRepository.save(destino);
        redirectAttributes.addFlashAttribute("sucesso", "Destino salvo com sucesso.");
        return "redirect:/admin/destinos";
    }

    @GetMapping("/admin/destinos/editar/{id}")
    public String editar(@PathVariable Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Destino destino = destinoRepository.findById(id).orElse(null);
        if (destino == null) {
            redirectAttributes.addFlashAttribute("erro", "Destino não encontrado.");
            return "redirect:/admin/destinos";
        }

        model.addAttribute("destino", destino);
        model.addAttribute("modoEdicao", true);
        return "admin/destinos/form";
    }

    @PostMapping("/admin/destinos/alternar-status/{id}")
    public String alternarStatus(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Destino destino = destinoRepository.findById(id).orElse(null);
        if (destino == null) {
            redirectAttributes.addFlashAttribute("erro", "Destino não encontrado.");
            return "redirect:/admin/destinos";
        }

        destino.setAtivo(!Boolean.TRUE.equals(destino.getAtivo()));
        destinoRepository.save(destino);
        redirectAttributes.addFlashAttribute("sucesso", "Status do destino atualizado.");
        return "redirect:/admin/destinos";
    }

    @PostMapping("/admin/destinos/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Destino destino = destinoRepository.findById(id).orElse(null);
        if (destino == null) {
            redirectAttributes.addFlashAttribute("erro", "Destino não encontrado.");
            return "redirect:/admin/destinos";
        }

        long aeroportosVinculados = aeroportoRepository.countByDestino_IdDestino(id);
        long solicitacoesVinculadas = solicitacaoViagemRepository.countByDestinoPrincipal_IdDestino(id);

        if (aeroportosVinculados > 0 || solicitacoesVinculadas > 0) {
            redirectAttributes.addFlashAttribute("erro", "Este destino possui vínculos com aeroportos ou solicitações. Para preservar o histórico do banco, use Inativar em vez de excluir.");
            return "redirect:/admin/destinos";
        }

        try {
            destinoRepository.delete(destino);
            redirectAttributes.addFlashAttribute("sucesso", "Destino excluído com sucesso.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir o destino porque existem registros vinculados.");
        }

        return "redirect:/admin/destinos";
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

    private void normalizar(Destino destino) {
        if (destino.getCidade() != null) {
            destino.setCidade(destino.getCidade().trim());
        }
        if (destino.getEstado() != null) {
            destino.setEstado(destino.getEstado().trim().toUpperCase());
        }
        if (destino.getDescricao() != null) {
            destino.setDescricao(destino.getDescricao().trim());
        }
        destino.setAtivo(Boolean.TRUE.equals(destino.getAtivo()));
    }
}
