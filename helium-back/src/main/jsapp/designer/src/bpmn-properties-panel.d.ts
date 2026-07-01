declare module '@bpmn-io/properties-panel' {
    export const ListGroup: any;
    export const SelectEntry: any;
    export const TextFieldEntry: any;
    export const isSelectEntryEdited: any;
    export const isTextFieldEntryEdited: any;
}

declare module 'bpmn-js-properties-panel' {
    export const BpmnPropertiesPanelModule: any;
    export const BpmnPropertiesProviderModule: any;
    export const CamundaPlatformPropertiesProviderModule: any;
    export function useService(name: string): any;
}
