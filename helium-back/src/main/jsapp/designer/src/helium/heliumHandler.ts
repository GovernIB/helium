import type { HandlerDto } from '../flowable/handlers';
import { getCandidats, resourcesAmbCandidats, resourcesAmbResponsable, separarPerComes } from './assignacio';

/**
 * Extensió de Helium per al BPMN 2.0 (ha de coincidir amb es.caib.helium.bpmn.handler.HeliumBpmnXml).
 *
 * Una service task que executa un handler de Helium es desa així, sense cap atribut de Flowable:
 *
 *   <bpmn:serviceTask implementation="http://www.caib.es/helium/bpmn/handler">
 *     <bpmn:extensionElements>
 *       <helium:handler classe="...">
 *         <helium:parametre nom="titol" valor="Text literal"/>
 *         <helium:parametre nom="motiu" expressio="${codiDada}"/>
 *       </helium:handler>
 *     </bpmn:extensionElements>
 *   </bpmn:serviceTask>
 */
export const HELIUM_IMPLEMENTATION = 'http://www.caib.es/helium/bpmn/handler';
export const HANDLER_TYPE = 'helium:Handler';
export const PARAMETRE_TYPE = 'helium:Parametre';

type BpmnElement = {
    businessObject: any;
};

type Modeling = {
    updateProperties: (element: BpmnElement, properties: Record<string, any>) => void;
    updateModdleProperties: (
        element: BpmnElement,
        moddleElement: any,
        properties: Record<string, any>
    ) => void;
};

export const getHeliumHandler = (businessObject: any): any | undefined =>
    businessObject?.extensionElements?.values?.find((v: any) => v.$type === HANDLER_TYPE);

export const isHeliumHandlerTask = (businessObject: any): boolean =>
    getHeliumHandler(businessObject) != null || businessObject?.implementation === HELIUM_IMPLEMENTATION;

/** El handler s'obté avaluant una expressió (helium:handler expressio="...") en lloc d'indicar-ne la classe. */
export const isHandlerPerExpressio = (businessObject: any): boolean =>
    getHeliumHandler(businessObject)?.expressio != null;

/**
 * Deixa el helium:handler de la tasca en el mode indicat (per classe o per expressió), sense paràmetres si canvia
 * de mode. El crea si no existeix.
 */
export const configurarHeliumHandler = (element: BpmnElement, modeling: Modeling, perExpressio: boolean) => {
    const handler = ensureHeliumHandler(element, modeling);
    if ((handler.expressio != null) !== perExpressio) {
        modeling.updateModdleProperties(element, handler, {
            classe: perExpressio ? undefined : '',
            expressio: perExpressio ? '' : undefined,
            parametres: [],
        });
    }
    return handler;
};

/** Retorna el helium:Handler de la tasca, creant-lo (amb l'extensionElements) si no existeix. */
export const ensureHeliumHandler = (element: BpmnElement, modeling: Modeling): any => {
    const businessObject = element.businessObject;
    const moddle = businessObject.$model;
    let extensionElements = businessObject.extensionElements;
    if (!extensionElements) {
        extensionElements = moddle.create('bpmn:ExtensionElements', { values: [] });
        extensionElements.$parent = businessObject;
        modeling.updateProperties(element, { extensionElements });
    }
    let handler = getHeliumHandler(businessObject);
    if (!handler) {
        handler = moddle.create(HANDLER_TYPE, { classe: '', parametres: [] });
        handler.$parent = extensionElements;
        modeling.updateModdleProperties(element, extensionElements, {
            values: [...(extensionElements.values || []), handler],
        });
    }
    return handler;
};

/** Elimina el helium:Handler de la tasca. */
export const removeHeliumHandler = (element: BpmnElement, modeling: Modeling) => {
    const extensionElements = element.businessObject.extensionElements;
    if (!extensionElements?.values) return;
    const values = extensionElements.values.filter((v: any) => v.$type !== HANDLER_TYPE);
    if (values.length === 0) {
        // No deixar un <bpmn:extensionElements/> buit
        modeling.updateProperties(element, { extensionElements: undefined });
    } else if (values.length !== extensionElements.values.length) {
        modeling.updateModdleProperties(element, extensionElements, { values });
    }
};

/** Noms dels paràmetres d'un handler del catàleg. */
export const nomsParametres = (handler: HandlerDto): string[] =>
    (handler.parametres || []).map((p) => p.codi || p.nom).filter((nom): nom is string => !!nom);

/** Descripció d'un paràmetre segons el catàleg de handlers. */
export const descripcioParametre = (handler: HandlerDto | undefined, nom: string): string | undefined => {
    const parametre = (handler?.parametres || []).find((p) => (p.codi || p.nom) === nom);
    return parametre ? parametre.paramDesc || parametre.nom || undefined : undefined;
};

/** Un valor és una expressió si conté ${...} o #{...} (ha de coincidir amb HandlerDefinicio.Parametre.esExpressio). */
export const esExpressio = (text: string | undefined | null): boolean =>
    !!text && (text.includes('${') || text.includes('#{'));

/** Valor d'un helium:parametre tal com es mostra a l'editor: l'expressió, si en té, o el valor literal. */
export const textParametre = (parametre: any): string => parametre?.expressio || parametre?.valor || '';

/**
 * Assigna el valor d'un helium:parametre a partir del text de l'editor: si és una expressió es desa a l'atribut
 * expressio (i es conserva el valor literal, que fa de valor per defecte), si no a l'atribut valor.
 */
export const assignarTextParametre = (element: BpmnElement, modeling: Modeling, parametre: any, text: string) => {
    if (esExpressio(text)) {
        modeling.updateModdleProperties(element, parametre, { expressio: text });
    } else {
        modeling.updateModdleProperties(element, parametre, { valor: text, expressio: undefined });
    }
};

/**
 * Substitueix els paràmetres del helium:Handler pels del handler del catàleg. Conserva el valor
 * dels paràmetres que ja existien amb el mateix nom.
 */
export const carregarParametres = (
    element: BpmnElement,
    modeling: Modeling,
    heliumHandler: any,
    handler: HandlerDto
) => {
    const moddle = element.businessObject.$model;
    const existents = new Map<string, any>(
        (heliumHandler.parametres || []).map((p: any) => [p.nom, p])
    );
    const parametres = nomsParametres(handler).map((nom) => {
        const anterior = existents.get(nom);
        const parametre = moddle.create(PARAMETRE_TYPE, {
            nom,
            valor: anterior?.expressio && !anterior?.valor ? undefined : (anterior?.valor ?? ''),
            expressio: anterior?.expressio,
        });
        parametre.$parent = heliumHandler;
        return parametre;
    });
    modeling.updateModdleProperties(element, heliumHandler, { parametres });
};

const valorCampFlowable = (field: any): { valor?: string; expressio?: string } => {
    const expressio = field.expression ?? field.expressionElement?.body;
    if (expressio != null && expressio !== '') return { expressio };
    return { valor: field.stringValue ?? field.get?.('string') ?? '' };
};

const flowElements = (container: any): any[] => {
    const elements: any[] = [];
    for (const element of container?.flowElements || []) {
        elements.push(element);
        if (element.flowElements) elements.push(...flowElements(element));
    }
    return elements;
};

/**
 * Atributs d'execució que el BPMN 2.0 no defineix i que es desen com a helium:xxx
 * (ha de coincidir amb es.caib.helium.bpmn.handler.HeliumBpmnXml.ATRIBUTS_EXTENSIO).
 */
export const ATRIBUTS_EXTENSIO = [
    'candidateStarterUsers',
    'candidateStarterGroups',
    'initiator',
    'formKey',
    'dueDate',
    'priority',
    'category',
    'expression',
    'resultVariableName',
    'resultVariable',
    'autoStoreVariables',
    'calledElementType',
    'businessKey',
    'inheritVariables',
    'inheritBusinessKey',
    'fallbackToDefaultTenant',
    'async',
    'exclusive',
    'skipExpression',
    'collection',
    'elementVariable',
];

const ATRIBUTS_BOOLEANS = new Set([
    'autoStoreVariables',
    'inheritVariables',
    'inheritBusinessKey',
    'fallbackToDefaultTenant',
    'async',
    'exclusive',
]);

const elementsAmbAtributs = (definitions: any): any[] => {
    const elements: any[] = [];
    for (const proces of (definitions?.rootElements || []).filter((e: any) => e.$type === 'bpmn:Process')) {
        elements.push(proces);
        for (const element of flowElements(proces)) {
            elements.push(element);
            if (element.loopCharacteristics) elements.push(element.loopCharacteristics);
        }
    }
    return elements;
};

/**
 * Passa els atributs flowable:xxx al format genèric:
 *  - els atributs d'execució (async, formKey, dueDate...) a helium:xxx;
 *  - l'assignació de les tasques d'usuari (assignee, candidateUsers, candidateGroups) als elements
 *    estàndard humanPerformer i potentialOwner.
 * Els atributs específics de les service tasks de Flowable (class, expression, delegateExpression) no
 * es toquen.
 *
 * @return el nombre d'atributs convertits.
 */
export const migrarAtributsFlowable = (definitions: any, moddle: any): number => {
    let migrats = 0;
    for (const element of elementsAmbAtributs(definitions)) {
        for (const nom of ATRIBUTS_EXTENSIO) {
            // Els atributs flowable:* ja no són al descriptor, així que el parser els deixa a $attrs
            const clau = `flowable:${nom}`;
            const valor = element.$attrs?.[clau] ?? element.get(clau);
            if (valor === undefined || valor === null || valor === '') continue;
            element.set(`helium:${nom}`, ATRIBUTS_BOOLEANS.has(nom) ? `${valor}` === 'true' : valor);
            if (element.$attrs) delete element.$attrs[clau];
            migrats++;
        }
        if (element.$type === 'bpmn:UserTask' && element.$attrs) {
            const attrs = element.$attrs;
            const assignee = attrs['flowable:assignee'];
            const usuaris = separarPerComes(attrs['flowable:candidateUsers']);
            const grups = separarPerComes(attrs['flowable:candidateGroups']);
            if (assignee) {
                element.resources = resourcesAmbResponsable(element, moddle, assignee);
                migrats++;
            }
            if (usuaris.length || grups.length) {
                const existents = getCandidats(element);
                element.resources = resourcesAmbCandidats(
                    element,
                    moddle,
                    [...existents.usuaris, ...usuaris],
                    [...existents.grups, ...grups]);
                migrats++;
            }
            delete attrs['flowable:assignee'];
            delete attrs['flowable:candidateUsers'];
            delete attrs['flowable:candidateGroups'];
        }
    }
    return migrats;
};

/**
 * Converteix les service tasks del format antic (flowable:class + flowable:field) que apunten a
 * un handler de Helium al format genèric (helium:handler + helium:parametre). Les tasques amb
 * classes que no són handlers de Helium es deixen com estan.
 *
 * @return el nombre de tasques convertides.
 */
export const migrarHandlersFlowable = (definitions: any, moddle: any, classesHandlers: Set<string>): number => {
    let migrades = 0;
    const processos = (definitions?.rootElements || []).filter((e: any) => e.$type === 'bpmn:Process');
    for (const proces of processos) {
        for (const tasca of flowElements(proces)) {
            if (tasca.$type !== 'bpmn:ServiceTask') continue;
            const classe = tasca.get('flowable:class');
            if (!classe || !classesHandlers.has(classe)) continue;
            let extensionElements = tasca.extensionElements;
            if (!extensionElements) {
                extensionElements = moddle.create('bpmn:ExtensionElements', { values: [] });
                extensionElements.$parent = tasca;
                tasca.extensionElements = extensionElements;
            }
            const values = extensionElements.values || [];
            const camps = values.filter((v: any) => v.$type === 'flowable:Field');
            const heliumHandler = moddle.create(HANDLER_TYPE, { classe, parametres: [] });
            heliumHandler.$parent = extensionElements;
            heliumHandler.parametres = camps.map((camp: any) => {
                const parametre = moddle.create(PARAMETRE_TYPE, { nom: camp.name, ...valorCampFlowable(camp) });
                parametre.$parent = heliumHandler;
                return parametre;
            });
            extensionElements.values = [
                ...values.filter((v: any) => v.$type !== 'flowable:Field' && v.$type !== HANDLER_TYPE),
                heliumHandler,
            ];
            tasca.set('flowable:class', undefined);
            tasca.implementation = HELIUM_IMPLEMENTATION;
            migrades++;
        }
    }
    return migrades;
};

const IDENTIFICADOR = /^[A-Za-z_][A-Za-z0-9_]*$/;

/**
 * Converteix els antics paràmetres varXxx dels handlers del catàleg (codi d'una dada de l'expedient) en una
 * expressió del paràmetre xxx: varXxx = codi passa a xxx = ${codi}. Si xxx tenia un valor literal, es conserva com
 * a valor per defecte (s'utilitza si la dada no té valor). Ha de coincidir amb es.caib.helium.bpmn.handler.ParametresVar.
 *
 * Només es converteixen els varXxx que el handler ja no té i dels quals sí que té el paràmetre xxx; els de sortida
 * (p. ex. varCsv, el codi de la dada on es desa un resultat) es deixen com estan.
 *
 * @return el nombre de paràmetres convertits.
 */
export const migrarParametresVar = (definitions: any, handlers: HandlerDto[]): number => {
    let migrats = 0;
    const processos = (definitions?.rootElements || []).filter((e: any) => e.$type === 'bpmn:Process');
    for (const proces of processos) {
        for (const tasca of flowElements(proces)) {
            const heliumHandler = getHeliumHandler(tasca);
            const handler = handlers.find((h) => h.classe === heliumHandler?.classe);
            if (!heliumHandler || !handler) continue;
            const noms = new Set(nomsParametres(handler));
            const moddle = tasca.$model;
            let parametres: any[] = [...(heliumHandler.parametres || [])];
            for (const parametre of [...parametres]) {
                const m = /^var([A-Za-z].*)$/.exec(parametre.nom || '');
                if (!m || noms.has(parametre.nom)) continue;
                const substitut = m[1].charAt(0).toLowerCase() + m[1].slice(1);
                if (!noms.has(substitut)) continue;
                const codi = (parametre.valor || '').trim();
                if (parametre.expressio) {
                    console.warn(`No es pot convertir el paràmetre ${parametre.nom} de la tasca ${tasca.id}: el codi de la dada és una expressió (${parametre.expressio})`);
                    continue;
                }
                parametres = parametres.filter((p) => p !== parametre);
                migrats++;
                if (!IDENTIFICADOR.test(codi)) continue;
                const expressio = `\${${codi}}`;
                const anterior = parametres.find((p) => p.nom === substitut);
                if (anterior) {
                    anterior.expressio = expressio;
                    if (!anterior.valor) anterior.valor = undefined;
                } else {
                    const nou = moddle.create(PARAMETRE_TYPE, { nom: substitut, expressio });
                    nou.$parent = heliumHandler;
                    parametres.push(nou);
                }
            }
            heliumHandler.parametres = parametres;
        }
    }
    return migrats;
};
