import './Toolbar.css';

interface ToolbarProps {
    showCode: () => void;
    download: () => void;
    save: () => void;
    upload?: (() => void);
}

export default (props: ToolbarProps) => {
    const { showCode, download, save, upload} = props;
    return (
        <div className="toolbar">
            <div className="grup">
                <button title="Codi" onClick={showCode}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <g id="System / Code">
                            <path
                                d="M15 7L20 12L15 17M9 17L4 12L9 7"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </g>
                    </svg>
                </button>
                {upload && (<button title="Carregar" onClick={upload}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <g id="Interface / Upload">
                            <path
                                d="M6 21H18M12 3V17M12 3L17 8M12 3L7 8"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </g>
                    </svg>
                </button>)}
                <button title="Descarregar" onClick={download}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <g id="Interface / Download">
                            <path
                                d="M6 21H18M12 3V17M12 17L17 12M12 17L7 12"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </g>
                    </svg>
                </button>
                <button title="Publicar" onClick={save}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <g id="Interface / Save">
                            <path
                                d="M4 4H16L20 8V20H4V4Z"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                            <path
                                d="M7 4V10H15V4"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </g>
                    </svg>
                </button>
            </div>
        </div>
    );
};
