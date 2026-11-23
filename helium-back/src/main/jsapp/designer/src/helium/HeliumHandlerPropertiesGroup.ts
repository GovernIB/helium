import { ArrowIcon, CreateIcon, SelectEntry, isSelectEntryEdited, useLayoutState } from '@bpmn-io/properties-panel';
import { h } from '@bpmn-io/properties-panel/preact';
import { useEffect, useMemo, useRef, useState } from '@bpmn-io/properties-panel/preact/hooks';
import { useService } from 'bpmn-js-properties-panel';
import { getHandlersSync } from '../flowable/handlers';
import {
    PARAMETRE_TYPE,
    assignarTextParametre,
    carregarParametres,
    descripcioParametre,
    ensureHeliumHandler,
    esExpressio,
    getHeliumHandler,
    nomsParametres,
    textParametre,
} from './heliumHandler';

type BpmnElement = {
    businessObject: any;
};

type EntryProps = {
    id: string;
    element: BpmnElement;
};

const TITOL_HANDLERS_PROPIS = 'HANDLERS PROPIS';

/** Entrada per triar la classe del handler. En canviar-la es carreguen els seus paràmetres. */
export const createHeliumHandlerClassEntry = () => ({
    id: 'helium-handler-classe',
    component: HeliumHandlerClassField,
    isEdited: isSelectEntryEdited,
});

const HeliumHandlerClassField = (props: EntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const translate = useService('translate');
    const currentValue = getHeliumHandler(element.businessObject)?.classe;
    return SelectEntry({
        element,
        id: 'helium-handler-classe',
        label: translate('Handler'),
        getValue: () => getHeliumHandler(element.businessObject)?.classe || '',
        setValue: (value: string) => {
            const heliumHandler = ensureHeliumHandler(element, modeling);
            const anterior = heliumHandler.classe;
            modeling.updateModdleProperties(element, heliumHandler, { classe: value });
            if (value && value !== anterior) {
                const handler = getHandlersSync().find((h) => h.classe === value);
                if (handler) {
                    carregarParametres(element, modeling, heliumHandler, handler);
                }
            }
        },
        getOptions: () => {
            const options: { value: string | null; label: string; disabled?: boolean }[] = [];
            const handlers = getHandlersSync();
            // Els predefinits arriben agrupats; els propis (pujats com a recursos) no tenen agrupació
            const predefinits = handlers.filter((h) => h.agrupacio);
            const propis = handlers.filter((h) => !h.agrupacio);
            let agrupacio: string | undefined | null = null;
            for (const handler of predefinits) {
                if (handler.agrupacio !== agrupacio) {
                    agrupacio = handler.agrupacio;
                    options.push({ value: null, label: String(agrupacio), disabled: true });
                }
                options.push({ value: handler.classe, label: handler.nom || handler.classe });
            }
            if (propis.length) {
                options.push({ value: null, label: TITOL_HANDLERS_PROPIS, disabled: true });
                for (const handler of propis) {
                    // Si dues classes de paquets diferents tenen el mateix nom es mostra el nom complet
                    const repetit = propis.some((h) => h !== handler && h.nom === handler.nom);
                    options.push({
                        value: handler.classe,
                        label: !handler.nom || repetit ? handler.classe : handler.nom,
                    });
                }
            }
            // El valor actual sempre ha de ser a la llista, encara que no sigui al catàleg
            if (currentValue && !options.some((o) => o.value === currentValue)) {
                options.unshift({ value: currentValue, label: currentValue });
            }
            return [{ value: '', label: '<none>' }, ...options];
        },
    });
};

/**
 * Paràmetres (helium:parametre) del handler, en una taula nom · valor. El valor pot ser un literal o una
 * expressió (${...}), que pot llegir les dades de l'expedient.
 *
 * La capçalera és com la dels ListGroup del panell (botó d'afegir, nombre de paràmetres i fletxa), però el contingut
 * és la taula en lloc d'una llista d'elements desplegables.
 */
export const createHeliumParametresGroup = () => ({
    id: 'helium-parametres',
    label: 'Paràmetres del handler',
    component: ParametresGroup,
});

type GroupProps = {
    id: string;
    label: string;
    element: BpmnElement;
};

const ParametresGroup = (props: GroupProps) => {
    const { id, label, element } = props;
    const modeling = useService('modeling');
    const translate = useService('translate');
    const [open, setOpen] = useLayoutState(['groups', id, 'open'], false);
    const parametres: any[] = getHeliumHandler(element.businessObject)?.parametres || [];
    const hasItems = parametres.length > 0;

    const afegir = (event: any) => {
        event.stopPropagation();
        const handler = ensureHeliumHandler(element, modeling);
        const parametre = element.businessObject.$model.create(PARAMETRE_TYPE, { nom: '', valor: '' });
        parametre.$parent = handler;
        modeling.updateModdleProperties(element, handler, {
            parametres: [...(handler.parametres || []), parametre],
        });
        setOpen(true);
    };

    return h('div', { class: 'bio-properties-panel-group', 'data-group-id': 'group-' + id },
        h('div', {
            class: 'bio-properties-panel-group-header' + (hasItems ? '' : ' empty') + (hasItems && open ? ' open' : ''),
            onClick: hasItems ? () => setOpen(!open) : undefined,
        },
            h('div', { class: 'bio-properties-panel-group-header-title' }, label),
            h('div', { class: 'bio-properties-panel-group-header-buttons' },
                h('button', {
                    type: 'button',
                    title: 'Afegir un paràmetre',
                    class: 'bio-properties-panel-group-header-button bio-properties-panel-add-entry',
                    onClick: afegir,
                },
                    h(CreateIcon, null),
                    !hasItems && h('span', { class: 'bio-properties-panel-add-entry-label' }, translate('Create'))),
                hasItems && h('div', {
                    class: 'bio-properties-panel-list-badge',
                    title: `${parametres.length} paràmetre${parametres.length !== 1 ? 's' : ''} configurat${parametres.length !== 1 ? 's' : ''}`,
                }, parametres.length),
                hasItems && h('button', {
                    type: 'button',
                    title: translate('Toggle section'),
                    class: 'bio-properties-panel-group-header-button bio-properties-panel-arrow',
                }, h(ArrowIcon, { class: open ? 'bio-properties-panel-arrow-down' : 'bio-properties-panel-arrow-right' })))),
        h('div', { class: 'bio-properties-panel-group-entries' + (open && hasItems ? ' open' : '') },
            h(ParametresTaula, { element })));
};

// Clau estable per fila: el mateix objecte helium:parametre conserva la seva fila encara que se n'eliminin d'altres
const clausFiles = new WeakMap<object, number>();
let seguentClau = 0;
const clauFila = (parametre: object): number => {
    let clau = clausFiles.get(parametre);
    if (clau === undefined) {
        clau = seguentClau++;
        clausFiles.set(parametre, clau);
    }
    return clau;
};

const ParametresTaula = (props: { element: BpmnElement }) => {
    const { element } = props;
    const modeling = useService('modeling');
    const heliumHandler = getHeliumHandler(element.businessObject);
    const parametres: any[] = heliumHandler?.parametres || [];
    const handlerCataleg = getHandlersSync().find((h) => h.classe === heliumHandler?.classe);
    const nomsCataleg = handlerCataleg ? nomsParametres(handlerCataleg) : [];
    const idLlista = `helium-parametres-noms-${element.businessObject.id}`;

    const eliminar = (parametre: any) => {
        modeling.updateModdleProperties(element, heliumHandler, {
            parametres: parametres.filter((p) => p !== parametre),
        });
    };

    return h('div', { class: 'helium-parametres' },
        parametres.length > 0 && h('table', { class: 'helium-parametres-taula' },
            h('thead', null,
                h('tr', null,
                    h('th', { class: 'helium-parametres-nom' }, 'Nom'),
                    h('th', null, 'Valor'),
                    h('th', { class: 'helium-parametres-accions' }))),
            h('tbody', null,
                parametres.map((parametre) => {
                    const descripcio = descripcioParametre(handlerCataleg, parametre.nom);
                    const text = textParametre(parametre);
                    return h('tr', { key: clauFila(parametre) },
                        h('td', { class: 'helium-parametres-nom' },
                            h(CellaText, {
                                valor: parametre.nom || '',
                                title: descripcio || parametre.nom,
                                list: idLlista,
                                onCanvi: (valor: string) => modeling.updateModdleProperties(element, parametre, { nom: valor }),
                            })),
                        h('td', null,
                            h(CellaText, {
                                valor: text,
                                placeholder: descripcio,
                                title: descripcio,
                                expressio: esExpressio(text),
                                onCanvi: (valor: string) => assignarTextParametre(element, modeling, parametre, valor),
                            }),
                            parametre.expressio && parametre.valor && h('div', { class: 'helium-parametres-defecte' },
                                `Si l'expressió no té valor: ${parametre.valor}`)),
                        h('td', { class: 'helium-parametres-accions' },
                            h('button', {
                                type: 'button',
                                class: 'helium-parametres-eliminar',
                                title: 'Eliminar el paràmetre',
                                'aria-label': `Eliminar el paràmetre ${parametre.nom || ''}`,
                                onClick: () => eliminar(parametre),
                            }, '\u00d7')));
                }))),
        h('datalist', { id: idLlista }, nomsCataleg.map((nom) => h('option', { key: nom, value: nom }))),
        h('div', { class: 'helium-parametres-peu bio-properties-panel-description' },
            'El valor pot ser un literal o una expressió que llegeixi les dades de l\'expedient, p. ex. ${codiDada}.'));
};

type CellaTextProps = {
    valor: string;
    onCanvi: (valor: string) => void;
    placeholder?: string;
    title?: string;
    list?: string;
    expressio?: boolean;
};

/** Camp de text d'una cel·la: desa el valor amb debounce, com els camps del panell de propietats. */
const CellaText = (props: CellaTextProps) => {
    const { valor, onCanvi, placeholder, title, list, expressio } = props;
    const debounce = useService('debounceInput');
    const [text, setText] = useState(valor);
    const onCanviRef = useRef(onCanvi);
    useEffect(() => {
        onCanviRef.current = onCanvi;
    });
    const desar = useMemo(() => debounce((v: string) => onCanviRef.current(v)), [debounce]);
    useEffect(() => setText(valor), [valor]);
    return h('input', {
        type: 'text',
        class: 'bio-properties-panel-input' + (expressio || esExpressio(text) ? ' helium-parametres-expressio' : ''),
        value: text,
        placeholder: placeholder || undefined,
        title: title || undefined,
        list,
        spellcheck: false,
        autocomplete: 'off',
        onInput: (event: any) => {
            setText(event.target.value);
            desar(event.target.value);
        },
    });
};
