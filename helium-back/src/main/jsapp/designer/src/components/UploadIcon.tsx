type UploadIconProps = {
    animate: boolean;
    scale?: number;
};

export default function UploadIcon({ animate, scale=1 }: UploadIconProps) {
    const arrowClass = animate ? "fu-arrow-dragging" : "fu-arrow-idle";

    return (
        <svg
            width={64 * scale}
            height={64 * scale}
            viewBox="0 0 64 64"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            aria-hidden="true"
        >
            {/* Dashed circle border, pulses while dragging */}
            <circle
                cx="32"
                cy="32"
                r="28"
                stroke="#94a3b8"
                strokeWidth="2"
                strokeDasharray="4 5"
                className={animate ? "fu-dash-pulse" : ""}
            />
            <g>
                <path
                    d="M20 40 V44 a2 2 0 0 0 2 2 H42 a2 2 0 0 0 2-2 V40"
                    stroke="#64748b"
                    strokeWidth="2.5"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    fill="none"
                />
                <g className={arrowClass}>
                    <path
                        d="M32 16 V38"
                        stroke={animate ? "#6366f1" : "#64748b"}
                        strokeWidth="2.5"
                        strokeLinecap="round"
                    />
                    <path
                        d="M22 26 L32 16 L42 26"
                        stroke={animate ? "#6366f1" : "#64748b"}
                        strokeWidth="2.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        fill="none"
                    />
                </g>
            </g>
        </svg>
    );
}
