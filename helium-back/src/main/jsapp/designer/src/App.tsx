import * as React from 'react';
import BpmnJS from 'bpmn-js/lib/Modeler';
import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
import '@bpmn-io/properties-panel/dist/assets/properties-panel.css';
import {
    BpmnPropertiesPanelModule,
    BpmnPropertiesProviderModule,
    CamundaPlatformPropertiesProviderModule,
} from 'bpmn-js-properties-panel';
import camundaModdle from 'camunda-bpmn-moddle/resources/camunda.json';
import initialDiagram from './assets/diagram.bpmn?raw';
import Modal from './components/Modal';
import Toolbar from './components/Toolbar';
import FlowablePropertiesProviderModule from './flowable/FlowablePropertiesProviderModule';
import flowableModdle from './flowable/flowable.json';

const App = () => {
    const [modalOpen, setModalOpen] = React.useState(false);
    const [modalContent, setModalContent] = React.useState<string>();
    const canvasRef = React.useRef<HTMLDivElement | null>(null);
    const propertiesRef = React.useRef<HTMLDivElement | null>(null);
    const modelerRef = React.useRef<BpmnJS | null>(null);
    const showCode = () => {
        modelerRef.current?.saveXML({ format: true }).then((response) => {
            setModalOpen(true);
            setModalContent(response.xml);
        });
    };
    const download = () => {
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
        });
    };
    React.useEffect(() => {
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
        modelerRef.current = modeler;
        modelerRef.current.importXML(initialDiagram);
        return () => {
            modeler.destroy();
        };
    }, []);
    return (
        <>
            <div style={{ display: 'flex' }}>
                <div id="canvas" ref={canvasRef} style={{ flexGrow: 1, height: '100vh' }}></div>
                <div id="properties" ref={propertiesRef} style={{ width: '340px' }}></div>
            </div>
            <Toolbar showCode={showCode} download={download} />
            <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Codi BPMN">
                <pre style={{ whiteSpace: 'pre-wrap' }}>{modalContent}</pre>
            </Modal>
        </>
    );
};

export default App;
