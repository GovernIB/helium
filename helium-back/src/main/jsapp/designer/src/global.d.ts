export {};

/** Colors CSS (#rrggbb) del tema de l'editor. */
export interface TemaEditor {
    fosc: boolean;
    /** Fons del llenç. */
    fons: string;
    /** Text, línies i vores dels elements. */
    text: string;
    /** Interior dels elements del diagrama. */
    farciment: string;
}

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
            /** 'ide' dins el plugin d'IntelliJ (veure ide.ts); per defecte, dins Helium. */
            mode?: 'helium' | 'ide';
            /** Colors del tema de l'IDE (mode IDE). Amb fosc, l'editor fa servir el tema fosc d'app.css. */
            tema?: TemaEditor;
        };
    }
}
