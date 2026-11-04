import {
    TextFieldEntry,
    SelectEntry,
    isTextFieldEntryEdited,
    isSelectEntryEdited,
} from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';
import { getHandlersSync, type HandlerDto } from './handlers';

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

type PropertiesEntryProps = {
    element: BpmnElement;
};

const getTypeValue = (businessObject: any) => {
    const classValue = businessObject.get('flowable:class');
    const expressionValue = businessObject.get('flowable:expression');
    const delegateExpressionValue = businessObject.get('flowable:delegateExpression');
    return classValue != null
        ? 'class'
        : expressionValue != null
          ? 'expression'
          : delegateExpressionValue != null
            ? 'delegateExpression'
            : undefined;
};

const fillFieldsFromHandler = (element: BpmnElement, modeling: Modeling, handler: HandlerDto) => {
    const moddle = element.businessObject.$model;
    const bpmnElement = element.businessObject;
    let extensionElements = bpmnElement.extensionElements;
    if (!extensionElements) {
        debugger
        extensionElements = moddle.create('bpmn:ExtensionElements', { values: [] });
        modeling.updateProperties(element, { extensionElements });
    }
    debugger;
    const nonFieldValues = extensionElements.values?.filter((v: any) => v.$type !== 'flowable:Field') ?? [];
    const fields = (handler.parametres || []).map((p) => {
        const field = moddle.create('flowable:Field', {
            name: p.codi || p.nom || '',
            stringValue: '',
        });
        field.$parent = extensionElements;
        return field;
    });
    modeling.updateModdleProperties(element, extensionElements, {
        values: [...nonFieldValues, ...fields],
    });
};

export const createJavaPropertiesGroup = (element: BpmnElement) => {
    const entries: any[] = [
        {
            id: 'flowable-java-group-type',
            component: FlowableJavaTypeField,
            isEdited: isSelectEntryEdited,
        },
    ];
    const typeValue = getTypeValue(element.businessObject);
    if (typeValue === 'class') {
        entries.push({
            id: 'flowable-java-group-class',
            component: FlowableJavaClassField,
            isEdited: isSelectEntryEdited,
        });
    }
    if (typeValue === 'expression') {
        entries.push({
            id: 'flowable-java-group-expression',
            component: FlowableJavaExpressionField,
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
        label: 'Flowable Java',
        entries,
    };
};

const FlowableJavaTypeField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    return SelectEntry({
        element,
        id: 'flowable-java-type',
        label: 'Type',
        getValue: () => getTypeValue(element.businessObject),
        setValue: (value: string | undefined) => {
            const valueChanged = getTypeValue(element.businessObject) !== value;
            const updates: Record<string, string | undefined> = {};
            if (valueChanged) {
                updates['flowable:class'] = undefined;
                updates['flowable:expression'] = undefined;
                updates['flowable:delegateExpression'] = undefined;
            }
            if (value) {
                updates['flowable:' + value] = '';
            }
            modeling.updateProperties(element, updates);
        },
        getOptions: () => [
            { value: undefined, label: '<none>' },
            { value: 'class', label: 'Java class' },
            { value: 'expression', label: 'Expression' },
            { value: 'delegateExpression', label: 'Delegate expression' },
        ],
    });
};

const FlowableJavaClassField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const translate = useService('translate');
    const currentValue = element.businessObject.get('flowable:class');
    return SelectEntry({
        element,
        id: 'flowable-java-class',
        label: translate('Java class'),
        getValue: () => {
            return element.businessObject.get('flowable:class');
        },
        setValue: (value: string) => {
            const previousClass = element.businessObject.get('flowable:class');
            modeling.updateProperties(element, {
                'flowable:class': value,
            });
            if (value && value !== previousClass) {
                const handler = getHandlersSync().find((h) => h.classe === value);
                if (handler?.parametres?.length) {
                    fillFieldsFromHandler(element, modeling, handler);
                }
            }
        },
        getOptions: () => {
            const handlers = getHandlersSync();

            const options= [];
            let agrupacio = null;
            for (const handler of handlers) {
                if(handler.agrupacio != agrupacio) {
                    agrupacio = handler.agrupacio;
                    options.push({
                        value: null,
                        label: handler.agrupacio,
                        disabled: true
                    });
                }

                options.push({
                    value: handler.classe,
                    label: handler.nom || handler.classe,
                });
            }
            // const options = handlers.map((h) => ({
            //     value: h.classe,
            //     label: h.nom || h.classe,
            // }));

            // Ensure current value is always in the options list
            if (currentValue && !options.some((o) => o.value === currentValue)) {
                options.unshift({ value: currentValue, label: currentValue });
            }
            return [{ value: '', label: '<none>' }, ...options];
        },
    });
};

const FlowableJavaExpressionField = (props: PropertiesEntryProps) => {
    const { element } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'flowable-java-expression',
        label: 'Expression',
        getValue: () => element.businessObject.get('flowable:expression') || '',
        setValue: (value: string) => {
            modeling.updateProperties(element, {
                'flowable:expression': value,
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
        label: 'Delegate expression',
        getValue: () => element.businessObject.get('flowable:delegateExpression') || '',
        setValue: (value: string) => {
            modeling.updateProperties(element, {
                'flowable:delegateExpression': value,
            });
        },
        debounce,
    });
};
