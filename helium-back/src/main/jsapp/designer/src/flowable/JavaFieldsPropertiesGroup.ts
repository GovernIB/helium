import {
    ListGroup,
    TextFieldEntry,
    SelectEntry,
    isTextFieldEntryEdited,
    isSelectEntryEdited,
} from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';

const TAG_NAME = 'flowable:field';

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

type PropertiesEntryProps = {
    id: string;
    element: BpmnElement;
    flowableField: any;
};

const getFlowableFields = (element: BpmnElement) => {
    const extensionElements = element.businessObject.extensionElements;
    if (!extensionElements?.values) return [];
    return extensionElements.values
        .filter((ext: any) => ext.$type === TAG_NAME)
        .map((field: any, index: number) => ({
            id: `field-${index}`,
            name: field.name || '',
            element: field,
        }));
};

const getTypeValue = (businessObject: any) => {
    // TODO revisar
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

export const createJavaFieldsPropertiesGroup = (element: BpmnElement, modeling: Modeling) => {
    const moddle = element.businessObject.$model;
    const fields = getFlowableFields(element);
    return {
        id: 'flowable-fields-group',
        label: 'Flowable fields',
        component: ListGroup,
        add: (event: EventWithPropagation) => {
            event.stopPropagation();
            const bpmnElement = element.businessObject;
            const newField = moddle.create(TAG_NAME, { name: '', type: 'string' });
            if (!bpmnElement.extensionElements) {
                const extensionElements = moddle.create('bpmn:ExtensionElements', {
                    values: [newField],
                });
                newField.$parent = extensionElements;
                modeling.updateProperties(element, { extensionElements });
            } else {
                newField.$parent = bpmnElement.extensionElements;
                modeling.updateModdleProperties(element, bpmnElement.extensionElements, {
                    values: [...bpmnElement.extensionElements.values, newField],
                });
            }
        },
        items: fields.map((field: any) => ({
            id: field.id,
            label: field.name || '<empty>',
            autoFocusEntry: `field-name-${field.id}`,
            entries: [
                {
                    id: `field-name-${field.id}`,
                    component: FlowableJavaFieldNameField,
                    isEdited: isTextFieldEntryEdited,
                    flowableField: field.element,
                },
                {
                    id: `field-type-${field.id}`,
                    component: FlowableJavaFieldTypeField,
                    isEdited: isSelectEntryEdited,
                    flowableField: field.element,
                },
                {
                    id: `field-string-${field.id}`,
                    component: FlowableJavaFieldStringField,
                    isEdited: isTextFieldEntryEdited,
                    flowableField: field.element,
                },
                {
                    id: `field-expression-${field.id}`,
                    component: FlowableJavaFieldExpressionField,
                    isEdited: isTextFieldEntryEdited,
                    flowableField: field.element,
                },
            ],
            remove: (event: EventWithPropagation) => {
                event.stopPropagation();
                const bpmnElement = element.businessObject;
                if (!bpmnElement.extensionElements?.values) return;
                const values = bpmnElement.extensionElements.values.filter(
                    (ext: any) => ext !== field.element
                );
                modeling.updateModdleProperties(element, bpmnElement.extensionElements, { values });
            },
        })),
    };
};

const FlowableJavaFieldNameField = (props: PropertiesEntryProps) => {
    const { element, flowableField } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: props.id,
        label: 'Name',
        getValue: () => getTypeValue(element.businessObject),
        setValue: (value: string) => {
            modeling.updateModdleProperties(element, flowableField, { name: value });
        },
        debounce,
    });
};

const FlowableJavaFieldTypeField = (props: PropertiesEntryProps) => {
    const { element, flowableField } = props;
    return SelectEntry({
        element,
        id: 'field-type-${item.id}',
        label: 'Type',
        getValue: () => flowableField?.type || 'string',
        setValue: (value: string) => {
            console.log('>>> setValue', value, flowableField?.type);
            // TODO revisar
            /*if (item.element) {
                item.element.type = value;
                modeling.updateProperties(element, {});
            }*/
            //flowableField
        },
        getOptions: () => [
            { value: 'string', label: 'String' },
            { value: 'expression', label: 'Expression' },
        ],
    });
};

const FlowableJavaFieldStringField = (props: PropertiesEntryProps) => {
    const { element, flowableField } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'field-string-${item.id}',
        label: 'String',
        getValue: () => flowableField?.string || '',
        setValue: (value: string) => {
            modeling.updateModdleProperties(element, flowableField, { string: value });
        },
        debounce,
    });
};

const FlowableJavaFieldExpressionField = (props: PropertiesEntryProps) => {
    const { element, flowableField } = props;
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    return TextFieldEntry({
        element,
        id: 'field-expression-${item.id}',
        label: 'Expression',
        getValue: () => flowableField?.expression || '',
        setValue: (value: string) => {
            modeling.updateModdleProperties(element, flowableField, { expression: value });
        },
        debounce,
    });
};
