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
import { createHeliumHandlerClassEntry } from '../helium/HeliumHandlerPropertiesGroup';

type BpmnElement = {
    businessObject: any;
};

type PropertiesEntryProps = {
    element: BpmnElement;
};

/**
 * Tipus d'implementació de la service task. Els de Helium són BPMN 2.0 genèric:
 *  - helium: handler de Helium indicat per classe (<helium:handler classe="...">).
 *  - heliumExpressio: handler de Helium obtingut d'una expressió (<helium:handler expressio="${...}">).
 *  - expressio: expressió avaluada pel motor (helium:expression, amb helium:resultVariableName opcional).
 *
 * Els de Flowable (class, delegateExpression) només es mostren si la tasca ja els fa servir (diagrames antics),
 * perquè no es perdin en editar-los. Les flowable:expression es converteixen a helium:expression en obrir el diagrama.
 */
export type TipusImplementacio = 'helium' | 'heliumExpressio' | 'expressio' | 'class' | 'delegateExpression';

export const getTypeValue = (businessObject: any): TipusImplementacio | undefined => {
    if (isHeliumHandlerTask(businessObject)) {
        return isHandlerPerExpressio(businessObject) ? 'heliumExpressio' : 'helium';
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
    if (typeValue === 'helium') {
        entries.push(createHeliumHandlerClassEntry());
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
            const esHandler = value === 'helium' || value === 'heliumExpressio';
            modeling.updateProperties(element, {
                'flowable:class': undefined,
                'flowable:delegateExpression': undefined,
                'helium:expression': value === 'expressio' ? '' : undefined,
                implementation: esHandler ? HELIUM_IMPLEMENTATION : undefined,
            });
            if (esHandler) {
                configurarHeliumHandler(element, modeling, value === 'heliumExpressio');
            } else {
                removeHeliumHandler(element, modeling);
            }
        },
        getOptions: () => {
            const options: { value: string | undefined; label: string }[] = [
                { value: undefined, label: '<cap>' },
                { value: 'helium', label: 'Handler de Helium' },
                { value: 'heliumExpressio', label: 'Handler de Helium (expressió)' },
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
        description: 'Ha de retornar un objecte que implementi HeliumBpmnHandler. Ex: ${nomDelBean}',
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
