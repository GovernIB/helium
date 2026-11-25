import { is } from 'bpmn-js/lib/util/ModelUtil';
import { useService } from 'bpmn-js-properties-panel';
import {
    TextFieldEntry,
    isCheckboxEntryEdited,
    isTextFieldEntryEdited,
} from '@bpmn-io/properties-panel';
import { createEntryFactories } from './EntryFactories';
import {
    getCandidats,
    getResponsable,
    resourcesAmbCandidats,
    resourcesAmbResponsable,
    separarPerComes,
} from '../helium/assignacio';
import { createJavaPropertiesGroup, getTypeValue } from './JavaPropertiesGroup';
import { createHeliumParametresGroup } from '../helium/HeliumHandlerPropertiesGroup';
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

type Translate = (template: string, replacements?: Record<string, string>) => string;

type FlowablePropertiesProviderInstance = {
    _modeling: Modeling;
    _translate: Translate;
};

function FlowablePropertiesProvider(
    this: FlowablePropertiesProviderInstance,
    propertiesPanel: any,
    modeling: Modeling,
    translate: Translate
) {
    propertiesPanel.registerProvider(LOW_PRIORITY, this);
    this._modeling = modeling;
    this._translate = translate;
}

FlowablePropertiesProvider.$inject = ['propertiesPanel', 'modeling', 'translate'];

FlowablePropertiesProvider.prototype.getGroups = function (
    this: FlowablePropertiesProviderInstance,
    element: BpmnElement
) {
    const modeling = this._modeling;
    const translate = this._translate;
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
            const tipus = getTypeValue(element.businessObject);
            if (tipus === 'helium' || tipus === 'heliumExpressio') {
                groups.push(createHeliumParametresGroup());
            } else if (tipus === 'class' || tipus === 'delegateExpression') {
                groups.push(createJavaFieldsPropertiesGroup(element, modeling));
            }
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
        // Els components de grup de @bpmn-io/properties-panel només tradueixen els seus textos (títols dels botons
        // de plegar, indicador de canvis...) si la definició del grup porta `translate`, i cap proveïdor l'hi posa
        for (const group of groups) {
            group.translate ??= translate;
        }
        return groups;
    };
};

// ---------------------------------------------------------------------------
// Entry factories: les propietats d'execució es desen com a atributs helium:* (BPMN 2.0 genèric)
// ---------------------------------------------------------------------------

const {
    createTextField,
    createCheckbox,
    createModdleTextField,
} = createEntryFactories('helium');

// ---------------------------------------------------------------------------
// Flowable Process
// ---------------------------------------------------------------------------

const CandidateStarterUsers = createTextField({
    property: 'candidateStarterUsers',
    label: 'Usuaris que poden iniciar el procés',
});

const CandidateStarterGroups = createTextField({
    property: 'candidateStarterGroups',
    label: 'Grups que poden iniciar el procés',
});

const createProcessPropertiesGroup = () => {
    return {
        id: 'flowable-process',
        label: 'Procés',
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
    label: 'Iniciador',
});

const StartEventFormKey = createTextField({
    property: 'formKey',
    label: 'Clau del formulari',
});

const createStartEventPropertiesGroup = () => {
    return {
        id: 'flowable-start-event',
        label: "Esdeveniment d'inici",
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

// L'assignació es desa amb els elements estàndard bpmn:humanPerformer i bpmn:potentialOwner

const Assignee = ({ element, id }: { element: BpmnElement; id: string }) => {
    const modeling = useService('modeling');
    const debounce = useService('debounceInput');
    const businessObject = element.businessObject;
    return TextFieldEntry({
        element,
        id,
        label: 'Responsable',
        description: 'Codi d\'usuari o expressió (bpmn:humanPerformer)',
        debounce,
        getValue: () => getResponsable(businessObject),
        setValue: (value: string) =>
            modeling.updateProperties(element, {
                resources: resourcesAmbResponsable(businessObject, businessObject.$model, value),
            }),
    });
};

const createCandidatsField = (tipus: 'usuaris' | 'grups', label: string, description: string) =>
    ({ element, id }: { element: BpmnElement; id: string }) => {
        const modeling = useService('modeling');
        const debounce = useService('debounceInput');
        const businessObject = element.businessObject;
        return TextFieldEntry({
            element,
            id,
            label,
            description,
            debounce,
            getValue: () => getCandidats(businessObject)[tipus].join(', '),
            setValue: (value: string) => {
                const candidats = { ...getCandidats(businessObject), [tipus]: separarPerComes(value) };
                modeling.updateProperties(element, {
                    resources: resourcesAmbCandidats(
                        businessObject,
                        businessObject.$model,
                        candidats.usuaris,
                        candidats.grups),
                });
            },
        });
    };

const CandidateUsers = createCandidatsField(
    'usuaris',
    'Usuaris candidats',
    'Separats per comes (bpmn:potentialOwner, user(...))');

const CandidateGroups = createCandidatsField(
    'grups',
    'Grups candidats',
    'Separats per comes (bpmn:potentialOwner, group(...))');

const DueDate = createTextField({
    property: 'dueDate',
    label: 'Data de venciment',
});

const Priority = createTextField({
    property: 'priority',
    label: 'Prioritat',
});

const Category = createTextField({
    property: 'category',
    label: 'Categoria',
});

const UserTaskFormKey = createTextField({
    property: 'formKey',
    label: 'Clau del formulari',
});

const createUserTaskPropertiesGroup = () => {
    return {
        id: 'flowable-user-task',
        label: "Tasca d'usuari",
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
    label: 'Dada del resultat',
});

const createServiceTaskPropertiesGroup = () => {
    return {
        id: 'flowable-service-task',
        label: 'Tasca de servei',
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
    label: 'Variable del resultat',
});

const AutoStoreVariables = createCheckbox({
    property: 'autoStoreVariables',
    label: 'Desar les variables automàticament',
    defaultValue: false,
});

const createScriptTaskPropertiesGroup = () => {
    return {
        id: 'flowable-script-task',
        label: 'Tasca de script',
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

/** Clau del procés que es crida: atribut estàndard bpmn:callActivity/@calledElement. */
const CalledElement = createTextField({
    property: 'bpmn:calledElement',
    label: 'Procés cridat',
});

const CalledElementType = createTextField({
    property: 'calledElementType',
    label: "Tipus de referència del procés cridat (key o id)",
});

const BusinessKey = createTextField({
    property: 'businessKey',
    label: 'Clau de negoci',
});

const InheritVariables = createCheckbox({
    property: 'inheritVariables',
    label: 'Heretar les variables',
    defaultValue: false,
});

const InheritBusinessKey = createCheckbox({
    property: 'inheritBusinessKey',
    label: 'Heretar la clau de negoci',
    defaultValue: false,
});

const FallbackToDefaultTenant = createCheckbox({
    property: 'fallbackToDefaultTenant',
    label: 'Usar el tenant per defecte si no es troba',
    defaultValue: false,
});

const createCallActivityPropertiesGroup = () => {
    return {
        id: 'flowable-call-activity',
        label: 'Activitat de crida',
        entries: [
            {
                id: 'flowable-called-element',
                component: CalledElement,
                isEdited: isTextFieldEntryEdited,
            },
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
    label: 'Asíncron',
    defaultValue: false,
});

const Exclusive = createCheckbox({
    property: 'exclusive',
    label: 'Exclusiu',
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
    if (element.businessObject.get('helium:async')) {
        entries.push({
            id: 'flowable-exclusive',
            component: Exclusive,
            isEdited: isCheckboxEntryEdited,
        });
    }
    return {
        id: 'flowable-async',
        label: 'Continuació asíncrona',
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
        label: 'Expressió de la condició',
        getValue,
        setValue,
        debounce,
    });
};

const createConditionExpressionPropertiesGroup = () => {
    return {
        id: 'flowable-condition',
        label: 'Condició',
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
    label: 'Expressió per ometre',
});

const createSkipExpressionPropertiesGroup = () => {
    return {
        id: 'flowable-skip-expression',
        label: 'Expressió per ometre',
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
    label: 'Col·lecció',
});

const ElementVariable = createModdleTextField({
    property: 'elementVariable',
    label: "Variable de l'element",
});

const createMultiInstancePropertiesGroup = () => {
    return {
        id: 'flowable-multi-instance',
        label: 'Multiinstància',
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
