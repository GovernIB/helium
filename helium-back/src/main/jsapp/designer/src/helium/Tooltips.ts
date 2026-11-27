/**
 * Tooltips de l'editor (paleta d'eines, menú contextual dels elements, menú de canvi de tipus, barra d'eines i plafó
 * de propietats).
 *
 * Les llibreries posen el text a l'atribut title, i el navegador el mostra com a tooltip natiu. Dins l'IDE, però, el
 * navegador (JCEF fora de pantalla) no mostra els tooltips natius. Per tenir-los igual als dos llocs, en passar per
 * sobre d'un element amb title el text es mou a data-titol (perquè no surti també el natiu) i es mostra un tooltip
 * propi.
 */

const RETARD_MS = 450;
const MARGE = 6;

let tooltip: HTMLDivElement | null = null;
let temporitzador: number | undefined;
let actual: HTMLElement | null = null;

const titolDe = (element: HTMLElement): string | null => {
    const title = element.getAttribute('title');
    if (title) {
        // El text passa a data-titol: si el component el torna a posar, es tornarà a moure al següent pas del ratolí
        element.setAttribute('data-titol', title);
        element.removeAttribute('title');
        if (!element.getAttribute('aria-label') && !element.textContent?.trim()) {
            element.setAttribute('aria-label', title);
        }
    }
    return element.getAttribute('data-titol');
};

const amagar = () => {
    window.clearTimeout(temporitzador);
    temporitzador = undefined;
    actual = null;
    if (tooltip) {
        tooltip.style.display = 'none';
    }
};

const mostrar = (element: HTMLElement, text: string) => {
    if (!tooltip) {
        tooltip = document.createElement('div');
        tooltip.className = 'helium-tooltip';
        tooltip.setAttribute('role', 'tooltip');
        document.body.appendChild(tooltip);
    }
    tooltip.textContent = text;
    tooltip.style.display = 'block';
    tooltip.style.left = '0px';
    tooltip.style.top = '0px';
    const rect = element.getBoundingClientRect();
    const mida = tooltip.getBoundingClientRect();
    // A la paleta (vertical, a l'esquerra) a la dreta de l'eina, per no tapar les del costat
    if (element.closest('.djs-palette')) {
        tooltip.style.left = `${Math.round(rect.right + MARGE)}px`;
        tooltip.style.top = `${Math.round(Math.min(Math.max(MARGE, rect.top + rect.height / 2 - mida.height / 2),
            window.innerHeight - mida.height - MARGE))}px`;
        return;
    }
    // Per defecte a sota de l'element; si no hi cap, a sobre. Sempre dins la finestra
    let top = rect.bottom + MARGE;
    if (top + mida.height > window.innerHeight - MARGE) {
        top = Math.max(MARGE, rect.top - mida.height - MARGE);
    }
    let left = rect.left + rect.width / 2 - mida.width / 2;
    left = Math.min(Math.max(MARGE, left), window.innerWidth - mida.width - MARGE);
    tooltip.style.left = `${Math.round(left)}px`;
    tooltip.style.top = `${Math.round(top)}px`;
};

const enEntrar = (event: MouseEvent) => {
    const objectiu = event.target instanceof Element
        ? (event.target.closest('[title], [data-titol]') as HTMLElement | null)
        : null;
    if (objectiu === actual) {
        return;
    }
    amagar();
    if (!objectiu) {
        return;
    }
    const text = titolDe(objectiu);
    if (!text) {
        return;
    }
    actual = objectiu;
    temporitzador = window.setTimeout(() => {
        if (actual === objectiu && objectiu.isConnected) {
            mostrar(objectiu, text);
        }
    }, RETARD_MS);
};

/** Activa els tooltips propis a tot el document. Només s'ha de cridar una vegada. */
export const activarTooltips = () => {
    document.addEventListener('mouseover', enEntrar, true);
    document.addEventListener('mousedown', amagar, true);
    document.addEventListener('keydown', amagar, true);
    document.addEventListener('wheel', amagar, { capture: true, passive: true });
    document.addEventListener('scroll', amagar, true);
    window.addEventListener('blur', amagar);
    document.documentElement.addEventListener('mouseleave', amagar);
};
