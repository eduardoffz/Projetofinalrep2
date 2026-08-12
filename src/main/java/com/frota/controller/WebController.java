package com.frota.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.frota.model.*;
import com.frota.repository.*;
import com.frota.service.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    private final LocacaoService locacaoService;
    private final ProprietarioService propService;
    private final AuthService authService;
    private final MaquinaRepository maqRepo;
    private final TelemetriaRepository telRepo;
    private final TelemetriaService telemetriaService;
    private final ServicoAdicionalRepository servicoRepo;
    private final LocacaoServicoRepository locacaoServicoRepo;
    private final UsuarioRepository usuarioRepo;
    private final LocacaoRepository locacaoRepo;

    public WebController(LocacaoService ls, ProprietarioService ps, AuthService as,
                         MaquinaRepository m, TelemetriaRepository t, TelemetriaService ts, ServicoAdicionalRepository sr,
                         LocacaoServicoRepository lsr, UsuarioRepository ur, LocacaoRepository lr) {
        this.locacaoService = ls; this.propService = ps; this.authService = as;
        this.maqRepo = m; this.telRepo = t; this.telemetriaService = ts; this.servicoRepo = sr;
        this.locacaoServicoRepo = lsr; this.usuarioRepo = ur; this.locacaoRepo = lr;
    }

    private boolean estaLogado(HttpSession s) { return s.getAttribute("token") != null; }
    private String redirectSeNaoLogado(HttpSession s) { return estaLogado(s) ? null : "redirect:/login"; }

    // --- AUTH ---
    @GetMapping("/login")
    public String login(HttpSession s) { return estaLogado(s) ? "redirect:/" : "login"; }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email, @RequestParam String senha, HttpSession s, Model m) {
        try {
            LoginRequest dto = new LoginRequest(); dto.setEmail(email); dto.setSenha(senha);
            LoginResponse r = authService.login(dto);
            s.setAttribute("token", r.getToken());
            s.setAttribute("usuarioNome", r.getNome());
            s.setAttribute("usuarioEmail", r.getEmail());
            s.setAttribute("usuarioRole", r.getRole());

            // Carregar proprietarioId se for PROPRIETARIO
            if ("PROPRIETARIO".equals(r.getRole())) {
                Usuario u = usuarioRepo.buscarPorEmail(r.getEmail()).orElse(null);
                if (u != null) {
                    Proprietario p = propService.buscarPorUsuario(u.getId());
                    if (p != null) s.setAttribute("proprietarioId", p.getId());
                }
            }

            return "redirect:/";
        } catch (Exception e) {
            m.addAttribute("erro", "Email ou senha invalidos");
            return "login";
        }
    }

    @GetMapping("/cadastrar")
    public String cadastrar(HttpSession s) { return estaLogado(s) ? "redirect:/" : "cadastrar"; }

    @PostMapping("/cadastrar")
    public String cadastrarSubmit(@RequestParam String nome, @RequestParam String email,
                                   @RequestParam String senha, @RequestParam String role,
                                   @RequestParam(required=false) String telefone,
                                   @RequestParam(required=false) String documento,
                                   @RequestParam(required=false) String chavePix,
                                   @RequestParam(required=false) String endereco,
                                   HttpSession s, Model m) {
        try {
            UsuarioRequest dto = new UsuarioRequest();
            dto.setNome(nome); dto.setEmail(email); dto.setSenha(senha); dto.setRole(role);
            dto.setTelefone(telefone); dto.setDocumento(documento);
            dto.setChavePix(chavePix); dto.setEndereco(endereco);

            Usuario u = authService.cadastrar(dto);
            if ("PROPRIETARIO".equals(u.getRole())) {
                Proprietario p = propService.registrar(u.getId(), documento, chavePix, endereco);
                if (p != null) s.setAttribute("proprietarioId", p.getId());
            }

            LoginRequest ldto = new LoginRequest(); ldto.setEmail(email); ldto.setSenha(senha);
            LoginResponse r = authService.login(ldto);
            s.setAttribute("token", r.getToken());
            s.setAttribute("usuarioNome", r.getNome());
            s.setAttribute("usuarioEmail", r.getEmail());
            s.setAttribute("usuarioRole", r.getRole());

            if ("PROPRIETARIO".equals(r.getRole()) && s.getAttribute("proprietarioId") == null) {
                Usuario uFound = usuarioRepo.buscarPorEmail(r.getEmail()).orElse(null);
                if (uFound != null) {
                    Proprietario p = propService.buscarPorUsuario(uFound.getId());
                    if (p != null) s.setAttribute("proprietarioId", p.getId());
                }
            }

            return "redirect:/";
        } catch (Exception e) {
            m.addAttribute("erro", "Erro: " + e.getMessage());
            return "cadastrar";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession s) { s.invalidate(); return "redirect:/login"; }

    // --- DASHBOARD ---
    @GetMapping("/")
    public String dashboard(HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        String role = (String) s.getAttribute("usuarioRole");
        long totalMaquinas = maqRepo.contarDisponiveis();
        long locacoesAtivas = locacaoService.contarAtivas();

        m.addAttribute("totalMaquinas", totalMaquinas);
        m.addAttribute("locacoesAtivas", locacoesAtivas);
        m.addAttribute("maquinasDestaque", maqRepo.listarDestaques(4));

        // Dados especificos por perfil
        if ("CLIENTE".equals(role)) {
            Usuario usuario = usuarioRepo.buscarPorEmail((String) s.getAttribute("usuarioEmail")).orElse(null);
            long meusAlugueis = usuario != null ? locacaoRepo.contarPorCliente(usuario.getId()) : 0;
            m.addAttribute("meusAlugueis", meusAlugueis);
        } else if ("PROPRIETARIO".equals(role)) {
            Object propId = s.getAttribute("proprietarioId");
            if (propId != null) {
                long minhasMaquinas = maqRepo.listarPorProprietario((Long) propId).size();
                long minhasLocacoes = locacaoRepo.listarPorProprietario((Long) propId).size();
                BigDecimal faturamentoTotal = locacaoRepo.calcularFaturamentoProprietario((Long) propId);
                m.addAttribute("minhasMaquinas", minhasMaquinas);
                m.addAttribute("minhasLocacoes", minhasLocacoes);
                m.addAttribute("faturamentoTotal", faturamentoTotal);
            }
        }

        // Dados apenas para ADMIN
        if ("ADMIN".equals(role)) {
            m.addAttribute("totalProprietarios", propService.contarProprietarios());
            m.addAttribute("totalClientes", usuarioRepo.contarPorRole("CLIENTE"));
        }

        // Dados para graficos
        Map<String, Long> statusCount = locacaoRepo.contarPorStatus();
        m.addAttribute("chartPendente", statusCount.getOrDefault("pendente", 0L));
        m.addAttribute("chartAtiva", statusCount.getOrDefault("ativa", 0L));
        m.addAttribute("chartConcluida", statusCount.getOrDefault("concluida", 0L));
        m.addAttribute("chartCancelada", statusCount.getOrDefault("cancelada", 0L));

        Map<String, Long> tipoCount = locacaoRepo.contarPorTipoMaquina();
        m.addAttribute("chartTrator", tipoCount.getOrDefault("trator", 0L));
        m.addAttribute("chartColheitadeira", tipoCount.getOrDefault("colheitadeira", 0L));
        m.addAttribute("chartPulverizador", tipoCount.getOrDefault("pulverizador", 0L));
        long outros = tipoCount.values().stream().mapToLong(Long::longValue).sum()
            - tipoCount.getOrDefault("trator", 0L)
            - tipoCount.getOrDefault("colheitadeira", 0L)
            - tipoCount.getOrDefault("pulverizador", 0L);
        m.addAttribute("chartOutros", Math.max(0, outros));

        // Atividades recentes
        m.addAttribute("locacoesRecentes", locacaoRepo.listarRecentes(6));
        return "dashboard";
    }

    // --- EXPLORAR MAQUINAS ---
    @GetMapping("/explorar")
    public String explorar(HttpSession s, Model m,
                           @RequestParam(required=false) String tipo,
                           @RequestParam(required=false) String busca) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        List<Maquina> maquinas;
        if (tipo != null && !tipo.isEmpty()) {
            maquinas = maqRepo.listarDisponiveis().stream()
                    .filter(mq -> mq.getTipo().equalsIgnoreCase(tipo))
                    .toList();
        } else {
            maquinas = maqRepo.listarDisponiveis();
        }

        if(tipo != null && !tipo.isEmpty()) { 

        }

        if (busca != null && !busca.isEmpty()) {
            String b = busca.toLowerCase();
            maquinas = maquinas.stream()
                    .filter(mq -> mq.getNome().toLowerCase().contains(b)
                            || mq.getFabricante().toLowerCase().contains(b)
                            || mq.getLocalizacao() != null && mq.getLocalizacao().toLowerCase().contains(b))
                    .toList();
        }

        m.addAttribute("maquinas", maquinas);
        m.addAttribute("tipos", List.of("TRATOR", "COLHEITADEIRA", "PULVERIZADOR", "PLANTADEIRA"));
        m.addAttribute("tipoSelecionado", tipo);
        m.addAttribute("busca", busca);
        return "explorar";
    }

    // --- DETALHES MAQUINA ---
    @GetMapping("/maquinas/{id}")
    public String maquinaDetalhes(@PathVariable Long id, HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        Maquina maq = maqRepo.buscarPorId(id).orElse(null);
        if (maq == null) return "redirect:/explorar";

        List<ServicoAdicional> servicos = servicoRepo.listarTodos();
        m.addAttribute("servicosMaquinario", maq );
        m.addAttribute("servicoTelemetria" , maq );
        m.addAttribute("maquina", maq);
        m.addAttribute("servicos", servicos);
        m.addAttribute("telemetrias", telRepo.listarPorMaquina(id));
        return "maquina-detalhes";
    }

    // --- CRIAR LOCACAO (via form) ---
    @PostMapping("/locacoes/nova")
    public String criarLocacao(@RequestParam Long maquinaId,
                                @RequestParam String dataInicio,
                                @RequestParam String dataFim,
                                @RequestParam(required=false) List<Long> servicoIds,
                                @RequestParam(required=false) String observacoes,
                                HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        try {
            LocacaoRequest dto = new LocacaoRequest();
            dto.setMaquinaId(maquinaId);
            dto.setDataInicio(LocalDate.parse(dataInicio));
            dto.setDataFim(LocalDate.parse(dataFim));
            dto.setServicoIds(servicoIds);
            dto.setObservacoes(observacoes);

            Usuario usuario = usuarioRepo.buscarPorEmail((String) s.getAttribute("usuarioEmail"))
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            Locacao loc = locacaoService.criarLocacao(dto, usuario.getId());
            m.addAttribute("sucesso", "Solicitacao de locacao enviada com sucesso!");
            return "redirect:/meus-alugueis";
        } catch (Exception e) {
            m.addAttribute("erro", "Erro: " + e.getMessage());
            Maquina maq = maqRepo.buscarPorId(maquinaId).orElse(null);
            m.addAttribute("maquina", maq);
            m.addAttribute("servicos", servicoRepo.listarTodos());
            return "maquina-detalhes";
        }
    }

    // --- MEUS ALUGUEIS (Cliente) ---
    @GetMapping("/meus-alugueis")
    public String meusAlugueis(HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        try {
            Usuario usuario = usuarioRepo.buscarPorEmail((String) s.getAttribute("usuarioEmail"))
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
            List<Locacao> locacoes = locacaoService.listarLocacoesCliente(usuario.getId());
            m.addAttribute("locacoes", locacoes);
        } catch (Exception e) {
            m.addAttribute("erro", "Erro ao carregar alugueis");
            m.addAttribute("locacoes", List.of());
        }
        return "meus-alugueis";
    }

    // --- MINHAS LOCACOES (Proprietario) ---
    @GetMapping("/minhas-locacoes")
    public String minhasLocacoes(HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        Object propIdAttr = s.getAttribute("proprietarioId");
        if (propIdAttr == null) {
            m.addAttribute("erro", "Apenas proprietarios podem acessar esta pagina");
            return "redirect:/";
        }

        Long proprietarioId = (Long) propIdAttr;
        m.addAttribute("locacoes", locacaoService.listarLocacoesProprietario(proprietarioId));
        m.addAttribute("maquinas", propService.listarMaquinas(proprietarioId));
        return "minhas-locacoes";
    }

    // --- PERFIL DO CLIENTE (Proprietario / Admin) ---
    @GetMapping("/clientes/{id}")
    public String verPerfilCliente(@PathVariable Long id, HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;

        Usuario cliente = usuarioRepo.buscarPorId(id).orElse(null);
        if (cliente == null) {
            m.addAttribute("erro", "Cliente não encontrado.");
            return "redirect:/minhas-locacoes";
        }

        List<Locacao> locacoes = locacaoService.listarLocacoesCliente(id);
        long totalLocacoes = locacoes.size();
        long totalConcluidas = locacoes.stream().filter(l -> "CONCLUIDA".equalsIgnoreCase(l.getStatus())).count();
        long taxaConclusao = totalLocacoes > 0 ? (totalConcluidas * 100 / totalLocacoes) : 100;

        String whatsappUrl = null;
        if (cliente.getTelefone() != null && !cliente.getTelefone().trim().isEmpty()) {
            String nums = cliente.getTelefone().replaceAll("[^0-9]", "");
            if (!nums.isEmpty()) {
                if (!nums.startsWith("55") && nums.length() <= 11) {
                    nums = "55" + nums;
                }
                whatsappUrl = "https://wa.me/" + nums;
            }
        }

        m.addAttribute("cliente", cliente);
        m.addAttribute("locacoes", locacoes);
        m.addAttribute("totalLocacoes", totalLocacoes);
        m.addAttribute("totalConcluidas", totalConcluidas);
        m.addAttribute("taxaConclusao", taxaConclusao);
        m.addAttribute("whatsappUrl", whatsappUrl);

        return "cliente-perfil";
    }

    // --- ACOES LOCACAO (Proprietario) ---
    @PostMapping("/locacoes/{id}/aprovar")
    public String aprovarLocacao(@PathVariable Long id, HttpSession s) {
        if (!estaLogado(s)) return "redirect:/login";
        try { locacaoService.aprovarLocacao(id); } catch (Exception ignored) {}
        return "redirect:/minhas-locacoes";
    }

    @PostMapping("/locacoes/{id}/concluir")
    public String concluirLocacao(@PathVariable Long id, HttpSession s) {
        if (!estaLogado(s)) return "redirect:/login";
        try { locacaoService.concluirLocacao(id); } catch (Exception ignored) {}
        return "redirect:/minhas-locacoes";
    }

    @PostMapping("/locacoes/{id}/cancelar")
    public String cancelarLocacao(@PathVariable Long id, HttpSession s) {
        if (!estaLogado(s)) return "redirect:/login";
        try { locacaoService.cancelarLocacao(id); } catch (Exception ignored) {}
        String role = (String) s.getAttribute("usuarioRole");
        return "redirect:" + ("PROPRIETARIO".equals(role) ? "/minhas-locacoes" : "/meus-alugueis");
    }

    // --- ANUNCIAR MAQUINA (Proprietario) ---
    @GetMapping("/anunciar")
    public String anunciar(HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;
        if (!"PROPRIETARIO".equals(s.getAttribute("usuarioRole")) && !"ADMIN".equals(s.getAttribute("usuarioRole"))) {
            m.addAttribute("erro", "Apenas proprietarios podem anunciar maquinas");
            return "redirect:/";
        }
        m.addAttribute("tipos", List.of("TRATOR", "COLHEITADEIRA", "PULVERIZADOR", "PLANTADEIRA", "RETROESCAVADEIRA", "CAMINHAO"));
        return "anunciar";
    }

    @PostMapping("/anunciar")
    public String anunciarSalvar(@RequestParam String nome, @RequestParam String modelo,
                                  @RequestParam String fabricante, @RequestParam Integer anoFabricacao,
                                  @RequestParam String tipo, @RequestParam(required=false) Double horasUsoTotais,
                                  @RequestParam BigDecimal precoDiaria, @RequestParam(required=false) BigDecimal caucao,
                                  @RequestParam(required=false) String localizacao,
                                  @RequestParam(required=false) String descricao,
                                  HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) {
            // Redirecionamento já tratado
        }

        try {
            Object propId = s.getAttribute("proprietarioId");
            if (propId == null) {
                m.addAttribute("erro", "Perfil de proprietario nao encontrado");
                m.addAttribute("tipos", List.of("TRATOR", "COLHEITADEIRA", "PULVERIZADOR", "PLANTADEIRA"));
                return "anunciar";
            }

            Maquina maq = new Maquina();
            maq.setProprietarioId((Long) propId);
            maq.setNome(nome); maq.setModelo(modelo); maq.setFabricante(fabricante);
            maq.setAnoFabricacao(anoFabricacao); maq.setTipo(tipo);
            maq.setHorasUsoTotais(horasUsoTotais != null ? horasUsoTotais : 0);
            maq.setPrecoDiaria(precoDiaria);
            maq.setCaucao(caucao != null ? caucao : BigDecimal.ZERO);
            maq.setLocalizacao(localizacao); maq.setDescricao(descricao);
            maq.setDisponivel(true);

            maqRepo.criar(maq);
            m.addAttribute("sucesso", "Maquina anunciada com sucesso!");
        } catch (Exception e) {
            m.addAttribute("erro", "Erro: " + e.getMessage());
        }

        m.addAttribute("tipos", List.of("TRATOR", "COLHEITADEIRA", "PULVERIZADOR", "PLANTADEIRA", "RETROESCAVADEIRA", "CAMINHAO"));
        return "anunciar";
    }

    // --- SERVICOS ADICIONAIS ---
    @GetMapping("/servicos")
    public String servicos(HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;
        m.addAttribute("servicos", servicoRepo.listarTodos());
        return "servicos";
    }

    // --- LOCACAO DETALHES ---
    @GetMapping("/locacoes/{id}")
    public String locacaoDetalhes(@PathVariable Long id, HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;
        try {
            Locacao loc = locacaoService.buscarDetalhes(id);
            m.addAttribute("locacao", loc);
            m.addAttribute("servicos", locacaoServicoRepo.listarPorLocacao(id));
            m.addAttribute("telemetrias", telRepo.listarPorLocacao(id));
            return "locacao-detalhes";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    // --- REGISTRAR TELEMETRIA (Form Web) ---
    @PostMapping("/maquinas/{id}/telemetria")
    public String registrarTelemetriaWeb(@PathVariable Long id,
                                          @RequestParam Double horasUso,
                                          @RequestParam Double temperaturaMotor,
                                          @RequestParam Double consumoCombustivel,
                                          @RequestParam Double rpmMotor,
                                          @RequestParam Double pressaoOleo,
                                          @RequestParam(required=false) Long locacaoId,
                                          HttpSession s, Model m) {
        String r = redirectSeNaoLogado(s);
        if (r != null) return r;
        try {
            Telemetria t = new Telemetria();
            t.setMaquinaId(id);
            t.setLocacaoId(locacaoId);
            t.setHorasUso(horasUso);
            t.setTemperaturaMotor(temperaturaMotor);
            t.setConsumoCombustivel(consumoCombustivel);
            t.setRpmMotor(rpmMotor);
            t.setPressaoOleo(pressaoOleo);

            telemetriaService.registrarTelemetria(t);
            m.addAttribute("sucesso", "Registro de telemetria adicionado com sucesso!");
        } catch (Exception e) {
            m.addAttribute("erro", "Erro ao registrar telemetria: " + e.getMessage());
        }
        return "redirect:/maquinas/" + id;
    }

    // --- API para calculo de preço (JSON) ---
    @GetMapping("/api/calcular-preco")
    @ResponseBody
    public String calcularPreco(@RequestParam Long maquinaId, @RequestParam String inicio,
                                 @RequestParam String fim, @RequestParam(required=false) List<Long> servicos) {
        try {
            Maquina maq = maqRepo.buscarPorId(maquinaId).orElse(null);
            if (maq == null) return "0";
            LocalDate dataInicio = LocalDate.parse(inicio);
            LocalDate dataFim = LocalDate.parse(fim);
            long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
            BigDecimal total = maq.getPrecoDiaria().multiply(BigDecimal.valueOf(dias));
            if (servicos != null) {
                for (Long sId : servicos) {
                    ServicoAdicional serv = servicoRepo.buscarPorId(sId).orElse(null);
                    if (serv != null) total = total.add(serv.getPreco());
                }
            }
            return total.toString();
        } catch (Exception e) {
            return "0";
        }
    }
}
