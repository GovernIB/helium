import { is } from 'bpmn-js/lib/util/ModelUtil';
import { useService } from 'bpmn-js-properties-panel';
import { TextFieldEntry, isTextFieldEntryEdited } from '@bpmn-io/properties-panel';
import { createJavaPropertiesGroup } from './JavaPropertiesGroup';
import { createJavaFieldsPropertiesGroup } from './JavaFieldsPropertiesGroup';

const LOW_PRIORITY = 50;

type FlowablePropertiesProviderInstance = {
    _modeling: any;
};

function FlowablePropertiesProvider(this: FlowablePropertiesProviderInstance, propertiesPanel: any, modeling: any) {
    propertiesPanel.registerProvider(LOW_PRIORITY, this);
    this._modeling = modeling;
}

// @ts-ignore
function CandidateGroups({ element, id }) {
    const modeling = useService('modeling');
    const translate = useService('translate');
    const debounce = useService('debounceInput');

    return TextFieldEntry({
        element,
        id,
        label: translate('Candidate groups'),
        debounce,
        getValue: () => element.businessObject.get('flowable:candidateGroups') || '',
        setValue: (value: any) =>
            modeling.updateProperties(element, { 'flowable:candidateGroups': value })
    });
}

// @ts-ignore
function CandidateUsers({ element, id }) {
    const modeling = useService('modeling');
    const translate = useService('translate');
    const debounce = useService('debounceInput');

    return TextFieldEntry({
        element,
        id,
        label: translate('Candidate users'),
        debounce,
        getValue: () => element.businessObject.get('flowable:candidateUsers') || '',
        setValue: (value: any) =>
            modeling.updateProperties(element, { 'flowable:candidateUsers': value })
    });
}

FlowablePropertiesProvider.$inject = ['propertiesPanel', 'modeling'];
FlowablePropertiesProvider.prototype.getGroups = function (this: FlowablePropertiesProviderInstance, element: any) {
    const modeling = this._modeling;
    return (groups: any) => {
        if (is(element, 'bpmn:UserTask')) {
            groups.push({
                id: 'flowable',
                label: 'Flowable',
                entries: [{
                    id: 'candidateGroups',
                    component: CandidateGroups,
                    isEdited: isTextFieldEntryEdited
                }, {
                    id: 'candidateUsers',
                    component: CandidateUsers,
                    isEdited: isTextFieldEntryEdited
                }]
            });
        } else if (is(element, 'bpmn:ServiceTask')) {
            return [
                ...groups,
                createJavaPropertiesGroup(element),
                createJavaFieldsPropertiesGroup(element, modeling),
            ];
        }
        return groups;
    };
};

export const FlowablePropertiesProviderModule = {
    __init__: ['flowablePropertiesProvider'],
    flowablePropertiesProvider: ['type', FlowablePropertiesProvider],
};

export default FlowablePropertiesProviderModule;
