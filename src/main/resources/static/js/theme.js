(function () {
  const chave = 'si_tema';
  const media = window.matchMedia('(prefers-color-scheme: dark)');

  function aplicar(preferencia) {
    const tema = preferencia === 'system' ? (media.matches ? 'dark' : 'light') : preferencia;
    document.documentElement.setAttribute('data-theme', tema === 'dark' ? 'dark' : 'light');
  }

  window.applyTheme = function (preferencia) {
    const valor = ['light', 'dark', 'system'].includes(preferencia) ? preferencia : 'dark';
    localStorage.setItem(chave, valor);
    aplicar(valor);
  };

  aplicar(localStorage.getItem(chave) || 'dark');
  media.addEventListener('change', () => {
    if (localStorage.getItem(chave) === 'system') aplicar('system');
  });
})();
