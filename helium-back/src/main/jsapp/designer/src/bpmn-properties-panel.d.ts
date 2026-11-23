declare module '@bpmn-io/properties-panel' {
    export const ListGroup: any;
    export const SelectEntry: any;
    export const TextFieldEntry: any;
    export const isSelectEntryEdited: any;
    export const isTextFieldEntryEdited: any;
    export const isTextAreaEntryEdited: any;
    export const CheckboxEntry: any;
    export const isCheckboxEntryEdited: any;
    export const TextAreaEntry: any;
    export const ArrowIcon: any;
    export const CreateIcon: any;
    export function useLayoutState<T>(path: string[], valorPerDefecte: T): [T, (valor: T) => void];
}

declare module 'bpmn-js-properties-panel' {
    export const BpmnPropertiesPanelModule: any;
    export const BpmnPropertiesProviderModule: any;
    export const CamundaPlatformPropertiesProviderModule: any;
    export function useService(name: string): any;
}

declare module '@bpmn-io/properties-panel/preact' {
    export const h: any;
}

declare module '@bpmn-io/properties-panel/preact/hooks' {
    export function useState<T>(initial: T): [T, (valor: T) => void];
    export function useEffect(efecte: () => void | (() => void), deps?: unknown[]): void;
    export function useMemo<T>(fabrica: () => T, deps: unknown[]): T;
    export function useRef<T>(inicial: T): { current: T };
}
