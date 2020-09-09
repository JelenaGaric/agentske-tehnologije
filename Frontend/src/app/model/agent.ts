import { AID } from './AID';

export class Agent {
    id : AID
    isRunning : boolean

    constructor() {
        this.isRunning = false;
    }
}