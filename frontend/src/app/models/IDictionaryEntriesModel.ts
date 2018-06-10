import {DictionaryEntry} from "./DictionaryEntry";
import {IDictionaryEntriesMap} from "./IDictionaryEntriesMap";


export interface IDictionaryEntriesModel {
  [key: string]: IDictionaryEntriesMap[];
};
