import { is } from 'bpmn-js/lib/util/ModelUtil';
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
FlowablePropertiesProvider.$inject = ['propertiesPanel', 'modeling'];
FlowablePropertiesProvider.prototype.getGroups = function (this: FlowablePropertiesProviderInstance, element: any) {
    const modeling = this._modeling;
    return (groups: any) => {
        if (is(element, 'bpmn:ServiceTask')) {
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
