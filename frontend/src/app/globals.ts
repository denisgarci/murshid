import { Injectable } from '@angular/core';
import {IEnum} from "./models/IEnum";

@Injectable()
export class Globals {
  partsOfSpeech: IEnum;
  accidenceTypes: IEnum;

}
