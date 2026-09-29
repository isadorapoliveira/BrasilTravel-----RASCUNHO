package br.com.brasiltravel.brasiltravel.controller;

import br.com.brasiltravel.brasiltravel.model.Aeroporto;
import br.com.brasiltravel.brasiltravel.model.Destino;
import br.com.brasiltravel.brasiltravel.repository.AeroportoRepository;
import br.com.brasiltravel.brasiltravel.repository.DestinoRepository;
import br.com.brasiltravel.brasiltravel.repository.VooRepository;
import br.com.brasiltravel.brasiltravel.service.SessaoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
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
public class AdminAeroportoController {

    private final SessaoService sessaoService;
    private final AeroportoRepository aeroportoRepository;
    private final DestinoRepository destinoRepository;
    private final VooRepository vooRepository;

    public AdminAeroportoController(SessaoService sessaoService,
                                     AeroportoRepository aeroportoRepository,
                                     DestinoRepository destinoRepository,
                                     VooRepository vooRepository) {
        this.sessaoService = sessaoService;
        this.aeroportoRepository = aeroportoRepository;
        this.destinoRepository = destinoRepository;
        this.vooRepository = vooRepository;
    }

    @GetMapping("/admin/aeroportos")
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        model.addAttribute("aeroportos", aeroportoRepository.findAllByOrderByCodigoIataAsc());
        return "admin/aeroportos/lista";
    }

    @GetMapping("/admin/aeroportos/novo")
    public String novo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Aeroporto aeroporto = new Aeroporto();
        aeroporto.setDestino(new Destino());
        aeroporto.setAtivo(true);

        prepararFormulario(model, aeroporto, false);
        return "admin/aeroportos/form";
    }

    @PostMapping("/admin/aeroportos/salvar")
    public String salvar(@Valid @ModelAttribute("aeroporto") Aeroporto aeroporto,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        normalizar(aeroporto);
        validarCodigoIata(aeroporto, bindingResult);
        carregarDestinoSelecionado(aeroporto, bindingResult);

        if (bindingResult.hasErrors()) {
            prepararFormulario(model, aeroporto, aeroporto.getIdAeroporto() != null);
            return "admin/aeroportos/form";
        }

        aeroportoRepository.save(aeroporto);
        redirectAttributes.addFlashAttribute("sucesso", "Aeroporto salvo com sucesso.");
        return "redirect:/admin/aeroportos";
    }

    @GetMapping("/admin/aeroportos/editar/{id}")
    public String editar(@PathVariable Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Aeroporto aeroporto = aeroportoRepository.findByIdAeroporto(id).orElse(null);
        if (aeroporto == null) {
            redirectAttributes.addFlashAttribute("erro", "Aeroporto não encontrado.");
            return "redirect:/admin/aeroportos";
        }

        prepararFormulario(model, aeroporto, true);
        return "admin/aeroportos/form";
    }

    @PostMapping("/admin/aeroportos/alternar-status/{id}")
    public String alternarStatus(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Aeroporto aeroporto = aeroportoRepository.findById(id).orElse(null);
        if (aeroporto == null) {
            redirectAttributes.addFlashAttribute("erro", "Aeroporto não encontrado.");
            return "redirect:/admin/aeroportos";
        }

        aeroporto.setAtivo(!Boolean.TRUE.equals(aeroporto.getAtivo()));
        aeroportoRepository.save(aeroporto);
        redirectAttributes.addFlashAttribute("sucesso", "Status do aeroporto atualizado.");
        return "redirect:/admin/aeroportos";
    }

    @PostMapping("/admin/aeroportos/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        String acesso = validarAdmin(session, redirectAttributes);
        if (acesso != null) {
            return acesso;
        }

        Aeroporto aeroporto = aeroportoRepository.findById(id).orElse(null);
        if (aeroporto == null) {
            redirectAttributes.addFlashAttribute("erro", "Aeroporto não encontrado.");
            return "redirect:/admin/aeroportos";
        }

        long voosOrigem = vooRepository.countByAeroportoOrigem_IdAeroporto(id);
        long voosDestino = vooRepository.countByAeroportoDestino_IdAeroporto(id);

        if (voosOrigem > 0 || voosDestino > 0) {
            redirectAttributes.addFlashAttribute("erro", "Este aeroporto possui voos vinculados como origem ou destino. Para preservar o histórico do banco, use Inativar em vez de excluir.");
            return "redirect:/admin/aeroportos";
        }

        try {
            aeroportoRepository.delete(aeroporto);
            redirectAttributes.addFlashAttribute("sucesso", "Aeroporto excluído com sucesso.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir o aeroporto porque existem registros vinculados.");
        }

        return "redirect:/admin/aeroportos";
    }

    private void prepararFormulario(Model model, Aeroporto aeroporto, boolean modoEdicao) {
        List<Destino> destinos = destinoRepository.findAllByOrderByCidadeAscEstadoAsc();
        model.addAttribute("aeroporto", aeroporto);
        model.addAttribute("destinos", destinos);
        model.addAttribute("modoEdicao", modoEdicao);
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

    private void normalizar(Aeroporto aeroporto) {
        if (aeroporto.getCodigoIata() != null) {
            aeroporto.setCodigoIata(aeroporto.getCodigoIata().trim().toUpperCase());
        }
        if (aeroporto.getNome() != null) {
            aeroporto.setNome(aeroporto.getNome().trim());
        }
        if (aeroporto.getEndereco() != null) {
            aeroporto.setEndereco(aeroporto.getEndereco().trim());
        }
        aeroporto.setAtivo(Boolean.TRUE.equals(aeroporto.getAtivo()));
    }

    private void validarCodigoIata(Aeroporto aeroporto, BindingResult bindingResult) {
        String codigo = aeroporto.getCodigoIata();
        if (codigo == null || codigo.isBlank()) {
            return;
        }

        if (codigo.length() != 3) {
            bindingResult.rejectValue("codigoIata", "codigoIata.tamanho", "Informe o código IATA com exatamente 3 letras.");
            return;
        }

        boolean duplicado = aeroportoRepository.existsByCodigoIataIgnoreCase(codigo);
        if (duplicado) {
            Aeroporto existente = aeroportoRepository.findAll().stream()
                    .filter(a -> a.getCodigoIata() != null && a.getCodigoIata().equalsIgnoreCase(codigo))
                    .findFirst()
                    .orElse(null);

            boolean mesmoRegistro = existente != null
                    && aeroporto.getIdAeroporto() != null
                    && aeroporto.getIdAeroporto().equals(existente.getIdAeroporto());

            if (!mesmoRegistro) {
                bindingResult.rejectValue("codigoIata", "codigoIata.duplicado", "Já existe um aeroporto cadastrado com este código IATA.");
            }
        }
    }

    private void carregarDestinoSelecionado(Aeroporto aeroporto, BindingResult bindingResult) {
        if (aeroporto.getDestino() == null || aeroporto.getDestino().getIdDestino() == null) {
            bindingResult.rejectValue("destino", "destino.obrigatorio", "Selecione o destino/cidade do aeroporto.");
            return;
        }

        Destino destino = destinoRepository.findById(aeroporto.getDestino().getIdDestino()).orElse(null);
        if (destino == null) {
            bindingResult.rejectValue("destino", "destino.invalido", "Destino/cidade não encontrado.");
            return;
        }

        aeroporto.setDestino(destino);
    }
}
