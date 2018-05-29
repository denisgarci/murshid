import {DictionaryEntryInflected} from "./DictionaryEntryInflected";

/**
 * Interface for a map of all the DictionaryEntries relevant for a song, with the key in the form "टूटे_ 4"
 */
export interface IDictionaryEntriesInflected {
  [key: string]: DictionaryEntryInflected;
};
