import { StrictMode } from 'react';
import { createRoot, type Root} from 'react-dom/client';
import App from './App.tsx';

let root: Root | null = null;

function mountApp() {
    const container = document.getElementById("react-root") ?? document.getElementById("root");
    if (!container) return;
    if (root) {
        root.unmount(); // clean up previous instance if remounting
    }
    root = createRoot(container);
    root.render(
        <StrictMode>
            <App />
        </StrictMode>
    );
}

// @ts-ignore
window['mountRootApp'] = mountApp;
mountApp();
