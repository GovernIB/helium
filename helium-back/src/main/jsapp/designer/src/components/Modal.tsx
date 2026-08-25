import React from 'react';

type ModalProps = {
    isOpen: boolean;
    onClose: () => void;
    children: React.ReactNode;
    title?: string;
    width?: string;
};

export default ({ isOpen, onClose, children, title, width = '1200px' }: ModalProps) => {
    React.useEffect(() => {
        const handleEscape = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose();
        };
        if (isOpen) {
            document.addEventListener('keydown', handleEscape);
        }
        return () => {
            document.removeEventListener('keydown', handleEscape);
        };
    }, [isOpen, onClose]);
    if (!isOpen) return null;
    return (
        <div style={styles.overlay} onClick={onClose}>
            <div style={{...styles.modal, width}} onClick={(e) => e.stopPropagation()}>
                <div style={styles.header}>
                    {title && <h3 style={{ margin: 0 }}>{title}</h3>}
                    <button onClick={onClose} style={styles.closeBtn}>
                        ✕
                    </button>
                </div>

                <div style={styles.body}>{children}</div>
            </div>
        </div>
    );
};

const styles: Record<string, React.CSSProperties> = {
    overlay: {
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0,0,0,0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
    },
    modal: {
        fontFamily: 'sans serif',
        background: 'white',
        borderRadius: 8,
        maxWidth: '90%',
        maxHeight: '80vh',
        overflow: 'auto',
        boxShadow: '0 10px 30px rgba(0,0,0,0.3)',
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '12px 16px',
        borderBottom: '1px solid #eee',
    },
    body: {
        padding: 16,
    },
    closeBtn: {
        border: 'none',
        background: 'transparent',
        fontSize: 18,
        cursor: 'pointer',
    },
};
