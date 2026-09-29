package br.com.brasiltravel.brasiltravel.controller;

import br.com.brasiltravel.brasiltravel.model.CompanhiaAerea;
import br.com.brasiltravel.brasiltravel.repository.CompanhiaAereaRepository;
import br.com.brasiltravel.brasiltravel.repository.VooRepository;
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
public class AdminCompanhiaController {

    private final SessaoService sessaoService;
    private final CompanhiaAereaRepository companhiaAereaRepository;
    private final VooRepository vooRepository;

    public AdminCompanhiaController(SessaoService sessaoService,
                                    CompanhiaAereaRepository companhiaAereaRepository,
                                    VooRepository vooRepository) {
        this.sessaoService = sessaoService;
        this.companhiaAereaRepository = companhiaAereaRepository;
        this.vooRepository = vooRepository;
    }

    @GetMapping("/admin/companhias")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        model.addAttribute("companhias", companhiaAereaRepository.findAllByOrderByNomeAsc());
        return "admin/companhias/lista";
    }

    @GetMapping("/admin/companhias/novo")
    public String novo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setAtivo(true);
        model.addAttribute("companhia", companhia);
        model.addAttribute("modoEdicao", false);
        return "admin/companhias/form";
    }

    @PostMapping("/admin/companhias/salvar")
    public String salvar(@Valid @ModelAttribute("companhia") CompanhiaAerea companhia,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        normalizar(companhia);
        validarCodigoIata(companhia, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", companhia.getIdCompanhia() != null);
            return "admin/companhias/form";
        }

        companhiaAereaRepository.save(companhia);
        redirectAttributes.addFlashAttribute("sucesso", "Companhia aérea salva com sucesso.");
        return "redirect:/admin/companhias";
    }

    @GetMapping("/admin/companhias/editar/{id}")
    public String editar(@PathVariable Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findByIdCompanhia(id).orElse(null);
        if (companhia == null) {
            redirectAttributes.addFlashAttribute("erro", "Companhia aérea não encontrada.");
            return "redirect:/admin/companhias";
        }

        model.addAttribute("companhia", companhia);
        model.addAttribute("modoEdicao", true);
        return "admin/companhias/form";
    }

    @PostMapping("/admin/companhias/alternar-status/{id}")
    public String alternarStatus(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findById(id).orElse(null);
        if (companhia == null) {
            redirectAttributes.addFlashAttribute("erro", "Companhia aérea não encontrada.");
            return "redirect:/admin/companhias";
        }

        companhia.setAtivo(!Boolean.TRUE.equals(companhia.getAtivo()));
        companhiaAereaRepository.save(companhia);
        redirectAttributes.addFlashAttribute("sucesso", "Status da companhia aérea atualizado.");
        return "redirect:/admin/companhias";
    }

    @PostMapping("/admin/companhias/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findById(id).orElse(null);
        if (companhia == null) {
            redirectAttributes.addFlashAttribute("erro", "Companhia aérea não encontrada.");
            return "redirect:/admin/companhias";
        }

        long voosVinculados = vooRepository.countByCompanhia_IdCompanhia(id);
        if (voosVinculados > 0) {
            redirectAttributes.addFlashAttribute("erro", "Esta companhia possui voos vinculados. Para preservar o histórico do banco, use Inativar em vez de excluir.");
            return "redirect:/admin/companhias";
        }

        try {
            companhiaAereaRepository.delete(companhia);
            redirectAttributes.addFlashAttribute("sucesso", "Companhia aérea excluída com sucesso.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir a companhia porque existem registros vinculados.");
        }

        return "redirect:/admin/companhias";
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

    private void normalizar(CompanhiaAerea companhia) {
        if (companhia.getNome() != null) {
            companhia.setNome(companhia.getNome().trim());
        }
        if (companhia.getCodigoIata() != null) {
            companhia.setCodigoIata(companhia.getCodigoIata().trim().toUpperCase());
        }
        if (companhia.getSite() != null) {
            companhia.setSite(companhia.getSite().trim());
        }
        if (companhia.getTelefone() != null) {
            companhia.setTelefone(companhia.getTelefone().trim());
        }
        companhia.setAtivo(Boolean.TRUE.equals(companhia.getAtivo()));
    }

    private void validarCodigoIata(CompanhiaAerea companhia, BindingResult bindingResult) {
        String codigo = companhia.getCodigoIata();
        if (codigo == null || codigo.isBlank()) {
            return;
        }

        if (codigo.length() != 2 && codigo.length() != 3) {
            bindingResult.rejectValue("codigoIata", "codigoIata.tamanho", "Informe o código IATA da companhia com 2 ou 3 caracteres.");
            return;
        }

        boolean duplicado = companhiaAereaRepository.existsByCodigoIataIgnoreCase(codigo);
        if (duplicado) {
            CompanhiaAerea existente = companhiaAereaRepository.findAll().stream()
                    .filter(c -> c.getCodigoIata() != null && c.getCodigoIata().equalsIgnoreCase(codigo))
                    .findFirst()
                    .orElse(null);

            boolean mesmoRegistro = existente != null
                    && companhia.getIdCompanhia() != null
                    && companhia.getIdCompanhia().equals(existente.getIdCompanhia());

            if (!mesmoRegistro) {
                bindingResult.rejectValue("codigoIata", "codigoIata.duplicado", "Já existe uma companhia cadastrada com este código.");
            }
        }
    }
}
