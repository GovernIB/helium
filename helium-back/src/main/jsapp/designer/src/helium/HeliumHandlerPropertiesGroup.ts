import {
    ListGroup,
    SelectEntry,
    TextFieldEntry,
    isSelectEntryEdited,
    isTextFieldEntryEdited,
} from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';
import { getHandlersSync } from '../flowable/handlers';
import {
    PARAMETRE_TYPE,
    carregarParametres,
    descripcioParametre,
    ensureHeliumHandler,
    getHeliumHandler,
} from './heliumHandler';

type BpmnElement = {
    businessObject: any;
};

type Modeling = {
    updateProperties: (element: BpmnElement, properties: Record<string, any>) => void;
    updateModdleProperties: (
        element: BpmnElement,
        moddleElement: any,
        properties: Record<string, any>
    ) => void;
};

type EventWithPropagation = {
    stopPropagation: () => void;
};

type EntryProps = {
    id: string;
    element: BpmnElement;
};

type ParametreEntryProps = EntryProps & {
    parametre: any;
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

/** Llista de paràmetres (helium:parametre) del handler. */
export const createHeliumParametresGroup = (element: BpmnElement, modeling: Modeling) => {
    const heliumHandler = getHeliumHandler(element.businessObject);
    const parametres: any[] = heliumHandler?.parametres || [];
    const handlerCataleg = getHandlersSync().find((h) => h.classe === heliumHandler?.classe);
    return {
        id: 'helium-parametres',
        label: 'Paràmetres del handler',
        component: ListGroup,
        add: (event: EventWithPropagation) => {
            event.stopPropagation();
            const handler = ensureHeliumHandler(element, modeling);
            const parametre = element.businessObject.$model.create(PARAMETRE_TYPE, { nom: '', valor: '' });
            parametre.$parent = handler;
            modeling.updateModdleProperties(element, handler, {
                parametres: [...(handler.parametres || []), parametre],
            });
        },
        items: parametres.map((parametre: any, index: number) => {
            const id = `helium-parametre-${index}`;
            return {
                id,
                label: parametre.nom || '<empty>',
                autoFocusEntry: `${id}-nom`,
                entries: [
                    {
                        id: `${id}-nom`,
                        component: ParametreNomField,
                        isEdited: isTextFieldEntryEdited,
                        parametre,
                        descripcio: descripcioParametre(handlerCataleg, parametre.nom),
                    },
                    {
                        id: `${id}-valor`,
                        component: ParametreValorField,
                        isEdited: isTextFieldEntryEdited,
                        parametre,
                    },
                    {
                        id: `${id}-expressio`,
                        component: ParametreExpressioField,
                        isEdited: isTextFieldEntryEdited,
                        parametre,
                    },
                ],
                remove: (event: EventWithPropagation) => {
                    event.stopPropagation();
                    modeling.updateModdleProperties(element, heliumHandler, {
                        parametres: parametres.filter((p) => p !== parametre),
                    });
                },
            };
        }),
    };
};

const ParametreNomField = (props: ParametreEntryProps & { descripcio?: string }) => {
    const { element, parametre, id, descripcio } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id,
        label: 'Nom',
        description: descripcio,
        getValue: () => parametre.nom || '',
        setValue: (value: string) => {
            modeling.updateModdleProperties(element, parametre, { nom: value });
        },
        debounce,
    });
};

const ParametreValorField = (props: ParametreEntryProps) => {
    const { element, parametre, id } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id,
        label: 'Valor',
        getValue: () => parametre.valor || '',
        setValue: (value: string) => {
            modeling.updateModdleProperties(element, parametre, { valor: value });
        },
        debounce,
    });
};

const ParametreExpressioField = (props: ParametreEntryProps) => {
    const { element, parametre, id } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id,
        label: 'Expressió',
        description: 'Si s\'informa, té preferència sobre el valor. Ex: ${nomVariable}',
        getValue: () => parametre.expressio || '',
        setValue: (value: string) => {
            modeling.updateModdleProperties(element, parametre, { expressio: value || undefined });
        },
        debounce,
    });
};
