import { is } from 'bpmn-js/lib/util/ModelUtil';
import { useService } from 'bpmn-js-properties-panel';
import {
    TextFieldEntry,
    isCheckboxEntryEdited,
    isTextFieldEntryEdited,
} from '@bpmn-io/properties-panel';
import { createEntryFactories } from './EntryFactories';
import { createJavaPropertiesGroup } from './JavaPropertiesGroup';
import { createJavaFieldsPropertiesGroup } from './JavaFieldsPropertiesGroup';

const LOW_PRIORITY = 50;

type BpmnElement = {
    businessObject: any;
    source?: BpmnElement;
};

type Modeling = {
    updateProperties: (element: BpmnElement, properties: Record<string, any>) => void;
    updateModdleProperties: (
        element: BpmnElement,
        moddleElement: any,
        properties: Record<string, any>
    ) => void;
};

type FlowablePropertiesProviderInstance = {
    _modeling: Modeling;
};

function FlowablePropertiesProvider(
    this: FlowablePropertiesProviderInstance,
    propertiesPanel: any,
    modeling: Modeling
) {
    propertiesPanel.registerProvider(LOW_PRIORITY, this);
    this._modeling = modeling;
}

FlowablePropertiesProvider.$inject = ['propertiesPanel', 'modeling'];

FlowablePropertiesProvider.prototype.getGroups = function (
    this: FlowablePropertiesProviderInstance,
    element: BpmnElement
) {
    const modeling = this._modeling;
    return (groups: any[]) => {
        if (is(element, 'bpmn:Process')) {
            groups.push(createProcessPropertiesGroup());
        }
        if (is(element, 'bpmn:StartEvent')) {
            groups.push(createStartEventPropertiesGroup());
        }
        if (is(element, 'bpmn:UserTask')) {
            groups.push(createUserTaskPropertiesGroup());
        }
        if (is(element, 'bpmn:ServiceTask')) {
            groups.push(createServiceTaskPropertiesGroup());
            groups.push(createJavaPropertiesGroup(element));
            groups.push(createJavaFieldsPropertiesGroup(element, modeling));
        }
        if (is(element, 'bpmn:ScriptTask')) {
            groups.push(createScriptTaskPropertiesGroup());
        }
        if (is(element, 'bpmn:CallActivity')) {
            groups.push(createCallActivityPropertiesGroup());
        }
        if (is(element, 'bpmn:Activity')) {
            groups.push(createAsyncPropertiesGroup(element));
            groups.push(createSkipExpressionPropertiesGroup());
            if (element.businessObject.loopCharacteristics) {
                groups.push(createMultiInstancePropertiesGroup());
            }
        }
        if (is(element, 'bpmn:SequenceFlow')) {
            groups.push(createConditionExpressionPropertiesGroup());
            groups.push(createSkipExpressionPropertiesGroup());
        }
        return groups;
    };
};

// ---------------------------------------------------------------------------
// Flowable entry factories
// ---------------------------------------------------------------------------

const {
    createTextField,
    createCheckbox,
    createModdleTextField,
} = createEntryFactories('flowable');

// ---------------------------------------------------------------------------
// Flowable Process
// ---------------------------------------------------------------------------

const CandidateStarterUsers = createTextField({
    property: 'candidateStarterUsers',
    label: 'Candidate starter users',
});

const CandidateStarterGroups = createTextField({
    property: 'candidateStarterGroups',
    label: 'Candidate starter groups',
});

const createProcessPropertiesGroup = () => {
    return {
        id: 'flowable-process',
        label: 'Process',
        entries: [
            {
                id: 'flowable-candidate-starter-users',
                component: CandidateStarterUsers,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-candidate-starter-groups',
                component: CandidateStarterGroups,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Start Event
// ---------------------------------------------------------------------------

const Initiator = createTextField({
    property: 'initiator',
    label: 'Initiator',
});

const StartEventFormKey = createTextField({
    property: 'formKey',
    label: 'Form key',
});

const createStartEventPropertiesGroup = () => {
    return {
        id: 'flowable-start-event',
        label: 'Start event',
        entries: [
            {
                id: 'flowable-initiator',
                component: Initiator,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-start-event-form-key',
                component: StartEventFormKey,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable User Task
// ---------------------------------------------------------------------------

const Assignee = createTextField({
    property: 'assignee',
    label: 'Assignee',
});

const CandidateUsers = createTextField({
    property: 'candidateUsers',
    label: 'Candidate users',
});

const CandidateGroups = createTextField({
    property: 'candidateGroups',
    label: 'Candidate groups',
});

const DueDate = createTextField({
    property: 'dueDate',
    label: 'Due date',
});

const Priority = createTextField({
    property: 'priority',
    label: 'Priority',
});

const Category = createTextField({
    property: 'category',
    label: 'Category',
});

const UserTaskFormKey = createTextField({
    property: 'formKey',
    label: 'Form key',
});

const createUserTaskPropertiesGroup = () => {
    return {
        id: 'flowable-user-task',
        label: 'User task',
        entries: [
            {
                id: 'flowable-assignee',
                component: Assignee,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-candidate-users',
                component: CandidateUsers,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-candidate-groups',
                component: CandidateGroups,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-due-date',
                component: DueDate,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-priority',
                component: Priority,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-category',
                component: Category,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-user-task-form-key',
                component: UserTaskFormKey,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Service Task
// ---------------------------------------------------------------------------

const ResultVariableName = createTextField({
    property: 'resultVariableName',
    label: 'Result variable name',
});

const createServiceTaskPropertiesGroup = () => {
    return {
        id: 'flowable-service-task',
        label: 'Service task',
        entries: [
            {
                id: 'flowable-result-variable-name',
                component: ResultVariableName,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Script Task
// ---------------------------------------------------------------------------

const ResultVariable = createTextField({
    property: 'resultVariable',
    label: 'Result variable',
});

const AutoStoreVariables = createCheckbox({
    property: 'autoStoreVariables',
    label: 'Auto store variables',
    defaultValue: false,
});

const createScriptTaskPropertiesGroup = () => {
    return {
        id: 'flowable-script-task',
        label: 'Script task',
        entries: [
            {
                id: 'flowable-result-variable',
                component: ResultVariable,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-auto-store-variables',
                component: AutoStoreVariables,
                isEdited: isCheckboxEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Call Activity
// ---------------------------------------------------------------------------

const CalledElementType = createTextField({
    property: 'calledElementType',
    label: 'Called element',
});

const BusinessKey = createTextField({
    property: 'businessKey',
    label: 'Business key',
});

const InheritVariables = createCheckbox({
    property: 'inheritVariables',
    label: 'Inherit variables',
    defaultValue: false,
});

const InheritBusinessKey = createCheckbox({
    property: 'inheritBusinessKey',
    label: 'Inherit business key',
    defaultValue: false,
});

const FallbackToDefaultTenant = createCheckbox({
    property: 'fallbackToDefaultTenant',
    label: 'Fallback to default tenant',
    defaultValue: false,
});

const createCallActivityPropertiesGroup = () => {
    return {
        id: 'flowable-call-activity',
        label: 'Call activity',
        entries: [
            {
                id: 'flowable-called-element-type',
                component: CalledElementType,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-business-key',
                component: BusinessKey,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-inherit-variables',
                component: InheritVariables,
                isEdited: isCheckboxEntryEdited,
            },
            {
                id: 'flowable-inherit-business-key',
                component: InheritBusinessKey,
                isEdited: isCheckboxEntryEdited,
            },
            {
                id: 'flowable-fallback-to-default-tenant',
                component: FallbackToDefaultTenant,
                isEdited: isCheckboxEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Async (all activities)
// ---------------------------------------------------------------------------

const Async = createCheckbox({
    property: 'async',
    label: 'Async',
    defaultValue: false,
});

const Exclusive = createCheckbox({
    property: 'exclusive',
    label: 'Exclusive',
    defaultValue: true,
});

const createAsyncPropertiesGroup = (element: BpmnElement) => {
    const entries: any[] = [
        {
            id: 'flowable-async',
            component: Async,
            isEdited: isCheckboxEntryEdited,
        },
    ];
    if (element.businessObject.get('flowable:async')) {
        entries.push({
            id: 'flowable-exclusive',
            component: Exclusive,
            isEdited: isCheckboxEntryEdited,
        });
    }
    return {
        id: 'flowable-async',
        label: 'Asynchronous continuations',
        entries,
    };
};

// ---------------------------------------------------------------------------
// Flowable Condition Expression (sequence flows)
// ---------------------------------------------------------------------------

const ConditionExpression = (props: { element: BpmnElement; id: string }) => {
    const { element } = props;
    const modeling = useService('modeling');
    const bpmnFactory = useService('bpmnFactory');
    const translate = useService('translate');
    const debounce = useService('debounceInput');

    const getValue = () => {
        return element.businessObject.get('conditionExpression')?.get('body') || '';
    };

    const setValue = (value: string) => {
        const businessObject = element.businessObject;
        const conditionExpression = businessObject.get('conditionExpression');

        if (element.source?.businessObject?.default === businessObject) {
            modeling.updateProperties(element.source, { default: undefined });
        }

        if (!value) {
            modeling.updateProperties(element, { conditionExpression: undefined });
            return;
        }

        if (conditionExpression) {
            modeling.updateModdleProperties(element, conditionExpression, { body: value });
            return;
        }

        const formalExpression = bpmnFactory.create('bpmn:FormalExpression', { body: value });
        formalExpression.$parent = businessObject;
        modeling.updateProperties(element, { conditionExpression: formalExpression });
    };

    return TextFieldEntry({
        element,
        id: props.id,
        label: translate('Condition expression'),
        getValue,
        setValue,
        debounce,
    });
};

const createConditionExpressionPropertiesGroup = () => {
    return {
        id: 'flowable-condition',
        label: 'Condition',
        entries: [
            {
                id: 'flowable-condition-expression',
                component: ConditionExpression,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Skip Expression (activities and sequence flows)
// ---------------------------------------------------------------------------

const SkipExpression = createTextField({
    property: 'skipExpression',
    label: 'Skip expression',
});

const createSkipExpressionPropertiesGroup = () => {
    return {
        id: 'flowable-skip-expression',
        label: 'Skip expression',
        entries: [
            {
                id: 'flowable-skip-expression',
                component: SkipExpression,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Flowable Multi-instance
// ---------------------------------------------------------------------------

const Collection = createModdleTextField({
    property: 'collection',
    label: 'Collection',
});

const ElementVariable = createModdleTextField({
    property: 'elementVariable',
    label: 'Element variable',
});

const createMultiInstancePropertiesGroup = () => {
    return {
        id: 'flowable-multi-instance',
        label: 'Multi-instance',
        entries: [
            {
                id: 'flowable-collection',
                component: Collection,
                isEdited: isTextFieldEntryEdited,
            },
            {
                id: 'flowable-element-variable',
                component: ElementVariable,
                isEdited: isTextFieldEntryEdited,
            },
        ],
    };
};

// ---------------------------------------------------------------------------
// Module
// ---------------------------------------------------------------------------

export const FlowablePropertiesProviderModule = {
    __init__: ['flowablePropertiesProvider'],
    flowablePropertiesProvider: ['type', FlowablePropertiesProvider],
};

export default FlowablePropertiesProviderModule;
