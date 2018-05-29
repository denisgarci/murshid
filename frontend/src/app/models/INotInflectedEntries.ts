import {NotInflectedEntry} from "./NotInflectedEntry";

/**
 * Interface for a map of the not_inflected_entries relevant for a song, with the key in the form "PLATTS_टख़त्म_होना_0"
 */
export interface INotInflectedEntries {
  [key: string]: NotInflectedEntry;
};
