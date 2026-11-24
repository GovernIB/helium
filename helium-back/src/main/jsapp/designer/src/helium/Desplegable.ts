import { h } from '@bpmn-io/properties-panel/preact';
import { useEffect, useMemo, useRef, useState } from '@bpmn-io/properties-panel/preact/hooks';

/** Element de la llista: una opció o el títol d'un grup (no seleccionable). */
export type ElementDesplegable =
    | { tipus: 'opcio'; valor: string; text: string; detall?: string }
    | { tipus: 'grup'; text: string };

type Props = {
    id: string;
    label: string;
    valor: string;
    elements: ElementDesplegable[];
    onChange: (valor: string) => void;
};

type Posicio = { left: number; width: number; maxHeight: number; top?: number; bottom?: number };

const MARGE = 8;
/** Per sota d'aquesta alçada, si a sobre hi ha més espai, la llista s'obre cap amunt. */
const ALCADA_MINIMA = 200;

const normalitzar = (text: string) => text.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

/**
 * Desplegable amb cerca, amb l'aspecte d'un select del plafó de propietats.
 *
 * Substitueix el select natiu per a llistes llargues: dins el navegador incrustat de l'IDE (JCEF sense finestra
 * pròpia) la llista d'un select natiu no pot sortir del navegador i queda tallada. Aquesta es col·loca a sota o a
 * sobre segons l'espai que hi ha, amb barra de desplaçament, i es pot filtrar escrivint.
 */
export const Desplegable = (props: Props) => {
    const { id, label, valor, elements, onChange } = props;
    const idInput = 'bio-properties-panel-' + id;
    const idLlista = idInput + '-llista';
    const [obert, setObert] = useState(false);
    const [cerca, setCerca] = useState('');
    /**
     * Opció activa (índex a visibles). La llista només s'hi desplaça si l'ha canviat el teclat o l'obertura: si la
     * canvia el ratolí, l'opció ja és visible, i desplaçar la llista en posaria una altra sota el cursor (també quan
     * la llista es desplaça amb la roda, que fa que el navegador enviï mousemove).
     */
    const [{ index: actiu, desplacar }, setActiuEstat] = useState({ index: -1, desplacar: false });
    const setActiu = (index: number, desplacarFins = true) => setActiuEstat({ index, desplacar: desplacarFins });
    const [posicio, setPosicio] = useState<Posicio | null>(null);
    const boto = useRef<HTMLButtonElement | null>(null);
    const capa = useRef<HTMLDivElement | null>(null);
    const llista = useRef<HTMLUListElement | null>(null);
    const inputCerca = useRef<HTMLInputElement | null>(null);

    // Elements visibles amb la cerca: una opció es mostra si coincideix ella o el seu grup, i un grup si té opcions
    const visibles = useMemo(() => {
        const filtre = normalitzar(cerca.trim());
        if (!filtre) {
            return elements;
        }
        const resultat: ElementDesplegable[] = [];
        let grup: ElementDesplegable | null = null;
        let grupCoincideix = false;
        let grupAfegit = false;
        for (const element of elements) {
            if (element.tipus === 'grup') {
                grup = element;
                grupCoincideix = normalitzar(element.text).includes(filtre);
                grupAfegit = false;
                continue;
            }
            const coincideix = grupCoincideix
                || normalitzar(element.text).includes(filtre)
                || normalitzar(element.detall || element.valor).includes(filtre);
            if (coincideix) {
                if (grup && !grupAfegit) {
                    resultat.push(grup);
                    grupAfegit = true;
                }
                resultat.push(element);
            }
        }
        return resultat;
    }, [elements, cerca]);

    const opcions = visibles
        .map((element, index) => ({ element, index }))
        .filter(({ element }) => element.tipus === 'opcio');

    const seleccionat = elements.find((e) => e.tipus === 'opcio' && e.valor === valor);
    const textSeleccionat = seleccionat ? seleccionat.text : valor;

    const calcularPosicio = (): Posicio | null => {
        const rect = boto.current?.getBoundingClientRect();
        if (!rect) {
            return null;
        }
        const width = Math.min(Math.max(rect.width, 240), window.innerWidth - 2 * MARGE);
        const left = Math.max(MARGE, Math.min(rect.left, window.innerWidth - width - MARGE));
        const espaiSota = window.innerHeight - rect.bottom - MARGE;
        const espaiSobre = rect.top - MARGE;
        if (espaiSota >= ALCADA_MINIMA || espaiSota >= espaiSobre) {
            return { left, width, top: rect.bottom + 2, maxHeight: espaiSota };
        }
        return { left, width, bottom: window.innerHeight - rect.top + 2, maxHeight: espaiSobre };
    };

    const obrir = () => {
        setCerca('');
        setPosicio(calcularPosicio());
        setObert(true);
    };

    const tancar = (tornarAlBoto: boolean) => {
        setObert(false);
        if (tornarAlBoto) {
            boto.current?.focus();
        }
    };

    const triar = (nouValor: string) => {
        tancar(true);
        if (nouValor !== valor) {
            onChange(nouValor);
        }
    };

    // En obrir (o en canviar la cerca) l'opció activa és la seleccionada, o la primera visible. Només llavors: la
    // llista d'elements es torna a construir a cada render, i si depengués d'ella l'opció activa tornaria a la
    // seleccionada (i la llista s'hi desplaçaria) cada cop que el ratolí passa per sobre d'una altra opció.
    useEffect(() => {
        if (!obert) {
            return;
        }
        const index = visibles.findIndex((e) => e.tipus === 'opcio' && e.valor === valor);
        setActiu(index >= 0 ? index : (opcions[0]?.index ?? -1));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [obert, cerca]);

    useEffect(() => {
        if (obert) {
            inputCerca.current?.focus();
        }
    }, [obert]);

    useEffect(() => {
        if (obert && actiu >= 0 && desplacar) {
            llista.current?.querySelector(`[data-index="${actiu}"]`)?.scrollIntoView({ block: 'nearest' });
        }
    }, [obert, actiu, desplacar]);

    // Es tanca en fer clic fora, en desplaçar el plafó o en canviar la mida de la finestra (la posició és fixa)
    useEffect(() => {
        if (!obert) {
            return;
        }
        const clicFora = (event: MouseEvent) => {
            const objectiu = event.target as Node;
            if (!capa.current?.contains(objectiu) && !boto.current?.contains(objectiu)) {
                tancar(false);
            }
        };
        const desplacament = (event: Event) => {
            if (!capa.current?.contains(event.target as Node)) {
                tancar(false);
            }
        };
        const canviMida = () => tancar(false);
        document.addEventListener('mousedown', clicFora, true);
        window.addEventListener('scroll', desplacament, true);
        window.addEventListener('resize', canviMida);
        return () => {
            document.removeEventListener('mousedown', clicFora, true);
            window.removeEventListener('scroll', desplacament, true);
            window.removeEventListener('resize', canviMida);
        };
    }, [obert]);

    const moure = (pas: number) => {
        if (!opcions.length) {
            return;
        }
        const posicioActual = opcions.findIndex((o) => o.index === actiu);
        const nova = posicioActual < 0
            ? (pas > 0 ? 0 : opcions.length - 1)
            : Math.max(0, Math.min(opcions.length - 1, posicioActual + pas));
        setActiu(opcions[nova].index);
    };

    const teclaCerca = (event: KeyboardEvent) => {
        switch (event.key) {
            case 'ArrowDown':
                moure(1);
                break;
            case 'ArrowUp':
                moure(-1);
                break;
            case 'PageDown':
                moure(10);
                break;
            case 'PageUp':
                moure(-10);
                break;
            case 'Enter': {
                const element = visibles[actiu];
                if (element?.tipus === 'opcio') {
                    triar(element.valor);
                }
                break;
            }
            case 'Escape':
                tancar(true);
                break;
            case 'Tab':
                tancar(false);
                return;
            default:
                return;
        }
        event.preventDefault();
        event.stopPropagation();
    };

    const teclaBoto = (event: KeyboardEvent) => {
        if (['ArrowDown', 'ArrowUp', 'Enter', ' '].includes(event.key)) {
            event.preventDefault();
            obrir();
        }
    };

    const estilCapa = posicio
        ? {
            left: posicio.left + 'px',
            width: posicio.width + 'px',
            maxHeight: posicio.maxHeight + 'px',
            ...(posicio.top !== undefined ? { top: posicio.top + 'px' } : { bottom: posicio.bottom + 'px' }),
        }
        : {};

    return h('div', { class: 'bio-properties-panel-entry', 'data-entry-id': id },
        h('div', { class: 'bio-properties-panel-select helium-desplegable' },
            h('label', { for: idInput, class: 'bio-properties-panel-label' }, label),
            h('button', {
                ref: boto,
                id: idInput,
                type: 'button',
                // El valor el fa servir el plafó per marcar el grup com a editat
                value: valor,
                class: 'bio-properties-panel-input helium-desplegable-boto',
                'aria-haspopup': 'listbox',
                'aria-expanded': obert,
                'aria-controls': obert ? idLlista : undefined,
                title: seleccionat?.tipus === 'opcio' ? seleccionat.detall : undefined,
                onClick: () => (obert ? tancar(true) : obrir()),
                onKeyDown: teclaBoto,
            },
            h('span', { class: 'helium-desplegable-text' }, textSeleccionat),
            h('svg', { width: 12, height: 12, viewBox: '0 0 12 12', 'aria-hidden': 'true' },
                h('path', { d: 'M2.5 4.5 6 8l3.5-3.5', fill: 'none', stroke: 'currentColor', 'stroke-width': 1.5 })))),
        obert && h('div', { ref: capa, class: 'helium-desplegable-capa', style: estilCapa },
            h('input', {
                class: 'bio-properties-panel-input helium-desplegable-cerca',
                type: 'search',
                placeholder: 'Cercar…',
                'aria-label': 'Cercar ' + label.toLowerCase(),
                'aria-controls': idLlista,
                'aria-activedescendant': actiu >= 0 ? `${idLlista}-${actiu}` : undefined,
                value: cerca,
                ref: inputCerca,
                onInput: (event: Event) => setCerca((event.target as HTMLInputElement).value),
                onKeyDown: teclaCerca,
            }),
            h('ul', { ref: llista, id: idLlista, role: 'listbox', class: 'helium-desplegable-llista' },
                visibles.length === 0 && h('li', { class: 'helium-desplegable-buit' }, 'Cap coincidència'),
                visibles.map((element, index) => element.tipus === 'grup'
                    ? h('li', { key: 'g' + index, role: 'presentation', class: 'helium-desplegable-grup' }, element.text)
                    : h('li', {
                        key: element.valor || '(cap)',
                        id: `${idLlista}-${index}`,
                        'data-index': index,
                        role: 'option',
                        'aria-selected': element.valor === valor,
                        title: element.detall,
                        class: 'helium-desplegable-opcio'
                            + (index === actiu ? ' actiu' : '')
                            + (element.valor === valor ? ' seleccionat' : ''),
                        // Sense perdre el focus de la cerca
                        onMouseDown: (event: MouseEvent) => event.preventDefault(),
                        onMouseMove: () => {
                            if (index !== actiu) {
                                setActiu(index, false);
                            }
                        },
                        onClick: () => triar(element.valor),
                    }, element.text)))));
};
