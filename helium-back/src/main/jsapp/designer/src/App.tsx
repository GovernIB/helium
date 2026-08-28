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
    CamundaPlatformPropertiesProviderModule,
} from 'bpmn-js-properties-panel';
import camundaModdle from 'camunda-bpmn-moddle/resources/camunda.json';
import FlowablePropertiesProviderModule from './flowable/FlowablePropertiesProviderModule';

import Modal from './components/Modal';
import Toolbar from './components/Toolbar';
import LoadingOverlay from "./components/LoadingOverlay";

import flowableModdle from './flowable/flowable.json';
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

    const [modalOpen, setModalOpen] = React.useState<boolean>(false);
    const [modalDespOpen, setModalDespOpen] = React.useState<boolean>(false);
    const [modalContent, setModalContent] = React.useState<string>();
    const [isLoading, setIsLoading] = React.useState(true);
    const [isDragging, setIsDragging] = React.useState(false);
    const [data, setData] = React.useState<ProcessDefinitionDataType>({
        expedientsTipus: []
    });

    const [deployForm, setDeployForm] = React.useState<DeployFormType>({
        expedientTipusId: config.expedientTipusId,
        etiqueta: config.definicioProcesEtiqueta,
		hasStartTask: !!config.hasStartTask,
        actualitzarExpedientsActius:  false,
        errors: []
    });

    const canvasRef = React.useRef<HTMLDivElement | null>(null);
    const propertiesRef = React.useRef<HTMLDivElement | null>(null);
    const modelerRef = React.useRef<BpmnJS | null>(null);
    const inputRef = React.useRef<HTMLInputElement | null>(null);

    const [isDirty, setIsDirty] = React.useState<boolean>(false);

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
        setIsLoading(true);
        init().finally(() => {
            setIsLoading(false);
        })
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
                CamundaPlatformPropertiesProviderModule,
                FlowablePropertiesProviderModule,
            ],
            moddleExtensions: {
                camunda: camundaModdle,
                flowable: flowableModdle,
            },
        });

        const eventBus:any = modeler.get('eventBus');
        const handleCommandStackChange = () => {
            const commandStack: any = modeler.get('commandStack');
            setIsDirty(commandStack.canUndo());
        };

        eventBus.on('commandStack.changed', handleCommandStackChange);
        modelerRef.current = modeler;

        setDeployForm({
            expedientTipusId: config.expedientTipusId,
            etiqueta: config.definicioProcesEtiqueta,
            hasStartTask: config.hasStartTask,
            actualitzarExpedientsActius:  false,
            errors: []
        });

        await loadData();

        if(isNew) {
            await modelerRef.current.createDiagram();
        } else {
            await loadDiagram();
        }

        return () => {
            modeler.destroy();
        };
    }

    const showCode = () => {
        modelerRef.current?.saveXML({ format: true }).then((response) => {
            setModalOpen(true);
            setModalContent(response.xml);
        });
    };

    const download = () => {
        setIsLoading(true);
        modelerRef.current?.saveXML({ format: true }).then((response) => {
            if (!response.error) {
                const blob = new Blob([response.xml ?? ''], { type: 'text/plain' });
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = 'process_definition.bpmn';
                link.click();
                URL.revokeObjectURL(url);
            }
        })
        .finally(() => {setIsLoading(false);});
    };

    const save = async () => {
        // Comprovar que te tasca inicial
        const base: any =  modelerRef.current!.getDefinitions();
        const startEvent: ModdleElement = base.rootElements[0].flowElements[0];
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
        const response = await fetch(`${document.URL}/editorXml`);
        const xml = await response.text();
        await modelerRef.current!.importXML(xml);
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
            if(!saveResponse.ok) {
                const formResponse: FormResponse = await saveResponse.json();
                const errorsGlobals = formResponse.errorsGlobals?.map((err) => `${err.camp}: ${err.missatge}`);
                const errorsCamps = formResponse.errorsCamps?.map((err) => `${err.camp}: ${err.missatge}`);
                setDeployForm({...deployForm, errors: [...(errorsGlobals||[]), ...(errorsCamps||[])]});
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
        if(f == null)
            return;
        if(!f.name.endsWith('.bpmn')) {
            alert(`Fitxer no valid, Només s\'accepten ".bpmn"`);
            return;
        }
        try {
            await modelerRef.current?.importXML(await f.text());
        } catch (e: any) {
            alert(e.message);
        }
    }

    const handleDrop = useCallback(
        async (e: React.DragEvent<HTMLDivElement>) => {
            e.preventDefault();
            if(!isNew) return;
            setIsDragging(false);
            const flist: FileList = e.dataTransfer.files;
            if(flist.length > 0) {
                loadFile(flist.item(0)!);
            }
        },
        []
    );

    const handleDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        if(!isNew) return;
        setIsDragging(true);
    }, []);

    const handleDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        if(!isNew) return;
        setIsDragging(false);
    }, [modelerRef.current]);

    const onFileChange = async (e: React.ChangeEvent<HTMLInputElement, HTMLInputElement>) => {
        if(e.target.files && e.target.files.length > 0)
            loadFile(e.target.files?.item(0)!)
    }

    return (
        <>
            <input
                ref={inputRef}
                type="file"
                multiple={false}
                onChange={onFileChange}
                style={{ display: "none" }}
            />
            <LoadingOverlay isLoading={isLoading}/>
            <div
                style={{
                    display: 'flex',
                    border: `${isDragging ? "2px dashed  #3b82f6" : "none"}`,
                    zIndex:999,
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
                <Toolbar showCode={showCode} download={download} save={save} upload={isNew? upload: undefined} />
                <div id="canvas" ref={canvasRef} style={{ flexGrow: 1, height: '100vh' }}></div>
                <div id="properties" ref={propertiesRef} style={{ width: '340px' }}></div>
            </div>

            <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Codi BPMN">
                <pre style={{ whiteSpace: 'pre-wrap' }}>{modalContent}</pre>
            </Modal>
            <Modal width="40vw" isOpen={modalDespOpen} onClose={() => setModalDespOpen(false)} title="Desplegar definició de process">
                {deployForm.errors.length > 0 && <div style={styles.errorContainer}>
                    {
                        deployForm.errors.map((e, i) => (
                            <span style={styles.errorText} key={i}>{e}</span>
                        ))
                    }
                </div>}
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <div style={styles.formGroup}>
                        <label style={styles.label} htmlFor="etiqueta">Etiqueta</label>
                        <input style={styles.input} id="etiqueta" value={deployForm.etiqueta} onChange={(e) => setDeployForm({...deployForm, etiqueta: e.target.value})} />
                    </div>
					<div style={styles.formGroup}>
					    <label htmlFor="hasStartTask" style={styles.label}>
							<input id="hasStartTask" type={'checkbox'} checked={deployForm.hasStartTask} onChange={(e) => {
                                setDeployForm({...deployForm, hasStartTask: e.target.checked});
                            }}/>
							<span>Amb tasca inicial?</span>
					    </label>
					</div>
                    <div style={styles.formGroup}>
                        <label style={styles.label} htmlFor="expedientTipusId">Tipus d'expedient</label>
                        <select style={styles.input} id="expedientTipusId" onChange={(e) => setDeployForm({...deployForm, expedientTipusId: e.target.value})} >
                            <option></option>
                            {
                                data.expedientsTipus.map((te, i) => (
                                    <option key={i} selected={deployForm.expedientTipusId == te.codi} value={te.codi}>{te.nom}</option>
                                ))
                            }
                        </select>
                    </div>
                    <div style={styles.formGroup}>
                        <label htmlFor="actualitzarExpedientsActius" style={styles.label}>
							<input id="actualitzarExpedientsActius" type={'checkbox'} checked={deployForm.actualitzarExpedientsActius} onChange={(e) => setDeployForm({...deployForm, actualitzarExpedientsActius: e.target.checked})}/>
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
