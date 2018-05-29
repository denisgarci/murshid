import {DictionaryEntryNotInflected} from "./DictionaryEntryNotInflected";

/**
 * Interface for a map of all the not-inflected DictionaryEntries relevant for a song, with the key in the form "टूटे_ 4"
 */
export interface IDictionaryEntriesNotInflected {
  [key: string]: DictionaryEntryNotInflected;
};
