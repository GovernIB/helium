/**
 * Traducció al català dels textos de l'editor: plafó de propietats (bpmn-js-properties-panel i
 * @bpmn-io/properties-panel), paleta d'eines, menú contextual dels elements i menú de canvi de tipus (bpmn-js).
 *
 * Substitueix el servei `translate` de diagram-js, que és el que fan servir les llibreries per a tots els textos de
 * la interfície. Els textos que no són al diccionari es mostren tal com arriben (en anglès), igual que fa el servei
 * original. Les etiquetes dels grups propis de Helium ja s'escriuen directament en català.
 */

const TEXTOS: Record<string, string> = {
    // Estats del plafó
    'Select an element to edit its properties.': 'Seleccioneu un element per editar-ne les propietats.',
    'Multiple elements are selected. Select a single element to edit its properties.':
        'Hi ha diversos elements seleccionats. Seleccioneu-ne només un per editar-ne les propietats.',

    // Grups i camps generals
    'General': 'General',
    'Name': 'Nom',
    'ID': 'ID',
    'Participant ID': 'ID del participant',
    'Participant Name': 'Nom del participant',
    'Process ID': 'ID del procés',
    'Process name': 'Nom del procés',
    'Executable': 'Executable',
    'Documentation': 'Documentació',
    'Element documentation': 'Documentació de l\'element',
    'Process documentation': 'Documentació del procés',
    'Type': 'Tipus',
    'Value': 'Valor',
    'Code': 'Codi',
    'Date': 'Data',
    '<none>': '<cap>',
    '<empty>': '<buit>',

    // Validació de l'ID
    'ID must not be empty.': 'L\'ID no pot estar buit.',
    'ID must be unique.': 'L\'ID ha de ser únic.',
    'ID must not contain spaces.': 'L\'ID no pot contenir espais.',
    'ID must not contain prefix.': 'L\'ID no pot contenir cap prefix.',
    'ID must be a valid QName.': 'L\'ID ha de ser un QName vàlid.',

    // Definicions d'esdeveniments
    'Message': 'Missatge',
    'Signal': 'Senyal',
    'Error': 'Error',
    'Escalation': 'Escalat',
    'Link': 'Enllaç',
    'Timer': 'Temporitzador',
    'Compensation': 'Compensació',
    'Global message reference': 'Referència al missatge global',
    'Global signal reference': 'Referència al senyal global',
    'Global error reference': 'Referència a l\'error global',
    'Global escalation reference': 'Referència a l\'escalat global',
    'Create new ...': 'Crear-ne un de nou...',
    'Activity reference': 'Referència a l\'activitat',
    'Wait for completion': 'Esperar que finalitzi',

    // Temporitzadors
    'Cycle': 'Cicle',
    'Duration': 'Durada',
    'Documentation: Timer events': 'Documentació: esdeveniments de temporitzador',
    'A specific point in time defined as ISO 8601 combined date and time representation.':
        'Un moment concret, en format de data i hora combinades ISO 8601.',
    'A time duration defined as ISO 8601 durations format.': 'Una durada, en format de durades ISO 8601.',
    'A cycle defined as ISO 8601 repeating intervals format.': 'Un cicle, en format d\'intervals repetits ISO 8601.',
    'UTC time': 'Hora UTC',
    'UTC plus 2 hours zone offset': 'UTC amb una diferència horària de +2 hores',
    '15 seconds': '15 segons',
    '1 hour and 30 minutes': '1 hora i 30 minuts',
    '14 days': '14 dies',
    'every 10 seconds, up to 5 times': 'cada 10 segons, fins a 5 vegades',
    'every day, infinitely': 'cada dia, indefinidament',

    // Multiinstància
    'Multi-instance': 'Multiinstància',
    'Loop cardinality': 'Cardinalitat del bucle',
    'Completion': 'Finalització',
    'Completion condition': 'Condició de finalització',
    'Cancel remaining instances': 'Cancel·lar les instàncies restants',

    // Components del plafó (@bpmn-io/properties-panel)
    'Create': 'Crear',
    'Create new list item': 'Afegir un element a la llista',
    'Delete item': 'Eliminar l\'element',
    'Toggle list item': 'Desplegar o plegar l\'element',
    'Toggle section': 'Desplegar o plegar la secció',
    'Section contains edits': 'La secció té valors informats',
    'Section contains an error': 'La secció conté un error',
    'Open documentation': 'Obrir la documentació',
    'Open pop-up editor': 'Obrir l\'editor emergent',
    'Opened in editor': 'Obert a l\'editor',
    'JSON contains errors': 'El JSON conté errors',
    'FEEL expression is mandatory': 'L\'expressió FEEL és obligatòria',
    'Unparsable FEEL expression.': 'L\'expressió FEEL no és vàlida.',

    // Tipus d'element de la capçalera del plafó (els esdeveniments es tradueixen a traduirEsdeveniment)
    'Process': 'Procés',
    'Collaboration': 'Col·laboració',
    'Participant': 'Participant',
    'Lane': 'Carril',
    'Group': 'Grup',
    'Text Annotation': 'Anotació de text',
    'Association': 'Associació',
    'Sequence Flow': 'Flux de seqüència',
    'Conditional Flow': 'Flux condicional',
    'Default Flow': 'Flux per defecte',
    'Message Flow': 'Flux de missatges',
    'Task': 'Tasca',
    'User Task': 'Tasca d\'usuari',
    'Service Task': 'Tasca de servei',
    'Script Task': 'Tasca de script',
    'Send Task': 'Tasca d\'enviament',
    'Receive Task': 'Tasca de recepció',
    'Manual Task': 'Tasca manual',
    'Business Rule Task': 'Tasca de regla de negoci',
    'Call Activity': 'Activitat de crida',
    'Transaction': 'Transacció',
    'Event Sub Process': 'Subprocés d\'esdeveniment',
    'Collapsed Sub Process': 'Subprocés contret',
    'Expanded Sub Process': 'Subprocés expandit',
    'Collapsed Ad Hoc Sub Process': 'Subprocés ad hoc contret',
    'Expanded Ad Hoc Sub Process': 'Subprocés ad hoc expandit',
    'Gateway': 'Passarel·la',
    'Exclusive Gateway': 'Passarel·la exclusiva',
    'Inclusive Gateway': 'Passarel·la inclusiva',
    'Parallel Gateway': 'Passarel·la paral·lela',
    'Complex Gateway': 'Passarel·la complexa',
    'Event Based Gateway': 'Passarel·la basada en esdeveniments',
    'Data Input': 'Entrada de dades',
    'Data Output': 'Sortida de dades',
    'Data Input Association': 'Associació d\'entrada de dades',
    'Data Output Association': 'Associació de sortida de dades',
    'Data Object Reference': 'Referència a objecte de dades',
    'Data Store Reference': 'Referència a magatzem de dades',

    // Paleta d'eines
    'Activate hand tool': 'Activar l\'eina de mà',
    'Activate lasso tool': 'Activar l\'eina de selecció',
    'Activate create/remove space tool': 'Activar l\'eina per crear o eliminar espai',
    'Activate global connect tool': 'Activar l\'eina de connexió',
    'Create start event': 'Crear un esdeveniment d\'inici',
    'Create intermediate/boundary event': 'Crear un esdeveniment intermedi o de vora',
    'Create end event': 'Crear un esdeveniment final',
    'Create gateway': 'Crear una passarel·la',
    'Create task': 'Crear una tasca',
    'Create data object reference': 'Crear una referència a objecte de dades',
    'Create data store reference': 'Crear una referència a magatzem de dades',
    'Create expanded sub-process': 'Crear un subprocés expandit',
    'Create pool/participant': 'Crear un pool/participant',
    'Create group': 'Crear un grup',

    // Menú contextual dels elements
    'Append end event': 'Afegir un esdeveniment final',
    'Append gateway': 'Afegir una passarel·la',
    'Append task': 'Afegir una tasca',
    'Append intermediate/boundary event': 'Afegir un esdeveniment intermedi o de vora',
    'Append receive task': 'Afegir una tasca de recepció',
    'Append message intermediate catch event': 'Afegir un esdeveniment intermedi de captura de missatge',
    'Append timer intermediate catch event': 'Afegir un esdeveniment intermedi de captura de temporitzador',
    'Append conditional intermediate catch event': 'Afegir un esdeveniment intermedi de captura condicional',
    'Append signal intermediate catch event': 'Afegir un esdeveniment intermedi de captura de senyal',
    'Append compensation activity': 'Afegir una activitat de compensació',
    'Add text annotation': 'Afegir una anotació de text',
    'Add lane above': 'Afegir un carril a sobre',
    'Add lane below': 'Afegir un carril a sota',
    'Divide into two lanes': 'Dividir en dos carrils',
    'Divide into three lanes': 'Dividir en tres carrils',
    'Change element': 'Canviar l\'element',
    'Connect to other element': 'Connectar amb un altre element',
    'Connect using association': 'Connectar amb una associació',
    'Connect using data input association': 'Connectar amb una associació d\'entrada de dades',
    'Delete': 'Eliminar',
    'Open {element}': 'Obrir {element}',
    'Search in diagram': 'Cercar al diagrama',
    'Align elements': 'Alinear els elements',
    'Align elements left': 'Alinear a l\'esquerra',
    'Align elements center': 'Centrar horitzontalment',
    'Align elements right': 'Alinear a la dreta',
    'Align elements top': 'Alinear a dalt',
    'Align elements middle': 'Centrar verticalment',
    'Align elements bottom': 'Alinear a baix',
    'Distribute elements horizontally': 'Distribuir els elements horitzontalment',
    'Distribute elements vertically': 'Distribuir els elements verticalment',

    // Menú de canvi de tipus (els esdeveniments es tradueixen a traduirEsdeveniment)
    'Parallel multi-instance': 'Multiinstància paral·lela',
    'Sequential multi-instance': 'Multiinstància seqüencial',
    'Loop': 'Bucle',
    'Collection': 'Col·lecció',
    'Participant multiplicity': 'Multiplicitat del participant',
    'Toggle non-interrupting': 'Canviar entre interruptor i no interruptor',
    'User task': 'Tasca d\'usuari',
    'Service task': 'Tasca de servei',
    'Script task': 'Tasca de script',
    'Send task': 'Tasca d\'enviament',
    'Receive task': 'Tasca de recepció',
    'Manual task': 'Tasca manual',
    'Business rule task': 'Tasca de regla de negoci',
    'Call activity': 'Activitat de crida',
    'Sub-process': 'Subprocés',
    'Sub-process (collapsed)': 'Subprocés (contret)',
    'Sub-process (expanded)': 'Subprocés (expandit)',
    'Ad-hoc sub-process': 'Subprocés ad hoc',
    'Ad-hoc sub-process (collapsed)': 'Subprocés ad hoc (contret)',
    'Ad-hoc sub-process (expanded)': 'Subprocés ad hoc (expandit)',
    'Event sub-process': 'Subprocés d\'esdeveniment',
    'Exclusive gateway': 'Passarel·la exclusiva',
    'Parallel gateway': 'Passarel·la paral·lela',
    'Inclusive gateway': 'Passarel·la inclusiva',
    'Complex gateway': 'Passarel·la complexa',
    'Event-based gateway': 'Passarel·la basada en esdeveniments',
    'Event based instantiating Gateway': 'Passarel·la basada en esdeveniments que inicia el procés',
    'Parallel Event based instantiating Gateway': 'Passarel·la paral·lela basada en esdeveniments que inicia el procés',
    'Sequence flow': 'Flux de seqüència',
    'Default flow': 'Flux per defecte',
    'Conditional flow': 'Flux condicional',
    'Data object reference': 'Referència a objecte de dades',
    'Data store reference': 'Referència a magatzem de dades',
    'Expanded pool/participant': 'Pool/participant expandit',
    'Empty pool/participant': 'Pool/participant buit',
    'Empty pool/participant (removes content)': 'Pool/participant buit (n\'elimina el contingut)',
};

/*
 * Esdeveniments: el text combina la definició, el tipus i si és interruptor. La capçalera del plafó els escriu
 * en majúscules inicials ("Timer Start Event (Non Interrupting)") i el menú de canvi de tipus en minúscula
 * ("Timer start event (non-interrupting)"), així que es compara sense distingir majúscules. Claus en minúscula.
 */
const ESDEVENIMENTS: Record<string, string> = {
    'start event': 'Esdeveniment d\'inici',
    'end event': 'Esdeveniment final',
    'boundary event': 'Esdeveniment de vora',
    'intermediate catch event': 'Esdeveniment intermedi de captura',
    'intermediate throw event': 'Esdeveniment intermedi de llançament',
};

const DEFINICIONS_ESDEVENIMENT: Record<string, string> = {
    'timer': 'de temporitzador',
    'message': 'de missatge',
    'signal': 'de senyal',
    'error': 'd\'error',
    'escalation': 'd\'escalat',
    'conditional': 'condicional',
    'compensate': 'de compensació',
    'compensation': 'de compensació',
    'cancel': 'de cancel·lació',
    'link': 'd\'enllaç',
    'terminate': 'de terminació',
    'multiple': 'múltiple',
    'parallel multiple': 'múltiple paral·lel',
    'parallel': 'paral·lel',
};

const PATRO_ESDEVENIMENT = new RegExp(
    `^(?:(${Object.keys(DEFINICIONS_ESDEVENIMENT).join('|')}) )?`
    + `(${Object.keys(ESDEVENIMENTS).join('|')})`
    + '( \\(non[ -]interrupting\\))?$',
    'i');

const traduirEsdeveniment = (text: string): string | undefined => {
    const coincidencia = PATRO_ESDEVENIMENT.exec(text);
    if (!coincidencia) {
        return undefined;
    }
    const [, definicio, tipus, noInterruptor] = coincidencia;
    return [
        ESDEVENIMENTS[tipus.toLowerCase()],
        definicio && DEFINICIONS_ESDEVENIMENT[definicio.toLowerCase()],
        noInterruptor && '(no interruptor)',
    ].filter(Boolean).join(' ');
};

/** Mateixa signatura que el `translate` de diagram-js: tradueix i substitueix els {marcadors}. */
export const traduir = (template: string, replacements?: Record<string, string>): string => {
    const text = TEXTOS[template] ?? traduirEsdeveniment(template) ?? template;
    return text.replace(/{([^}]+)}/g, (_, clau: string) => replacements?.[clau] || `{${clau}}`);
};

/** Mòdul de diagram-js que substitueix el servei `translate`. */
const TraduccionsModule = {
    translate: ['value', traduir],
};

export default TraduccionsModule;
