import React from "react";

export interface LoadingOverlayProps {
    isLoading: boolean;
    message?: string;
    fullScreen?: boolean;
    blur?: boolean;
}

export default function LoadingOverlay({
                                           isLoading,
                                           message = "Carregant...",
                                           fullScreen = true,
                                           blur = true,
                                       }: LoadingOverlayProps): React.ReactElement | null {
    if (!isLoading) return null;

    return (
        <div
            role="status"
            aria-live="polite"
            aria-busy="true"
            style={{
                position: fullScreen ? "fixed" : "absolute",
                inset: 0,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                gap: "12px",
                backgroundColor: "rgba(255, 255, 255, 0.75)",
                backdropFilter: blur ? "blur(2px)" : "none",
                WebkitBackdropFilter: blur ? "blur(2px)" : "none",
                zIndex: 1000,
                transition: "opacity 0.2s ease-in-out",
            }}
        >
            <Spinner />
            {message && (
                <span
                    style={{
                        fontSize: "14px",
                        color: "#374151",
                        fontFamily: "system-ui, -apple-system, sans-serif",
                    }}
                >
          {message}
        </span>
            )}
        </div>
    );
}

function Spinner(): React.ReactElement {
    return (
        <>
            <div
                style={{
                    width: "40px",
                    height: "40px",
                    border: "4px solid #e5e7eb",
                    borderTopColor: "#4f46e5",
                    borderRadius: "50%",
                    animation: "loading-overlay-spin 0.8s linear infinite",
                }}
            />
            <style>{`
        @keyframes loading-overlay-spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
        </>
    );
}
