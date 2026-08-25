import React, { useCallback, useRef, useState } from "react";

interface FileUploadBoxProps {
    onFilesSelected?: (files: File[]) => void;
    accept?: string;
    multiple?: boolean;
    maxSizeMB?: number;
}

interface StagedFile {
    file: File;
    id: string;
}

const formatBytes = (bytes: number): string => {
    if (bytes === 0) return "0 B";
    const units = ["B", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`;
};

const FileUploadBox: React.FC<FileUploadBoxProps> = ({
                                                         onFilesSelected,
                                                         accept,
                                                         multiple = true,
                                                         maxSizeMB = 25,
                                                     }) => {
    const [isDragging, setIsDragging] = useState(false);
    const [files, setFiles] = useState<StagedFile[]>([]);
    const [error, setError] = useState<string | null>(null);
    const inputRef = useRef<HTMLInputElement>(null);

    const addFiles = useCallback(
        (incoming: FileList | null) => {
            if (!incoming || incoming.length === 0) return;
            const maxBytes = maxSizeMB * 1024 * 1024;
            const accepted: StagedFile[] = [];
            let rejected = false;

            Array.from(incoming).forEach((file) => {
                if (file.size > maxBytes) {
                    rejected = true;
                    return;
                }
                accepted.push({
                    file,
                    id: `${file.name}-${file.size}-${file.lastModified}-${Math.random()
                        .toString(36)
                        .slice(2, 8)}`,
                });
            });

            if (rejected) {
                setError(`El fitxer supera la mida màxima de ${maxSizeMB}MB s'ha ignorat.`);
            } else {
                setError(null);
            }

            setFiles((prev) => {
                const next = multiple ? [...prev, ...accepted] : accepted.slice(0, 1);
                onFilesSelected?.(next.map((f) => f.file));
                return next;
            });
        },
        [maxSizeMB, multiple, onFilesSelected]
    );

    const handleDrop = useCallback(
        (e: React.DragEvent<HTMLDivElement>) => {
            e.preventDefault();
            setIsDragging(false);
            addFiles(e.dataTransfer.files);
        },
        [addFiles]
    );

    const handleDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragging(true);
    }, []);

    const handleDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragging(false);
    }, []);

    const removeFile = (id: string) => {
        setFiles((prev) => {
            const next = prev.filter((f) => f.id !== id);
            onFilesSelected?.(next.map((f) => f.file));
            return next;
        });
    };

    return (
        <div style={{ maxWidth: 480, width: "100%" }}>
            <div
                onClick={() => inputRef.current?.click()}
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        inputRef.current?.click();
                    }
                }}
                style={{
                    border: `2px dashed ${isDragging ? "#3b82f6" : "#d1d5db"}`,
                    padding: "2.5rem 1.5rem",
                    textAlign: "center",
                    cursor: "pointer",
                    backgroundColor: isDragging ? "#eff6ff" : "#fafafa",
                    transition: "background-color 0.15s, border-color 0.15s",
                }}
            >
                <input
                    ref={inputRef}
                    type="file"
                    accept={accept}
                    multiple={multiple}
                    onChange={(e) => addFiles(e.target.files)}
                    style={{ display: "none" }}
                />

                <p style={{ margin: 0, fontWeight: 500, color: "#111827" }}>
                    Afegir jar
                </p>
                <p style={{ margin: "4px 0 0", fontSize: 13, color: "#6b7280" }}>
                    click per afegir {accept ? ` ${accept}` : ""}
                </p>
                <p style={{ margin: "4px 0 0", fontSize: 12, color: "#9ca3af" }}>
                    Màxim {maxSizeMB}MB per fitxer
                </p>
            </div>

            {error && (
                <p style={{ color: "#dc2626", fontSize: 13, marginTop: 8 }}>{error}</p>
            )}

            {files.length > 0 && (
            <ul
                style={{
                    listStyle: "none",
                    margin: "1px 0 0",
                    padding: 0,
                    display: "flex",
                    flexDirection: "column",
                    gap: 6,
                }}
            >
                {files.map(({ file, id }) => (
                <li
                    key={id}
                    style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        border: "1px solid #e5e7eb",
                        padding: "8px 12px",
                        fontSize: 13,
                        backgroundColor: "#fff",
                    }}
                >
                <span style={{
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                      color: "#111827",
                      marginRight: 8,
                  }}>
                    {file.name}
                </span>
                <span style={{ display: "flex", alignItems: "center", gap: 10 }}>
                    <span style={{ color: "#6b7280", flexShrink: 0 }}>
                      {formatBytes(file.size)}
                    </span>
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            removeFile(id);
                        }}
                        aria-label={`Esborrar ${file.name}`}
                        style={{
                            border: "none",
                            background: "none",
                            cursor: "pointer",
                            color: "#9ca3af",
                            fontSize: 16,
                            lineHeight: 1,
                            padding: 0,
                        }} >
                      ×
                    </button>
                </span>
                </li>
                    ))}
            </ul>
            )}
        </div>
    );
};

export default FileUploadBox;
