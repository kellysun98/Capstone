export interface Transit {
    routeNode: Array<any>;
    cost: number;
    nodetype: Array<number>;
    distance: number;
    ttcname: string;
    nstop: number;
    startname: string;
    stopname: string;
    description: string;
    risk: Array<any>;
    time: number;
    walkingtime: number;
    ttctime: number;
}
