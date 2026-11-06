import { useService } from 'bpmn-js-properties-panel';
import {
    CheckboxEntry,
    SelectEntry,
    TextFieldEntry,
} from '@bpmn-io/properties-panel';

export type BpmnElement = {
    businessObject: any;
};

export type EntryProps = {
    element: BpmnElement;
    id: string;
};

export type TextFieldConfig = {
    property: string;
    label: string;
};

export type CheckboxConfig = {
    property: string;
    label: string;
    defaultValue?: boolean;
};

export type SelectConfig = {
    property: string;
    label: string;
    getOptions: (element: BpmnElement) => { value: string; label: string }[];
};

export type EntryFactories = {
    createTextField: (config: TextFieldConfig) => (props: EntryProps) => any;
    createCheckbox: (config: CheckboxConfig) => (props: EntryProps) => any;
    createSelect: (config: SelectConfig) => (props: EntryProps) => any;
    createModdleTextField: (config: TextFieldConfig) => (props: EntryProps) => any;
};

export function createEntryFactories(prefix: string): EntryFactories {
    const fullProperty = (property: string) =>
        property.includes(':') ? property : `${prefix}:${property}`;

    const createTextField =
        (config: TextFieldConfig) =>
        ({ element, id }: EntryProps) => {
            const modeling = useService('modeling');
            const translate = useService('translate');
            const debounce = useService('debounceInput');
            const prop = fullProperty(config.property);
            return TextFieldEntry({
                element,
                id,
                label: translate(config.label),
                debounce,
                getValue: () => element.businessObject.get(prop) || '',
                setValue: (value: string) =>
                    modeling.updateProperties(element, { [prop]: value }),
            });
        };

    const createCheckbox =
        (config: CheckboxConfig) =>
        ({ element, id }: EntryProps) => {
            const modeling = useService('modeling');
            const translate = useService('translate');
            const prop = fullProperty(config.property);
            return CheckboxEntry({
                element,
                id,
                label: translate(config.label),
                getValue: () => {
                    const value = element.businessObject.get(prop);
                    return value ?? config.defaultValue ?? false;
                },
                setValue: (value: boolean) =>
                    modeling.updateProperties(element, { [prop]: value }),
            });
        };

    const createSelect =
        (config: SelectConfig) =>
        ({ element, id }: EntryProps) => {
            const modeling = useService('modeling');
            const translate = useService('translate');
            const prop = fullProperty(config.property);
            return SelectEntry({
                element,
                id,
                label: translate(config.label),
                getValue: () => element.businessObject.get(prop),
                setValue: (value: string) =>
                    modeling.updateProperties(element, { [prop]: value }),
                getOptions: () => config.getOptions(element),
            });
        };

    const createModdleTextField =
        (config: TextFieldConfig) =>
        ({ element, id }: EntryProps) => {
            const modeling = useService('modeling');
            const translate = useService('translate');
            const debounce = useService('debounceInput');
            const prop = fullProperty(config.property);
            const loopCharacteristics = element.businessObject.loopCharacteristics;
            return TextFieldEntry({
                element,
                id,
                label: translate(config.label),
                debounce,
                getValue: () => loopCharacteristics?.get(prop) || '',
                setValue: (value: string) =>
                    modeling.updateModdleProperties(element, loopCharacteristics, {
                        [prop]: value,
                    }),
            });
        };

    return { createTextField, createCheckbox, createSelect, createModdleTextField };
}
