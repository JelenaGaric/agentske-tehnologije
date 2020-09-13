import { AID } from './AID'

export class ACLMessageDTO {
    content: string
    performative: string
    sender: AID
    userArgs: any
}