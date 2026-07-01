import {
    TextFieldEntry,
    SelectEntry,
    isTextFieldEntryEdited,
    isSelectEntryEdited,
} from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';

type BpmnElement = {
    businessObject: any;
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
            isEdited: isTextFieldEntryEdited,
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
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'flowable-java-class',
        label: 'Java class',
        getValue: () => {
            return element.businessObject.get('flowable:class');
        },
        setValue: (value: string) => {
            modeling.updateProperties(element, {
                'flowable:class': value,
            });
        },
        debounce,
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
