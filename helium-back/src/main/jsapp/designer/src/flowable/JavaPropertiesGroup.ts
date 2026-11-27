import {
    TextFieldEntry,
    SelectEntry,
    isTextFieldEntryEdited,
    isSelectEntryEdited,
} from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';
import {
    HELIUM_IMPLEMENTATION,
    configurarHeliumHandler,
    getHeliumHandler,
    isHandlerPerExpressio,
    isHeliumHandlerTask,
    removeHeliumHandler,
} from '../helium/heliumHandler';
import { createHeliumHandlerClassEntry, esHandlerPredefinit, type TipusHandler } from '../helium/HeliumHandlerPropertiesGroup';
import { getHandlersSync } from './handlers';

type BpmnElement = {
    businessObject: any;
};

type PropertiesEntryProps = {
    element: BpmnElement;
};

/**
 * Tipus d'implementació de la service task. Els de Helium són BPMN 2.0 genèric:
 *  - handlerPredefinit: handler del catàleg de Helium, indicat per classe (<helium:handler classe="...">).
 *  - handlerPropi: handler propi (del projecte o pujat com a recurs), indicat per classe. Al XML és igual que un
 *    predefinit: la diferència la fa el catàleg (els predefinits tenen agrupació).
 *  - heliumExpressio: handler obtingut d'una expressió, p. ex. un bean (<helium:handler expressio="${...}">).
 *  - expressio: expressió avaluada pel motor (helium:expression, amb helium:resultVariableName opcional).
 *
 * Els de Flowable (class, delegateExpression) només es mostren si la tasca ja els fa servir (diagrames antics),
 * perquè no es perdin en editar-los. Les flowable:expression es converteixen a helium:expression en obrir el diagrama.
 */
export type TipusImplementacio =
    | 'handlerPredefinit'
    | 'handlerPropi'
    | 'heliumExpressio'
    | 'expressio'
    | 'class'
    | 'delegateExpression';

/**
 * Tipus triat per a les tasques que encara no tenen classe: el XML no permet distingir un handler predefinit d'un de
 * propi fins que no se'n tria la classe.
 */
const tipusSenseClasse = new WeakMap<object, TipusHandler>();

const tipusHandlerPerClasse = (businessObject: any): TipusHandler => {
    const classe = getHeliumHandler(businessObject)?.classe;
    if (!classe) {
        return tipusSenseClasse.get(businessObject) ?? 'predefinit';
    }
    // Una classe que no és al catàleg (p. ex. un handler propi encara no compilat o pujat) es tracta com a pròpia
    return esHandlerPredefinit(classe) || (!getHandlersSync().length && classe.startsWith('es.caib.helium.'))
        ? 'predefinit'
        : 'propi';
};

export const esTipusHandler = (tipus: TipusImplementacio | undefined): boolean =>
    tipus === 'handlerPredefinit' || tipus === 'handlerPropi' || tipus === 'heliumExpressio';

export const getTypeValue = (businessObject: any): TipusImplementacio | undefined => {
    if (isHeliumHandlerTask(businessObject)) {
        if (isHandlerPerExpressio(businessObject)) {
            return 'heliumExpressio';
        }
        return tipusHandlerPerClasse(businessObject) === 'predefinit' ? 'handlerPredefinit' : 'handlerPropi';
    }
    if (businessObject.get('helium:expression') != null) {
        return 'expressio';
    }
    if (businessObject.get('flowable:class') != null) {
        return 'class';
    }
    if (businessObject.get('flowable:delegateExpression') != null) {
        return 'delegateExpression';
    }
    return undefined;
};

export const createJavaPropertiesGroup = (element: BpmnElement) => {
    const entries: any[] = [
        {
            id: 'flowable-java-group-type',
            component: TipusField,
            isEdited: isSelectEntryEdited,
        },
    ];
    const typeValue = getTypeValue(element.businessObject);
    if (typeValue === 'handlerPredefinit' || typeValue === 'handlerPropi') {
        entries.push(createHeliumHandlerClassEntry(typeValue === 'handlerPredefinit' ? 'predefinit' : 'propi'));
    }
    if (typeValue === 'heliumExpressio') {
        entries.push({
            id: 'helium-handler-expressio',
            component: HandlerExpressioField,
            isEdited: isTextFieldEntryEdited,
        });
    }
    if (typeValue === 'expressio') {
        entries.push({
            id: 'helium-expression',
            component: ExpressioField,
            isEdited: isTextFieldEntryEdited,
        });
    }
    if (typeValue === 'class') {
        entries.push({
            id: 'flowable-java-group-class',
            component: FlowableJavaClassField,
            isEdited: isTextFieldEntryEdited,
        });
    }
    if (typeValue === 'delegateExpression') {
        entries.push({
            id: 'flowable-java-group-delegate-expression',
            component: FlowableJavaDelegateExpressionField,
            isEdited: isTextFieldEntryEdited,
        });
    }
    return {
        id: 'flowable-java',
        label: 'Implementació',
        entries,
    };
};

const TipusField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    return SelectEntry({
        element,
        id: 'flowable-java-type',
        label: 'Tipus',
        getValue: () => getTypeValue(element.businessObject),
        setValue: (value: TipusImplementacio | undefined) => {
            if (getTypeValue(element.businessObject) === value) {
                return;
            }
            const esHandler = esTipusHandler(value);
            modeling.updateProperties(element, {
                'flowable:class': undefined,
                'flowable:delegateExpression': undefined,
                'helium:expression': value === 'expressio' ? '' : undefined,
                implementation: esHandler ? HELIUM_IMPLEMENTATION : undefined,
            });
            if (esHandler) {
                const handler = configurarHeliumHandler(element, modeling, value === 'heliumExpressio');
                if (value === 'handlerPredefinit' || value === 'handlerPropi') {
                    // Canvi entre predefinit i propi: la classe i els paràmetres anteriors ja no serveixen
                    tipusSenseClasse.set(element.businessObject, value === 'handlerPredefinit' ? 'predefinit' : 'propi');
                    if (handler.classe) {
                        modeling.updateModdleProperties(element, handler, { classe: '', parametres: [] });
                    }
                }
            } else {
                removeHeliumHandler(element, modeling);
            }
        },
        getOptions: () => {
            const options: { value: string | undefined; label: string }[] = [
                { value: undefined, label: '<cap>' },
                { value: 'handlerPredefinit', label: 'Handler predefinit' },
                { value: 'handlerPropi', label: 'Handler propi' },
                { value: 'heliumExpressio', label: 'Handler per expressió' },
                { value: 'expressio', label: 'Expressió' },
            ];
            // Les opcions de Flowable només per als diagrames que ja les fan servir
            const actual = getTypeValue(element.businessObject);
            if (actual === 'class') {
                options.push({ value: 'class', label: 'Classe Java (Flowable)' });
            }
            if (actual === 'delegateExpression') {
                options.push({ value: 'delegateExpression', label: 'Expressió delegada (Flowable)' });
            }
            return options;
        },
    });
};

const HandlerExpressioField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'helium-handler-expressio',
        label: 'Expressió del handler',
        description: 'Ha de retornar el handler (un objecte que implementi HeliumBpmnHandler), p. ex. un bean: ${nomDelBean}. '
            + 'Els paràmetres s\'injecten a l\'objecte retornat: si és compartit, també ho són els paràmetres.',
        getValue: () => getHeliumHandler(element.businessObject)?.expressio || '',
        setValue: (value: string) => {
            const handler = configurarHeliumHandler(element, modeling, true);
            modeling.updateModdleProperties(element, handler, { expressio: value || '' });
        },
        debounce,
    });
};

const ExpressioField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'helium-expression',
        label: 'Expressió',
        description: 'S\'avalua en executar la tasca. El resultat es pot desar amb "Dada del resultat".',
        getValue: () => element.businessObject.get('helium:expression') || '',
        setValue: (value: string) => {
            modeling.updateProperties(element, {
                'helium:expression': value || '',
            });
        },
        debounce,
    });
};

const FlowableJavaClassField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'flowable-java-class',
        label: 'Classe Java',
        description: 'Específic de Flowable: classe que implementa org.flowable.engine.delegate.JavaDelegate.',
        getValue: () => element.businessObject.get('flowable:class') || '',
        setValue: (value: string) => {
            modeling.updateProperties(element, {
                'flowable:class': value,
            });
        },
        debounce,
    });
};

const FlowableJavaDelegateExpressionField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'flowable-java-delegate-expression',
        label: 'Expressió delegada',
        description: 'Específic de Flowable: ha de retornar un org.flowable.engine.delegate.JavaDelegate.',
        getValue: () => element.businessObject.get('flowable:delegateExpression') || '',
        setValue: (value: string) => {
            modeling.updateProperties(element, {
                'flowable:delegateExpression': value,
            });
        },
        debounce,
    });
};
