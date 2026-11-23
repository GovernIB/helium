/**
 * Assignació de les tasques d'usuari amb els elements estàndard del BPMN 2.0:
 *
 *   <bpmn:humanPerformer>   → responsable (una sola expressió: codi d'usuari o ${...})
 *   <bpmn:potentialOwner>   → candidats, amb la sintaxi "user(codi), group(codi)"
 *
 * Flowable els entén directament (assignee, candidateUsers i candidateGroups).
 */

const HUMAN_PERFORMER = 'bpmn:HumanPerformer';
const POTENTIAL_OWNER = 'bpmn:PotentialOwner';

/** Separa per comes respectant les expressions (${a, b}) i els parèntesis. */
export const separarPerComes = (text: string | undefined): string[] => {
    if (!text) return [];
    const parts: string[] = [];
    let profunditat = 0;
    let actual = '';
    for (const c of text) {
        if (c === '{' || c === '(') profunditat++;
        if (c === '}' || c === ')') profunditat = Math.max(0, profunditat - 1);
        if (c === ',' && profunditat === 0) {
            parts.push(actual);
            actual = '';
        } else {
            actual += c;
        }
    }
    parts.push(actual);
    return parts.map((p) => p.trim()).filter((p) => p.length > 0);
};

const expressioDeRol = (rol: any): string | undefined =>
    rol?.resourceAssignmentExpression?.expression?.body;

const recursos = (businessObject: any): any[] => businessObject?.resources || [];

export const getResponsable = (businessObject: any): string =>
    expressioDeRol(recursos(businessObject).find((r: any) => r.$type === HUMAN_PERFORMER)) || '';

export const getCandidats = (businessObject: any): { usuaris: string[]; grups: string[] } => {
    const usuaris: string[] = [];
    const grups: string[] = [];
    for (const rol of recursos(businessObject).filter((r: any) => r.$type === POTENTIAL_OWNER)) {
        for (const part of separarPerComes(expressioDeRol(rol))) {
            const usuari = /^user\((.*)\)$/.exec(part);
            const grup = /^group\((.*)\)$/.exec(part);
            if (usuari) usuaris.push(usuari[1].trim());
            else if (grup) grups.push(grup[1].trim());
            else grups.push(part); // Sense prefix, Flowable ho tracta com a grup
        }
    }
    return { usuaris, grups };
};

const crearRol = (moddle: any, tipus: string, expressio: string, parent: any) => {
    const formalExpression = moddle.create('bpmn:FormalExpression', { body: expressio });
    const assignment = moddle.create('bpmn:ResourceAssignmentExpression', { expression: formalExpression });
    formalExpression.$parent = assignment;
    const rol = moddle.create(tipus, { resourceAssignmentExpression: assignment });
    assignment.$parent = rol;
    rol.$parent = parent;
    return rol;
};

/** Retorna la nova llista de resources amb el responsable indicat (buit = sense responsable). */
export const resourcesAmbResponsable = (businessObject: any, moddle: any, responsable: string | undefined): any[] => {
    const altres = recursos(businessObject).filter((r: any) => r.$type !== HUMAN_PERFORMER);
    const valor = responsable?.trim();
    return valor ? [crearRol(moddle, HUMAN_PERFORMER, valor, businessObject), ...altres] : altres;
};

/** Retorna la nova llista de resources amb els candidats indicats (en un sol potentialOwner). */
export const resourcesAmbCandidats = (
    businessObject: any,
    moddle: any,
    usuaris: string[],
    grups: string[]
): any[] => {
    const altres = recursos(businessObject).filter((r: any) => r.$type !== POTENTIAL_OWNER);
    const parts = [...usuaris.map((u) => `user(${u})`), ...grups.map((g) => `group(${g})`)];
    return parts.length ? [...altres, crearRol(moddle, POTENTIAL_OWNER, parts.join(', '), businessObject)] : altres;
};
