import * as React from 'react';
import BpmnJS from 'bpmn-js/lib/Modeler';
import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
import '@bpmn-io/properties-panel/dist/assets/properties-panel.css';
import './app.css';
import {
    BpmnPropertiesPanelModule,
    BpmnPropertiesProviderModule,
    //CamundaPlatformPropertiesProviderModule,
} from 'bpmn-js-properties-panel';
import FlowablePropertiesProviderModule from './flowable/FlowablePropertiesProviderModule';
import {fetchHandlers, getHandlersSync} from './flowable/handlers';

import Modal from './components/Modal';
import Toolbar from './components/Toolbar';
import LoadingOverlay from "./components/LoadingOverlay";

import flowableModdle from './flowable/flowable.json';
import heliumModdle from './helium/helium.json';
import {migrarAtributsFlowable, migrarHandlersFlowable, migrarParametresVar} from './helium/heliumHandler';
//import camundaModdle from 'camunda-bpmn-moddle/resources/camunda.json';

import {useCallback} from "react";
import UploadIcon from "./components/UploadIcon.tsx";
import type {ModdleElement} from "bpmn-js/lib/model/Types";

interface DeployFormType {
    expedientTipusId: string;
    etiqueta: string;
    hasStartTask: boolean;
    actualitzarExpedientsActius: boolean;
    errors: string[];
}

interface ProcessDefinitionDataType {
    expedientsTipus: {
        codi: string;
        nom: string;
    }[];
}

/** Amplada del plafó de propietats (px): per defecte, mínima i clau on es recorda per a l'usuari. */
const AMPLADA_PROPIETATS_DEFECTE = 340;
const AMPLADA_PROPIETATS_MIN = 260;
const AMPLADA_PROPIETATS_CLAU = 'helium.designer.ampladaPropietats';
const CLASSE_PANTALLA_COMPLETA = 'helium-editor-pantalla-completa';

const ampladaPropietatsMax = () => Math.max(AMPLADA_PROPIETATS_MIN, Math.round(window.innerWidth * 0.7));

const limitarAmpladaPropietats = (amplada: number) =>
    Math.min(Math.max(Math.round(amplada), AMPLADA_PROPIETATS_MIN), ampladaPropietatsMax());

const llegirAmpladaPropietats = (): number => {
    try {
        const valor = Number(window.localStorage.getItem(AMPLADA_PROPIETATS_CLAU));
        return valor > 0 ? limitarAmpladaPropietats(valor) : AMPLADA_PROPIETATS_DEFECTE;
    } catch {
        return AMPLADA_PROPIETATS_DEFECTE;
    }
};

const desarAmpladaPropietats = (amplada: number) => {
    try {
        window.localStorage.setItem(AMPLADA_PROPIETATS_CLAU, String(amplada));
    } catch {
        // Sense emmagatzematge (navegació privada...): només es perd la preferència
    }
};

/** Camps de text on Ctrl+Z ha de desfer el text i no el diagrama. */
const esCampEditable = (target: EventTarget | null) => {
    const element = target as HTMLElement | null;
    return !!element && (
        element.isContentEditable
        || ['INPUT', 'TEXTAREA', 'SELECT'].includes(element.tagName)
    );
};

interface CommandStack {
    undo(): void;
    redo(): void;
}

interface Canvas {
    resized(): void;
}

interface FormError {
    camp: string;
    missatge: string;
}

interface FormResponse {
    objecte: any;
    estat: string;
    errorsGlobals: FormError[];
    errorsCamps: FormError[];
}

const App = () => {
    const config = window.__APP_CONFIG__ || {};
    const isNew: boolean = config.isNew;

    const initializedRef = React.useRef<boolean>(false);
    const canvasRef = React.useRef<HTMLDivElement | null>(null);
    const propertiesRef = React.useRef<HTMLDivElement | null>(null);
    const modelerRef = React.useRef<BpmnJS | null>(null);
    const inputRef = React.useRef<HTMLInputElement | null>(null);

    const [isLoading, setIsLoading] = React.useState(true);
    const [isDirty, setIsDirty] = React.useState<boolean>(false);
    const [canUndo, setCanUndo] = React.useState<boolean>(false);
    const [canRedo, setCanRedo] = React.useState<boolean>(false);
    const [ampladaPropietats, setAmpladaPropietats] = React.useState<number>(llegirAmpladaPropietats);
    const [redimensionant, setRedimensionant] = React.useState<boolean>(false);
    const [pantallaCompleta, setPantallaCompleta] = React.useState<boolean>(false);
    const [modalOpen, setModalOpen] = React.useState<boolean>(false);
    const [modalDespOpen, setModalDespOpen] = React.useState<boolean>(false);
    const [modalContent, setModalContent] = React.useState<string>();
    const [isDragging, setIsDragging] = React.useState(false);
    const [data, setData] = React.useState<ProcessDefinitionDataType>({
        expedientsTipus: []
    });

    const [deployForm, setDeployForm] = React.useState<DeployFormType>({
        expedientTipusId: config.expedientTipusId,
        etiqueta: config.definicioProcesEtiqueta,
        hasStartTask: !!config.hasStartTask,
        actualitzarExpedientsActius: false,
        errors: []
    });

    React.useEffect(() => {
        const handleBeforeUnload = (event: Event) => {
            if (isDirty) {
                event.preventDefault();
                event.returnValue = false;
            }
        };

        window.addEventListener('beforeunload', handleBeforeUnload);
        return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }, [isDirty]);

    React.useEffect(() => {
        if(initializedRef.current)
            return;
        setIsLoading(true);
        init().finally(() => {
            setIsLoading(false);
        });
        initializedRef.current = true;
    }, []);

    const init = async () => {
        if (!canvasRef.current) return;
        const modeler = new BpmnJS({
            container: canvasRef.current,
            propertiesPanel: {
                parent: propertiesRef.current,
            },
            additionalModules: [
                BpmnPropertiesPanelModule,
                BpmnPropertiesProviderModule,
                FlowablePropertiesProviderModule,
                //CamundaPlatformPropertiesProviderModule
            ],
            moddleExtensions: {
                //camunda: camundaModdle,
                flowable: flowableModdle,
                helium: heliumModdle,
            },
        });

        const eventBus: any = modeler.get('eventBus');
        const handleCommandStackChange = () => {
            const commandStack: any = modeler.get('commandStack');
            setIsDirty(commandStack.canUndo());
            setCanUndo(commandStack.canUndo());
            setCanRedo(commandStack.canRedo());
        };

        eventBus.on('commandStack.changed', handleCommandStackChange);
        modelerRef.current = modeler;

        setDeployForm({
            expedientTipusId: config.expedientTipusId,
            etiqueta: config.definicioProcesEtiqueta,
            hasStartTask: config.hasStartTask,
            actualitzarExpedientsActius: false,
            errors: []
        });

        await loadData();
        await fetchHandlers();

        if (isNew) {
            await modelerRef.current.createDiagram();
            marcarProcessosExecutables();
        } else {
            await loadDiagram();
        }

        return () => {
            modeler.destroy();
        };
    }

    /**
     * El diagrama buit de bpmn-js crea el procés amb isExecutable="false" i Flowable ignora els processos no
     * executables. Es canvia directament al model (sense passar per la pila de comandes) per no marcar el
     * diagrama com a modificat.
     */
    const marcarProcessosExecutables = () => {
        const definitions: any = modelerRef.current?.getDefinitions();
        for (const element of definitions?.rootElements || []) {
            if (element.$type === 'bpmn:Process') {
                element.isExecutable = true;
            }
        }
    };

    const undo = () => {
        modelerRef.current?.get<CommandStack>('commandStack').undo();
    };

    const redo = () => {
        modelerRef.current?.get<CommandStack>('commandStack').redo();
    };

    /*
     * Ctrl+Z / Ctrl+Shift+Z (i Ctrl+Y) a tot l'editor. bpmn-js només escolta el teclat quan el llenç té el focus
     * (i el plafó de propietats, als seus camps), així que després de fer clic a la barra d'eines o fora del llenç
     * les dreceres no farien res. Si ja les ha gestionat bpmn-js (defaultPrevented) no es tornen a aplicar, i en
     * altres camps de text (diàlegs, edició d'etiquetes) es deixa el comportament del navegador.
     */
    React.useEffect(() => {
        const handleKeyDown = (event: KeyboardEvent) => {
            if (event.defaultPrevented || event.altKey || !(event.ctrlKey || event.metaKey)
                || esCampEditable(event.target)) {
                return;
            }
            const tecla = event.key.toLowerCase();
            const esDesfer = tecla === 'z' && !event.shiftKey;
            const esRefer = (tecla === 'z' && event.shiftKey) || (tecla === 'y' && !event.shiftKey);
            if (!esDesfer && !esRefer) {
                return;
            }
            // Amb un diàleg obert (codi, desplegar) no es modifica el diagrama de darrere
            if (modalOpen || modalDespOpen) {
                return;
            }
            event.preventDefault();
            if (esDesfer) {
                undo();
            } else {
                redo();
            }
        };
        document.addEventListener('keydown', handleKeyDown);
        return () => document.removeEventListener('keydown', handleKeyDown);
    }, [modalOpen, modalDespOpen]);

    /** El llenç ha de recalcular la mida quan canvia l'amplada del plafó o es passa a pantalla completa. */
    React.useEffect(() => {
        modelerRef.current?.get<Canvas>('canvas').resized();
    }, [ampladaPropietats, pantallaCompleta]);

    /*
     * Pantalla completa: el navegador passa a pantalla completa i l'editor ocupa tota la finestra, amagant la
     * capçalera i el peu de Helium. Si el navegador no ho permet, l'editor igualment ocupa tota la finestra.
     */
    const togglePantallaCompleta = () => {
        if (pantallaCompleta) {
            setPantallaCompleta(false);
            if (document.fullscreenElement) {
                document.exitFullscreen().catch(() => undefined);
            }
        } else {
            setPantallaCompleta(true);
            document.documentElement.requestFullscreen?.().catch(() => undefined);
        }
    };

    React.useEffect(() => {
        // Sortir de la pantalla completa del navegador (Esc, F11...) també surt del mode de l'editor
        const handleFullscreenChange = () => {
            if (!document.fullscreenElement) {
                setPantallaCompleta(false);
            }
        };
        document.addEventListener('fullscreenchange', handleFullscreenChange);
        return () => document.removeEventListener('fullscreenchange', handleFullscreenChange);
    }, []);

    React.useEffect(() => {
        document.body.classList.toggle(CLASSE_PANTALLA_COMPLETA, pantallaCompleta);
        return () => document.body.classList.remove(CLASSE_PANTALLA_COMPLETA);
    }, [pantallaCompleta]);

    /** Arrossegar el separador canvia l'amplada del plafó de propietats (doble clic: amplada per defecte). */
    const iniciarRedimensio = (event: React.PointerEvent<HTMLDivElement>) => {
        if (event.button !== 0) {
            return;
        }
        event.preventDefault();
        const separador = event.currentTarget;
        separador.setPointerCapture(event.pointerId);
        const xInicial = event.clientX;
        const ampladaInicial = ampladaPropietats;
        let amplada = ampladaInicial;
        setRedimensionant(true);
        const moure = (e: PointerEvent) => {
            amplada = limitarAmpladaPropietats(ampladaInicial + xInicial - e.clientX);
            setAmpladaPropietats(amplada);
        };
        const acabar = () => {
            separador.removeEventListener('pointermove', moure);
            separador.removeEventListener('pointerup', acabar);
            separador.removeEventListener('pointercancel', acabar);
            setRedimensionant(false);
            desarAmpladaPropietats(amplada);
        };
        separador.addEventListener('pointermove', moure);
        separador.addEventListener('pointerup', acabar);
        separador.addEventListener('pointercancel', acabar);
    };

    const canviarAmpladaPropietats = (amplada: number) => {
        const nova = limitarAmpladaPropietats(amplada);
        setAmpladaPropietats(nova);
        desarAmpladaPropietats(nova);
    };

    const handleSeparadorKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
        const pas = event.shiftKey ? 80 : 20;
        if (event.key === 'ArrowLeft') {
            canviarAmpladaPropietats(ampladaPropietats + pas);
        } else if (event.key === 'ArrowRight') {
            canviarAmpladaPropietats(ampladaPropietats - pas);
        } else if (event.key === 'Home') {
            canviarAmpladaPropietats(ampladaPropietatsMax());
        } else if (event.key === 'End') {
            canviarAmpladaPropietats(AMPLADA_PROPIETATS_MIN);
        } else {
            return;
        }
        event.preventDefault();
    };

    const showCode = () => {
        modelerRef.current?.saveXML({format: true}).then((response) => {
            setModalOpen(true);
            setModalContent(response.xml);
        });
    };

    const download = () => {
        setIsLoading(true);
        modelerRef.current?.saveXML({format: true}).then((response) => {
            if (!response.error) {
                const blob = new Blob([response.xml ?? ''], {type: 'text/plain'});
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = 'process_definition.bpmn';
                link.click();
                URL.revokeObjectURL(url);
            }
        })
            .finally(() => {
                setIsLoading(false);
            });
    };

    const save = async () => {
        // Comprovar que te tasca inicial
        const base: any = modelerRef.current!.getDefinitions();
        const rootElement: any = base.rootElements[0];
        const startEvent: ModdleElement = rootElement.flowElements[0];
        if (!rootElement.name)
            rootElement.name = rootElement.id;

        console.log({base});
        const hasStartTask: boolean = startEvent.outgoing?.length == 1 &&
            startEvent.outgoing[0].$type == 'bpmn:SequenceFlow' &&
            startEvent.outgoing[0].targetRef.$type == 'bpmn:UserTask';

        setDeployForm({...deployForm, hasStartTask, errors: []});
        setModalDespOpen(true);
    }

    const upload = () => {
        inputRef.current?.click();
    }

    const loadDiagram = async () => {
        // La pestanya Editor pot deixar la URL acabada en /editor: es normalitza igual que a handlers.ts
        const base = document.URL.split(/[?#]/)[0].replace(/\/editor\/?$/, '').replace(/\/$/, '');
        const response = await fetch(`${base}/editorXml`);
        const xml = await response.text();
        await importarXml(xml);
    }

    /** Importa el diagrama i passa els atributs i handlers del format antic (flowable:*) al BPMN genèric. */
    const importarXml = async (xml: string) => {
        const modeler = modelerRef.current!;
        await modeler.importXML(xml);
        const classesHandlers = new Set(getHandlersSync().map((h) => h.classe));
        const definitions = modeler.getDefinitions();
        const moddle = modeler.get('moddle');
        const handlers = migrarHandlersFlowable(definitions, moddle, classesHandlers);
        const atributs = migrarAtributsFlowable(definitions, moddle);
        const parametresVar = migrarParametresVar(definitions, getHandlersSync());
        if (handlers > 0 || atributs > 0 || parametresVar > 0) {
            console.info(`Convertit a BPMN genèric: ${handlers} handlers, ${atributs} atributs de Flowable i ${parametresVar} paràmetres varXXX`);
            setIsDirty(true);
        }
    }

    const deploy = async () => {
        setIsLoading(true);
        try {
            const [response] = await Promise.all([modelerRef.current?.saveXML({format: true})]);

            if (response == undefined)
                return;

            setIsDirty(false);
            const formData = new FormData();
            const file = new File([response.xml!], 'processDefinition.bpmn');
            formData.append('file', file);
            formData.append('accio', 'PROCES_DESPLEGAR');
            formData.append('id', config.definicioProcesId);
            formData.append('entornId', config.entornId);
            formData.append('etiqueta', deployForm.etiqueta);
            formData.append('hasStartTask', `${deployForm.hasStartTask}`);
            formData.append('actualitzarExpedientsActius', `${deployForm.actualitzarExpedientsActius}`);
            formData.append('expedientTipusId', deployForm.expedientTipusId);
            const saveResponse: Response = await fetch(`${config.baseUrl}/definicioProces/editor/desplegar`, {
                method: 'POST',
                body: formData,
            });
            if (!saveResponse.ok) {
                const formResponse: FormResponse = await saveResponse.json();
                const errorsGlobals = formResponse.errorsGlobals?.map((err) => `${err.camp}: ${err.missatge}`);
                const errorsCamps = formResponse.errorsCamps?.map((err) => `${err.camp}: ${err.missatge}`);
                setDeployForm({...deployForm, errors: [...(errorsGlobals || []), ...(errorsCamps || [])]});
                alert('Error desant definició de procès \n' + (await saveResponse.text()));
                return;
            }
            setModalDespOpen(false);
            window.location.href = config.returnUrl;
        } finally {
            setIsLoading(false);
        }
    }

    const loadData = async () => {
        const response = await fetch(config.baseUrl + '/definicioProces/editor/data');
        setData(await response.json());
    }

    const loadFile = async (f: File) => {
        if (f == null)
            return;
        if (!f.name.endsWith('.bpmn')) {
            alert(`Fitxer no valid, Només s\'accepten ".bpmn"`);
            return;
        }
        try {
            await importarXml(await f.text());
        } catch (e: any) {
            alert(e.message);
        }
    }

    const handleDrop = useCallback(
        async (e: React.DragEvent<HTMLDivElement>) => {
            e.preventDefault();
            if (!isNew) return;
            setIsDragging(false);
            const flist: FileList = e.dataTransfer.files;
            if (flist.length > 0) {
                loadFile(flist.item(0)!);
            }
        },
        []
    );

    const handleDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        if (!isNew) return;
        setIsDragging(true);
    }, []);

    const handleDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        if (!isNew) return;
        setIsDragging(false);
    }, [modelerRef.current]);

    const onFileChange = async (e: React.ChangeEvent<HTMLInputElement, HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0)
            loadFile(e.target.files?.item(0)!)
    }

    return (
        <>
            <input
                ref={inputRef}
                type="file"
                multiple={false}
                onChange={onFileChange}
                style={{display: "none"}}
            />
            <LoadingOverlay isLoading={isLoading}/>
            <div
                className={`helium-editor${pantallaCompleta ? ' pantalla-completa' : ''}${redimensionant ? ' redimensionant' : ''}`}
                style={{
                    display: 'flex',
                    position: 'relative',
                    border: `${isDragging ? "2px dashed  #3b82f6" : "none"}`,
                }}
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
            >
                {isDragging && (<div style={{
                    display: "flex",
                    justifyContent: "center",
                    position: "absolute",
                    zIndex: 10,
                    width: '100%',
                    height: '100%',
                    alignItems: "center",
                    backgroundColor: "white",
                    opacity: 0.5,
                    pointerEvents: "none",
                }}>
                    <UploadIcon animate={true} scale={5}></UploadIcon>
                </div>)}
                <Toolbar showCode={showCode} download={download} save={save} upload={isNew ? upload : undefined}
                         undo={undo} redo={redo} canUndo={canUndo} canRedo={canRedo}
                         pantallaCompleta={pantallaCompleta} togglePantallaCompleta={togglePantallaCompleta}
                         style={{right: ampladaPropietats + 40}}/>
                <div id="canvas" ref={canvasRef} style={{flexGrow: 1, minWidth: 0, height: '100vh'}}></div>
                <div
                    className="helium-editor-separador"
                    role="separator"
                    aria-orientation="vertical"
                    aria-label="Amplada del plafó de propietats"
                    aria-valuenow={ampladaPropietats}
                    aria-valuemin={AMPLADA_PROPIETATS_MIN}
                    title="Arrossegar per canviar l'amplada del plafó (doble clic: amplada per defecte)"
                    tabIndex={0}
                    onPointerDown={iniciarRedimensio}
                    onDoubleClick={() => canviarAmpladaPropietats(AMPLADA_PROPIETATS_DEFECTE)}
                    onKeyDown={handleSeparadorKeyDown}
                ></div>
                <div id="properties" ref={propertiesRef} style={{width: ampladaPropietats, flexShrink: 0}}></div>
            </div>

            <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Codi BPMN">
                <pre style={{whiteSpace: 'pre-wrap'}}>{modalContent}</pre>
            </Modal>
            <Modal width="40vw" isOpen={modalDespOpen} onClose={() => setModalDespOpen(false)}
                   title="Desplegar definició de process">
                {deployForm.errors.length > 0 && <div style={styles.errorContainer}>
                    {
                        deployForm.errors.map((e, i) => (
                            <span style={styles.errorText} key={i}>{e}</span>
                        ))
                    }
                </div>}
                <div style={{display: 'flex', flexDirection: 'column'}}>
                    <div style={styles.formGroup}>
                        <label style={styles.label} htmlFor="etiqueta">Etiqueta</label>
                        <input style={styles.input} id="etiqueta" value={deployForm.etiqueta}
                               onChange={(e) => setDeployForm({...deployForm, etiqueta: e.target.value})}/>
                    </div>
                    <div style={styles.formGroup}>
                        <label htmlFor="hasStartTask" style={styles.label}>
                            <input id="hasStartTask" type={'checkbox'} checked={deployForm.hasStartTask}
                                   onChange={(e) => {
                                       setDeployForm({...deployForm, hasStartTask: e.target.checked});
                                   }}/>
                            <span>Amb tasca inicial?</span>
                        </label>
                    </div>
                    <div style={styles.formGroup}>
                        <label style={styles.label} htmlFor="expedientTipusId">Tipus d'expedient</label>
                        <select style={styles.input} id="expedientTipusId"
                                onChange={(e) => setDeployForm({...deployForm, expedientTipusId: e.target.value})}>
                            <option></option>
                            {
                                data.expedientsTipus.map((te, i) => (
                                    <option key={i} selected={deployForm.expedientTipusId == te.codi}
                                            value={te.codi}>{te.nom}</option>
                                ))
                            }
                        </select>
                    </div>
                    <div style={styles.formGroup}>
                        <label htmlFor="actualitzarExpedientsActius" style={styles.label}>
                            <input id="actualitzarExpedientsActius" type={'checkbox'}
                                   checked={deployForm.actualitzarExpedientsActius} onChange={(e) => setDeployForm({
                                ...deployForm,
                                actualitzarExpedientsActius: e.target.checked
                            })}/>
                            <span>Actualitzar expedients actius?</span>
                        </label>
                    </div>
                    <div style={styles.footer}>
                        <button onClick={deploy} style={{...styles.btn, ...styles.btnSuccess}}>Desplegar</button>
                    </div>
                </div>
            </Modal>
        </>
    );
};

const styles: Record<string, React.CSSProperties> = {
    label: {
        flex: 1,
        display: 'flex',
        alignItems: 'center',
        columnGap: '5px',
        paddingTop: '10px',
        paddingBottom: '5px',
        fontWeight: 'bold',
        fontSize: '14px',
    },
    input: {
        border: '1px solid #ccc',
        borderStyle: 'solid',
        borderWidth: '1px',
        borderRadius: 4,
        padding: '6px 12px',
        fontSize: '16px',
        backgroundColor: 'white',
    },
    formGroup: {
        paddingRight: '2em',
        paddingLeft: '2em',
        paddingBottom: '1em',
        display: 'flex',
        justifyContent: 'space-between',
        flexDirection: 'column',
    },
    btn: {
        display: 'inline-block',
        padding: '6px 12px',
        marginBottom: 0,
        fontSize: '14px',
        fontWeight: 400,
        textAlign: 'center',
        whiteSpace: 'nowrap',
        verticalAlign: 'middle',
        touchAction: 'manipulation',
        cursor: 'pointer',
        userSelect: 'none',
        backgroundImage: 'none',
        border: '1px solid transparent',
        borderRadius: '4px',
    },
    btnSuccess: {
        color: '#fff',
        backgroundColor: '#5cb85c',
        borderColor: '#4cae4c',
    },
    footer: {
        display: 'flex',
        borderTop: '1px solid #ccc',
        flexDirection: 'row-reverse',
        paddingTop: '0.5em',
    },
    errorContainer: {
        borderRadius: 5,
        backgroundColor: '#ffb4b4',
        padding: '12px',
        display: 'flex',
        flexDirection: 'column',
    },
    errorText: {
        color: '#ff0000',
        paddingBottom: '10px',
    }
};

export default App;
