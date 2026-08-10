document.addEventListener("DOMContentLoaded", function () {
    // Saudacao dinamica
    var el = document.getElementById("saudacaoDinamica");
    if (el) {
        var h = new Date().getHours();
        el.textContent = (h >= 5 && h < 12 ? "Bom dia" : h >= 12 && h < 18 ? "Boa tarde" : "Boa noite") + ",";
    }

    // Active link na navbar
    var path = window.location.pathname;
    document.querySelectorAll(".navbar .nav-link").forEach(function (link) {
        var href = link.getAttribute("href");
        if (href && (path === href || (href !== "/" && path.startsWith(href)))) {
            link.classList.add("active");
        }
    });

    // Classe CSS na pagina para cores de aba
    var pageMap = {
        "/": "page-dashboard",
        "/explorar": "page-explorar",
        "/meus-alugueis": "page-alugueis",
        "/minhas-locacoes": "page-locacoes",
        "/anunciar": "page-anunciar",
        "/servicos": "page-servicos"
    };
    for (var p in pageMap) {
        if (path === p || (p !== "/" && path.startsWith(p))) {
            document.body.classList.add(pageMap[p]);
            break;
        }
    }

    // Loading state em formularios
    document.querySelectorAll("form").forEach(function (form) {
        form.addEventListener("submit", function () {
            var btn = form.querySelector("button[type=submit]");
            if (btn && !btn.classList.contains("no-loading")) {
                btn.classList.add("btn-loading");
            }
        });
    });

    // Dark Mode Toggle Logic
    const themeToggleBtn = document.getElementById('theme-toggle');
    if (themeToggleBtn) {
        const currentTheme = localStorage.getItem('theme') || 'light';
        if (currentTheme === 'dark') {
            document.documentElement.setAttribute('data-bs-theme', 'dark');
        } else {
            document.documentElement.setAttribute('data-bs-theme', 'light');
        }

        themeToggleBtn.addEventListener('click', function () {
            let theme = document.documentElement.getAttribute('data-bs-theme');
            if (theme === 'dark') {
                theme = 'light';
            } else {
                theme = 'dark';
            }
            document.documentElement.setAttribute('data-bs-theme', theme);
            localStorage.setItem('theme', theme);
        });
    }
});

// ===== TOAST =====
function showToast(message, type) {
    type = type || "success";
    var container = document.getElementById("toastContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "toastContainer";
        container.className = "toast-container";
        document.body.appendChild(container);
    }

    var icons = { success: "✓", error: "✕", info: "i" };
    var toast = document.createElement("div");
    toast.className = "toast-agri toast-" + type;
    toast.innerHTML =
        '<div class="toast-icon">' + (icons[type] || "i") + '</div>' +
        '<div class="toast-text">' + message + '</div>' +
        '<button class="toast-close" onclick="this.parentElement.remove()">&times;</button>';

    container.appendChild(toast);
    setTimeout(function () {
        if (toast.parentElement) {
            toast.style.opacity = "0";
            toast.style.transition = "opacity 0.3s ease";
            setTimeout(function () { if (toast.parentElement) toast.remove(); }, 300);
        }
    }, 4000);
}

// ===== MODAL CONFIRMACAO =====
function confirmAction(title, text, callback) {
    var modal = document.getElementById("confirmModal");
    if (!modal) {
        modal = document.createElement("div");
        modal.id = "confirmModal";
        modal.className = "modal fade";
        modal.tabIndex = -1;
        modal.setAttribute("data-bs-backdrop", "static");
        modal.innerHTML =
            '<div class="modal-dialog modal-sm modal-dialog-centered">' +
            '<div class="modal-content" style="border-radius:12px;border:1px solid #e2e8f0;">' +
            '<div class="modal-body text-center py-4">' +
            '<div class="mb-3" style="width:48px;height:48px;border-radius:50%;background:#fef3c7;display:flex;align-items:center;justify-content:center;margin:0 auto;">' +
            '<span style="font-size:1.2rem;font-weight:700;color:#d97706;">?</span></div>' +
            '<h6 class="fw-bold mb-1" id="confirmTitle"></h6>' +
            '<p class="text-muted small mb-3" id="confirmText"></p>' +
            '<div class="d-flex gap-2 justify-content-center">' +
            '<button class="btn btn-outline-secondary btn-sm no-loading" data-bs-dismiss="modal">Cancelar</button>' +
            '<button class="btn btn-success btn-sm no-loading" id="confirmBtn">Confirmar</button>' +
            '</div></div></div></div>';
        document.body.appendChild(modal);
    }
    document.getElementById("confirmTitle").textContent = title;
    document.getElementById("confirmText").textContent = text;
    var btn = document.getElementById("confirmBtn");
    var newBtn = btn.cloneNode(true);
    btn.parentNode.replaceChild(newBtn, btn);

    var bsModal = new bootstrap.Modal(modal);
    bsModal.show();

    newBtn.addEventListener("click", function () {
        bsModal.hide();
        if (callback) callback();
    });
}

// ===== FUNCOES EXISTENTES =====
function validarSenha() {
    var input = document.getElementById("senha");
    var feedback = document.getElementById("feedbackSenha");
    if (!input || !feedback) return;
    var senha = input.value;
    if (senha.length === 0) {
        feedback.style.display = "none";
        input.classList.remove("campo-valido", "campo-invalido");
        return;
    }
    feedback.style.display = "block";
    if (senha.length < 6) {
        feedback.textContent = "A senha deve ter no minimo 6 caracteres.";
        feedback.style.color = "#dc2626";
        input.classList.add("campo-invalido");
        input.classList.remove("campo-valido");
    } else {
        feedback.textContent = "Senha valida.";
        feedback.style.color = "#16a34a";
        input.classList.add("campo-valido");
        input.classList.remove("campo-invalido");
    }
}

function toggleProprietarioFields() {
    var role = document.getElementById("role");
    var extra = document.getElementById("proprietario-fields");
    if (!role || !extra) return;
    extra.style.display = role.value === "PROPRIETARIO" ? "block" : "none";
}

function calcularTotal() {
    var inicio = document.getElementById("dataInicio");
    var fim = document.getElementById("dataFim");
    var maquinaId = document.querySelector('input[name=maquinaId]');
    var valorEl = document.getElementById("valorEstimado");
    if (!inicio || !fim || !maquinaId || !valorEl) return;
    if (!inicio.value || !fim.value) return;
    var servicos = [];
    document.querySelectorAll('input[name=servicoIds]:checked').forEach(function (cb) { servicos.push(cb.value); });
    var url = '/api/calcular-preco?maquinaId=' + maquinaId.value + '&inicio=' + inicio.value + '&fim=' + fim.value;
    if (servicos.length > 0) url += '&servicos=' + servicos.join(',');
    fetch(url).then(function (r) { return r.text(); }).then(function (valor) {
        var num = parseFloat(valor);
        valorEl.textContent = 'R$ ' + num.toFixed(2).replace('.', ',');
    }).catch(function () { valorEl.textContent = 'R$ 0,00'; });
}

function filtrarMaquinasTempoReal() {
    var input = document.getElementById("buscaTempoReal");
    if (!input) return;
    var termo = input.value.toLowerCase().trim();
    var cards = document.querySelectorAll(".machine-card[data-busca]");
    var encontrados = 0;
    cards.forEach(function (card) {
        var texto = card.getAttribute("data-busca").toLowerCase();
        if (termo === "" || texto.includes(termo)) {
            card.classList.remove("card-oculto");
            encontrados++;
        } else {
            card.classList.add("card-oculto");
        }
    });
    var contador = document.getElementById("contadorResultados");
    if (contador) {
        contador.textContent = encontrados + " maquina(s) encontrada(s)";
        contador.style.color = encontrados === 0 ? "#dc2626" : "#16a34a";
    }
    var semResultado = document.getElementById("semResultadoFiltro");
    if (semResultado) semResultado.style.display = encontrados === 0 ? "block" : "none";
}
