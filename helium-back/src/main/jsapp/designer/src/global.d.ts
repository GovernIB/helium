export {};

declare global {
    interface Window {
        __APP_CONFIG__: {
            baseUrl: string;
            returnUrl: string;
            hasStartTask: boolean;
            entornId: string;
            expedientTipusId: string;
            definicioProcesId: string;
            definicioProcesEtiqueta: string;
            isNew: boolean;
        };
    }
}
