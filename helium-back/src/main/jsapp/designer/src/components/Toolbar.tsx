import type {CSSProperties, ReactNode} from 'react';
import './Toolbar.css';

interface ToolbarProps {
    showCode?: () => void;
    download?: () => void;
    save?: () => void;
    upload?: (() => void);
    undo: () => void;
    redo: () => void;
    canUndo: boolean;
    canRedo: boolean;
    pantallaCompleta: boolean;
    togglePantallaCompleta?: () => void;
    style?: CSSProperties;
}

const Icona = ({children}: { children: ReactNode }) => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
         strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        {children}
    </svg>
);

/** Ctrl a Linux/Windows, ⌘ a Mac: només per als títols dels botons. */
const MOD = typeof navigator !== 'undefined' && /Mac|iPhone|iPad/.test(navigator.platform) ? '⌘' : 'Ctrl';

const Toolbar = (props: ToolbarProps) => {
    const {
        showCode, download, save, upload, undo, redo, canUndo, canRedo,
        pantallaCompleta, togglePantallaCompleta, style,
    } = props;
    return (
        <div className="toolbar" style={style}>
            <div className="grup">
                <button title={`Desfer (${MOD}+Z)`} aria-label="Desfer" onClick={undo} disabled={!canUndo}>
                    <Icona>
                        <path d="M9 14L4 9L9 4"/>
                        <path d="M4 9H14.5C17.5 9 20 11.5 20 14.5C20 17.5 17.5 20 14.5 20H11"/>
                    </Icona>
                </button>
                <button title={`Refer (${MOD}+Shift+Z)`} aria-label="Refer" onClick={redo} disabled={!canRedo}>
                    <Icona>
                        <path d="M15 14L20 9L15 4"/>
                        <path d="M20 9H9.5C6.5 9 4 11.5 4 14.5C4 17.5 6.5 20 9.5 20H13"/>
                    </Icona>
                </button>
            </div>
            {(showCode || upload || download || save) && <div className="separator"/>}
            <div className="grup">
                {showCode && (<button title="Codi" onClick={showCode}>
                    <Icona>
                        <path d="M15 7L20 12L15 17M9 17L4 12L9 7"/>
                    </Icona>
                </button>)}
                {upload && (<button title="Carregar" onClick={upload}>
                    <Icona>
                        <path d="M6 21H18M12 3V17M12 3L17 8M12 3L7 8"/>
                    </Icona>
                </button>)}
                {download && (<button title="Descarregar" onClick={download}>
                    <Icona>
                        <path d="M6 21H18M12 3V17M12 17L17 12M12 17L7 12"/>
                    </Icona>
                </button>)}
                {save && (<button title="Publicar" onClick={save}>
                    <Icona>
                        <path d="M4 4H16L20 8V20H4V4Z"/>
                        <path d="M7 4V10H15V4"/>
                    </Icona>
                </button>)}
            </div>
            {togglePantallaCompleta && <div className="separator"/>}
            {togglePantallaCompleta && <div className="grup">
                <button title={pantallaCompleta ? 'Sortir de la pantalla completa' : 'Pantalla completa'}
                        aria-label={pantallaCompleta ? 'Sortir de la pantalla completa' : 'Pantalla completa'}
                        aria-pressed={pantallaCompleta}
                        onClick={togglePantallaCompleta}>
                    {pantallaCompleta ? (
                        <Icona>
                            <path d="M9 4V9H4M15 4V9H20M9 20V15H4M15 20V15H20"/>
                        </Icona>
                    ) : (
                        <Icona>
                            <path d="M4 9V4H9M20 9V4H15M4 15V20H9M20 15V20H15"/>
                        </Icona>
                    )}
                </button>
            </div>}
        </div>
    );
};

export default Toolbar;
