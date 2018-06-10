import {DictionaryEntry} from "./DictionaryEntry";

/**
 * Interface for a map of all the DictionaryEntries relevant for a song, with the key in the form MURSHID => DictionaryEntry
 */
export interface IDictionaryEntriesMap {
  [key: string]: DictionaryEntry[];
};
