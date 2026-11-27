/**
 * Comunicació amb l'IDE quan l'editor s'executa dins el plugin d'IntelliJ (helium-intellij-plugin).
 *
 * En aquest mode no hi ha servidor de Helium: el diagrama i el catàleg de handlers arriben de l'IDE i cada canvi del
 * diagrama s'envia a l'IDE, que el desa al document del fitxer. El document és la font de veritat: si es modifica
 * des de l'editor de text, l'IDE torna a carregar el XML aquí.
 *
 * Protocol (missatges JSON):
 *  - Editor → IDE, amb window.__heliumIdeEnviar (el defineix el plugin amb un JBCefJSQuery). El plugin el torna a
 *    definir en acabar de carregar la pàgina i crida window.__heliumIdeConnectat: els missatges que no s'han pogut
 *    enviar abans es guarden i s'envien llavors.
 *      {tipus: 'preparat'}            l'editor ja pot rebre el diagrama
 *      {tipus: 'canvi', xml}          el diagrama ha canviat
 *      {tipus: 'desfer' | 'refer'}    Ctrl+Z / Ctrl+Shift+Z: els aplica l'IDE sobre el document
 *      {tipus: 'error', missatge}     error en importar el XML
 *      {tipus: 'repintar'}            l'IDE ha de tornar a pintar la vista (vegeu demanarRepintat)
 *  - IDE → editor, amb les funcions de window.heliumIde (registrarIde):
 *      carregar(xml, handlers)        primera càrrega
 *      actualitzar(xml)               el document ha canviat fora de l'editor
 *      handlers(handlers)             catàleg de handlers (p. ex. quan l'IDE acaba d'indexar el projecte)
 */
import type { HandlerDto } from './flowable/handlers';

export type MissatgeIde =
    | { tipus: 'preparat' }
    | { tipus: 'canvi'; xml: string }
    | { tipus: 'desfer' }
    | { tipus: 'refer' }
    | { tipus: 'error'; missatge: string }
    | { tipus: 'repintar' };

export interface ReceptorIde {
    carregar: (xml: string, handlers: HandlerDto[]) => void;
    actualitzar: (xml: string) => void;
    handlers: (handlers: HandlerDto[]) => void;
}

declare global {
    interface Window {
        __heliumIdeEnviar?: (missatge: string) => void;
        __heliumIdeConnectat?: () => void;
        heliumIde?: ReceptorIde;
    }
}

/** L'editor s'executa dins l'IDE (el plugin defineix la configuració amb mode 'ide'). */
export const esModeIde = (): boolean => window.__APP_CONFIG__?.mode === 'ide';

const pendents: string[] = [];

const intentarEnviar = (json: string): boolean => {
    try {
        if (window.__heliumIdeEnviar) {
            window.__heliumIdeEnviar(json);
            return true;
        }
    } catch (e) {
        // El pont encara no està connectat (la funció nativa de JCEF no existeix)
        console.debug('Missatge a l\'IDE pendent', e);
    }
    return false;
};

export const enviarIde = (missatge: MissatgeIde) => {
    const json = JSON.stringify(missatge);
    if (pendents.length > 0 || !intentarEnviar(json)) {
        pendents.push(json);
    }
};

window.__heliumIdeConnectat = () => {
    while (pendents.length > 0 && intentarEnviar(pendents[0])) {
        pendents.shift();
    }
};

let repintatProper: ReturnType<typeof setTimeout> | undefined;
let repintatFinal: ReturnType<typeof setTimeout> | undefined;

/**
 * Demana a l'IDE que torni a pintar tota la vista. Dins l'IDE el navegador (JCEF fora de pantalla) de vegades no
 * actualitza la imatge després d'un canvi: elements que es veuen encara seleccionats, o parts tallades del plafó de
 * propietats en plegar o desplegar un grup. Es corregeix quan es canvia de vista; amb això, de seguida.
 *
 * S'envia dues vegades: poc després de la interacció i quan ja han acabat les transicions del plafó. Les crides
 * seguides s'agrupen.
 */
export const demanarRepintat = () => {
    if (!esModeIde()) {
        return;
    }
    clearTimeout(repintatProper);
    clearTimeout(repintatFinal);
    repintatProper = setTimeout(() => enviarIde({ tipus: 'repintar' }), 60);
    repintatFinal = setTimeout(() => enviarIde({ tipus: 'repintar' }), 450);
};

export const registrarIde = (receptor: ReceptorIde) => {
    window.heliumIde = receptor;
};
