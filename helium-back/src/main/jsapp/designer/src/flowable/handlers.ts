export interface HandlerParametreDto {
    nom: string;
    codi: string;
    obligatori: boolean;
    param: string | null;
    paramDesc: string | null;
    varParam: string | null;
    varParamDesc: string | null;
}

export interface HandlerDto {
    classe: string;
    nom: string;
    descripcio?: string;
    parametres?: HandlerParametreDto[];
    agrupacio?: string;
}

let cachedHandlers: HandlerDto[] = [];
let fetchPromise: Promise<HandlerDto[]> | null = null;

export async function fetchHandlers(): Promise<HandlerDto[]> {
    if (fetchPromise) {
        return fetchPromise;
    }
    const url = document.URL.replace(/\/editor\/?$/, '').replace(/\/new\/?$/, '') + '/handlers';
    fetchPromise = fetch(url)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`Error fetching handlers: ${response.status}`);
            }
            return response.json();
        })
        .then((data: HandlerDto[]) => {
            cachedHandlers = data;
            return data;
        })
        .catch((error) => {
            console.error('Error fetching handlers:', error);
            fetchPromise = null;
            return [];
        });
    return fetchPromise;
}

export function getHandlersSync(): HandlerDto[] {
    return cachedHandlers;
}
