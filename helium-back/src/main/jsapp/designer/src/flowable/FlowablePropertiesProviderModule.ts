import { is } from 'bpmn-js/lib/util/ModelUtil';
import { createJavaPropertiesGroup } from './JavaPropertiesGroup';
import { createJavaFieldsPropertiesGroup } from './JavaFieldsPropertiesGroup';

const LOW_PRIORITY = 50;

function FlowablePropertiesProvider(propertiesPanel, modeling) {
    propertiesPanel.registerProvider(LOW_PRIORITY, this);
    this._modeling = modeling;
}
FlowablePropertiesProvider.$inject = ['propertiesPanel', 'modeling'];
FlowablePropertiesProvider.prototype.getGroups = function (element) {
    const modeling = this._modeling;
    return (groups) => {
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
