import { ArrowIcon, CreateIcon, useLayoutState } from '@bpmn-io/properties-panel';
import { h } from '@bpmn-io/properties-panel/preact';
import { useService } from 'bpmn-js-properties-panel';
import { CellaText } from './HeliumHandlerPropertiesGroup';
import { esExpressio } from './heliumHandler';

/**
 * Dades de l'expedient que es passen entre un procés i el subprocés que crida (activitat de crida). Ha de coincidir
 * amb es.caib.helium.bpmn.handler.HeliumBpmnXml (ELEMENT_ENTRADA, ELEMENT_SORTIDA) i HeliumCallActivityBehavior:
 *
 *   <bpmn:callActivity calledElement="subproces">
 *     <bpmn:extensionElements>
 *       <helium:entrada origen="dadaProces" desti="dadaSubproces"/>
 *       <helium:entrada expressio="${import * 2}" desti="doble"/>
 *       <helium:sortida origen="resultat" desti="resultatSubproces"/>
 *     </bpmn:extensionElements>
 *   </bpmn:callActivity>
 *
 * Les entrades es copien al subprocés abans que comenci; les sortides, al procés quan el subprocés acaba. L'origen és
 * el codi d'una dada o una expressió (s'avalua a la instància d'origen); si no hi ha destí, té el mateix codi.
 */
export type TipusDades = 'entrada' | 'sortida';

const TIPUS_MODDLE: Record<TipusDades, string> = {
    entrada: 'helium:Entrada',
    sortida: 'helium:Sortida',
};

type BpmnElement = {
    businessObject: any;
};

type Modeling = {
    updateProperties: (element: BpmnElement, properties: Record<string, any>) => void;
    updateModdleProperties: (element: BpmnElement, moddleElement: any, properties: Record<string, any>) => void;
};

export const getDadesSubproces = (businessObject: any, tipus: TipusDades): any[] =>
    (businessObject?.extensionElements?.values || []).filter((v: any) => v.$type === TIPUS_MODDLE[tipus]);

/** Text de l'origen tal com es mostra a l'editor: l'expressió, si en té, o el codi de la dada. */
export const textOrigen = (dada: any): string => dada?.expressio || dada?.origen || '';

/** Assigna l'origen a partir del text de l'editor: si és una expressió es desa a expressio, si no a origen. */
export const assignarOrigen = (element: BpmnElement, modeling: Modeling, dada: any, text: string) => {
    if (esExpressio(text)) {
        modeling.updateModdleProperties(element, dada, { expressio: text, origen: undefined });
    } else {
        modeling.updateModdleProperties(element, dada, { origen: text, expressio: undefined });
    }
};

const afegirDada = (element: BpmnElement, modeling: Modeling, tipus: TipusDades) => {
    const businessObject = element.businessObject;
    const moddle = businessObject.$model;
    let extensionElements = businessObject.extensionElements;
    if (!extensionElements) {
        extensionElements = moddle.create('bpmn:ExtensionElements', { values: [] });
        extensionElements.$parent = businessObject;
        modeling.updateProperties(element, { extensionElements });
    }
    const dada = moddle.create(TIPUS_MODDLE[tipus], { origen: '', desti: '' });
    dada.$parent = extensionElements;
    modeling.updateModdleProperties(element, extensionElements, {
        values: [...(extensionElements.values || []), dada],
    });
};

const eliminarDada = (element: BpmnElement, modeling: Modeling, dada: any) => {
    const extensionElements = element.businessObject.extensionElements;
    const values = (extensionElements?.values || []).filter((v: any) => v !== dada);
    if (values.length === 0) {
        // No deixar un <bpmn:extensionElements/> buit
        modeling.updateProperties(element, { extensionElements: undefined });
    } else {
        modeling.updateModdleProperties(element, extensionElements, { values });
    }
};

const TEXTOS: Record<TipusDades, { titol: string; descripcio: string; origen: string; desti: string }> = {
    entrada: {
        titol: "Dades d'entrada",
        descripcio: "Es copien del procés al subprocés abans que comenci. L'origen és el codi d'una dada del procés o "
            + "una expressió (p. ex. ${import * 2}); si no s'indica el destí, la dada del subprocés té el mateix codi.",
        origen: 'Dada del procés o expressió',
        desti: 'Dada del subprocés',
    },
    sortida: {
        titol: 'Dades de sortida',
        descripcio: "Es copien del subprocés al procés quan el subprocés acaba. L'origen és el codi d'una dada del "
            + "subprocés o una expressió; si no s'indica el destí, la dada del procés té el mateix codi.",
        origen: 'Dada del subprocés o expressió',
        desti: 'Dada del procés',
    },
};

/** Grup del plafó de propietats amb la taula d'entrades o de sortides de l'activitat de crida. */
export const createDadesSubprocesGroup = (tipus: TipusDades) => ({
    id: `helium-dades-${tipus}`,
    label: TEXTOS[tipus].titol,
    tipus,
    component: DadesGroup,
});

type GroupProps = {
    id: string;
    label: string;
    element: BpmnElement;
    tipus: TipusDades;
};

const DadesGroup = (props: GroupProps) => {
    const { id, label, element, tipus } = props;
    const modeling = useService('modeling');
    const translate = useService('translate');
    const [open, setOpen] = useLayoutState(['groups', id, 'open'], false);
    const dades = getDadesSubproces(element.businessObject, tipus);
    const hasItems = dades.length > 0;

    const afegir = (event: any) => {
        event.stopPropagation();
        afegirDada(element, modeling, tipus);
        setOpen(true);
    };

    return h('div', { class: 'bio-properties-panel-group', 'data-group-id': 'group-' + id },
        h('div', {
            class: 'bio-properties-panel-group-header' + (hasItems ? '' : ' empty') + (hasItems && open ? ' open' : ''),
            onClick: hasItems ? () => setOpen(!open) : undefined,
        },
            h('div', { class: 'bio-properties-panel-group-header-title', title: TEXTOS[tipus].descripcio }, label),
            h('div', { class: 'bio-properties-panel-group-header-buttons' },
                h('button', {
                    type: 'button',
                    title: tipus === 'entrada' ? 'Afegir una dada d\'entrada' : 'Afegir una dada de sortida',
                    class: 'bio-properties-panel-group-header-button bio-properties-panel-add-entry',
                    onClick: afegir,
                },
                    h(CreateIcon, null),
                    !hasItems && h('span', { class: 'bio-properties-panel-add-entry-label' }, translate('Create'))),
                hasItems && h('div', {
                    class: 'bio-properties-panel-list-badge',
                    title: `${dades.length} dada${dades.length !== 1 ? 'es' : ''}`,
                }, dades.length),
                hasItems && h('button', {
                    type: 'button',
                    title: translate('Toggle section'),
                    class: 'bio-properties-panel-group-header-button bio-properties-panel-arrow',
                }, h(ArrowIcon, { class: open ? 'bio-properties-panel-arrow-down' : 'bio-properties-panel-arrow-right' })))),
        h('div', { class: 'bio-properties-panel-group-entries' + (open && hasItems ? ' open' : '') },
            h(DadesTaula, { element, tipus })));
};

// Clau estable per fila: el mateix objecte conserva la seva fila encara que se n'eliminin d'altres
const clausFiles = new WeakMap<object, number>();
let seguentClau = 0;
const clauFila = (dada: object): number => {
    let clau = clausFiles.get(dada);
    if (clau === undefined) {
        clau = seguentClau++;
        clausFiles.set(dada, clau);
    }
    return clau;
};

const DadesTaula = (props: { element: BpmnElement; tipus: TipusDades }) => {
    const { element, tipus } = props;
    const modeling = useService('modeling');
    const dades = getDadesSubproces(element.businessObject, tipus);
    const textos = TEXTOS[tipus];

    return h('div', { class: 'helium-parametres' },
        dades.length > 0 && h('table', { class: 'helium-parametres-taula helium-dades-subproces' },
            h('thead', null,
                h('tr', null,
                    h('th', { title: textos.origen }, 'Origen'),
                    h('th', { title: textos.desti }, 'Destí'),
                    h('th', { class: 'helium-parametres-accions' }))),
            h('tbody', null,
                dades.map((dada) => {
                    const origen = textOrigen(dada);
                    return h('tr', { key: clauFila(dada) },
                        h('td', null,
                            h(CellaText, {
                                valor: origen,
                                placeholder: textos.origen,
                                title: textos.origen,
                                expressio: esExpressio(origen),
                                onCanvi: (valor: string) => assignarOrigen(element, modeling, dada, valor),
                            })),
                        h('td', null,
                            h(CellaText, {
                                valor: dada.desti || '',
                                // Sense destí, la dada té el mateix codi que l'origen (si l'origen és un codi)
                                placeholder: dada.origen || textos.desti,
                                title: textos.desti,
                                onCanvi: (valor: string) => modeling.updateModdleProperties(element, dada, { desti: valor || undefined }),
                            })),
                        h('td', { class: 'helium-parametres-accions' },
                            h('button', {
                                type: 'button',
                                class: 'helium-parametres-eliminar',
                                title: 'Eliminar la dada',
                                'aria-label': `Eliminar la dada ${origen}`,
                                onClick: () => eliminarDada(element, modeling, dada),
                            }, '×')));
                }))),
        h('div', { class: 'helium-parametres-peu bio-properties-panel-description' }, textos.descripcio));
};
